package thucthe;

public abstract class Nguoi {
    private String maNguoi;
    private String hoTen;
    private String soDienThoai;
    private String matKhau; // Bổ sung mật khẩu để làm chức năng Đăng nhập/Online như bạn đề xuất

    public Nguoi() {
    }

    public Nguoi(String maNguoi, String hoTen, String soDienThoai, String matKhau) {
        this.maNguoi = maNguoi;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.matKhau = matKhau;
    }

    // Các hàm Get/Set
    public String getMaNguoi() { return maNguoi; }
    public void setMaNguoi(String maNguoi) { this.maNguoi = maNguoi; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    // Hàm xuất thông tin chung (dùng kỹ thuật in định dạng canh lề)
    public void xuatThongTin() {
        System.out.printf("Mã: %-8s | Họ tên: %-20s | SĐT: %-12s | ", maNguoi, hoTen, soDienThoai);
    }
}