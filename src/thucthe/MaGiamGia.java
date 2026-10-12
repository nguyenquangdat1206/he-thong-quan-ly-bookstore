package thucthe;

public class MaGiamGia {
    private String maCode;       // VD: SALE10, VIP2026
    private float phanTramGiam;  // VD: 0.1 (giảm 10%), 0.2 (giảm 20%)
    private int soLuongGioiHan;  // Số lượng mã tối đa được dùng

    public MaGiamGia() {}

    public MaGiamGia(String maCode, float phanTramGiam, int soLuongGioiHan) {
        this.maCode = maCode;
        this.phanTramGiam = phanTramGiam;
        this.soLuongGioiHan = soLuongGioiHan;
    }

    public String getMaCode() { return maCode; }
    public void setMaCode(String maCode) { this.maCode = maCode; }

    public float getPhanTramGiam() { return phanTramGiam; }
    public void setPhanTramGiam(float phanTramGiam) { this.phanTramGiam = phanTramGiam; }

    public int getSoLuongGioiHan() { return soLuongGioiHan; }
    public void setSoLuongGioiHan(int soLuongGioiHan) { this.soLuongGioiHan = soLuongGioiHan; }

    public void giamSoLuong() {
        if (soLuongGioiHan > 0) {
            soLuongGioiHan--;
        }
    }

    public void xuatThongTin() {
        System.out.printf("Mã: %-10s | Giảm: %-4.0f%% | Còn lại: %-3d lượt\n",
                maCode, phanTramGiam * 100, soLuongGioiHan);
    }
}