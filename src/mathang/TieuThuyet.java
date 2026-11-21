package mathang;

import tienich.*;

public class TieuThuyet extends Sach {
    private final double thue = 20;
    private final double loinhuan = 20;

    public TieuThuyet() {
        super();
    }

    public TieuThuyet(String ma, String ten, double giaNhap, int soLuong, String tacGia, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuong, tacGia, nhaXuatBan, theloai);
    }

    @Override
    protected String getTienToMa() { return "TTH"; }

    @Override
    public double getThue() { return thue; }
    @Override
    public double getLoinhuan() { return loinhuan; }

    @Override
    public void nhap() {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (TTHxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        setTheloai(TienIch.nhapChuoi("Nhập thể loại: "));

        setTacGia(TienIch.nhapChuoi("Nhập tác giả: "));

        setNhaXuatBan(TienIch.nhapChuoi("Nhà xuất bản: "));

        setGiaNhap(TienIch.nhapGiaTien("Nhập giá nhập: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    @Override
    public void nhap(String tenNCC) {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (TTHxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        setTheloai(TienIch.nhapChuoi("Nhập thể loại: "));

        setTacGia(TienIch.nhapChuoi("Nhập tác giả: "));

        setNhaXuatBan(tenNCC);

        setGiaNhap(TienIch.nhapGiaTien("Nhập giá nhập: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Thể loại: " + getTheloai());

        System.out.println("Tác giả: " + getTacGia());

        System.out.println("Nhà xuất bản: " + getNhaXuatBan());

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override
    public double tinhGiaBan() {
        double giaBan = getGiaNhap() * (1 + loinhuan / 100.0) * (1 + thue / 100.0);
        return Math.round(giaBan * 100.0) / 100.0;
    }

    @Override 
    public String getDanhMuc(){
        return "Tiểu thuyết";
    }

    // toString kế thừa từ Sach: danhMuc;ma;ten;giaNhap;soLuong;tacGia;nhaXuatBan;theloai
}
