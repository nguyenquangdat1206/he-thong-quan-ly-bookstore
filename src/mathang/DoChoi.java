package mathang;

import tienich.*;

public class DoChoi extends MatHang{
    private final double thue = 20;
    private final double loinhuan = 30;

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

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public int getdotuoi() {return dotuoi;}

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

        dotuoi = TienIch.nhapSoNguyenDuong("Nhập độ tuổi: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
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
    public String toString(){
        return super.toString()+";"+getdotuoi()+";"+getnhaSX();
    }
}
