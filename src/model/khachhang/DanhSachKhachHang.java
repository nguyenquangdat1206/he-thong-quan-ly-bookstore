package model.khachhang;

import model.interfaces.IQuanLy;
import model.interfaces.IFileIO;
import model.utils.TienIch;
import java.io.*;

public class DanhSachKhachHang implements IQuanLy, IFileIO {
    private int soLuong;
    private KhachHang[] ds;
    
    //khởi tạo danh sách
    public DanhSachKhachHang(){
        ds = new KhachHang[1000];
        soLuong = 0;
    }
    
    //tạo thêm danh sách nếu bị đầy
    private void tangKichThuoc(){
        KhachHang[] temp = new KhachHang[ds.length + 100];
        for(int i=0; i<ds.length; i++){
            temp[i] = ds[i];
        }
        ds = temp;
    }
    
    //nhập
    @Override
    public void nhap(){
        int n = TienIch.nhapSoNguyenDuong("Nhập số lượng khách hàng: ");
        if(n <= 0){
            System.out.println("Số lượng phải lớn hơn 0!");
            return;
        }
        for(int i=0; i<n; i++){
            System.out.println("\n--------KHÁCH HÀNG THỨ " + (i + 1) + "--------");
            if(soLuong == ds.length) tangKichThuoc();
            KhachHang kh = new KhachHang();
            kh.nhap();
            ds[soLuong] = kh;
            soLuong++;
        }
    }
    
    //xuat
    @Override
    public void xuat() {
        if (soLuong == 0) {
            System.out.println("Danh sách Khách hàng hiện đang trống.");
            return;
        }
        for(int i=0; i<soLuong; i++){
            System.out.println("\n--------THÔNG TIN KHÁCH HÀNG THỨ " + (i + 1) + "--------");
            ds[i].xuat();
        }
    }
    
    //thêm
    @Override
    public void them() {
        if(soLuong == ds.length) tangKichThuoc();
        KhachHang kh = new KhachHang();
        boolean hopLe;
        do{
            kh.nhap();
            hopLe = true;
            for(int j=0; j<soLuong; j++){
                if(kh.getMaKH().equalsIgnoreCase(ds[j].getMaKH())){
                    System.out.println("Mã khách hàng đã tồn tại! Vui lòng nhập lại.");
                    hopLe = false;
                    break;
                }
            }
        } while(!hopLe);
        ds[soLuong++] = kh;
        System.out.println("Thêm khách hàng thành công!");
    }
    
    //xóa (ĐÃ XÓA CHỮ @Override)
    public void xoa() {
        if (soLuong == 0) {
            System.out.println("Danh sách trống!");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã khách hàng cần xóa: ");
        xoa(ma);
    }

    public void xoa(String ma){
        boolean found  = false;
        for(int i=0; i<soLuong; i++){
            if(ds[i].getMaKH().equalsIgnoreCase(ma)){
                for(int j=i; j<soLuong-1; j++){
                    ds[j] = ds[j+1];
                }
                found = true;
                ds[soLuong-1] = null;
                soLuong--;
                System.out.println("Đã xóa khách hàng có mã " + ma);
                break;
            }
        }
        if(!found){
            System.out.println("Không tồn tại khách hàng có mã " + ma);
        }
    }
    
    //sửa (ĐÃ XÓA CHỮ @Override)
    public void sua() {
        if (soLuong == 0) {
            System.out.println("Danh sách trống!");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã khách hàng cần sửa: ");
        sua(ma);
    }

    public void sua(String ma) {
        boolean found = false;
        for(int i=0; i<soLuong; i++){
            if(ds[i].getMaKH().equalsIgnoreCase(ma)) {
                found = true;
                int choice;
                do {
                    System.out.println("Lựa chọn thông tin cần sửa của khách hàng ");
                    System.out.println("1. Tên khách hàng");
                    System.out.println("2. Số điện thoại");
                    System.out.println("0. Thoát");
                    choice = TienIch.nhapSoNguyenDuong("Chọn: ");
                    switch (choice) {
                        case 1:
                            ds[i].setTenKH(TienIch.nhapChuoi("Nhập tên mới: "));
                            break;
                        case 2:
                            ds[i].setSoDienThoai(TienIch.nhapChuoi("Nhập SĐT mới: "));
                            break;
                        case 0:
                            System.out.println("Thoát sửa khách hàng.");
                            break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ.");
                            break;
                    }
                } while (choice != 0);
                System.out.println("Đã sửa thông tin khách hàng thành công!");
                break;
            }
        }
        if(!found){
            System.out.println("Không tồn tại khách hàng có mã " + ma);
        }
    }
    
    //tim kiem
    public KhachHang timkiem_khachhang(String ma){
        for(int i=0; i<soLuong; i++) {
            if (ds[i].getMaKH().equalsIgnoreCase(ma)) return ds[i];
        }
        return null;
    }
    
    // Ghi dữ liệu ra file
    @Override
    public void ghiFile(String duongDan) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(duongDan))) {
            for (int i = 0; i < soLuong; i++) {
                pw.println(ds[i].getMaKH() + ";" + ds[i].getTenKH() + ";" + ds[i].getSoDienThoai());
            }
            System.out.println("Đã ghi thành công " + soLuong + " khách hàng vào file " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }
    
    // Đọc dữ liệu từ file
    @Override
    public void docFile(String duongDan) {
        try (BufferedReader br = new BufferedReader(new FileReader(duongDan))) {
            String line;
            soLuong = 0;
            ds = new KhachHang[1000];

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(";");
                if (data.length == 3) {
                    KhachHang kh = new KhachHang(data[0].trim(), data[1].trim(), data[2].trim());
                    if (soLuong == ds.length) tangKichThuoc();
                    ds[soLuong++] = kh;
                }
            }
            System.out.println("Đã tải dữ liệu từ file " + duongDan + " thành công!");
        } catch (FileNotFoundException e) {
            System.out.println("Chưa tìm thấy file " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file: " + e.getMessage());
        }
    }
}