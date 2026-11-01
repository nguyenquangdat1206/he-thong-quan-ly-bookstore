package hoadon;

import mathang.MatHang;

public class ChiTietHoaDon {
    private int soLuong;
    private double donGia;
    private MatHang mathang;

    public ChiTietHoaDon(int soLuong, MatHang mathang){
        this.soLuong = soLuong;
        this.mathang = mathang;
        this.donGia = mathang.tinhGiaBan();
    }

    public ChiTietHoaDon(int soLuong, MatHang mathang, double donGia){
        this.soLuong = soLuong;
        this.mathang = mathang;
        this.donGia = donGia;
    }
    // get soLuong bán ra của từng mathang trong hóa đơn
    public int getsoLuong(){
        return this.soLuong;
    }
    // set soLuong
    public void setsoLuong(int soLuong){
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
        System.out.println("Mặt hàng: " + mathang);
        System.out.println("Đơn giá bán: " + donGia);
        System.out.println("Thành tiền: " + thanhTien());
    }


}



