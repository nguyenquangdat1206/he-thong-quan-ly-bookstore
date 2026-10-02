package model.mathang;

import model.utils.TienIch;

public class TieuThuyet extends MatHang {
    private String tacGia;
    private String nhaXuatBan;
    private String theloai;

    public TieuThuyet() {
        super();
        setThue(20);
        setLoinhuan(20);
        this.tacGia = "";
        this.nhaXuatBan = "";
        this.theloai = "";
    }

    public TieuThuyet(String ma, String ten, double giaNhap, int soLuongTon, String tacGia, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuongTon, 20, 20);
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
        setGiaNhap(TienIch.nhapGiaTien("Nhập giá nhập: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(20);
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