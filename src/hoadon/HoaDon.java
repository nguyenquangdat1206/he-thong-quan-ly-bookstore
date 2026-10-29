package hoadon;


import java.time.LocalDate;
import khachhang.KhachHang;
import mathang.MatHang;
import nhanvien.NhanVienBanHang;
import thanhtoan.IThanhToan;
import tienich.TienIch;

public class HoaDon {
    //Thuộc tính
    private String maHD;
    private LocalDate ngayLap;
    private KhachHang khachHang;
    private NhanVienBanHang nhanVienLap;
    private ChiTietHoaDon[] chiTiet;
    private int soLuongChiTiet;
    private IThanhToan hinhThucThanhToan;
    private boolean daThanhToan;
    private static final int MAX_CHI_TIET = 10;

    //Constructor
    public HoaDon(String maHD, LocalDate ngayLap, KhachHang khachHang, NhanVienBanHang nhanVienLap) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.khachHang = khachHang;
        this.nhanVienLap = nhanVienLap;
        this.chiTiet = new ChiTietHoaDon[MAX_CHI_TIET];
        this.soLuongChiTiet = 0;
    }
    // Tăng số dòng chi tiết trong hóa đơn
    public void tangKichThuocChiTiet() {
        ChiTietHoaDon[] temp = new ChiTietHoaDon[chiTiet.length + MAX_CHI_TIET];
        for (int i = 0; i < chiTiet.length; i++) {
            temp[i] = chiTiet[i];
        }
        chiTiet = temp;
    }
    //Thêm 1 dòng chi tiết vào hóa đơn
    public boolean themChiTiet(ChiTietHoaDon ct) {
        if (ct == null || daThanhToan) return false;
        if (soLuongChiTiet >= chiTiet.length) {
            tangKichThuocChiTiet();
        }
        chiTiet[soLuongChiTiet++] = ct;
        return true;
    }
    // Tính thành tiền (tổng tiền)
    public double tinhThanhTien() {
        double tong = 0;
        for (int i = 0; i < soLuongChiTiet; i++) {
            tong += chiTiet[i].thanhTien();
        }
        return tong;
    }

    private void kiemTraChuaThanhToan() {
        if (daThanhToan) {
            throw new IllegalStateException("Hóa đơn đã thanh toán, không thể sửa");
        }
    }

    private void kiemTraViTri(int viTri) {
        if (viTri < 0 || viTri >= soLuongChiTiet) {
            throw new IllegalArgumentException("Vị trí dòng không hợp lệ");
        }
    }

    public void suaSoLuongChiTiet(int viTri, int soLuongMoi) {
        kiemTraChuaThanhToan();
        kiemTraViTri(viTri);
        if (soLuongMoi <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        chiTiet[viTri].setsoLuong(soLuongMoi);
    }

    public void xoaChiTiet(int viTri) {
        kiemTraChuaThanhToan();
        kiemTraViTri(viTri);
        if (soLuongChiTiet == 1) {
            throw new IllegalStateException("Hóa đơn phải còn ít nhất 1 dòng, hãy xóa cả hóa đơn thay vì xóa dòng cuối");
        }
        for (int i = viTri; i < soLuongChiTiet - 1; i++) {
            chiTiet[i] = chiTiet[i + 1];
        }
        chiTiet[soLuongChiTiet - 1] = null;
        soLuongChiTiet--;
    }
    // In gọn danh sách dòng để chọn khi sửa
    public void inChiTiet() {
        for (int i = 0; i < soLuongChiTiet; i++) {
            ChiTietHoaDon ct = chiTiet[i];
            System.out.println((i + 1) + ". " + ct.getMathang().getTen()
                    + " | SL: " + ct.getsoLuong());
        }
    }
    // Thanh toán + tự động giảm số lượng của mặt hàng
    public boolean thanhToan() {
        if (daThanhToan) {
            System.out.println("Hóa đơn này đã được thanh toán!");
            return false;
        }

        if (hinhThucThanhToan == null) {
            System.out.println("Chưa chọn hình thức thanh toán!");
            return false;
        }

        if (soLuongChiTiet == 0) {
            System.out.println("Hóa đơn chưa có mặt hàng!");
            return false;
        }

        for (int i = 0; i < soLuongChiTiet; i++) {
            ChiTietHoaDon ct = chiTiet[i];
            MatHang mh = ct.getMathang();
            if(mh.getSoLuong() < ct.getsoLuong()){
                System.out.println("Lỗi: Mặt hàng '" + mh.getTen() + "' chỉ còn "
                                    + mh.getSoLuong() + " cái trên kệ hàng, không đủ để bán ");
                return false;// Dừng thanh toán vì không đủ hàng để bán
            }
        }
        // Tiền hành thu tiền
        double tongTien = tinhThanhTien();
        boolean ketQua = hinhThucThanhToan.xuLyThanhToan(tongTien);
        if(ketQua){
            for(int i=0; i<soLuongChiTiet; i++){
                ChiTietHoaDon ct = chiTiet[i];
                MatHang mh = ct.getMathang();
                //trừ trực tiếp số lượng mặt hàng đang có
                mh.setSoLuong(mh.getSoLuong()-ct.getsoLuong());
            }
            daThanhToan = true;
            System.out.println("Thanh toán thành công. Đã cập nhật lại số lượng trên kệ hàng ");
        }
        return ketQua;
    }
    //xuất thông tin hóa đơn
    public void xuat() {
        System.out.println("Mã hóa đơn: " + maHD);
        System.out.println("Ngày lập: " + TienIch.ngaytoString(ngayLap));
        System.out.println("Khách hàng: " + khachHang.getTenKH());
        System.out.println("Nhân viên lập: " + (nhanVienLap == null ? "(không rõ)" : nhanVienLap.getHoTen()));
        System.out.println("Trạng thái: " + (daThanhToan ? "Đã thanh toán" : "Chưa thanh toán"));
        System.out.println("--- Chi tiết ---");
        for (int i = 0; i < soLuongChiTiet; i++) {
            ChiTietHoaDon ct = chiTiet[i];
            System.out.printf("%d. %s | SL: %d | Đơn giá: %.0f | Thành tiền: %.0f%n", i + 1,
                    ct.getMathang().getTen(), ct.getsoLuong(), ct.getDonGia(), ct.thanhTien());
        }
        System.out.printf("Tổng cộng %.0f%n", tinhThanhTien());
    }

    // Chỉ dùng khi nạp hóa đơn đã thanh toán từ file (không trừ hàng lần nữa)
    // Phải gọi SAU khi đã thêm đủ chi tiết, vì thêm chi tiết bị chặn khi đã thanh toán
    void danhDauDaThanhToan() {
        this.daThanhToan = true;
    }

    //get && set
    public IThanhToan getHinhThucThanhToan() {
        return hinhThucThanhToan;
    }

    public void setHinhThucThanhToan(IThanhToan httt) {
        this.hinhThucThanhToan = httt;
    }

    public NhanVienBanHang getNhanVienLap(){
        return nhanVienLap;
    }

    public String getMaHD() {
        return maHD;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public int getSoLuongChiTiet() {
        return soLuongChiTiet;
    }

    public ChiTietHoaDon[] getChiTiet() {
        return chiTiet;
    }

    public boolean isDaThanhToan() {
        return daThanhToan;
    }
}
