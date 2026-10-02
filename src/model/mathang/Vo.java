package model.mathang;

import model.utils.TienIch;

public class Vo extends MatHang {
    private int sotrang;
    private String nhaSX;

    public Vo() {
        super();
        setThue(10);
        setLoinhuan(15);
        this.sotrang = 0;
        this.nhaSX = "";
    }

    public Vo(String ma, String ten, double giaNhap, int soLuongTon, int sotrang, String nhaSX) {
        super(ma, ten, giaNhap, soLuongTon, 15, 10);
        this.sotrang = sotrang;
        this.nhaSX = nhaSX;
    }

    public String getNhaSX() { return nhaSX; }
    public void setNhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public int getSotrang() { return sotrang; }
    public void setSotrang(int sotrang) { this.sotrang = sotrang; }

    @Override
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(10);
        setLoinhuan(15);

        sotrang = TienIch.nhapSoNguyenDuong("Nhập số trang: ");
        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.println("Nhà sản xuất: " + nhaSX + " | Số trang: " + sotrang);
    }
}