package model.mathang;

import model.utils.TienIch;

public class Sach extends MatHang {
    private String tacGia;
    private String nhaXuatBan;
    private String theloai;

    public Sach() {
        super();
        setThue(25);
        setLoinhuan(20);
        this.tacGia = "";
        this.nhaXuatBan = "";
        this.theloai = "";
    }

    public Sach(String ma, String ten, double giaNhap, int soLuongTon, String tacGia, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuongTon, 20, 25); // loinhuan 20, thue 25
        this.tacGia = tacGia;
        this.nhaXuatBan = nhaXuatBan;
        this.theloai = theloai;
    }

    public String getTheloai() { return theloai; }
    public void setTheloai(String theloai) { this.theloai = theloai; }

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    @Override
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(25);
        setLoinhuan(20);

        theloai = TienIch.nhapChuoi("Nhập thể loại: ");
        tacGia = TienIch.nhapChuoi("Nhập tác giả: ");
        nhaXuatBan = TienIch.nhapChuoi("Nhà xuất bản: ");
    }

    @Override
    public void xuat() {
        super.xuat(); 
        System.out.println("Thể loại: " + theloai + " | Tác giả: " + tacGia + " | NXB: " + nhaXuatBan);
    }
}