package model.mathang;

import model.utils.TienIch;

public class Khac extends MatHang {
    private String nhaSX;

    public Khac() {
        super();
        setThue(20);
        setLoinhuan(17);
        this.nhaSX = "";
    }

    public Khac(String ma, String ten, double giaNhap, int soLuongTon, String nhaSX) {
        super(ma, ten, giaNhap, soLuongTon, 17, 20); // loinhuan: 17, thue: 20
        this.nhaSX = nhaSX;
    }

    public String getNhaSX() { return nhaSX; }
    public void setNhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    @Override
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(20);
        setLoinhuan(17);

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.println("Nhà sản xuất: " + nhaSX);
    }
}