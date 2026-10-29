package hoadon;
import java.io.*;
import java.time.LocalDate;

import mathang.*;
import interfaces.*;
import tienich.*;
import nhanvien.NhanVienBanHang;


import khachhang.DanhSachKhachHang;
import khachhang.KhachHang;
public class DanhSachHoaDon implements IQuanLy, IFileIO{
    //Thuộc tính
    private HoaDon[] ds;
    private int soLuong;
    private DanhSachMatHang dsmh;
    private DanhSachKhachHang dskh;
    //constructor
    public DanhSachHoaDon(DanhSachMatHang dsmh, DanhSachKhachHang dskh){
        ds = new HoaDon[1000];
        soLuong = 0;
        this.dsmh = dsmh;
        this.dskh = dskh;
    }
    //tạo thêm danh sách nếu bị đầy
    private void tangKichThuocHoaDon(){
        HoaDon[] temp = new HoaDon[ds.length + 100];
        for(int i=0; i<ds.length; i++){
            temp[i] = ds[i];
        }
        ds = temp;
    }
    //nhap
    @Override
    public void nhap() {
        System.out.println("Hóa đơn không nhập trực tiếp từ danh sách. "
                + "Hãy dùng taoHoaDon(...) để lập hóa đơn.");
    }
    //xuat
    @Override
    public void xuat() {
        if (soLuong == 0) {
            System.out.println("Chưa có hóa đơn nào.");
            return;
        }
        for (int i = 0; i < soLuong; i++) {
            ds[i].xuat();
            System.out.println("------------------------");
        }
    }
    //them
    @Override
    public void them() {
        System.out.println("Hóa đơn cần khách hàng, nhân viên và mặt hàng, "
                + "hãy dùng taoHoaDon(...) để lập hóa đơn.");
    }

    public int timViTri(String maHD){
        for(int i=0; i<soLuong; i++){
            if(ds[i].getMaHD().equalsIgnoreCase(maHD))
                return i;
        }
        return -1;
    }

