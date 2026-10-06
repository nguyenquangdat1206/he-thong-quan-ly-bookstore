package model.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import model.interfaces.IGetMa; // Bắt buộc import để dùng đa hình

public class TienIch {
    public static Scanner sc = new Scanner(System.in);
    // Dùng formatter chuẩn của LocalDate thay cho SimpleDateFormat cũ
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ================= 1. CÁC HÀM NHẬP LIỆU CƠ BẢN =================

    // Nhập chuỗi chống để trống
    public static String nhapChuoi(String thongBao) {
        String ketQua = "";
        while (true) {
            System.out.print(thongBao);
            ketQua = sc.nextLine().trim();
            if (!ketQua.isEmpty()) {
                break;
            }
            System.out.println("LỖI: Không được để trống! Vui lòng nhập lại.");
        }
        return ketQua;
    }

    // Nhập số nguyên dương (Dành cho Số lượng, Độ tuổi, Số trang...)
    public static int nhapSoNguyenDuong(String thongBao) {
        int so = 0;
        while (true) {
            try {
                System.out.print(thongBao);
                so = Integer.parseInt(sc.nextLine().trim());
                if (so >= 0) {
                    break; 
                }
                System.out.println("LỖI: Giá trị phải >= 0! Vui lòng nhập lại.");
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số nguyên! Hãy nhập lại.");
            }
        }
        return so;
    }

    // Nhập Giá tiền / Số thực dương (Dành cho Giá bán, Giá nhập, Lương...)
    public static double nhapGiaTien(String thongBao) {
        double so = 0;
        while (true) {
            try {
                System.out.print(thongBao);
                so = Double.parseDouble(sc.nextLine().trim());
                if (so >= 0) { // Chấp nhận giá 0 đ (ví dụ quà tặng/khuyến mãi)
                    break; 
                }
                System.out.println("LỖI: Giá tiền phải >= 0! Vui lòng nhập lại.");
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số hợp lệ! Hãy nhập lại.");
            }
        }
        return so;
    }

    // Nhập số trong một khoảng (Dành cho việc chọn Menu từ 1 đến N)
    public static int nhapSoTuKhoang(String thongBao, int min, int max) {
        int so = 0;
        while (true) {
            try {
                System.out.print(thongBao + " (" + min + " - " + max + "): ");
                so = Integer.parseInt(sc.nextLine().trim());
                if (so >= min && so <= max) {
                    break;
                }
                System.out.println("LỖI: Vui lòng chọn số từ " + min + " đến " + max + "!");
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số! Hãy nhập lại.");
            }
        }
        return so;
    }

    // ================= 2. CÁC HÀM DÀNH CHO NHÂN SỰ & KHÁCH HÀNG =================

    // Nhập số điện thoại (Bắt buộc 10 số, bắt đầu bằng 0)
    public static String nhapSoDienThoai(String thongBao) {
        String sdt = "";
        while (true) {
            System.out.print(thongBao);
            sdt = sc.nextLine().trim();
            if (sdt.matches("^0\\d{9}$")) {
                break;
            }
            System.out.println("LỖI: Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0! Vui lòng nhập lại.");
        }
        return sdt;
    }

    // ================= 3. CÁC HÀM XỬ LÝ NGÀY THÁNG =================

    // Nhập ngày chuẩn LocalDate
    public static LocalDate nhapNgay(String thongBao) {
        String input;
        while (true) {
            input = nhapChuoi(thongBao + " (dd/MM/yyyy): ");
            if (input.isEmpty()) return null;
            try {
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                System.out.println("LỖI! Vui lòng nhập đúng định dạng ngày tháng dd/MM/yyyy.");
            }
        }
    }

    // Chuyển LocalDate thành String (Dùng để in ra màn hình hoặc ghi file)
    public static String chuyenNgaySangChuoi(LocalDate ngay) {
        if (ngay == null) return "N/A";
        return ngay.format(formatter);
    }
    
    // Alias dự phòng (Đề phòng có thành viên gọi nhầm tên cũ)
    public static String ngaytoString(LocalDate ngay) {
        return chuyenNgaySangChuoi(ngay);
    }

    // ================= 4. CÁC HÀM XỬ LÝ MẢNG TỔNG QUÁT (ĐA HÌNH) =================

    // Tìm kiếm vị trí siêu việt (Tìm được cho cả mảng Hàng Hóa, Phiếu Nhập, Khách Hàng...)
    public static int timKiemViTri(IGetMa[] ds, int soLuong, String maCanTim) {
        if (ds == null || maCanTim == null || maCanTim.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < soLuong; i++) {
            if (ds[i] != null && ds[i].getMa().equalsIgnoreCase(maCanTim.trim())) {
                return i;
            }
        }
        return -1;
    }

    // Bắt buộc nhập mã không được trùng lặp
    public static String nhapMa(IGetMa[] ds, int soLuong, String thongBao) {
        String ma;
        while (true) {
            ma = nhapChuoi(thongBao);
            if (timKiemViTri(ds, soLuong, ma) != -1) {
                System.out.println("LỖI: Mã này đã tồn tại trong hệ thống! Vui lòng nhập mã khác.");
            } else {
                break;
            }
        }
        return ma;
    }

    // ================= 5. TRẢI NGHIỆM NGƯỜI DÙNG (UX) =================

    // Dừng màn hình để người dùng đọc thông báo (Chống trôi lệnh Menu)
    public static void dungManHinh() {
        System.out.println("\n(Nhấn phím Enter để quay lại Menu...)");
        sc.nextLine();
    }
}