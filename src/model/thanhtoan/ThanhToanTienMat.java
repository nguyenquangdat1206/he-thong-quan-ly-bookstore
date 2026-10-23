package thanhtoan;
class ThanhToanTienMat implements IThanhToan{
    private double tienKhachDua;
    //hàm khởi tạo
    public ThanhToanTienMat(double tienKhachDua){
        this.tienKhachDua = tienKhachDua;
    }
    //get tienKhachDua
    public double getTienKhachDua(){
        return tienKhachDua;
    }
    //set tienKhachDua
    public void setTienKhachDua(double tienKhachDua){
        this.tienKhachDua = tienKhachDua;
    }
    //Ghi đè phương thức xuLyThanhToan
    @Override
    public boolean xuLyThanhToan(double soTien) {
        if(tienKhachDua < soTien) {
            System.out.println("Số tiền khách đưa không đủ! " + "Cần " + soTien + ",đưa " + getTienKhachDua());
            return false;
        }
        double tienThua = tienKhachDua - soTien;
        System.out.println("Thanh toán tiền mặt thành công. Tiền thối lại " + tienThua);
        return true;
    }
}
