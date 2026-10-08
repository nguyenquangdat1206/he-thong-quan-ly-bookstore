package model.khachhang;

import model.utils.TienIch;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String soDienThoai;

    public KhachHang() {}

    public KhachHang(String maKH, String tenKH, String soDienThoai) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
    }

    // CÁC HÀM GET
    public String getMaKH() { return maKH; }
    public String getTenKH() { return tenKH; }
    public String getSoDienThoai() { return soDienThoai; }

    // CÁC HÀM SET BỔ SUNG ĐỂ SỬA ĐƯỢC LỖI
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public void nhap() {
        maKH = TienIch.nhapChuoi("Nhập mã khách hàng: ");
        tenKH = TienIch.nhapChuoi("Nhập tên khách hàng: ");
        soDienThoai = TienIch.nhapChuoi("Nhập số điện thoại: "); // Đã đổi về nhapChuoi chuẩn
    }

    public void xuat() {
        System.out.println("Mã KH: " + maKH + " | Tên KH: " + tenKH + " | SĐT: " + soDienThoai);
    }
}