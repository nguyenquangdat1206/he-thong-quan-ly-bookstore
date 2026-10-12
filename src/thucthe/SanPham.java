package thucthe;

public abstract class SanPham {
    private String maSP;
    private String tenSP;
    private double donGia;
    private int soLuongKho;  // Dành cho Nhân viên Kho quản lý
    private int soLuongQuay; // Dành cho Bán hàng và Khách hàng tương tác

    public SanPham() {}

    public SanPham(String maSP, String tenSP, double donGia, int soLuongKho, int soLuongQuay) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.donGia = donGia;
        this.soLuongKho = soLuongKho;
        this.soLuongQuay = soLuongQuay;
    }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public int getSoLuongKho() { return soLuongKho; }
    public void setSoLuongKho(int soLuongKho) { this.soLuongKho = soLuongKho; }

    public int getSoLuongQuay() { return soLuongQuay; }
    public void setSoLuongQuay(int soLuongQuay) { this.soLuongQuay = soLuongQuay; }

    public abstract double tinhGiaBan();

    public void xuatThongTin() {
        // Hàm này tạm để trống, chúng ta đã dùng hàm xuat() chia bảng ở DanhSachSanPham rồi
    }
}