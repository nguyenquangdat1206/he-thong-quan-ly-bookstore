package mathang;

import tienich.*;

public class TrangTri extends MatHang{
    private final double thue = 20;
    private final double loinhuan = 19;
    private String nhaSX;


    public TrangTri(){
        super();
        this.nhaSX ="";
    }

    public TrangTri(String ma, String ten, double giaNhap, int soLuong, String nhaSX) {
        super(ma, ten, giaNhap, soLuong);
        this.nhaSX = nhaSX;
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    @Override
    public double tinhGiaBan(){
        return getGiaNhap() * (1 + loinhuan/100)*(1+thue/100);
    }

    @Override
    // Nhap
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

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
    public String toString(){
        return super.toString()+";"+ getnhaSX();
    }
}
