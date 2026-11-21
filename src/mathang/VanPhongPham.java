package mathang;

import tienich.*;

public class VanPhongPham extends MatHang {
    private final double thue = 15;
    private final double loinhuan = 20;

    @Override public double getThue() { return thue; }
    @Override public double getLoiNhuan() { return loinhuan; }
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

    @Override
    public void setMa(String ma) {
        if (ma == null || !ma.matches("VPP\\d{4}")) {
            throw new IllegalArgumentException(
                "Mã phải có dạng VPPxxxx"
            );
        }
        super.setMa(ma);
    }

    public String getnhaSX() { return nhaSX; }
    public void setnhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public String getLoaiVPP(){ return loaiVPP; }
    public void setLoaiVPP(String loaiVPP){ this.loaiVPP = loaiVPP; }

    @Override
    public double tinhGiaBan() {
        double giaBan = getGiaNhap() * (1 + loinhuan / 100.0) * (1 + thue / 100.0);
        return Math.round(giaBan * 100.0) / 100.0;
    }

    @Override
    public void nhap() {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (VPPxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        loaiVPP = TienIch.nhapChuoi("Loại văn phòng phẩm: ");

        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    public void nhap(String tenNCC) {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (VPPxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        loaiVPP = TienIch.nhapChuoi("Loại văn phòng phẩm: ");

        nhaSX = tenNCC;

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

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
    public void suathuoctinhrieng() {
        System.out.println("\n Lựa chọn thuộc tính riêng");
        System.out.println("1. Loại văn phòng phẩm");
        System.out.println("2. Nhà sản xuất");
        System.out.println("0. Quay lại");
        switch (TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ")) {
            case 0: break;
            case 1: loaiVPP = TienIch.nhapChuoi("Nhập lại loại văn phòng phẩm: "); break;
            case 2: nhaSX = TienIch.nhapChuoi("Nhập lại nhà sản xuất: "); break;
            default: System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getLoaiVPP()+";"+getnhaSX();
    }
}
