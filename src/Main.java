import model.kho.Kho;
import model.mathang.DanhSachMatHang;
import model.utils.TienIch;

public class Main {
    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("   HỆ THỐNG QUẢN LÝ CỬA HÀNG - TEST RUN");
        System.out.println("===========================================");

        // 1. Khởi tạo 2 "Trái tim" của hệ thống
        DanhSachMatHang dsHangHoa = new DanhSachMatHang();
        Kho khoHang = new Kho();

        // 2. Chạy thử tính năng Đọc File tự động (Nếu nhóm bạn đã có file data/mathang.txt)
        System.out.println("Đang nạp dữ liệu...");
        dsHangHoa.docFile("data/mathang.txt"); 
        
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n=============== MENU CHÍNH ===============");
            System.out.println("1. Quản lý Hàng Hóa (Thử in danh sách)");
            System.out.println("2. Quản lý Kho Bãi (Thử lập Phiếu nhập)");
            System.out.println("3. Thoát chương trình");
            System.out.println("==========================================");
            
            // Gọi hàm Tiện Ích chuẩn của nhóm để chống nhập bậy
            int chon = TienIch.nhapSoTuKhoang("Nhập lựa chọn của bạn", 1, 3);
            
            switch (chon) {
                case 1:
                    System.out.println("\n--- TÍNH NĂNG: XEM DANH SÁCH HÀNG HÓA ---");
                    dsHangHoa.xuat();
                    break;
                case 2:
                    System.out.println("\n--- TÍNH NĂNG: LẬP PHIẾU NHẬP KHO ---");
                    khoHang.them(); // Gọi hàm thêm phiếu nhập của Thành viên 4
                    break;
                case 3:
                    System.out.println("\nĐã thoát chương trình. Tạm biệt!");
                    tiepTuc = false;
                    break;
            }
        }
    }
}