package mathang;

import tienich.*;
import interfaces.IGetMa;

public abstract class MatHang implements IGetMa, Cloneable {
    private String ma;
    private String ten;
    private double giaNhap;
    private int soLuong;
    private double loinhuan;
    private double thue;

    public double getLoiNhuan(){
        return loinhuan;
    }
    public double getThue(){
        return thue;
    }

    // constructor

    public MatHang() {
        this.ma ="";
        this.ten = "";
        this.giaNhap =0;
        this.soLuong =0;
    }

    public MatHang(String ma, String ten, double giaNhap, int soLuong) {
        this.ma = ma;
        this.ten = ten;
        this.giaNhap = giaNhap;
        this.soLuong = soLuong;
    }

    // Get_xx & Set_xx
    @Override 
    public String getMa() {
        return ma;
    }
    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }
    public void setTen(String ten) {
        this.ten = ten;
    }

    public double getGiaNhap() {return giaNhap;}
    public void setGiaNhap(double giaNhap){ this.giaNhap = giaNhap;}

    public int getSoLuong() {
        return soLuong;
    }
    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    /** Các thuộc tính mặt hàng hiện chỉ gồm kiểu nguyên thủy và String bất biến. */
    public MatHang saoChep() {
        try {
            return (MatHang) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    //Tinh gia ban
    public abstract double tinhGiaBan();

    protected double tinhGiaBan(double loiNhuan, double thueSuat) {
        if (!Double.isFinite(giaNhap) || giaNhap < 0) return Double.NaN;
        double value = giaNhap * (1 + loiNhuan / 100.0) * (1 + thueSuat / 100.0);
        // Math.round trả long và bão hòa với giá lớn; ở độ lớn này double không còn phần xu.
        return Math.abs(value) >= Long.MAX_VALUE / 100.0 ? value : Math.round(value * 100.0) / 100.0;
    }

    // Nhap
    public void nhap() {

        ma = TienIch.nhapChuoi("Nhập mã: ");

        ten = TienIch.nhapChuoi("Nhập tên: ");

        giaNhap = TienIch.nhapGiaTien("Giá nhập hàng: ");

        soLuong = TienIch.nhapSoNguyenKhongAm("Nhập số lượng: ");

    }

    // Xuat
    public void xuat() {
        System.out.println("Mã: " + ma);
        System.out.println("Tên: " + ten);
        System.out.println("Số lượng: " + soLuong);
        System.out.println("Giá bán: " + tinhGiaBan());
    }
    
    
    //coi mặt hàng thuộc danh mục gì
    public String getDanhMuc(){
        return " ";
    }

    //Hàm thuộc tính riêng
    public void suathuoctinhrieng(){
        System.out.println("Loại hàng này không có thuộc tính riêng");
    }

    
    //ghi mặt hàng ra thành String để ghi File
    public String toString(){
        return getDanhMuc()+";"+getMa()+";"+getTen()+";"+getGiaNhap()+";"+getSoLuong();
    }

}
