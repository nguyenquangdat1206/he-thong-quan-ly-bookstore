package mathang;

import tienich.*;

public class DoChoi extends MatHang{
    private final double thue = 20;
    private final double loinhuan = 30;

    @Override public double getThue() { return thue; }
    @Override public double getLoiNhuan() { return loinhuan; }

    private String nhaSX;
    private int dotuoi;


    public DoChoi(){
        super();
        this.nhaSX ="";
        this.dotuoi = 0;
    }

    public DoChoi(String ma, String ten, double giaNhap, int soLuong, String nhaSX, int dotuoi) {
        super(ma, ten, giaNhap, soLuong);
        this.nhaSX = nhaSX;
        this.dotuoi = dotuoi;
    }

    @Override
    public void setMa(String ma) {
        if (ma == null || !ma.matches("DCH\\d{4}")) {
            throw new IllegalArgumentException(
                "Mã phải có dạng DCHxxxx"
            );
        }
        super.setMa(ma);
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public int getdotuoi() {return dotuoi;}

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
                setMa(TienIch.nhapChuoi("Nhập mã (DCHxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        dotuoi = TienIch.nhapSoNguyenDuong("Nhập độ tuổi: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));
    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    public void nhap(String tenNCC) {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (DCHxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        nhaSX = tenNCC;

        dotuoi = TienIch.nhapSoNguyenDuong("Nhập độ tuổi: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));
    }

    @Override
    // Xuat
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Nhà sản xuất: " + nhaSX);

        System.out.println("Độ tuổi: " + dotuoi);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Đồ chơi";
    }

    @Override
    public void suathuoctinhrieng() {
        System.out.println("\n Lựa chọn thuộc tính riêng");
        System.out.println("1. Nhà sản xuất");
        System.out.println("2. Độ tuổi");
        System.out.println("0. Quay lại");
        switch (TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ")) {
            case 0: break;
            case 1: nhaSX = TienIch.nhapChuoi("Nhập lại nhà sản xuất: "); break;
            case 2: dotuoi = TienIch.nhapSoNguyenDuong("Nhập lại độ tuổi: "); break;
            default: System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getdotuoi()+";"+getnhaSX();
    }
}
