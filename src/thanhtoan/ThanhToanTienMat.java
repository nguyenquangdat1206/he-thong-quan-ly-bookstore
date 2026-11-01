package thanhtoan;
public class ThanhToanTienMat implements IThanhToan {
    private double tienKhachDua;

    //hàm khởi tạo
    public ThanhToanTienMat(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    //get tienKhachDua
    public double getTienKhachDua() {
        return tienKhachDua;
    }

    //set tienKhachDua
    public void setTienKhachDua(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    //Ghi đè phương thức xuLyThanhToan
    @Override
    public boolean xuLyThanhToan(double soTien) {
        if (tienKhachDua < soTien) {
            System.out.printf("Số tiền khách đưa không đủ! Cần %.0f, đưa %.0f%n", soTien, tienKhachDua);
            return false;
        }
        System.out.printf("Thanh toán tiền mặt thành công. Tiền thối lại %.0f%n", tienKhachDua - soTien);
        return true;
    }
}
