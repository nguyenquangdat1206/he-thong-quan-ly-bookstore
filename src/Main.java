import hoadon.*;
import khachhang.*;
import kho.*;
import mathang.*;
import nhanvien.*;
import phieunhap.*;
import nhacungcap.*;
import giohang.*;
import thanhtoan.*;
import tienich.TienIch;
import tichhop.LuuTruMatHang;

/** TV5: lắp ghép menu với các module nguyên gốc của TV2,3,4. */
public class Main {
    static final String FILE_NV = "nhanvien.txt";
    static final String ANSI = "\u001B[36m", RESET = "\u001B[0m";
    static Kho kho = new Kho();
    static DanhSachMatHang dsmh = new DanhSachMatHang();
    static DanhSachKhachHang dskh = new DanhSachKhachHang();
    static DanhSachNCC dsncc = new DanhSachNCC();
    static DanhSachPhieuNhap dspn = new DanhSachPhieuNhap(dsncc);
    static DanhSachHoaDon dshd = new DanhSachHoaDon(dsmh, dskh);
    static DanhSachNhanVien dsnv = new DanhSachNhanVien();
    static NhanVien dangDangNhap;
    static NhanVienBanHang nvBanHang;

    static String hoiFile(String macDinh) {
        System.out.print("Đường dẫn file (Enter hoặc . để dùng '" + macDinh + "'): ");
        String s = TienIch.sc.nextLine().trim();
        return (s.isEmpty() || s.equals(".")) ? macDinh : s;
    }
    static void menuChinh() {
        System.out.println("\n" + ANSI + "======== QUẢN LÝ NHÀ SÁCH - TV5 MERGE ========" + RESET);
        System.out.println("1. Đăng nhập nhân viên");
        System.out.println("2. Xem danh sách mặt hàng (khách vãng lai)");
        System.out.println("0. Thoát");
    }
    // ====== Kho ======
    static void menuKho() {
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
            System.out.println("8. Đọc kho từ file");
            System.out.println("9. Ghi kho ra file");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: kho.xuat(); break;
                case 2: kho.them(); break;
                case 3: kho.nhap(); break;
                case 4: kho.sua(); break;
                case 5: kho.xoa(); break;
                case 6: kho.timHangTon(); break;
                case 7: kho.kiemTraSucChua(); break;
                case 8: kho.docFile(hoiFile("kho/kho_input.txt")); break;
                case 9: kho.ghiFile(hoiFile("kho/kho_output.txt")); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 
    // ====== Phiếu nhập ======
    static void menuPhieuNhap() {
        int chon;
        do {
            System.out.println("\n---------- PHIẾU NHẬP ----------");
            System.out.println("1. Xem danh sách phiếu nhập");
            System.out.println("2. Thêm 1 phiếu nhập");
            System.out.println("3. Thêm nhiều phiếu nhập");
            System.out.println("4. Sửa phiếu nhập");
            System.out.println("5. Xóa phiếu nhập");
            System.out.println("6. Nhập hàng của phiếu vào kho");
            System.out.println("7. Xem hàng tồn trong kho");
            System.out.println("8. Đọc phiếu nhập từ file");
            System.out.println("9. Ghi phiếu nhập ra file");
            System.out.println("10. Xem danh sách nhà cung cấp trong lần nhập này");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: dspn.xuat(); break;
                case 2: dspn.them(); break;
                case 3: dspn.nhap(); break;
                case 4: dspn.sua(); break;
                case 5: dspn.xoa(); break;
                case 6: dspn.nhapKho(kho); break;
                case 7: kho.xuat(); break;
                case 8: dspn.docFile(hoiFile("phieunhap/phieunhap_input.txt")); break;
                case 9: dspn.ghiFile(hoiFile("phieunhap/phieunhap_output.txt")); break;
                case 10: dspn.getDanhSachNCC().xuat(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 
    // ====== Mặt hàng ======
    static void menuMatHang() {
        int chon;
        do {
            System.out.println("\n---------- MẶT HÀNG (CỬA HÀNG) ----------");
            System.out.println("1. Xem số lượng mặt hàng đang bày bán");
            System.out.println("2. Nhập hàng loạt từ kho lên");
            System.out.println("3. Nhập 1 mặt hàng cụ thể từ kho lên");
            System.out.println("4. Xuất danh sách mặt hàng");
            System.out.println("5. Thêm mặt hàng");
            System.out.println("6. Xóa mặt hàng");
            System.out.println("7. Sửa mặt hàng");
            System.out.println("8. Tìm kiếm theo mã");
            System.out.println("9. Bộ lọc nâng cao");
            System.out.println("10. Đọc mặt hàng từ file");
            System.out.println("11. Ghi mặt hàng ra file");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: System.out.println("Số mặt hàng: " + dsmh.getSoMH()); break;
                case 2: dsmh.nhapHangLoat(kho); break;
                case 3: {
                    // dsmh.nhap() dùng field kho chưa được khởi tạo nên gọi nhapTuKho trực tiếp
                    String ma = TienIch.nhapChuoi("Nhập mã hàng cần lấy từ kho: ");
                    int sl = TienIch.nhapSoNguyenDuong("Nhập số lượng cần lấy: ");
                    dsmh.nhapTuKho(kho, ma, sl);
                    break;
                }
                case 4: dsmh.xuat(); break;
                case 5: dsmh.them(); break;
                case 6: dsmh.xoa(); break;
                case 7: dsmh.sua(); break;
                case 8: dsmh.timkiem(); break;
                case 9: dsmh.timkiem_nangcao(); break;
                case 10: LuuTruMatHang.docFile(dsmh, hoiFile("mathang.txt")); break;
                case 11: LuuTruMatHang.ghiFile(dsmh, hoiFile("mathang.txt")); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 
    // ====== Khách hàng ======
    static void menuKhachHang() {
        int chon;
        do {
            System.out.println("\n---------- KHÁCH HÀNG ----------");
            System.out.println("1. Xem danh sách");
            System.out.println("2. Thêm khách hàng");
            System.out.println("3. Nhập nhiều khách hàng");
            System.out.println("4. Sửa khách hàng");
            System.out.println("5. Xóa khách hàng");
            System.out.println("6. Đọc từ file");
            System.out.println("7. Ghi ra file");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: dskh.xuat(); break;
                case 2: dskh.them(); break;
                case 3: dskh.nhap(); break;
                case 4: dskh.sua(); break;
                case 5: dskh.xoa(); break;
                case 6: dskh.docFile(hoiFile("khachhang.txt")); break;
                case 7: dskh.ghiFile(hoiFile("khachhang.txt")); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 
    // ====== Hóa đơn ======
    static void menuHoaDon() {
        int chon;
        do {
            System.out.println("\n---------- HÓA ĐƠN ----------");
            System.out.println("1. Xem danh sách hóa đơn");
            System.out.println("2. Lập hóa đơn mới");
            System.out.println("3. Sửa hóa đơn");
            System.out.println("4. Xóa hóa đơn");
            System.out.println("5. Đọc từ file");
            System.out.println("6. Ghi ra file");
            System.out.println("7. Thanh toán hóa đơn chưa thanh toán");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: dshd.xuat(); break;
                case 2: {
                    String maKH = TienIch.nhapChuoi("Nhập mã khách hàng: ");
                    KhachHang kh = dskh.timkiem_khachhang(maKH);
                    if (kh == null) {
                        System.out.println("Không tìm thấy khách hàng! Hãy thêm khách hàng trước.");
                        break;
                    }
                    String maHD = TienIch.nhapChuoi("Nhập mã hóa đơn: ");
                    NhanVienBanHang nhanVienLap = layNguoiBanHang();
                    if (nhanVienLap == null) {
                        System.out.println("Cần tạo tài khoản bán hàng trước khi lập hóa đơn.");
                        break;
                    }
                    dshd.taoHoaDon(maHD, kh, nhanVienLap);
                    break;
                }
                case 3: dshd.sua(); break;
                case 4: dshd.xoa(); break;
                case 5: dshd.docFile(hoiFile("hoadon.txt"), dskh, dsmh); break;
                case 6: dshd.ghiFile(hoiFile("hoadon.txt")); break;
                case 7: thanhToanHoaDon(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 

    /** Áp dụng phân quyền tại cửa chọn chức năng; chỉ nhân viên đã xác thực mới vào các menu ghi dữ liệu. */
    static boolean duocPhep(NhanVien nv, String quyen) {
        return nv != null && ("ADMIN".equals(nv.getQuyen()) || quyen.equals(nv.getQuyen()));
    }
    static void dangNhap() {
        String ma = TienIch.nhapChuoi("Mã nhân viên: ");
        String mk = TienIch.nhapChuoi("Mật khẩu: ");
        NhanVien nv = dsnv.dangNhap(ma, mk);
        if (nv == null) { System.out.println("Sai mã hoặc mật khẩu."); return; }
        dangDangNhap = nv;
        System.out.println("Đăng nhập thành công: " + nv.getHoTen() + " | Quyền " + nv.getQuyen());
        if ("BAN_HANG".equals(nv.getQuyen())) nvBanHang = (NhanVienBanHang) nv;
        if ("ADMIN".equals(nv.getQuyen()) && nvBanHang == null) {
            for (int i = 0; i < dsnv.getSoLuong(); i++) {
                NhanVien n = dsnv.getDanhSach()[i];
                if (n instanceof NhanVienBanHang) { nvBanHang = (NhanVienBanHang) n; break; }
            }
        }
        try {
            if ("ADMIN".equals(nv.getQuyen())) menuAdmin();
            else if ("BAN_HANG".equals(nv.getQuyen())) menuBanHang();
            else if ("NHAP_KHO".equals(nv.getQuyen())) menuNhanVienKho();
            else System.out.println("Vai trò chưa được hỗ trợ.");
        } finally { dangDangNhap = null; }
    }
    static void menuAdmin() {
        int chon;
        do {
            System.out.println("\n" + ANSI + "=== ADMIN / TOÀN QUYỀN ===" + RESET);
            System.out.println("1. Kho   2. Phiếu nhập   3. Mặt hàng   4. Khách hàng");
            System.out.println("5. Hóa đơn   6. Nhà cung cấp   7. Nhân sự   8. Giỏ hàng");
            System.out.println("0. Đăng xuất");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: menuKho(); break;
                case 2: menuPhieuNhap(); break;
                case 3: menuMatHang(); break;
                case 4: menuKhachHang(); break;
                case 5: menuHoaDon(); break;
                case 6: menuNhaCungCap(); break;
                case 7: menuNhanSu(); break;
                case 8: menuGioHang(); break;
                case 0: System.out.println("Đã đăng xuất."); break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0 && dangDangNhap != null);
    }
    static void menuBanHang() {
        int chon;
        do {
            System.out.println("\n" + ANSI + "=== NHÂN VIÊN BÁN HÀNG ===" + RESET);
            System.out.println("1. Xem mặt hàng   2. Khách hàng   3. Hóa đơn   4. Giỏ hàng");
            System.out.println("0. Đăng xuất");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: dsmh.xuat(); break;
                case 2: menuKhachHang(); break;
                case 3: menuHoaDon(); break;
                case 4: menuGioHang(); break;
                case 0: System.out.println("Đã đăng xuất."); break;
                default: System.out.println("Không được phép truy cập chức năng này.");
            }
        } while (chon != 0);
    }
    static void menuNhanVienKho() {
        int chon;
        do {
            System.out.println("\n" + ANSI + "=== NHÂN VIÊN NHẬP KHO ===" + RESET);
            System.out.println("1. Kho   2. Phiếu nhập   3. Nhà cung cấp   4. Xem mặt hàng");
            System.out.println("0. Đăng xuất");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: menuKho(); break;
                case 2: menuPhieuNhap(); break;
                case 3: menuNhaCungCap(); break;
                case 4: dsmh.xuat(); break;
                case 0: System.out.println("Đã đăng xuất."); break;
                default: System.out.println("Không được phép truy cập chức năng này.");
            }
        } while (chon != 0);
    }
    static void menuNhaCungCap() {
        int chon;
        do {
            System.out.println("\n--- NHÀ CUNG CẤP (TV4) ---");
            System.out.println("1. Xem   2. Thêm   3. Sửa   4. Xóa   5. Đọc file   6. Ghi file   0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: dsncc.xuat(); break;
                case 2: dsncc.them(); break;
                case 3: dsncc.sua(); break;
                case 4: dsncc.xoa(); break;
                case 5: dsncc.docFile(hoiFile("nhacungcap.txt")); break;
                case 6: dsncc.ghiFile(hoiFile("nhacungcap.txt")); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0);
    }
    static void menuNhanSu() {
        if (!duocPhep(dangDangNhap, "ADMIN")) { System.out.println("Từ chối: chỉ ADMIN."); return; }
        int chon;
        do {
            System.out.println("\n--- QUẢN LÝ NHÂN SỰ (TV5) ---");
            System.out.println("1. Xem   2. Thêm   3. Sửa   4. Xóa   5. Tìm theo mã");
            System.out.println("6. Tổng lương cơ bản   7. Đọc file   8. Ghi file   0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: dsnv.xuat(); break;
                case 2: dsnv.them(); break;
                case 3: dsnv.sua(); break;
                case 4: {
                    String ma = TienIch.nhapChuoi("Mã cần xóa: ");
                    if (dangDangNhap.getMaNguoi().equalsIgnoreCase(ma))
                        System.out.println("Không thể xóa tài khoản đang đăng nhập.");
                    else System.out.println(dsnv.xoa(ma) ? "Đã xóa." : "Không tìm thấy hoặc không được xóa quản lý cuối cùng.");
                    break;
                }
                case 5: dsnv.timKiem(TienIch.nhapChuoi("Mã cần tìm: ")); break;
                case 6: System.out.printf("Tổng lương cơ bản: %.0f%n", dsnv.tongLuongCoBan()); break;
                case 7: {
                    // Đọc lại file chỉ từ giao diện Admin; không khóa mất phiên do thay danh sách.
                    String f = hoiFile(FILE_NV);
                    dsnv.docFile(f);
                    nvBanHang = null;
                    System.out.println("Đã đọc file; kết thúc phiên để xác thực lại với dữ liệu mới.");
                    dangDangNhap = null;
                    return;
                }
                case 8: dsnv.ghiFile(hoiFile(FILE_NV)); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0);
    }
    static NhanVienBanHang layNguoiBanHang() {
        if (dangDangNhap instanceof NhanVienBanHang)
            return (NhanVienBanHang) dangDangNhap;
        for (int i=0; i<dsnv.getSoLuong(); i++)
            if (dsnv.getDanhSach()[i] instanceof NhanVienBanHang)
                return (NhanVienBanHang) dsnv.getDanhSach()[i];
        return null;
    }
    static void thanhToanHoaDon() {
        String ma = TienIch.nhapChuoi("Mã hóa đơn thanh toán: ");
        HoaDon hd = dshd.timKiem(ma);
        if (hd == null) { System.out.println("Không tồn tại hóa đơn."); return; }
        if (hd.isDaThanhToan()) { System.out.println("Đã thanh toán trước đó."); return; }
        System.out.printf("Số tiền cần thanh toán: %.0f%n", hd.tinhThanhTien());
        int cach = TienIch.nhapSoNguyenDuong("1. Tiền mặt / 2. Thẻ / 0. Hủy: ");
        if (cach == 1) {
            double tien = nhapSoTienKhongAm("Khách đưa: ");
            hd.setHinhThucThanhToan(new ThanhToanTienMat(tien));
        } else if (cach == 2) {
            String maGD = TienIch.nhapChuoi("Mã giao dịch: ");
            String nganHang = TienIch.nhapChuoi("Ngân hàng: ");
            hd.setHinhThucThanhToan(new ThanhToanThe(maGD, nganHang));
        } else return;
        hd.thanhToan();
    }
    static double nhapSoTienKhongAm(String thongBao) {
        while (true) {
            String s = TienIch.nhapChuoi(thongBao);
            try { double x = Double.parseDouble(s); if (Double.isFinite(x) && x >= 0) return x; }
            catch (NumberFormatException ignored) { }
            System.out.println("Số tiền không hợp lệ.");
        }
    }
    /** Bổ sung lối vào giỏ hàng có sẵn của TV3; thanh toán vẫn do TV3 thực hiện. */
    static void menuGioHang() {
        NhanVienBanHang nhanVienLap = layNguoiBanHang();
        if (nhanVienLap == null) {
            System.out.println("Cần tạo ít nhất một tài khoản bán hàng trước khi lập hóa đơn.");
            return;
        }
        String maKH = TienIch.nhapChuoi("Mã khách hàng: ");
        KhachHang kh = dskh.timkiem_khachhang(maKH);
        if (kh == null) { System.out.println("Không tìm thấy khách hàng."); return; }
        GioHang gh = new GioHang(kh);
        int chon;
        do {
            System.out.println("\n--- GIỎ HÀNG (TV3) ---");
            System.out.println("1. Thêm hàng   2. Xem   3. Đổi số lượng   4. Xóa hàng   5. Tạo hóa đơn từ giỏ   0. Quay lại");
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch (chon) {
                case 1: {
                    MatHang mh = dsmh.timKiem_hang(TienIch.nhapChuoi("Mã mặt hàng: "));
                    int sl = TienIch.nhapSoNguyenDuong("Số lượng: ");
                    gh.themVaoGio(mh, sl); break;
                }
                case 2: gh.xemGioHang(); break;
                case 3: {
                    String ma = TienIch.nhapChuoi("Mã mặt hàng: ");
                    int sl = TienIch.nhapSoNguyenDuong("Số lượng mới: ");
                    gh.capNhatSoLuong(ma, sl); break;
                }
                case 4: gh.xoaKhoiGio(TienIch.nhapChuoi("Mã mặt hàng: ")); break;
                case 5: {
                    String ma = TienIch.nhapChuoi("Mã hóa đơn: ");
                    HoaDon hd = gh.xacNhanMua(dshd, ma, nhanVienLap);
                    if (hd != null) System.out.println("Đã tạo hóa đơn; cần thanh toán tại menu Hóa đơn, mục 7.");
                    break;
                }
                case 0: if (!gh.isRong()) System.out.println("Giỏ chưa chốt được bỏ khi rời menu."); break;
                default: System.out.println("Không hợp lệ.");
            }
        } while (chon != 0);
    }
    static void khoiTaoNhanVien() {
        java.nio.file.Path f = java.nio.file.Path.of(FILE_NV);
        if (java.nio.file.Files.exists(f)) dsnv.docFile(FILE_NV);
        else {
            System.out.println("Chưa có nhanvien.txt, khởi tạo 3 tài khoản DEMO.");
            dsnv.them(new QuanLy("ADMIN", "Quản trị demo", "0900000001", "admin123", "Cả ngày", 16000000));
            dsnv.them(new NhanVienBanHang("BH01", "Nhân viên bán", "0900000002", "bh123", "Sáng", 8000000));
            dsnv.them(new NhanVienNhapKho("KHO01", "Nhân viên kho", "0900000003", "kho123", "Chiều", 8500000));
            dsnv.ghiFile(FILE_NV);
        }
        if (dsnv.soLuongAdmin() == 0)
            System.out.println("CẢNH BÁO: Không có tài khoản ADMIN trong file nhân viên!");
    }
    public static void main(String[] args) {
        khoiTaoNhanVien();
        int chon;
        do {
            menuChinh();
            chon = TienIch.nhapSoNguyenDuong("Chọn chức năng: ");
            switch (chon) {
                case 1: dangNhap(); break;
                case 2: dsmh.xuat(); break;
                case 0: dsnv.ghiFile(FILE_NV); System.out.println("Tạm biệt!"); break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
}
