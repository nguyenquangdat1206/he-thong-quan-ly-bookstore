import model.mathang.DanhSachMatHang;
import model.kho.Kho;
import model.utils.TienIch;

public class Main {
    // 1. KHỞI TẠO CÁC ĐỐI TƯỢNG QUẢN LÝ TOÀN CỤC (Biến static để dùng chung cho mọi hàm)
    static DanhSachMatHang dsHangHoa = new DanhSachMatHang();
    static Kho khoHang = new Kho();
    
    // TODO: Sau này ráp code của Thành viên 3 và 5 thì tạo thêm ở đây
    // static DanhSachHoaDon dsHoaDon = new DanhSachHoaDon();
    // static DanhSachNhanVien dsNhanVien = new DanhSachNhanVien();

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   CHƯƠNG TRÌNH QUẢN LÝ CỬA HÀNG VĂN PHÒNG PHẨM");
        System.out.println("==================================================");

        // Nạp dữ liệu tự động ngay khi mở chương trình (Xóa dấu // để kích hoạt khi đã có file txt)
        // System.out.println("[HỆ THỐNG] Đang tải dữ liệu Hàng hóa...");
        // dsHangHoa.docFile("data/mathang.txt");
        // System.out.println("[HỆ THỐNG] Đang tải dữ liệu Lịch sử Phiếu Nhập...");
        // khoHang.docFile("data/phieunhap.txt");

        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n=================== MENU CHÍNH ===================");
            System.out.println("1. Quản lý Mặt Hàng (Của Thành viên 2)");
            System.out.println("2. Quản lý Kho Bãi & Phiếu Nhập (Của Thành viên 4)");
            System.out.println("3. Quản lý Bán Hàng & Hóa Đơn (Đang bảo trì ráp code)");
            System.out.println("4. Quản lý Nhân Sự (Đang bảo trì ráp code)");
            System.out.println("5. Lưu dữ liệu & Thoát chương trình");
            System.out.println("==================================================");
            
            // Dùng hàm chuẩn của team để chống nhập tào lao (nhập chữ, nhập số âm...)
            int chon = TienIch.nhapSoTuKhoang("Nhập lựa chọn module", 1, 5);
            
            switch (chon) {
                case 1:
                    menuHangHoa(); // Chuyển hướng sang Sub-menu Hàng Hóa
                    break;
                case 2:
                    menuKhoBai();  // Chuyển hướng sang Sub-menu Kho Bãi
                    break;
                case 3:
                    System.out.println("\n[INFO] Module Hóa Đơn đang được Thành viên 3 ráp code. Vui lòng thử lại sau!");
                    TienIch.dungManHinh();
                    break;
                case 4:
                    System.out.println("\n[INFO] Module Nhân Sự đang được Thành viên 5 ráp code. Vui lòng thử lại sau!");
                    TienIch.dungManHinh();
                    break;
                case 5:
                    System.out.println("\n[HỆ THỐNG] Đang lưu toàn bộ dữ liệu xuống ổ cứng...");
                    // Bật các dòng này lên khi code xong tính năng Ghi file
                    // dsHangHoa.ghiFile("data/mathang.txt");
                    // khoHang.ghiFile("data/phieunhap.txt");
                    System.out.println("[HỆ THỐNG] Lưu thành công. Tạm biệt!");
                    tiepTuc = false;
                    break;
            }
        }
    }

    // ================= SUB-MENU 1: MẶT HÀNG =================
    private static void menuHangHoa() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n--- MODULE: QUẢN LÝ MẶT HÀNG ---");
            System.out.println("1. Thêm mặt hàng mới");
            System.out.println("2. Xem danh sách mặt hàng hiện có");
            System.out.println("3. Sửa thông tin mặt hàng");
            System.out.println("4. Xóa mặt hàng");
            System.out.println("5. Trở về Menu Chính");
            
            int chon = TienIch.nhapSoTuKhoang("Chọn chức năng", 1, 5);
            switch (chon) {
                case 1:
                    System.out.println("\n-- THÊM MẶT HÀNG --");
                    dsHangHoa.nhap(); 
                    TienIch.dungManHinh();
                    break;
                case 2:
                    dsHangHoa.xuat(); 
                    TienIch.dungManHinh();
                    break;
                case 3:
                    System.out.println("\n-- SỬA MẶT HÀNG --");
                    dsHangHoa.sua(); 
                    TienIch.dungManHinh();
                    break;
                case 4:
                    System.out.println("\n-- XÓA MẶT HÀNG --");
                    dsHangHoa.xoa();
                    TienIch.dungManHinh();
                    break;
                case 5:
                    tiepTuc = false;
                    break;
            }
        }
    }

    // ================= SUB-MENU 2: KHO BÃI =================
    private static void menuKhoBai() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n--- MODULE: QUẢN LÝ KHO BÃI ---");
            System.out.println("1. Lập Phiếu Nhập Kho (Nhập thêm hàng)");
            System.out.println("2. Xem lịch sử Phiếu Nhập");
            System.out.println("3. Kiểm tra không gian sức chứa của Kho");
            System.out.println("4. Trở về Menu Chính");
            
            int chon = TienIch.nhapSoTuKhoang("Chọn chức năng", 1, 4);
            switch (chon) {
                case 1:
                    System.out.println("\n-- LẬP PHIẾU NHẬP --");
                    // Lưu ý: Cần truyền dsHangHoa vào để Kho có thể cộng dồn số lượng!
                    // khoHang.nhapHangTuPhieu(PhieuNhap, dsHangHoa); <-- Hướng dẫn TV4 triển khai gọi hàm này
                    khoHang.them(); 
                    TienIch.dungManHinh();
                    break;
                case 2:
                    khoHang.xuat(); 
                    TienIch.dungManHinh();
                    break;
                case 3:
                    // Đoạn code này minh họa cực kỳ rõ sự liên kết (Single Source of Truth)
                    int dangChua = dsHangHoa.getTongSoLuongTonToanHeThong();
                    int tongChua = khoHang.getSucChuaToiDa();
                    System.out.println("\n--- THỐNG KÊ KHO ---");
                    System.out.println("Sức chứa tối đa: " + tongChua + " sản phẩm");
                    System.out.println("Đang lưu trữ   : " + dangChua + " sản phẩm");
                    System.out.println("Còn trống      : " + (tongChua - dangChua) + " sản phẩm");
                    TienIch.dungManHinh();
                    break;
                case 4:
                    tiepTuc = false;
                    break;
            }
        }
    }
}