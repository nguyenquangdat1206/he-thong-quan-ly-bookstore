package khachhang;
import java.io.*;

import interfaces.*;
import tienich.*;
import java.nio.charset.StandardCharsets;
public class DanhSachKhachHang implements IQuanLy,IFileIO{
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
        int n = TienIch.nhapSoNguyenKhongAm("Nhập số lượng khách hàng: ");
        if(n <= 0){
            System.out.println("Số lượng phải lớn hơn 0!");
            return;
        }
        for(int i=0; i<n; i++){
            System.out.println("\n--------KHÁCH HÀNG THỨ " + (i + 1) + "--------");
            them();
        }
    }
    //xuat
    @Override
    public void xuat() {
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
        }
        while(!hopLe);
        ds[soLuong++] = kh;
        System.out.println("Thêm khách hàng thành công!");
    }
    //xóa
    @Override
    public void xoa() {
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
    //sửa
    @Override
    public void sua() {
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
                    choice = TienIch.nhapSoNguyenKhongAm("Chọn: ");
                    switch (choice) {
                        case 1:
                            ds[i].setTenKH(TienIch.nhapChuoi("Nhập tên mới "));
                            break;
                        case 2:
                            ds[i].setSoDienThoai(TienIch.nhapChuoi("Nhập SĐT mới "));
                            break;
                        case 0:
                            System.out.println("Thoát sửa khách hàng.");
                            break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ.");
                            break;
                    }
                }
                while (choice != 0);
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
    //Chức năng mới: Menu tìm kiếm khách hàng theo Mã/Tên/SĐT
    public void timKiem() {
        if (soLuong == 0) {
            System.out.println("Danh sách khách hàng đang trống.");
            return;
        }
        System.out.println("\n--- TÌM KIẾM KHÁCH HÀNG ---");
        System.out.println("1. Tìm theo mã khách hàng");
        System.out.println("2. Tìm theo tên khách hàng (gần đúng)");
        System.out.println("3. Tìm theo số điện thoại");
        int chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
        switch (chon) {
            case 1: {
                String ma = TienIch.nhapChuoi("Nhập mã khách hàng: ");
                KhachHang kh = timkiem_khachhang(ma);
                if (kh == null) System.out.println("Không tìm thấy khách hàng có mã " + ma);
                else {
                    System.out.println("-------- KẾT QUẢ --------");
                    kh.xuat();
                }
                break;
            }
            case 2: {
                String tuKhoa = TienIch.nhapChuoi("Nhập tên cần tìm: ").toLowerCase();
                int dem = 0;
                for (int i = 0; i < soLuong; i++) {
                    if (ds[i].getTenKH().toLowerCase().contains(tuKhoa)) {
                        System.out.println("-------------------------");
                        ds[i].xuat();
                        dem++;
                    }
                }
                if (dem == 0) System.out.println("Không tìm thấy khách hàng nào có tên chứa '" + tuKhoa + "'.");
                else System.out.println("=> Tìm thấy " + dem + " khách hàng.");
                break;
            }
            case 3: {
                String sdt = TienIch.nhapChuoi("Nhập số điện thoại cần tìm: ");
                int dem = 0;
                for (int i = 0; i < soLuong; i++) {
                    if (ds[i].getSoDienThoai().contains(sdt)) {
                        System.out.println("-------------------------");
                        ds[i].xuat();
                        dem++;
                    }
                }
                if (dem == 0) System.out.println("Không tìm thấy khách hàng có SĐT chứa '" + sdt + "'.");
                else System.out.println("=> Tìm thấy " + dem + " khách hàng.");
                break;
            }
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }

    // Getter, Setter
    public int getSoLuong() { return soLuong; }
    public boolean them(KhachHang kh) {
        if (kh == null || kh.getMaKH() == null || kh.getMaKH().isBlank()
                || kh.getTenKH() == null || kh.getTenKH().isBlank() || kh.getSoDienThoai() == null
                || timkiem_khachhang(kh.getMaKH()) != null) return false;
        if (soLuong == ds.length) tangKichThuoc();
        ds[soLuong++] = kh;
        return true;
    }
    public KhachHang[] getDs() { return ds; }

    // Ghi dữ liệu ra file
    @Override
    public void ghiFile(String duongDan) {
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(duongDan), StandardCharsets.UTF_8))) {
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
        DanhSachKhachHang moi = new DanhSachKhachHang();
        try (BufferedReader br = java.nio.file.Files.newBufferedReader(java.nio.file.Path.of(duongDan), StandardCharsets.UTF_8)) {
            String line;
            boolean dongDau = true;
            while ((line = br.readLine()) != null) {
                if (dongDau) {
                    if (line.startsWith("\uFEFF")) line = line.substring(1);
                    dongDau = false;
                }
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(";", -1); // -1: giữ trường rỗng ở cuối để đếm đúng
                if (data.length != 3 || data[0].trim().isEmpty() || data[1].trim().isEmpty()
                        || data[2].trim().isEmpty() || moi.timkiem_khachhang(data[0].trim()) != null) {
                    System.out.println("Bỏ qua dòng khách hàng không hợp lệ hoặc trùng mã.");
                    continue;
                }
                moi.them(new KhachHang(data[0].trim(), data[1].trim(), data[2].trim()));
            }
            ds = moi.ds;
            soLuong = moi.soLuong;
            System.out.println("Đã tải " + soLuong + " khách hàng từ file " + duongDan);
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("Chưa tìm thấy file " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file: " + e.getMessage());
        }
    }
}
