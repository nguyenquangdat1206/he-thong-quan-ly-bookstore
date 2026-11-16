package mathang;

import tienich.*;

public class Truyen extends Sach {
    private final double thue = 25;
    private final double loinhuan = 20;

    public Truyen() {
        super();
    }

    public Truyen(String ma, String ten, double giaNhap, int soLuong, String tacGia, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuong, tacGia, nhaXuatBan, theloai);
    }

    @Override
    protected String getTienToMa() { return "TRU"; }

    @Override
    public double getThue() { return thue; }
    @Override
    public double getLoinhuan() { return loinhuan; }
    @Override public double getLoiNhuan() { return loinhuan; }

    @Override
    public void nhap() {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (TRUxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        setTheloai(TienIch.nhapChuoi("Nhập thể loại: "));

        setTacGia(TienIch.nhapChuoi("Nhập tác giả: "));

        setNhaXuatBan(TienIch.nhapChuoi("Nhà xuất bản: "));

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Thể loại: "+ getTheloai());

        System.out.println("Tác giả: " + getTacGia());

        System.out.println("Nhà xuất bản: " + getNhaXuatBan());

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap() * (1 + loinhuan / 100) * (1 + thue / 100);
    }

    @Override 
    public String getDanhMuc(){
        return "Truyện";
    }

    // toString kế thừa từ Sach: danhMuc;ma;ten;giaNhap;soLuong;tacGia;nhaXuatBan;theloai
}
