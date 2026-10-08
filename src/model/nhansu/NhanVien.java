package model.nhansu;

public abstract class NhanVien extends Nguoi {
    private String caLamViec;
    private double luongCoBan;

    public NhanVien() {
        super();
    }

    public NhanVien(String maNguoi, String hoTen, String soDienThoai, String matKhau, String caLamViec, double luongCoBan) {
        super(maNguoi, hoTen, soDienThoai, matKhau);
        this.caLamViec = caLamViec;
        this.luongCoBan = luongCoBan;
    }

    public String getCaLamViec() { return caLamViec; }
    public void setCaLamViec(String caLamViec) { this.caLamViec = caLamViec; }

    public double getLuongCoBan() { return luongCoBan; }
    public void setLuongCoBan(double luongCoBan) { this.luongCoBan = luongCoBan; }

    // Phương thức trừu tượng CỰC KỲ QUAN TRỌNG ĐỂ PHÂN QUYỀN
    public abstract String getQuyen();

    public void xuatThongTin() {
        super.xuatThongTin(); // In Mã, Tên, SĐT từ lớp Nguoi
        System.out.printf("Ca làm: %-8s | Lương CB: %-10.2f | ", caLamViec, luongCoBan);
    }
}