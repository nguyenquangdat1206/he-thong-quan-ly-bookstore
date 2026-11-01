package thanhtoan;
public class ThanhToanThe implements IThanhToan {
    private String maGiaoDich;
    private String nganHang;

    //hàm khởi tạo rỗng
    public ThanhToanThe() {

    }

    //hàm khởi tạo có tham số
    public ThanhToanThe(String maGiaoDich, String nganHang) {
        this.maGiaoDich = maGiaoDich;
        this.nganHang = nganHang;
    }

    public String getmaGiaoDich() {
        return maGiaoDich;
    }

    public String getNganHang() {
        return nganHang;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public void setNganHang(String nganHang) {
        this.nganHang = nganHang;
    }

    //Ghi đè phương thức xuLyThanhToan
    @Override
    public boolean xuLyThanhToan(double soTien) {
        if (maGiaoDich == null || maGiaoDich.trim().isEmpty()
                || nganHang == null || nganHang.trim().isEmpty()) {
            System.out.println("Thiếu mã giao dịch hoặc ngân hàng, thanh toán thẻ thất bại!");
            return false;
        }
        System.out.printf("Thanh toán %.0f qua thẻ ngân hàng %s | mã giao dịch %s%n",
                soTien, nganHang, maGiaoDich);
        return true;
    }
}