package thucthe;

public class BaoTapChi extends SanPham {
    private String ngayPhatHanh;

    public BaoTapChi() { super(); }

    public BaoTapChi(String maSP, String tenSP, double donGia, int soLuongKho, int soLuongQuay, String ngayPhatHanh) {
        super(maSP, tenSP, donGia, soLuongKho, soLuongQuay);
        this.ngayPhatHanh = ngayPhatHanh;
    }

    public String getNgayPhatHanh() { return ngayPhatHanh; }
    public void setNgayPhatHanh(String ngayPhatHanh) { this.ngayPhatHanh = ngayPhatHanh; }

    @Override
    public double tinhGiaBan() { return getDonGia() * 1.05; }
}