    public HoaDon timKiem(String maHD){
        int vt = timViTri(maHD);
        return vt == -1 ? null : ds[vt];
    }
    //them (hàm riêng)
    public boolean them(HoaDon hd) {
        if(hd == null || timKiem(hd.getMaHD()) != null/* ktra có tồn tại ma HD hoặc có bị trùng maHD không*/) return false;
        if(soLuong == ds.length) tangKichThuocHoaDon();
        ds[soLuong++] = hd;
        return true;
    }
    //sua
    @Override
    public void sua() {
        String maHD = TienIch.nhapChuoi("Nhập mã hóa đơn cần sửa: ");
        HoaDon hd = timKiem(maHD);
        if (hd == null) {
            System.out.println("Không tồn tại hóa đơn " + maHD);
            return;
        }
        if (hd.isDaThanhToan()) {
            System.out.println("Hóa đơn đã thanh toán, không thể sửa.");
            return;
        }
        int chon;
        do {
            System.out.println("\n--- Sửa hóa đơn " + maHD + " ---");
            hd.inChiTiet();
            System.out.println("1. Sửa số lượng một dòng");
            System.out.println("2. Xóa một dòng");
            System.out.println("0. Thoát");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            if (chon != 1 && chon != 2) continue;
            int stt = TienIch.nhapSoNguyenDuong("Dòng: ");
            try {
                if (chon == 1) {
                    hd.suaSoLuongChiTiet(stt - 1, TienIch.nhapSoNguyenDuong("Số lượng: "));
                }
                else {
                    hd.xoaChiTiet(stt - 1);
                }
                System.out.println("Đã cập nhật. Tổng tiền mới: " + hd.tinhThanhTien());
            }
            catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Lỗi: " + e.getMessage());

            }
        }
        while (chon != 0) ;
    }
    //xoa
    public void xoa (){
        String maHD = TienIch.nhapChuoi("Nhập mã hóa đơn cần xóa: ");
        int vt = timViTri(maHD);
        if (vt == -1) {
            System.out.println("Không tìm thấy hóa đơn: " + maHD);
            return;
        }
        if (ds[vt].isDaThanhToan()) {
            System.out.println("Hóa đơn đã thanh toán, không thể xóa!");
            return;
        }
        for (int i = vt; i < soLuong -1; i++) {
            ds[i] = ds[i + 1];
        }
        ds[soLuong - 1] = null;
        soLuong--;
        return;
    }
    //get & set
    public HoaDon[] getDs(){return ds;}
    public int getSoLuong(){return soLuong;}
    //==================== Hàm nghiệp vụ tạo hóa đơn ====================
    //Trả về hóa đơn vừa tạo hoặc null nếu tạo không thành công
    public HoaDon taoHoaDon(String maHD, KhachHang kh, NhanVienBanHang nv){
        if(timKiem(maHD) != null){
            System.out.println("Mã hóa đơn đã tồn tại");
            return null;
        }
        HoaDon hd = new HoaDon(maHD,TienIch.nhapNgay("Nhập ngày lập: "),kh,nv);  //tạo 1 hóa đơn mới
        String tiep;
        do {
            System.out.println("Nhập mã mặt hàng cần mua");
            String maHang = TienIch.nhapChuoi("Nhập mã mặt hàng: ");
            MatHang mh = this.dsmh.timKiem_hang(maHang);
            if(mh == null){
                System.out.println("Không tìm thấy mặt hàng");
            }
            else{
                System.out.println("Nhập số lượng của mặt hàng cần mua: ");
                int sl = TienIch.nhapSoNguyenDuong("Nhập số lượng: ");
                if(sl <= 0){
                    System.out.println("Số lượng phải lơn hơn 0!");
                }
                else {
                    themVaoHoaDon(hd, mh, sl);
                }
            }
            tiep = TienIch.nhapChuoi("Tiếp tục thêm hàng (c/k): ");
        }
        while(tiep.equalsIgnoreCase("c"));
        if(hd.getSoLuongChiTiet() == 0){
            System.out.println("Hóa đơn chưa có mặt hàng");
            return null;
        }
        them(hd);
        System.out.println("Tạo hóa đơn thành công! Tổng tiền " + hd.tinhThanhTien());
        return hd;
    }
    //Mặt hàng đã có trong hóa đơn thì cộng dồn số lượng, chưa có thì thêm dòng mới
    //Không cho tổng số lượng vượt quá số lượng còn trên kệ
    private void themVaoHoaDon(HoaDon hd, MatHang mh, int sl){
        for(int i=0; i<hd.getSoLuongChiTiet(); i++){
            ChiTietHoaDon ct  = hd.getChiTiet()[i];
            if(ct.getMathang().getMa().equalsIgnoreCase(mh.getMa())){
                if(ct.getsoLuong() + sl > mh.getSoLuong()){
                    System.out.println("Không đủ hàng! Trên kệ còn " + mh.getSoLuong()
                            + ", hóa đơn đã có " + ct.getsoLuong());
                    return;
                }
                hd.suaSoLuongChiTiet(i,ct.getsoLuong()+sl);
                return;
            }
        }
        if(sl > mh.getSoLuong()){
            System.out.println("Không đủ hàng! Trên kệ chỉ còn " + mh.getSoLuong());
            return;
        }
        hd.themChiTiet(new ChiTietHoaDon(sl,mh));
    }

    // Ghi dữ liệu hóa đơn ra file
    // Dòng đầu mỗi hóa đơn: maHD;ngayLap;maKH;maNV;tenNV;daThanhToan(1/0)
    @Override
    public void ghiFile(String duongDan) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(duongDan))) {
            for (int i = 0; i < soLuong; i++) {
                HoaDon hd = ds[i];
                NhanVienBanHang nv = hd.getNhanVienLap();
                String maNV = (nv == null) ? "" : nv.getMaNguoi();
                String tenNV = (nv == null) ? "" : nv.getHoTen();
                pw.println(hd.getMaHD() + ";" + hd.getNgayLap() + ";" + hd.getKhachHang().getMaKH()
                        + ";" + maNV + ";" + tenNV + ";" + (hd.isDaThanhToan() ? 1 : 0));
                pw.println(hd.getSoLuongChiTiet());
                for (int j = 0; j < hd.getSoLuongChiTiet(); j++) {
                    ChiTietHoaDon ct = hd.getChiTiet()[j];
                    pw.println(ct.getMathang().getMa() + ";" + ct.getsoLuong() + ";" + ct.getDonGia());
                }
            }
            System.out.println("Đã ghi dữ liệu hóa đơn ra file " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    // Bắt buộc phải có hàm này để không bị lỗi Interface IFileIO
    @Override
    public void docFile(String duongDan) {
        docFile(duongDan,this.dskh, this.dsmh);
    }

    // Hàm thực sự làm nhiệm vụ đọc file (Nạp chồng phương thức)
    // Vẫn đọc được file cũ chỉ có 3 trường ở dòng đầu (khi đó coi là chưa thanh toán, không có nhân viên)
    public void docFile(String duongDan, DanhSachKhachHang dskh, DanhSachMatHang dsmh) {
        try (BufferedReader br = new BufferedReader(new FileReader(duongDan))) {
            String line;
            soLuong = 0;
            ds = new HoaDon[1000];

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] info = line.split(";");
                String maHD = info[0];
                LocalDate ngayLap = LocalDate.parse(info[1]);
                KhachHang kh = dskh.timkiem_khachhang(info[2]);

                // Luôn đọc hết các dòng chi tiết của hóa đơn này trước, để dù bỏ qua cũng không lệch dòng
                int soLuongCT = Integer.parseInt(br.readLine().trim());
                String[] dongCT = new String[soLuongCT];
                for (int j = 0; j < soLuongCT; j++) {
                    dongCT[j] = br.readLine();
                }

                if (kh == null) {
                    System.out.println("Bỏ qua hóa đơn " + maHD + " do không tìm thấy dữ liệu khách hàng.");
                    continue;
                }
                if (timKiem(maHD) != null) {
                    System.out.println("Bỏ qua hóa đơn " + maHD + " do bị trùng mã trong file.");
                    continue;
                }

                // Nhân viên chưa có danh sách riêng nên chỉ khôi phục mã và tên đã lưu
                NhanVienBanHang nv = null;
                if (info.length > 4 && !info[3].trim().isEmpty()) {
                    nv = new NhanVienBanHang(info[3].trim(), info[4].trim(), "", "", "", 0);
                }
                boolean daThanhToan = info.length > 5 && info[5].trim().equals("1");

                HoaDon hd = new HoaDon(maHD, ngayLap, kh, nv);
                for (int j = 0; j < soLuongCT; j++) {
                    String[] ctData = dongCT[j].split(";");
                    MatHang mh = dsmh.timKiem_hang(ctData[0]);
                    int slMua = Integer.parseInt(ctData[1].trim());

                    if (mh != null) {
                        // Giữ đúng đơn giá lúc bán nếu file có lưu
                        double donGia = (ctData.length > 2) ? Double.parseDouble(ctData[2].trim()) : mh.tinhGiaBan();
                        hd.themChiTiet(new ChiTietHoaDon(slMua, mh, donGia));
                    }
                }

                if (hd.getSoLuongChiTiet() == 0) {
                    System.out.println("Bỏ qua hóa đơn " + maHD + " do toàn bộ mặt hàng không còn tồn tại.");
                    continue;
                }
                // Phải đánh dấu sau khi đã thêm xong chi tiết; không trừ hàng lại vì lúc thanh toán đã trừ rồi
                if (daThanhToan) hd.danhDauDaThanhToan();

                if (soLuong == ds.length) tangKichThuocHoaDon();
                ds[soLuong++] = hd;
            }
            System.out.println("Đã tải dữ liệu từ file " + duongDan);
        } catch (FileNotFoundException e) {
            System.out.println("Chưa tìm thấy file " + duongDan);
        } catch (Exception e) {
            System.out.println("Lỗi khi đọc file: " + e.getMessage());
        }
    }

}



