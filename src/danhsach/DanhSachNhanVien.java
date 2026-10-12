package danhsach;

import thucthe.NhanVien;
import java.util.Scanner;

public class DanhSachNhanVien implements IThaoTacThucThe {
    private NhanVien[] danhSach;
    private int soLuong;
    private Scanner sc = new Scanner(System.in);

    public DanhSachNhanVien() {
        danhSach = new NhanVien[2];
        soLuong = 0;
    }

    public void tangKichThuocMang() {
        NhanVien[] mangMoi = new NhanVien[danhSach.length * 2];
        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    public void them(NhanVien nv) {
        if (soLuong == danhSach.length) {
            tangKichThuocMang();
        }
        danhSach[soLuong] = nv;
        soLuong++;
    }

    // --- TÍNH NĂNG ĐĂNG NHẬP (Dành cho Nhân viên) ---
    public NhanVien dangNhap(String maNguoi, String matKhau) {
        for (int i = 0; i < soLuong; i++) {
            // Nhân viên thì đăng nhập bằng Mã và Mật khẩu
            if (danhSach[i].getMaNguoi().equals(maNguoi) && danhSach[i].getMatKhau().equals(matKhau)) {
                return danhSach[i];
            }
        }
        return null;
    }

    @Override
    public void them() {
        System.out.println("Chức năng cấp tài khoản nhân viên sẽ dành riêng cho Admin.");
    }

    @Override
    public void xoa(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri == -1) {
            System.out.println("Không tìm thấy nhân viên!");
            return;
        }
        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }
        danhSach[soLuong - 1] = null;
        soLuong--;
        System.out.println("Đã xóa nhân viên!");
    }

    @Override
    public void sua(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri != -1) {
            System.out.print("Nhập tên mới (Enter để bỏ qua): ");
            String tenMoi = sc.nextLine();
            if (!tenMoi.isEmpty()) {
                danhSach[viTri].setHoTen(tenMoi);
                System.out.println("Cập nhật thành công!");
            }
        }
    }

    @Override
    public void timKiem(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri != -1) {
            danhSach[viTri].xuatThongTin();
        } else {
            System.out.println("Không tìm thấy!");
        }
    }

    @Override
    public void xuat() {
        System.out.println("=== DANH SÁCH NHÂN VIÊN ===");
        for (int i = 0; i < soLuong; i++) {
            danhSach[i].xuatThongTin();
        }
    }

    public int timKiemViTri(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNguoi().equalsIgnoreCase(ma)) {
                return i;
            }
        }
        return -1;
    }

    public NhanVien[] getDanhSach() { return danhSach; }
    public int getSoLuong() { return soLuong; }
}