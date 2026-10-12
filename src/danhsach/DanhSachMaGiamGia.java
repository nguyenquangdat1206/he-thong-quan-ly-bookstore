package danhsach;

import thucthe.MaGiamGia;
import java.util.Scanner;

public class DanhSachMaGiamGia implements IThaoTacThucThe {
    private MaGiamGia[] danhSach;
    private int soLuong;
    private Scanner sc = new Scanner(System.in);

    public DanhSachMaGiamGia() {
        danhSach = new MaGiamGia[2];
        soLuong = 0;
    }

    public void tangKichThuocMang() {
        MaGiamGia[] mangMoi = new MaGiamGia[danhSach.length * 2];
        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    public void them(MaGiamGia mgm) {
        if (soLuong == danhSach.length) tangKichThuocMang();
        danhSach[soLuong] = mgm;
        soLuong++;
    }

    // --- HÀM XỬ LÝ: Khách hàng áp dụng mã ---
    public float apDungMaGiamGia(String maNhapVao) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaCode().equalsIgnoreCase(maNhapVao)) {
                if (danhSach[i].getSoLuongGioiHan() > 0) {
                    System.out.println("Áp dụng mã giảm giá thành công! Bạn được giảm " + (danhSach[i].getPhanTramGiam() * 100) + "%");
                    danhSach[i].giamSoLuong(); // Trừ đi 1 lượt dùng
                    return danhSach[i].getPhanTramGiam();
                } else {
                    System.out.println("Mã giảm giá này đã hết lượt sử dụng!");
                    return 0;
                }
            }
        }
        System.out.println("Mã giảm giá không hợp lệ!");
        return 0;
    }

    @Override
    public void them() {
        System.out.println("Chức năng thêm mã giảm giá mới (Dành cho Quản lý).");
    }

    @Override
    public void xoa(String ma) {
        // Tương tự các hàm xóa khác, tạm ẩn để tránh dài dòng (bạn có thể tự code như DanhSachSanPham)
    }

    @Override
    public void sua(String ma) {
    }

    @Override
    public void timKiem(String ma) {
    }

    @Override
    public void xuat() {
        System.out.println("=== DANH SÁCH MÃ GIẢM GIÁ HIỆN CÓ ===");
        for (int i = 0; i < soLuong; i++) {
            danhSach[i].xuatThongTin();
        }
    }

    public MaGiamGia[] getDanhSach() { return danhSach; }
    public int getSoLuong() { return soLuong; }
}