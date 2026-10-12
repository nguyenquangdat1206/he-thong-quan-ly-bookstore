package thucthe;

public class ChiTietHoaDon {
    private SanPham sanPham;
    private int soLuongMua;

    public ChiTietHoaDon() {}

    public ChiTietHoaDon(SanPham sanPham, int soLuongMua) {
        this.sanPham = sanPham;
        this.soLuongMua = soLuongMua;
    }

    public SanPham getSanPham() { return sanPham; }
    public void setSanPham(SanPham sanPham) { this.sanPham = sanPham; }

    public int getSoLuongMua() { return soLuongMua; }
    public void setSoLuongMua(int soLuongMua) { this.soLuongMua = soLuongMua; }

    // Tính thành tiền của món này = Giá bán * Số lượng
    public double tinhThanhTien() {
        return sanPham.tinhGiaBan() * soLuongMua;
    }

    public void xuatChiTiet() {
        // [ĐÃ SỬA]: Thêm mã sản phẩm vào đầu
        System.out.printf("- [%s] %-20s | SL: %-3d | Đơn giá: %-10.2f | Thành tiền: %-10.2f\n",
                sanPham.getMaSP(), sanPham.getTenSP(), soLuongMua, sanPham.tinhGiaBan(), tinhThanhTien());
    }
}