import model.mathang.DanhSachMatHang;
import model.kho.Kho;
import model.hoadon.DanhSachHoaDon;
import model.khachhang.DanhSachKhachHang;
import model.utils.TienIch;
import model.nhansu.*;

public class Main {
    // 1. KHỞI TẠO CÁC ĐỐI TƯỢNG QUẢN LÝ TOÀN CỤC
    static DanhSachMatHang dsHangHoa = new DanhSachMatHang();
    static Kho khoHang = new Kho();
    static DanhSachHoaDon dsHoaDon = new DanhSachHoaDon();
    static DanhSachNhanVien dsNhanVien = new DanhSachNhanVien();
    static DanhSachKhachHang dsKhachHang = new DanhSachKhachHang();
    
    // 2. BIẾN LƯU PHIÊN ĐĂNG NHẬP
    static NhanVien userHienTai = null;

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   CHƯƠNG TRÌNH QUẢN LÝ CỬA HÀNG VĂN PHÒNG PHẨM");
        System.out.println("==================================================");

        // ================= NẠP DỮ LIỆU TỰ ĐỘNG =================
        System.out.println("\n[HỆ THỐNG] Đang tải dữ liệu Hàng hóa từ file mathang.txt...");
        dsHangHoa.docFile("data/mathang.txt");

        System.out.println("[HỆ THỐNG] Đang tải dữ liệu Khách hàng từ file khachhang.txt...");
        dsKhachHang.docFile("data/khachhang.txt");

        System.out.println("[HỆ THỐNG] Đang tải dữ liệu Hóa đơn từ file hoadon.txt...");
        dsHoaDon.docFile("data/hoadon.txt", dsKhachHang, dsHangHoa);
        
        System.out.println("==================================================\n");

        // TẠO DỮ LIỆU NHÂN VIÊN MẪU
        dsNhanVien.them(new QuanLy("admin", "Nguyễn Quang Đạt", "0900000000", "123", "Sáng", 20000));
        dsNhanVien.them(new NhanVienBanHang("bh01", "Uyên Nie", "0911111111", "123", "Chiều", 10000));
        dsNhanVien.them(new NhanVienNhapKho("kho01", "Trần Thủ Kho", "0922222222", "123", "Tối", 12000));

        // ================= YÊU CẦU ĐĂNG NHẬP =================
        while (userHienTai == null) {
            System.out.println("\n--- VUI LÒNG ĐĂNG NHẬP ---");
            String tk = TienIch.nhapChuoi("Tài khoản (Mã NV): ");
            String mk = TienIch.nhapChuoi("Mật khẩu: ");
            
            userHienTai = dsNhanVien.dangNhap(tk, mk);
            
            if (userHienTai == null) {
                System.out.println("[LỖI] Sai tài khoản hoặc mật khẩu! (Mẹo: admin/123 hoặc bh01/123)");
            }
        }
        
        System.out.println("\n>>> ĐĂNG NHẬP THÀNH CÔNG!");
        System.out.println("Xin chào: " + userHienTai.getQuyen() + " - Tài khoản: " + userHienTai.getMaNguoi());
        TienIch.dungManHinh();

