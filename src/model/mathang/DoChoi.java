package model.mathang;

import model.utils.TienIch;

public class DoChoi extends MatHang {
    private String nhaSX;
    private int dotuoi;

    public DoChoi() {
        super();
        setThue(20);
        setLoinhuan(30);
        this.nhaSX = "";
        this.dotuoi = 0;
    }

    public DoChoi(String ma, String ten, double giaNhap, int soLuongTon, String nhaSX, int dotuoi) {
        super(ma, ten, giaNhap, soLuongTon, 30, 20); // loinhuan: 30, thue: 20
        this.nhaSX = nhaSX;
        this.dotuoi = dotuoi;
    }

    public String getNhaSX() { return nhaSX; }
    public void setNhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public int getDotuoi() { return dotuoi; }
    public void setDotuoi(int dotuoi) { this.dotuoi = dotuoi; }

    @Override
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(20);
        setLoinhuan(30);

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");
        dotuoi = TienIch.nhapSoNguyenDuong("Nhập độ tuổi: ");
    }

    @Override
    public void xuat() {
        super.xuat(); 
        System.out.println("Nhà sản xuất: " + nhaSX + " | Độ tuổi: " + dotuoi);
    }
}