package thanhtoan;
public class ThanhToanThe implements IThanhToan{
    private String maGiaoDich;
    private String nganHang;
    //hàm khởi tạo rỗng
    public ThanhToanThe() {

    }
    //hàm khởi tạo có tham số
    public ThanhToanThe(String maGiaoDich, String nganHang){
        this.maGiaoDich = maGiaoDich;
        this.nganHang = nganHang;
    }
    // get maGiaoDich
    public String getmaGiaoDich(){
        return maGiaoDich;
    }
    public String getNganHang(){
        return nganHang;
    }
    //Ghi đè phương thức xuLyThanhToan
    @Override
    public boolean xuLyThanhToan(double soTien){
        System.out.println("Thanh toán" + soTien + "qua thẻ ngân hàng " + getNganHang()
                            + "| mã giao dịch " + getmaGiaoDich());
        return true;
    }

}
