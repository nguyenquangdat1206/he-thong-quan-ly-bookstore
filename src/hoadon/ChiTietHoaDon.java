package hoadon;

import mathang.MatHang;

public class ChiTietHoaDon {
    private int soLuong;
    private double donGia;
    private MatHang mathang;
    private final String maMatHang;
    private final String tenMatHang;
    private boolean daChot;

    public ChiTietHoaDon(int soLuong, MatHang mathang){
        this(soLuong, mathang, mathang == null ? Double.NaN : mathang.tinhGiaBan());
    }

    public ChiTietHoaDon(int soLuong, MatHang mathang, double donGia){
        this(soLuong, mathang, donGia, mathang == null ? null : mathang.getMa(),
                mathang == null ? null : mathang.getTen());
    }

    public ChiTietHoaDon(int soLuong, MatHang mathang, double donGia, String ma, String ten){
        this.soLuong = soLuong;
        this.mathang = mathang;
        this.donGia = donGia;
        this.maMatHang = ma;
        this.tenMatHang = ten;
    }
    public String getMaMatHang() { return maMatHang; }
    public String getTenMatHang() { return tenMatHang; }

    void chotLichSu() {
        mathang = new MatHangLichSu(maMatHang, tenMatHang, donGia);
        daChot = true;
    }
    // get soLuong bán ra của từng mathang trong hóa đơn
    public int getsoLuong(){
        return this.soLuong;
    }
    // set soLuong
    public void setsoLuong(int soLuong){
        if (daChot) throw new IllegalStateException("Hóa đơn đã thanh toán, không thể sửa");
        this.soLuong = soLuong;
    }
    //get donGiaban
    public double getDonGia(){
        return this.donGia;
    }
    // get mathang
    public MatHang getMathang() {
        return this.mathang;
    }
    // tinhtien 1 mathang
    public double thanhTien(){
        return soLuong* donGia;
    }
    // In ra hóa đơn
    public void xuat(){

        System.out.println("Số lượng: " + soLuong);
        System.out.println("Mặt hàng: " + tenMatHang + " (" + maMatHang + ")");
        System.out.println("Đơn giá bán: " + donGia);
        System.out.println("Thành tiền: " + thanhTien());
    }


}



