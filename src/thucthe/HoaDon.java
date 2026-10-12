package thucthe;

public class HoaDon {
    // Thuộc tính STATIC dùng để tự động tăng mã hóa đơn (HD01, HD02...)
    public static int tongSoHoaDon = 0;

    private String maHoaDon;
    private KhachHang khachHang;
    private String ngayLap;
    private ChiTietHoaDon[] danhSachChiTiet;
    private int soLuongMon;
    private double tongTienThanhToan; // Lưu giá cuối cùng sau khi đã trừ Voucher, giảm giá

    // Khởi tạo Hóa đơn dựa trên dữ liệu từ Giỏ hàng
    public HoaDon(KhachHang khachHang, String ngayLap, ChiTietHoaDon[] chiTietTuGio, int soLuong, double tongTien) {
        tongSoHoaDon++;
        this.maHoaDon = "HD" + String.format("%03d", tongSoHoaDon); // Tạo mã HD001, HD002...
        this.khachHang = khachHang;
        this.ngayLap = ngayLap;
        this.soLuongMon = soLuong;
        this.tongTienThanhToan = tongTien;

        // Cấp phát và copy mảng ChiTietHoaDon từ Giỏ hàng sang Hóa đơn
        this.danhSachChiTiet = new ChiTietHoaDon[soLuong];
        for (int i = 0; i < soLuong; i++) {
            this.danhSachChiTiet[i] = chiTietTuGio[i];
        }
    }

    public String getMaHoaDon() { return maHoaDon; }

    public void xuatHoaDon() {
        System.out.println("=================================================");
        System.out.println("                  HÓA ĐƠN MUA HÀNG               ");
        System.out.println("Mã HD: " + maHoaDon + " | Ngày lập: " + ngayLap);
        System.out.println("Khách hàng: " + khachHang.getHoTen() + " | Hạng: " + khachHang.getHangThanhVien());
        System.out.println("-------------------------------------------------");
        for (int i = 0; i < soLuongMon; i++) {
            danhSachChiTiet[i].xuatChiTiet();
        }
        System.out.println("-------------------------------------------------");
        System.out.printf("TỔNG TIỀN THANH TOÁN (Đã áp dụng ưu đãi): %.2f\n", tongTienThanhToan);
        System.out.println("=================================================");
    }

    // [ĐÃ THÊM]: Các hàm Get để lấy dữ liệu in Bảng tóm tắt
    public String getNgayLap() { return ngayLap; }
    public KhachHang getKhachHang() { return khachHang; }
    public double getTongTienThanhToan() { return tongTienThanhToan; }
}