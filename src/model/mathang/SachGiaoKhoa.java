package model.mathang;

import model.utils.TienIch;

public class SachGiaoKhoa extends MatHang {
    private String monhoc;
    private String nhaXuatBan;

    public SachGiaoKhoa() {
        super();
        setThue(0);
        setLoinhuan(0);
        this.monhoc = "";
        this.nhaXuatBan = "";
    }

    public SachGiaoKhoa(String ma, String ten, double giaNhap, int soLuongTon, String monhoc, String nhaXuatBan) {
        super(ma, ten, giaNhap, soLuongTon, 0, 0);
        this.monhoc = monhoc;
        this.nhaXuatBan = nhaXuatBan;
    }

    public String getMonhoc() { return monhoc; }
    public void setMonhoc(String monhoc) { this.monhoc = monhoc; }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    @Override
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(0);
        setLoinhuan(0);

        monhoc = TienIch.nhapChuoi("Nhập môn học: ");
        nhaXuatBan = TienIch.nhapChuoi("Nhà xuất bản: ");
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.println("Môn học: " + monhoc + " | Nhà xuất bản: " + nhaXuatBan);
    }
}