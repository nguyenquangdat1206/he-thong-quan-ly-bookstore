package mathang;

import tienich.*;

public class BaoTapChi extends MatHang {
    private final double thue = 10;
    private final double loinhuan = 18;
    private String nhaXuatBan;
    private String theloai;

    public BaoTapChi() {
        super();
        this.nhaXuatBan = "";
        this.theloai = "";
    }

    public BaoTapChi(String ma, String ten, double giaNhap, int soLuong, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuong);
        this.nhaXuatBan = nhaXuatBan;
        this.theloai = theloai;
    }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    public String getTheloai(){ return theloai; }
    public void setTheloai(String theloai){ this.theloai = theloai; }

    @Override
    public double tinhGiaBan(){
        return getGiaNhap() * (1 + loinhuan/100)*(1+thue/100);
    }

    @Override
    public void nhap() {

        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        theloai = TienIch.nhapChuoi("Nhập thể loại: ");

        nhaXuatBan = TienIch.nhapChuoi("Nhà xuất bản: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Thể loại: " + theloai);

        System.out.println("Nhà xuất bản: " + nhaXuatBan);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Báo tạp chí";
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getTheloai()+";"+getNhaXuatBan();
    }
}
