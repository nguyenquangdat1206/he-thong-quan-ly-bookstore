package mathang;

import tienich.*;

public class SachGiaoKhoa extends MatHang {
    private String monhoc;
    private String nhaXuatBan;

    public SachGiaoKhoa() {
        super();
        this.nhaXuatBan = "";
        this.monhoc = "";
    }

    public SachGiaoKhoa(String ma, String ten, double giaNhap, int soLuong, String monhoc, String nhaXuatBan) {
        super(ma, ten, giaNhap, soLuong);
        this.monhoc = monhoc;
        this.nhaXuatBan = nhaXuatBan;
    }

    public String getMonhoc() { return monhoc; }
    public void setMonhoc(String monhoc) { this.monhoc = monhoc; }
    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setnhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan;}

    // Kiểm tra định dạng mã SGKxxxx
    @Override
    public void setMa(String ma) {
        if (ma == null || !ma.matches("SGK\\d{4}"))
            throw new IllegalArgumentException("Mã sách giáo khoa phải có dạng SGKxxxx (x là chữ số)!");
        super.setMa(ma);
    }

    @Override
    public void nhap() {
        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (SGKxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        monhoc = TienIch.nhapChuoi("Nhập môn học: ");
        nhaXuatBan = TienIch.nhapChuoi("Nhập nhà xuất bản (Gợi ý: NXB Giáo dục): ");
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));
    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    public void nhap(String tenNCC) {
        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (SGKxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        monhoc = TienIch.nhapChuoi("Nhập môn học: ");
        nhaXuatBan = tenNCC;
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));
    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap();   // lợi nhuận 0%, thuế 0%
    }

    @Override
    public void xuat() {
        System.out.println("Mã: " + getMa());
        System.out.println("Tên: " + getTen());
        System.out.println("Môn học: " + monhoc);
        System.out.println("Nhà xuất bản: " + nhaXuatBan);
        System.out.println("Số lượng: " + getSoLuong());
        System.out.println("Giá bán: " + tinhGiaBan());
    }

    @Override
    public String getDanhMuc() { return "Sách giáo khoa"; }

    @Override
    public void suathuoctinhrieng() {
        System.out.println("\n Lựa chọn thuộc tính riêng");
        System.out.println("1. Môn học");
        System.out.println("2. Nhà xuất bản");
        System.out.println("0. Quay lại");
        switch (TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ")) {
            case 0: break;
            case 1: monhoc = TienIch.nhapChuoi("Nhập lại tên môn học: "); break;
            case 2: nhaXuatBan = TienIch.nhapChuoi("Nhập lại tên NXB: "); break;
            default: System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    // Định dạng file: danhMuc;ma;ten;giaNhap;soLuong;monhoc;nhaXuatBan
    @Override
    public String toString() {
        return super.toString() + ";" + monhoc + ";" + nhaXuatBan;
    }
}