        // ================= MENU CHÍNH =================
        boolean tiepTuc = true;
        while (tiepTuc) {
            String quyen = userHienTai.getQuyen();
            int stt = 1; // Biến đếm số thứ tự
            int luaChonToiDa = 0; // Biến lưu số lượng menu hiện có cho từng role

            System.out.println("\n=================== MENU CHÍNH ===================");
            
            // MENU 1: HÀNG HÓA (Ai cũng có)
            System.out.println(stt++ + ". Quản lý Mặt Hàng");
            
            // CÁC MENU ĐỘNG THEO QUYỀN
            if (quyen.equals("ADMIN")) {
                System.out.println(stt++ + ". Quản lý Kho Bãi & Phiếu Nhập");
                System.out.println(stt++ + ". Quản lý Bán Hàng & Hóa Đơn");
                System.out.println(stt++ + ". Quản lý Nhân Sự");
                System.out.println(stt++ + ". Quản lý Khách Hàng");
            } 
            else if (quyen.equals("BAN_HANG")) {
                System.out.println(stt++ + ". Quản lý Bán Hàng & Hóa Đơn");
                System.out.println(stt++ + ". Quản lý Khách Hàng");
            } 
            else if (quyen.equals("NHAP_KHO")) {
                System.out.println(stt++ + ". Quản lý Kho Bãi & Phiếu Nhập");
            }

            // MENU CUỐI: ĐĂNG XUẤT
            System.out.println(stt + ". Lưu dữ liệu & Đăng xuất");
            System.out.println("==================================================");
            
            luaChonToiDa = stt; // Gán lại để biết người dùng được nhập tối đa số mấy
            int chon = TienIch.nhapSoTuKhoang("Nhập lựa chọn", 1, luaChonToiDa);
            
            // ================= XỬ LÝ LỰA CHỌN =================
            // Nút đầu tiên luôn là Hàng Hóa, Nút cuối cùng luôn là Đăng xuất
            if (chon == 1) {
                menuHangHoa();
            } 
            else if (chon == luaChonToiDa) {
                System.out.println("\n[HỆ THỐNG] Đang lưu toàn bộ dữ liệu xuống ổ cứng...");
                dsKhachHang.ghiFile("data/khachhang.txt");
                dsHoaDon.ghiFile("data/hoadon.txt"); // <-- Đã bổ sung lưu Hóa đơn
                System.out.println("[HỆ THỐNG] Lưu thành công. Tạm biệt " + userHienTai.getMaNguoi() + "!");
                tiepTuc = false;
            } 
            else {
                // Xử lý các lựa chọn ở giữa dựa theo Role
                if (quyen.equals("ADMIN")) {
                    switch (chon) {
                        case 2: menuKhoBai(); break;
                        case 3: menuHoaDon(); break;
                        case 4: menuNhanSu(); break;
                        case 5: menuKhachHang(); break;
                    }
                } 
                else if (quyen.equals("BAN_HANG")) {
                    switch (chon) {
                        case 2: menuHoaDon(); break; // Ở role Bán Hàng, menu 2 là Hóa Đơn
                        case 3: menuKhachHang(); break; // menu 3 là Khách Hàng
                    }
                } 
                else if (quyen.equals("NHAP_KHO")) {
                    switch (chon) {
                        case 2: menuKhoBai(); break; // Ở role Nhập Kho, menu 2 là Kho Bãi
                    }
                }
            }
        }
    }

    // ================= SUB-MENU 1: MẶT HÀNG =================
    private static void menuHangHoa() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            String quyen = userHienTai.getQuyen();
            System.out.println("\n--- MODULE: QUẢN LÝ MẶT HÀNG ---");
            
            if (quyen.equals("ADMIN")) {
                System.out.println("1. Thêm mặt hàng mới");
            }
            System.out.println("2. Xem danh sách mặt hàng hiện có");
            
            if (quyen.equals("ADMIN")) {
                System.out.println("3. Sửa thông tin mặt hàng");
                System.out.println("4. Xóa mặt hàng");
            }
            System.out.println("5. Trở về Menu Chính");
            
            int chon = TienIch.nhapSoTuKhoang("Chọn chức năng", 1, 5);
            
            if (!quyen.equals("ADMIN") && chon != 2 && chon != 5) {
                System.out.println("[CẢNH BÁO] Bạn chỉ có quyền Xem danh sách (Phím 2)!");
                TienIch.dungManHinh();
                continue;
            }

            switch (chon) {
                case 1: dsHangHoa.nhap(); TienIch.dungManHinh(); break;
                case 2: dsHangHoa.xuat(); TienIch.dungManHinh(); break;
                case 3: dsHangHoa.sua(); TienIch.dungManHinh(); break;
                case 4: dsHangHoa.xoa(); TienIch.dungManHinh(); break;
                case 5: tiepTuc = false; break;
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
                case 1: khoHang.them(dsHangHoa); TienIch.dungManHinh(); break;
                case 2: khoHang.xuat(); TienIch.dungManHinh(); break;
                case 3:
                    int dangChua = dsHangHoa.getTongSoLuongTonToanHeThong();
                    int tongChua = khoHang.getSucChuaToiDa();
                    System.out.println("\n--- THỐNG KÊ KHO ---");
                    System.out.println("Sức chứa tối đa: " + tongChua + " sản phẩm");
                    System.out.println("Đang lưu trữ   : " + dangChua + " sản phẩm");
                    System.out.println("Còn trống      : " + (tongChua - dangChua) + " sản phẩm");
                    TienIch.dungManHinh();
                    break;
                case 4: tiepTuc = false; break;
            }
        }
    }

    // ================= SUB-MENU 3: BÁN HÀNG & HÓA ĐƠN =================
    private static void menuHoaDon() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n--- MODULE: BÁN HÀNG & HÓA ĐƠN ---");
            System.out.println("1. Lập Hóa Đơn Bán Hàng");
            System.out.println("2. Xem lịch sử Hóa Đơn");
            System.out.println("3. Trở về Menu Chính");
            
            int chon = TienIch.nhapSoTuKhoang("Chọn chức năng", 1, 3);
            switch (chon) {
                case 1: dsHoaDon.them(dsHangHoa); TienIch.dungManHinh(); break;
                case 2: dsHoaDon.xuat(); TienIch.dungManHinh(); break;
                case 3: tiepTuc = false; break;
            }
        }
    }

    // ================= SUB-MENU 4: NHÂN SỰ =================
    private static void menuNhanSu() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n--- MODULE: QUẢN LÝ NHÂN SỰ ---");
            System.out.println("1. Thêm nhân viên mới (Cấp tài khoản)");
            System.out.println("2. Xem danh sách nhân viên");
            System.out.println("3. Sửa thông tin nhân viên");
            System.out.println("4. Xóa nhân viên");
            System.out.println("5. Trở về Menu Chính");
            
            int chon = TienIch.nhapSoTuKhoang("Chọn chức năng", 1, 5);
            switch (chon) {
                case 1: 
                    System.out.println("\n--- TIẾN HÀNH CẤP TÀI KHOẢN MỚI ---");
                    System.out.println("1. Cấp quyền Quản Lý (Admin)");
                    System.out.println("2. Cấp quyền Bán Hàng");
                    System.out.println("3. Cấp quyền Nhập Kho");
                    int loaiNV = TienIch.nhapSoTuKhoang("Chọn chức vụ cho nhân viên này", 1, 3);
                    
                    String ma = TienIch.nhapChuoi("Nhập mã NV (Dùng làm Tên đăng nhập): ");
                    String ten = TienIch.nhapChuoi("Nhập họ và tên: ");
                    String sdt = TienIch.nhapChuoi("Nhập số điện thoại: ");
                    String mk = TienIch.nhapChuoi("Nhập mật khẩu mặc định: ");
                    String ca = TienIch.nhapChuoi("Nhập ca làm việc (Sáng/Chiều/Tối): ");
                    int luong = TienIch.nhapSoNguyenDuong("Nhập mức lương cơ bản: ");
                    
                    // Sử dụng constructor có sẵn để nhúng thẳng vào danh sách
                    if (loaiNV == 1) {
                        dsNhanVien.them(new QuanLy(ma, ten, sdt, mk, ca, luong));
                    } else if (loaiNV == 2) {
                        dsNhanVien.them(new NhanVienBanHang(ma, ten, sdt, mk, ca, luong));
                    } else {
                        dsNhanVien.them(new NhanVienNhapKho(ma, ten, sdt, mk, ca, luong));
                    }
                    
                    System.out.println(">>> Đã cấp tài khoản thành công cho nhân viên: " + ten);
                    TienIch.dungManHinh(); 
                    break;
                
                case 2: dsNhanVien.xuat(); TienIch.dungManHinh(); break;
                case 3:
                    String maSua = TienIch.nhapChuoi("Nhập mã NV cần sửa: ");
                    dsNhanVien.sua(maSua);
                    TienIch.dungManHinh();
                    break;
                case 4:
                    String maXoa = TienIch.nhapChuoi("Nhập mã NV cần xóa: ");
                    dsNhanVien.xoa(maXoa);
                    TienIch.dungManHinh();
                    break;
                case 5: tiepTuc = false; break;
            }
        }
    }
    
    // ================= SUB-MENU 5: KHÁCH HÀNG =================
    private static void menuKhachHang() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n--- MODULE: QUẢN LÝ KHÁCH HÀNG ---");
            System.out.println("1. Thêm khách hàng mới");
            System.out.println("2. Xem danh sách khách hàng");
            System.out.println("3. Sửa thông tin khách hàng");
            System.out.println("4. Xóa khách hàng");
            System.out.println("5. Trở về Menu Chính");
            
            int chon = TienIch.nhapSoTuKhoang("Chọn chức năng", 1, 5);
            switch (chon) {
                case 1: dsKhachHang.them(); TienIch.dungManHinh(); break;
                case 2: dsKhachHang.xuat(); TienIch.dungManHinh(); break;
                case 3: dsKhachHang.sua(); TienIch.dungManHinh(); break;
                case 4: dsKhachHang.xoa(); TienIch.dungManHinh(); break;
                case 5: tiepTuc = false; break;
            }
        }
    }
}
