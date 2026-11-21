package kho;
 
import tienich.*;
 
public class QuanLyKho {
    static String hoiFile(String macDinh) {
        System.out.print("Đường dẫn file (Enter hoặc . để dùng '" + macDinh + "'): ");
        String s = TienIch.sc.nextLine().trim();
        return (s.isEmpty() || s.equals(".")) ? macDinh : s;
    }

    static Kho kho = new Kho();

    public static void menuKho() {
        int chon;
        do {
            System.out.println("\n---------- KHO ----------");
            System.out.println("1. Xem hàng tồn");
            System.out.println("2. Thêm 1 mặt hàng vào kho");
            System.out.println("3. Nhập nhiều mặt hàng vào kho");
            System.out.println("4. Sửa số lượng tồn");
            System.out.println("5. Xóa mặt hàng khỏi kho");
            System.out.println("6. Tra cứu theo mã");
            System.out.println("7. Kiểm tra sức chứa");
            System.out.println("8. Ghi kho ra file");
            System.out.println("9. Thống kê tồn kho theo danh mục");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: kho.xuat(); break;
                case 2: kho.them(); break;
                case 3: kho.nhap(); break;
                case 4: kho.sua(); break;
                case 5: kho.xoa(); break;
                case 6: kho.timHangTon(); break;
                case 7: kho.kiemTraSucChua(); break;
                case 8: kho.ghiFile(hoiFile("data/kho_output.txt")); break;
                case 9: kho.thongKeTheoDanhMuc(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
}
