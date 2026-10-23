package mathang;

import tienich.*;

public class TieuThuyet extends MatHang{
    private final double thue = 20;
    private final double loinhuan = 20;
    private String tacGia;
    private String nhaXuatBan;
    private String theloai;

    public TieuThuyet() {
        super();
        this.tacGia = "";
        this.nhaXuatBan = "";
        this.theloai = "";
    }

    public TieuThuyet(String ma, String ten, double giaNhap, int soLuong, String tacGia, String nhaXuatBan, String theloai) {
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

        setGiaNhap(TienIch.nhapGiaTien("Nhập giá nhập: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap() * (1 + loinhuan / 100) * (1 + thue / 100);
    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Tác giả: " + tacGia);

        System.out.println("Nhà xuất bản: " + nhaXuatBan);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Tiểu thuyết";
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getTacGia()+";"+getNhaXuatBan()+";"+getTheloai();
    }
}
