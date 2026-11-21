package mathang;

import tienich.*;

public class TrangTri extends MatHang{
    private final double thue = 20;
    private final double loinhuan = 19;

    @Override public double getThue() { return thue; }
    @Override public double getLoiNhuan() { return loinhuan; }
    private String nhaSX;


    public TrangTri(){
        super();
        this.nhaSX ="";
    }

    public TrangTri(String ma, String ten, double giaNhap, int soLuong, String nhaSX) {
        super(ma, ten, giaNhap, soLuong);
        this.nhaSX = nhaSX;
    }

    @Override
    public void setMa(String ma) {
        if (ma == null || !ma.matches("TTR\\d{4}")) {
            throw new IllegalArgumentException(
                "Mã phải có dạng TTRxxxx"
            );
        }
        super.setMa(ma);
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    @Override
    public double tinhGiaBan() {
        return tinhGiaBan(loinhuan, thue);
    }

    @Override
    // Nhap
    public void nhap() {
        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (TTRxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    public void nhap(String tenNCC) {
        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (TTRxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

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

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Trang trí";
    }

    @Override
    public void suathuoctinhrieng() {
        nhaSX = TienIch.nhapChuoi("Nhập lại nhà sản xuất: ");
    }

    @Override 
    public String toString(){
        return super.toString()+";"+ getnhaSX();
    }
}
