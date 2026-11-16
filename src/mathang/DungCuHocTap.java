package mathang;

import tienich.*;


public class DungCuHocTap extends MatHang {
    private final double thue = 12;
    private final double loinhuan = 18;

    @Override public double getThue() { return thue; }
    @Override public double getLoiNhuan() { return loinhuan; }
    private String loaidungcu;
    private String nhaSX;


    public DungCuHocTap(){
        super();
        this.loaidungcu ="";
        this.nhaSX = "";
    }

    public DungCuHocTap(String ma, String ten, double giaNhap, int soLuong, String nhaSX, String loaidungcu) {
        super(ma, ten, giaNhap, soLuong);
        this.nhaSX = nhaSX;
        this.loaidungcu = loaidungcu;
    }

    @Override
    public void setMa(String ma) {
        if (ma == null || !ma.matches("DCT\\d{4}")) {
            throw new IllegalArgumentException(
                "Mã phải có dạng DCTxxxx"
            );
        }
        super.setMa(ma);
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public String getLoaidungcu(){return loaidungcu;}
    public void setLoaidungcu(String loaidungcu){ this.loaidungcu = loaidungcu;}

    @Override
    public double tinhGiaBan(){
        return getGiaNhap() * (1 + loinhuan/100)*(1+thue/100);
    }

    @Override
    // Nhap
    public void nhap() {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (DCTxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        loaidungcu = TienIch.nhapChuoi("Loại dụng cụ: ");

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    @Override
    // Xuat
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Loại dụng cụ: " + loaidungcu);

        System.out.println("Nhà sản xuất: " + nhaSX);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Dụng cụ học tập";
    }


        @Override 
    public String toString(){
        return super.toString()+";"+getLoaidungcu()+";"+getnhaSX();
    }

}
