package mathang;

import tienich.*;

public class SachGiaoKhoa extends Sach {
    private final double thue = 0;
    private final double loinhuan = 0;
    private String monhoc;

    public SachGiaoKhoa() {
        super();
        this.monhoc = "";
    }

    public SachGiaoKhoa(String ma, String ten, double giaNhap, int soLuong, String monhoc, String nhaXuatBan) {
        super(ma, ten, giaNhap, soLuong, "", nhaXuatBan, "");
        this.monhoc = monhoc;
    }

    public String getMonhoc() { return monhoc; }
    public void setMonhoc(String monhoc) { this.monhoc = monhoc; }

    @Override
    public double getThue() { return thue; }
    @Override
    public double getLoinhuan() { return loinhuan; }

    @Override
    public void nhap() {

        setMa(TienIch.nhapChuoi("Nhập mã: "));

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        monhoc = TienIch.nhapChuoi("Nhập môn học: ");

        setNhaXuatBan(TienIch.nhapChuoi("Nhà xuất bản: "));

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

        System.out.println("Nhà xuất bản: " + getNhaXuatBan());

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override 
    public String getDanhMuc(){
        return "Sách giáo khoa";
    }

    // Giữ nguyên định dạng ghi file cũ: danhMuc;ma;ten;giaNhap;soLuong;monhoc;nhaXuatBan
    @Override 
    public String toString(){
        return getDanhMuc()+";"+getMa()+";"+getTen()+";"+getGiaNhap()+";"+getSoLuong()
                +";"+getMonhoc()+";"+getNhaXuatBan();
    }
}
