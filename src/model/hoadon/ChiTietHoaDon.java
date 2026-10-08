package model.hoadon;

import model.mathang.MatHang;

public class ChiTietHoaDon {
    private int soLuong;
    private double donGia;
    private MatHang mathang;

    public ChiTietHoaDon(int soLuong, MatHang mathang) {
        this.soLuong = soLuong;
        this.mathang = mathang;
        this.donGia = mathang.tinhGiaBan(); // Gọi đúng hàm tinhGiaBan() từ hệ thống của TV2
    }

    public int getsoLuong() { return this.soLuong; }
    public void setsoLuong(int soLuong) { this.soLuong = soLuong; }
    public double getDonGia() { return this.donGia; }
    public MatHang getMathang() { return this.mathang; }

    public double thanhTien() {
        return soLuong * donGia;
    }
}