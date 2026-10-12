package danhsach;

import thucthe.HoaDon;

public class DanhSachHoaDon {
    private HoaDon[] danhSach;
    private int soLuong;

    public DanhSachHoaDon() {
        danhSach = new HoaDon[5];
        soLuong = 0;
    }

    private void tangKichThuocMang() {
        HoaDon[] mangMoi = new HoaDon[danhSach.length * 2];
        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    public void them(HoaDon hd) {
        if (soLuong == danhSach.length) {
            tangKichThuocMang();
        }
        danhSach[soLuong] = hd;
        soLuong++;
    }

    public void xuatDanhSach() {
        if (soLuong == 0) {
            System.out.println("Chưa có hóa đơn nào trong hệ thống!");
            return;
        }
        System.out.println("=== LỊCH SỬ HÓA ĐƠN ĐÃ BÁN ===");
        for (int i = 0; i < soLuong; i++) {
            danhSach[i].xuatHoaDon();
        }
    }

    // Tính tổng doanh thu của toàn bộ cửa hàng
    public double tinhTongDoanhThu() {
        // Doanh thu sẽ được cộng dồn dựa trên các hóa đơn
        // (Phần này ta sẽ gọi logic tính tiền sau ở class Main)
        return 0; // Sẽ cập nhật chi tiết sau
    }

    public HoaDon[] getDanhSach() { return danhSach; }
    public int getSoLuong() { return soLuong; }
}