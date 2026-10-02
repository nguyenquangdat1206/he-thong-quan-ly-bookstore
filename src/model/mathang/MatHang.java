package model.mathang;

import model.interfaces.IGetMa;
import model.utils.TienIch;

public abstract class MatHang implements IGetMa {
    private String ma;
    private String ten;
    private double giaNhap;
    private int soLuongTon; // Đổi tên để nhấn mạnh đây là Tồn Kho chuẩn
    private double loinhuan;
    private double thue;

    // Constructor rỗng
    public MatHang() {
        this.ma = "";
        this.ten = "";
        this.giaNhap = 0;
        this.soLuongTon = 0;
        this.loinhuan = 0;
        this.thue = 0;
    }

    // Constructor đầy đủ
    public MatHang(String ma, String ten, double giaNhap, int soLuongTon, double loinhuan, double thue) {
        this.ma = ma;
        this.ten = ten;
        this.giaNhap = giaNhap;
        this.soLuongTon = soLuongTon;
        this.loinhuan = loinhuan;
        this.thue = thue;
    }

    // Getters & Setters
    @Override
    public String getMa() { return ma; }
    public void setMa(String ma) { this.ma = ma; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) { this.giaNhap = giaNhap; }

    public int getSoLuongTon() { return soLuongTon; }
    // Khóa chặn số âm bằng toán tử 3 ngôi
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = (soLuongTon < 0) ? 0 : soLuongTon; }

    public double getLoinhuan() { return loinhuan; }
    public void setLoinhuan(double loinhuan) { this.loinhuan = loinhuan; }

    public double getThue() { return thue; }
    public void setThue(double thue) { this.thue = thue; }

    // Công thức tính giá bán chung cho MỌI MẶT HÀNG
    public double tinhGiaBan() {
        return giaNhap * (1 + loinhuan / 100) * (1 + thue / 100);
    }

    // Nhập
    public void nhap() {
        ma = TienIch.nhapChuoi("Nhập mã: ");
        ten = TienIch.nhapChuoi("Nhập tên: ");
        giaNhap = TienIch.nhapGiaTien("Giá nhập hàng: ");
        soLuongTon = TienIch.nhapSoNguyenDuong("Nhập số lượng: ");
        loinhuan = TienIch.nhapGiaTien("Nhập % lợi nhuận (VD: 20): ");
        thue = TienIch.nhapGiaTien("Nhập % thuế (VD: 8): ");
    }

    // Xuất
    public void xuat() {
        System.out.printf("Mã: %-10s | Tên: %-20s | Tồn kho: %-5d | Giá bán: %,.2f\n", 
                          ma, ten, soLuongTon, tinhGiaBan());
    }
}