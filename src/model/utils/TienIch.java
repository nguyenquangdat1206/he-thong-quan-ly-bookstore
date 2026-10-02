package model.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import model.interfaces.IGetMa; // Bắt buộc import để dùng đa hình

public class TienIch {
    public static Scanner sc = new Scanner(System.in);
    // Dùng formatter chuẩn của LocalDate thay cho SimpleDateFormat
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // 1. Nhập chuỗi chống rỗng
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

    // 2. Nhập số nguyên dương (Dành cho Số lượng)
    public static int nhapSoNguyenDuong(String thongBao) {
        int so = 0;
        while (true) {
            try {
                System.out.print(thongBao);
                so = Integer.parseInt(sc.nextLine().trim());
                if (so >= 0) {
                    break; 
                }
                System.out.println("LỖI: Số lượng phải >= 0! Vui lòng nhập lại.");
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số nguyên! Hãy nhập lại.");
            }
        }
        return so;
    }

    // 3. Nhập Giá tiền (Đã đổi tên từ nhapSoThucDuong để đồng bộ với các class MatHang)
    public static double nhapGiaTien(String thongBao) {
        double so = 0;
        while (true) {
            try {
                System.out.print(thongBao);
                so = Double.parseDouble(sc.nextLine().trim());
                if (so > 0) {
                    break; 
                }
                System.out.println("LỖI: Giá tiền phải > 0! Vui lòng nhập lại.");
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số hợp lệ! Hãy nhập lại.");
            }
        }
        return so;
    }

    // 4. Nhập số trong một khoảng (Dành cho chọn Menu từ 1 đến 5)
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

    // 5. Nhập số điện thoại (10 số, bắt đầu bằng số 0)
    public static String nhapSoDienThoai(String thongBao) {
        String sdt = "";
        while (true) {
            System.out.print(thongBao);
            sdt = sc.nextLine().trim();
            if (sdt.matches("^0\\d{9}$")) {
                break;
            }
            System.out.println("LỖI: Số điện thoại phải gồm 10 số và bắt đầu bằng số 0! Vui lòng nhập lại.");
        }
        return sdt;
    }

    // ================= CÁC HÀM XỬ LÝ NGÀY THÁNG =================

    // 6. Nhập ngày (Đã đồng bộ sang LocalDate cho khớp với PhieuNhap)
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

    // 7. Chuyển LocalDate thành String (Dùng để in ra màn hình hoặc ghi file)
    public static String chuyenNgaySangChuoi(LocalDate ngay) {
        if (ngay == null) return "";
        return ngay.format(formatter);
    }
    
    // Alias dự phòng (nếu có thành viên nào lỡ gọi hàm ngaytoString)
    public static String ngaytoString(LocalDate ngay) {
        return chuyenNgaySangChuoi(ngay);
    }

    // ================= CÁC HÀM XỬ LÝ MẢNG CHUNG (TÍNH ĐA HÌNH) =================

    // 8. Tìm kiếm vị trí siêu việt (Dùng Interface IGetMa để tìm được mọi loại mảng)
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

    // 9. Bắt buộc nhập mã không được trùng lặp
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
}