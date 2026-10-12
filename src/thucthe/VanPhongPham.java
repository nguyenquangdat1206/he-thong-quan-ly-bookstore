package thucthe;

public class VanPhongPham extends SanPham {
    private String thuongHieu;
    private String mauSac;

    public VanPhongPham() { super(); }

    public VanPhongPham(String maSP, String tenSP, double donGia, int soLuongKho, int soLuongQuay, String thuongHieu, String mauSac) {
        super(maSP, tenSP, donGia, soLuongKho, soLuongQuay);
        this.thuongHieu = thuongHieu;
        this.mauSac = mauSac;
    }

    public String getThuongHieu() { return thuongHieu; }
    public void setThuongHieu(String thuongHieu) { this.thuongHieu = thuongHieu; }
    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }

    @Override
    public double tinhGiaBan() { return getDonGia() * 1.1; }
}