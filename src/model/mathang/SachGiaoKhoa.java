package mathang;

import tienich.*;

public class SachGiaoKhoa extends MatHang{
    private final double thue = 0;
    private final double loinhuan = 0;
    private String monhoc;
    private String nhaXuatBan;

    public SachGiaoKhoa() {
        super();
        this.monhoc = "";
        this.nhaXuatBan = "";
    }

    public SachGiaoKhoa(String ma, String ten, double giaNhap, int soLuong, String monhoc, String nhaXuatBan) {
        super(ma, ten, giaNhap, soLuong);
        this.monhoc = monhoc;
        this.nhaXuatBan = nhaXuatBan;
    }

    public String getMonhoc() { return monhoc; }
    public void setMonhoc(String monhoc) { this.monhoc = monhoc; }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    public double getThue() { return thue; }
    public double getLoinhuan() { return loinhuan; }

    @Override
    public void nhap() {

        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        monhoc = TienIch.nhapChuoi("Nhập môn học: ");

        nhaXuatBan = TienIch.nhapChuoi("Nhà xuất bản: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));

    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap() * (1 + loinhuan / 100) * (1 + thue / 100);
    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Tên môn học: " + monhoc);

        System.out.println("Nhà xuất bản: " + nhaXuatBan);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Sách giáo khoa";
    }

    @Override 
    public String toString(){
        return super.toString()+";"+getMonhoc()+";"+getNhaXuatBan();
    }
}
