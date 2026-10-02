package model.mathang;

import model.utils.TienIch;

public class TrangTri extends MatHang {
    private String nhaSX;

    public TrangTri() {
        super();
        setThue(20);
        setLoinhuan(19);
        this.nhaSX = "";
    }

    public TrangTri(String ma, String ten, double giaNhap, int soLuongTon, String nhaSX) {
        super(ma, ten, giaNhap, soLuongTon, 19, 20);
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
        setLoinhuan(19);

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.println("Nhà sản xuất: " + nhaSX);
    }
}