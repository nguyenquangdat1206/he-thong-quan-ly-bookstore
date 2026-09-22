import java.util.Scanner;

public class TienIch {
    // Dùng chung 1 biến Scanner toàn dự án để không bị đụng nhau
    public static Scanner sc = new Scanner(System.in);

    // 1. Hàm nhập chuỗi (fix luôn lỗi trôi lệnh)
    public static String nhapChuoi(String thongBao) {
        System.out.print(thongBao);
        return sc.nextLine();
    }

    // 2. Hàm nhập số nguyên (dùng cho Số lượng) có try-catch chống văng app
    public static int nhapSoNguyen(String thongBao) {
        int so = 0;
        while (true) {
            try {
                System.out.print(thongBao);
                so = Integer.parseInt(sc.nextLine());
                break; // Nhập đúng thì thoát vòng lặp
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số nguyên! Hãy nhập lại.");
            }
        }
        return so;
    }

    // 3. Hàm nhập số thực (dùng cho Giá tiền/Đơn giá)
    public static double nhapSoThuc(String thongBao) {
        double so = 0;
        while (true) {
            try {
                System.out.print(thongBao);
                so = Double.parseDouble(sc.nextLine());
                break; 
            } catch (NumberFormatException e) {
                System.out.println("LỖI: Vui lòng chỉ nhập số hợp lệ! Hãy nhập lại.");
            }
        }
        return so;
    }
}