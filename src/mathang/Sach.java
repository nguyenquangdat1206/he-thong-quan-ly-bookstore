package mathang;

import tienich.*;

public class Sach extends MatHang {
    private final double thue = 25;
    private final double loinhuan = 20;
    private String tacGia;
    private String nhaXuatBan;
    private String theloai;

    public Sach() {
        super();
        this.tacGia="";
        this.nhaXuatBan ="";
        this.theloai ="";
    }

    public Sach(String ma, String ten, double giaNhap, int soLuong, String tacGia, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuong);
        this.tacGia = tacGia;
        this.nhaXuatBan = nhaXuatBan;
        this.theloai = theloai;
    }

    public String getTheloai(){return theloai; }
    public void setTheloai(String theloai){this.theloai = theloai;}

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    public double getThue() { return thue; }
    public double getLoinhuan() { return loinhuan; }

    @Override
    public void nhap() {

        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        theloai = TienIch.nhapChuoi("Nhập thể loại: ");

        tacGia = TienIch.nhapChuoi("Nhập tác giả: ");

        nhaXuatBan = TienIch.nhapChuoi("Nhà xuất bản: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

    }

    @Override
    public void xuat(){
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Thể loại: " +getTheloai());

        System.out.println("Nhà xuất bản: " + nhaXuatBan);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }
    @Override
    public double tinhGiaBan(){
        return getGiaNhap() * (1 + loinhuan/100)*(1+thue/100);
    }

    @Override 
    public String getDanhMuc(){
        return "Sách";
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getTacGia()+";"+getNhaXuatBan()+";"+getTheloai();
    }
}
