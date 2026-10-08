package model.hoadon;

import java.time.LocalDate;

import model.interfaces.IThanhToan;
import model.khachhang.KhachHang;
import model.mathang.DanhSachMatHang;
import model.utils.TienIch;

public class HoaDon {
    private String maHD;
    private LocalDate ngayLap;
    private KhachHang khachHang;
    // Tạm tắt NhanVienBanHang để test độc lập Hóa Đơn trước
    // private NhanVienBanHang nhanVienLap; 
    private ChiTietHoaDon[] chiTiet;
    private int soLuongChiTiet;
    private IThanhToan hinhThucThanhToan;
    private boolean daThanhToan;
    private static final int MAX_CHI_TIET = 10;

    public HoaDon(String maHD, LocalDate ngayLap, KhachHang khachHang) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.khachHang = khachHang;
        this.chiTiet = new ChiTietHoaDon[MAX_CHI_TIET];
        this.soLuongChiTiet = 0;
        this.daThanhToan = false;
    }

    public boolean themChiTiet(ChiTietHoaDon ct) {
        if (ct == null || daThanhToan) return false;
        if (soLuongChiTiet >= chiTiet.length) {
            ChiTietHoaDon[] temp = new ChiTietHoaDon[chiTiet.length + MAX_CHI_TIET];
            for (int i = 0; i < chiTiet.length; i++) temp[i] = chiTiet[i];
            chiTiet = temp;
        }
        chiTiet[soLuongChiTiet++] = ct;
        return true;
    }

    public double tinhThanhTien() {
        double tong = 0;
        for (int i = 0; i < soLuongChiTiet; i++) {
            tong += chiTiet[i].thanhTien();
        }
        return tong;
    }

    // Thanh toán: Yêu cầu Kệ hàng (DanhSachMatHang) trừ số lượng
    public boolean thanhToan(DanhSachMatHang dsHangHoa) {
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

        double tongTien = tinhThanhTien();
        boolean ketQua = hinhThucThanhToan.xuLyThanhToan(tongTien);
        if (ketQua) {
            // Thanh toán thành công -> Trừ thẳng số lượng trên Kệ hàng
            for (int i = 0; i < soLuongChiTiet; i++) {
                ChiTietHoaDon ct = chiTiet[i];
                dsHangHoa.giamSoLuongTon(ct.getMathang().getMa(), ct.getsoLuong());
            }
            daThanhToan = true;
            System.out.println(">>> ĐÃ TRỪ HÀNG TRÊN KỆ VÀ LƯU HÓA ĐƠN THÀNH CÔNG!");
        }
        return ketQua;
    }

    public void xuat() {
        System.out.println("Mã HĐ: " + maHD + " | Ngày: " + TienIch.chuyenNgaySangChuoi(ngayLap));
        System.out.println("Khách hàng: " + (khachHang != null ? khachHang.getTenKH() : "Khách lẻ"));
        System.out.println("Trạng thái: " + (daThanhToan ? "[ĐÃ THANH TOÁN]" : "[CHƯA THANH TOÁN]"));
        System.out.println("--- Chi tiết ---");
        for (int i = 0; i < soLuongChiTiet; i++) {
            ChiTietHoaDon ct = chiTiet[i];
            System.out.println("  " + (i + 1) + ". " + ct.getMathang().getTen()
                    + " | SL: " + ct.getsoLuong() + " | Đơn giá: " + ct.getDonGia() + " đ"
                    + " | Thành tiền: " + ct.thanhTien() + " đ");
        }
        System.out.println("=> TỔNG CỘNG: " + tinhThanhTien() + " đ");
    }

    public void setHinhThucThanhToan(IThanhToan httt) { this.hinhThucThanhToan = httt; }
    public String getMaHD() { return maHD; }
    public int getSoLuongChiTiet() { return soLuongChiTiet; }
    public ChiTietHoaDon[] getChiTiet() { return chiTiet; }
    public boolean isDaThanhToan() { return daThanhToan; }
}