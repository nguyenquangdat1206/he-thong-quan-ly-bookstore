package mathang;

import tienich.*;

public class VanPhongPham extends MatHang {
    private final double thue = 15;
    private final double loinhuan = 20;
    private String loaiVPP;
    private String nhaSX;

    public VanPhongPham(){
        super();
        this.loaiVPP = "";
        this.nhaSX = "";
    }

    public VanPhongPham(String ma, String ten, double giaNhap, int soLuong, String nhaSX, String loaiVPP) {
        super(ma, ten, giaNhap, soLuong);
        this.nhaSX = nhaSX;
        this.loaiVPP = loaiVPP;
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public String getLoaiVPP(){ return loaiVPP; }
    public void setLoaiVPP(String loaiVPP){ this.loaiVPP = loaiVPP; }

    @Override
    public double tinhGiaBan(){
        return getGiaNhap() * (1 + loinhuan/100)*(1+thue/100);
    }

    @Override
    public void nhap() {

        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        loaiVPP = TienIch.nhapChuoi("Loại văn phòng phẩm: ");

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Loại văn phòng phẩm: " + loaiVPP);

        System.out.println("Nhà sản xuất: " + nhaSX);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Văn phòng phẩm";
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getLoaiVPP()+";"+getnhaSX();
    }
}
