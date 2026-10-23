package nhanvien;

import java.util.Scanner;

public class DanhSachNhanVien {
    private NhanVien[] danhSach;
    private int soLuong;
    private Scanner sc = new Scanner(System.in);

    public DanhSachNhanVien() {
        danhSach = new NhanVien[10];
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

    public NhanVien dangNhap(String maNguoi, String matKhau) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaNguoi().equals(maNguoi) && danhSach[i].getMatKhau().equals(matKhau)) {
                return danhSach[i];
            }
        }
        return null;
    }

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

    public void sua(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri != -1) {
            boolean isEditing = true;
            while (isEditing) {
                System.out.println("\n--- ĐANG SỬA THÔNG TIN CHO NV: " + danhSach[viTri].getHoTen().toUpperCase() + " ---");
                System.out.println("1. Sửa Họ Tên");
                System.out.println("2. Sửa Số điện thoại");
                System.out.println("3. Sửa Mật khẩu đăng nhập");
                System.out.println("4. Sửa Ca làm việc (Sáng/Chiều/Tối)");
                System.out.println("5. Sửa Lương cơ bản");
                System.out.println("0. Hoàn tất & Thoát");
                System.out.print("Chọn thông tin cần sửa (0-5): ");

                String choiceStr = sc.nextLine();
                int choice = -1;
                try {
                    choice = Integer.parseInt(choiceStr);
                } catch (NumberFormatException e) {
                }

                switch (choice) {
                    case 1:
                        System.out.print("Nhập tên mới: ");
                        String tenMoi = sc.nextLine();
                        if (!tenMoi.trim().isEmpty()) {
                            danhSach[viTri].setHoTen(tenMoi);
                            System.out.println("-> Cập nhật Tên thành công!");
                        }
                        break;
                    case 2:
                        System.out.print("Nhập SĐT mới: ");
                        String sdtMoi = sc.nextLine();
                        if (!sdtMoi.trim().isEmpty()) {
                            danhSach[viTri].setSoDienThoai(sdtMoi);
                            System.out.println("-> Cập nhật SĐT thành công!");
                        }
                        break;
                    case 3:
                        System.out.print("Nhập mật khẩu mới: ");
                        String mkMoi = sc.nextLine();
                        if (!mkMoi.trim().isEmpty()) {
                            danhSach[viTri].setMatKhau(mkMoi);
                            System.out.println("-> Cập nhật Mật khẩu thành công!");
                        }
                        break;
                    case 4:
                        System.out.print("Nhập ca làm mới (Sáng/Chiều/Tối): ");
                        String caMoi = sc.nextLine();
                        if (!caMoi.trim().isEmpty()) {
                            danhSach[viTri].setCaLamViec(caMoi);
                            System.out.println("-> Cập nhật Ca làm thành công!");
                        }
                        break;
                    case 5:
                        System.out.print("Nhập mức lương mới (VNĐ): ");
                        String luongStr = sc.nextLine();
                        try {
                            double luongMoi = Double.parseDouble(luongStr);
                            danhSach[viTri].setLuongCoBan(luongMoi);
                            System.out.println("-> Cập nhật Lương thành công!");
                        } catch (NumberFormatException e) {
                            System.out.println("[LỖI] Tiền lương phải là một số hợp lệ!");
                        }
                        break;
                    case 0:
                        isEditing = false;
                        System.out.println("-> Đã lưu các thay đổi cho nhân viên " + ma);
                        break;
                    default:
                        System.out.println("[LỖI] Lựa chọn không hợp lệ.");
                        break;
                }
            }
        } else {
            System.out.println("[LỖI] Không tìm thấy nhân viên nào có mã: " + ma);
        }
    }

    public void timKiem(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri != -1) {
            danhSach[viTri].xuatThongTin();
        } else {
            System.out.println("Không tìm thấy!");
        }
    }

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