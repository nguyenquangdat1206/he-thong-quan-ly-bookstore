package thucthe;

public class Sach extends SanPham {
    private String tacGia;
    private String nhaXuatBan;
    private int namXuatBan;

    public Sach() { super(); }

    public Sach(String maSP, String tenSP, double donGia, int soLuongKho, int soLuongQuay, String tacGia, String nhaXuatBan, int namXuatBan) {
        super(maSP, tenSP, donGia, soLuongKho, soLuongQuay);
        this.tacGia = tacGia;
        this.nhaXuatBan = nhaXuatBan;
        this.namXuatBan = namXuatBan;
    }

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }
    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }
    public int getNamXuatBan() { return namXuatBan; }
    public void setNamXuatBan(int namXuatBan) { this.namXuatBan = namXuatBan; }

    @Override
    public double tinhGiaBan() { return getDonGia(); }
}