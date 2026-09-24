import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TienIch {
    private static Scanner sc = new Scanner(System.in);
    // Khai báo một bộ định dạng chuẩn dùng chung cho tất cả các hàm
    private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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

    // 3. Nhập số thực dương (Dành cho Giá tiền/Đơn giá)
    public static double nhapSoThucDuong(String thongBao) {
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

    // 6. Nhập ngày tháng năm chuẩn xác (Dùng khi nhập liệu từ bàn phím)
    public static Date nhapNgay(String thongBao) {
        sdf.setLenient(false); // Chống nhập ngày ảo như 32/13/2026
        while (true) {
            try {
                System.out.print(thongBao + " (dd/MM/yyyy): ");
                return sdf.parse(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("LỖI: Sai định dạng hoặc ngày không tồn tại! Hãy nhập lại.");
            }
        }
    }

    // ================= CÁC HÀM HỖ TRỢ ĐỌC / GHI FILE =================

    // 7. Chuyển Date thành String (Dùng để nén thành chuỗi trước khi GHI xuống file .txt)
    public static String chuyenNgaySangChuoi(Date date) {
        if (date == null) return "";
        return sdf.format(date); // Output: "24/09/2026"
    }

    // 8. Chuyển String thành Date (Dùng để dịch ngược lại khi ĐỌC từ file .txt lên)
    public static Date chuyenChuoiSangNgay(String str) {
        try {
            sdf.setLenient(false);
            return sdf.parse(str);
        } catch (Exception e) {
            System.out.println("CẢNH BÁO: Lỗi đọc dữ liệu ngày tháng từ file text!");
            return null; 
        }
    }
}