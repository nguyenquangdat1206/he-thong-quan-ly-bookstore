package mathang;

import tienich.*;

public class Vo extends MatHang{
    private final double thue = 10;
    private final double loinhuan = 15;
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

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public int getSotrang(){return sotrang;}
    public void setSotrang(int sotrang) {this.sotrang = sotrang;}

    @Override
    public double tinhGiaBan(){
        return getGiaNhap() * (1 + loinhuan/100)*(1+thue/100);
    }

    @Override
    // Nhap
    public void nhap() {

        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        sotrang = TienIch.nhapSoNguyenDuong("Nhập số trang: ");

        System.out.println("Nhà sản xuất: ");
        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        System.out.println("Giá nhập hàng: ");
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

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
    public String toString(){
        return super.toString()+";"+getSotrang()+";"+getnhaSX();
    }
}
