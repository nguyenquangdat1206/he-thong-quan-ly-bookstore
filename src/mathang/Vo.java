package mathang;

import tienich.*;

public class Vo extends MatHang{
    private final double thue = 10;
    private final double loinhuan = 15;

    @Override public double getThue() { return thue; }
    @Override public double getLoiNhuan() { return loinhuan; }
    private int sotrang;
    private String nhaSX;


    public Vo(){
        this.sotrang = 0;
        this.nhaSX = "";
    }

    public Vo(String ma, String ten, double giaNhap, int soLuong, int sotrang, String nhaSX) {
        super(ma, ten, giaNhap, soLuong);
        this.sotrang = sotrang;
        this.nhaSX = nhaSX;
    }

    @Override
    public void setMa(String ma) {
        if (ma == null || !ma.matches("VO\\d{4}")) {
            throw new IllegalArgumentException(
                "Mã phải có dạng VOxxxx"
            );
        }
        super.setMa(ma);
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public int getSotrang(){return sotrang;}
    public void setSotrang(int sotrang) {this.sotrang = sotrang;}

    @Override
    public double tinhGiaBan() {
        double giaBan = getGiaNhap() * (1 + loinhuan / 100.0) * (1 + thue / 100.0);
        return Math.round(giaBan * 100.0) / 100.0;
    }

    @Override
    // Nhap
    public void nhap() {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (VOxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        sotrang = TienIch.nhapSoNguyenDuong("Nhập số trang: ");

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    public void nhap(String tenNCC) {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (VOxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        sotrang = TienIch.nhapSoNguyenDuong("Nhập số trang: ");

        nhaSX = tenNCC;

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    @Override
    // Xuat
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Nhà sản xuất: " + nhaSX);

        System.out.println("Số trang: " + sotrang);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Vở";
    }

    @Override
    public void suathuoctinhrieng() {
        System.out.println("\n Lựa chọn thuộc tính riêng");
        System.out.println("1. Số trang");
        System.out.println("2. Nhà sản xuất");
        System.out.println("0. Quay lại");
        switch (TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ")) {
            case 0: break;
            case 1: sotrang = TienIch.nhapSoNguyenDuong("Nhập lại số trang: "); break;
            case 2: nhaSX = TienIch.nhapChuoi("Nhập lại nhà sản xuất: "); break;
            default: System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getSotrang()+";"+getnhaSX();
    }
}
