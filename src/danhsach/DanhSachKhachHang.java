package danhsach;

import thucthe.KhachHang;
import java.util.Scanner;

public class DanhSachKhachHang implements IThaoTacThucThe {
    private KhachHang[] danhSach;
    private int soLuong;
    private Scanner sc = new Scanner(System.in);

    public DanhSachKhachHang() {
        danhSach = new KhachHang[2];
        soLuong = 0;
    }

    // Thuật toán mảng động
    public void tangKichThuocMang() {
        KhachHang[] mangMoi = new KhachHang[danhSach.length * 2];
        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    public void them(KhachHang kh) {
        if (soLuong == danhSach.length) {
            tangKichThuocMang();
        }
        danhSach[soLuong] = kh;
        soLuong++;
    }

    // --- TÍNH NĂNG ĐĂNG NHẬP (Dành cho Khách hàng) ---
    public KhachHang dangNhap(String sdt, String matKhau) {
        for (int i = 0; i < soLuong; i++) {
            // Kiểm tra khớp cả Số điện thoại và Mật khẩu
            if (danhSach[i].getSoDienThoai().equals(sdt) && danhSach[i].getMatKhau().equals(matKhau)) {
                return danhSach[i]; // Trả về đối tượng khách hàng nếu đăng nhập đúng
            }
        }
        return null; // Trả về null nếu sai tài khoản hoặc mật khẩu
    }

    @Override
    public void them() {
        System.out.println("Chức năng đăng ký khách hàng mới sẽ được hoàn thiện ở phần Giao diện.");
    }

    @Override
    public void xoa(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri == -1) {
            System.out.println("Không tìm thấy khách hàng!");
            return;
        }
        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }
        danhSach[soLuong - 1] = null;
        soLuong--;
        System.out.println("Đã xóa khách hàng!");
    }

    @Override
    public void sua(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri == -1) {
            System.out.println("Không tìm thấy khách hàng!");
            return;
        }
        System.out.print("Nhập tên mới (Enter để bỏ qua): ");
        String tenMoi = sc.nextLine();
        if (!tenMoi.isEmpty()) {
            danhSach[viTri].setHoTen(tenMoi);
            System.out.println("Cập nhật thành công!");
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
        System.out.println("=== DANH SÁCH KHÁCH HÀNG ===");
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

    public KhachHang[] getDanhSach() { return danhSach; }
    public int getSoLuong() { return soLuong; }
}