package danhsach;

import thucthe.LichSuHoatDong;

public class DanhSachLichSu {
    private LichSuHoatDong[] danhSach;
    private int soLuong;

    public DanhSachLichSu() {
        danhSach = new LichSuHoatDong[10]; // Lịch sử thường nhiều nên cho sức chứa ban đầu to một chút
        soLuong = 0;
    }

    private void tangKichThuocMang() {
        LichSuHoatDong[] mangMoi = new LichSuHoatDong[danhSach.length * 2];
        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    public void ghiLog(LichSuHoatDong log) {
        if (soLuong == danhSach.length) tangKichThuocMang();
        danhSach[soLuong] = log;
        soLuong++;
    }

    public void xuatLichSuToanHeThong() {
        if (soLuong == 0) {
            System.out.println("Hệ thống chưa có lịch sử hoạt động nào.");
            return;
        }
        System.out.println("=== LỊCH SỬ HOẠT ĐỘNG HỆ THỐNG ===");
        for (int i = 0; i < soLuong; i++) {
            danhSach[i].xuatLog();
        }
    }
}