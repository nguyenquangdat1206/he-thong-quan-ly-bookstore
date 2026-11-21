import chinhanh.*;
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


public class Main {
    static final String FILE_NV = "data/nhanvien_cn.txt"; // file nhân viên mới: 8 cột, cột cuối là mã chi nhánh
    static final String ANSI = "\u001B[36m", RESET = "\u001B[0m";
    static Kho kho = new Kho();
    static DanhSachKhachHang dskh = new DanhSachKhachHang();
    static DanhSachNCC dsncc = new DanhSachNCC();
    static DanhSachPhieuNhap dspn = new DanhSachPhieuNhap(dsncc);
    static DanhSachNhanVien dsnv = new DanhSachNhanVien();
    static final String FILE_CN = "data/chinhanh.txt";
    // Mỗi chi nhánh có kệ (dsmh) và hóa đơn (dshd) riêng; tất cả dùng chung 1 kho.
    static DanhSachChiNhanh dsCN = new DanhSachChiNhanh(kho, dskh, dsnv);
    static ChiNhanh cnHienTai;
    static NhanVien dangDangNhap;
    static NhanVienBanHang nvBanHang;
    static boolean coTheGhiNhanVien = true;

    static String hoiFile(String macDinh) {
        System.out.print("Đường dẫn file (Enter hoặc . để dùng '" + macDinh + "'): ");
        String s = TienIch.sc.nextLine().trim();
        return (s.isEmpty() || s.equals(".")) ? macDinh : s;
    }
    static boolean coChiNhanh() {
        if (cnHienTai != null) return true;
        System.out.println("Chưa chọn chi nhánh. Admin hãy vào mục Chi nhánh để chọn hoặc tạo chi nhánh.");
        return false;
    }
    static ChiNhanh chonChiNhanh() {
        if (dsCN.getSoLuong() == 0) { System.out.println("Chưa có chi nhánh nào."); return null; }
        dsCN.xuat();
        while (true) {
            String ma = TienIch.nhapChuoi("Nhập mã chi nhánh (0 để bỏ qua): ");
            if (ma.equals("0")) return null;
            ChiNhanh c = dsCN.timTheoMa(ma);
            if (c != null) return c;
            System.out.println("Không tìm thấy chi nhánh.");
        }
    }
    static void menuChinh() {
        System.out.println("\n" + ANSI + "======== QUẢN LÝ NHÀ SÁCH ========" + RESET);
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
            System.out.println("11. Tự động nhập tất cả phiếu chờ vào kho và ghi danh sách đã nhập");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: dspn.xuat(); break;
                case 2: dspn.them(); break;
                case 3: dspn.nhap(); break;
                case 4: dspn.sua(); break;
                case 5: dspn.xoa(); break;
                case 6: dspn.nhapKho(kho); break;
                case 7: kho.xuat(); break;
                case 8: dspn.docFile(hoiFile("data/phieunhap_input.txt")); break;
                case 9: dspn.ghiFile(hoiFile("data/phieunhap_output.txt")); break;
                case 10: dspn.getDanhSachNCC().xuat(); break;
                case 11: dspn.tuDongNhapTatCaVaoKho(kho, hoiFile("data/phieunhap_danhan.txt")); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 
    // ====== Mặt hàng ======
    static void menuMatHang() {
        if (!coChiNhanh()) return;
        DanhSachMatHang dsmh = cnHienTai.getDsmh();
        int chon;
        do {
            System.out.println("\n---------- MẶT HÀNG - " + cnHienTai.getTen() + " ----------");
            System.out.println("1. Xem số lượng mặt hàng đang bày bán");
            System.out.println("2. Nhập 1 mặt hàng cụ thể từ kho lên");
            System.out.println("3. Nhập hàng loạt từ kho lên");
            System.out.println("4. Trả hàng từ kệ về kho");
            System.out.println("5. Xuất danh sách mặt hàng");
            System.out.println("6. Xóa mặt hàng");
            System.out.println("7. Sửa mặt hàng");
            System.out.println("8. Tìm kiếm theo mã");
            System.out.println("9. Bộ lọc nâng cao");
            System.out.println("10. Ghi mặt hàng ra file");
            System.out.println("11. Thống kê hàng trong kho chưa lên kệ bán");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: System.out.println("Số mặt hàng: " + dsmh.getSoMH()); break;
                case 2: dsmh.nhap(); break;
                case 3: {
                    dsmh.nhapHangLoat(kho);
                    break;
                }
                case 4: dsmh.traHangVeKho(kho); break;
                case 5: dsmh.xuat(); break;
                case 6: dsmh.xoa(); break;
                case 7: dsmh.sua(); break;
                case 8: dsmh.timKiem(); break;
                case 9: dsmh.timkiem_nangcao(); break;
                case 10: dsmh.ghiFile(hoiFile("data/" + cnHienTai.getMa() + "_mathang_output.txt")); break;
                case 11: dsmh.thongKeHangChuaLenKe(kho); break;
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
            System.out.println("8. Tìm kiếm khách hàng (Mã / Tên / SĐT)");
            System.out.println("9. Thống kê Top khách hàng chi tiêu cao nhất");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: dskh.xuat(); break;
                case 2: dskh.them(); break;
                case 3: dskh.nhap(); break;
                case 4: dskh.sua(); break;
                case 5: dskh.xoa(); break;
                case 6: dskh.docFile(hoiFile("data/khachhang.txt")); break;
                case 7: dskh.ghiFile(hoiFile("data/khachhang_output.txt")); break;
                case 8: dskh.timKiem(); break;
                case 9: if (coChiNhanh()) cnHienTai.getDshd().thongKeTopKhachHang(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 
    // ====== Hóa đơn ======
    static void menuHoaDon() {
        if (!coChiNhanh()) return;
        DanhSachMatHang dsmh = cnHienTai.getDsmh();
        DanhSachHoaDon dshd = cnHienTai.getDshd();
        int chon;
        do {
            System.out.println("\n---------- HÓA ĐƠN - " + cnHienTai.getTen() + " ----------");
            System.out.println("1. Xem danh sách hóa đơn");
            System.out.println("2. Lập hóa đơn mới (qua giỏ hàng)");
            System.out.println("3. Sửa hóa đơn");
            System.out.println("4. Xóa hóa đơn");
            System.out.println("5. Đọc từ file");
            System.out.println("6. Ghi ra file");
            System.out.println("7. Thanh toán hóa đơn chưa thanh toán");
            System.out.println("8. Tra cứu & lọc hóa đơn");
            System.out.println("9. Thống kê doanh thu & mặt hàng bán chạy");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: dshd.xuat(); break;
                case 2: menuGioHang(); break; // hóa đơn chỉ được lập từ giỏ hàng
                case 3: dshd.sua(); break;
                case 4: dshd.xoa(); break;
                case 5: dshd.docFile(hoiFile("data/" + cnHienTai.getMa() + "_hoadon.txt"), dskh, dsmh); break;
                case 6: dshd.ghiFile(hoiFile("data/" + cnHienTai.getMa() + "_hoadon_output.txt")); break;
                case 7: thanhToanHoaDon(); break;
                case 8: dshd.traCuuVaLoc(); break;
                case 9: dshd.thongKeDoanhThu(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
 

    static boolean duocPhep(NhanVien nv, String quyen) {
        return nv != null && ("ADMIN".equals(nv.getQuyen()) || quyen.equals(nv.getQuyen()));
    }
    static void dangNhap() {
        String ma = TienIch.nhapChuoi("Mã nhân viên: ");
        String mk = TienIch.nhapChuoi("Mật khẩu: ");
        NhanVien nv = dsnv.dangNhap(ma, mk);
        if (nv == null) { System.out.println("Sai mã hoặc mật khẩu."); return; }
        cnHienTai = null;
        if (nv.thuocChiNhanh()) {   // quản lý chi nhánh và nhân viên bán hàng bị khóa vào chi nhánh của mình
            cnHienTai = dsCN.timTheoMa(nv.getMaChiNhanh());
            if (cnHienTai == null) {
                System.out.println("Tài khoản chưa được gán chi nhánh hợp lệ ('" + nv.getMaChiNhanh()
                    + "'). Hãy nhờ Admin gán chi nhánh.");
                return;
            }
        }
        dangDangNhap = nv;
        System.out.println("Đăng nhập thành công: " + nv.getHoTen() + " | Quyền " + nv.getQuyen());
        System.out.println("Nơi làm việc: " + dsCN.moTaChiNhanhCua(nv));
        if ("BAN_HANG".equals(nv.getQuyen())) nvBanHang = (NhanVienBanHang) nv;
        if ("ADMIN".equals(nv.getQuyen()) && nvBanHang == null) {
            for (int i = 0; i < dsnv.getSoLuong(); i++) {
                NhanVien n = dsnv.getDanhSach()[i];
                if (n instanceof NhanVienBanHang) { nvBanHang = (NhanVienBanHang) n; break; }
            }
        }
        try {
            if ("ADMIN".equals(nv.getQuyen())) { cnHienTai = chonChiNhanh(); menuAdmin(); }
            else if ("QUAN_LY".equals(nv.getQuyen())) menuQuanLy();
            else if ("BAN_HANG".equals(nv.getQuyen())) menuBanHang();
            else if ("NHAP_KHO".equals(nv.getQuyen())) menuNhanVienKho();
            else System.out.println("Vai trò chưa được hỗ trợ.");
        } finally { dangDangNhap = null; cnHienTai = null; }
    }
    static void menuAdmin() {
        int chon;
        do {
            System.out.println("\n" + ANSI + "=== ADMIN / TOÀN QUYỀN ===" + RESET);
            System.out.println("Chi nhánh đang làm việc: " + (cnHienTai == null ? "(chưa chọn)" : cnHienTai.getMa() + " - " + cnHienTai.getTen()));
            System.out.println("1. Kho   2. Phiếu nhập   3. Mặt hàng   4. Khách hàng");
            System.out.println("5. Hóa đơn   6. Nhà cung cấp   7. Nhân sự   8. Giỏ hàng   9. Chi nhánh");
            System.out.println("0. Đăng xuất");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: menuKho(); break;
                case 2: menuPhieuNhap(); break;
                case 3: menuMatHang(); break;
                case 4: menuKhachHang(); break;
                case 5: menuHoaDon(); break;
                case 6: menuNhaCungCap(); break;
                case 7: menuNhanSu(); break;
                case 8: menuGioHang(); break;
                case 9: menuChiNhanh(); break;
                case 0: System.out.println("Đã đăng xuất."); break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0 && dangDangNhap != null);
    }
    static void menuChiNhanh() {
        int chon;
        do {
            System.out.println("\n--- CHI NHÁNH ---");
            System.out.println("1. Xem   2. Thêm   3. Xóa   4. Đổi chi nhánh đang làm việc");
            System.out.println("5. Đọc file   6. Ghi file   7. Sửa địa chỉ   8. Xem nhân viên theo chi nhánh   0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: dsCN.xuat(); break;
                case 2: {
                    String ma = TienIch.nhapChuoi("Mã chi nhánh: ");
                    String ten = TienIch.nhapChuoi("Tên chi nhánh: ");
                    String diaChi = TienIch.nhapChuoi("Địa chỉ chi nhánh: ");
                    System.out.println(dsCN.them(ma, ten, diaChi) ? "Đã thêm chi nhánh." : "Mã trùng hoặc không hợp lệ (không dùng dấu ';').");
                    break;
                }
                case 3: {
                    String ma = TienIch.nhapChuoi("Mã chi nhánh cần xóa: ");
                    boolean laHienTai = dsCN.timTheoMa(ma) == cnHienTai;
                    if (dsCN.xoa(ma)) { if (laHienTai) cnHienTai = null; System.out.println("Đã xóa chi nhánh."); }
                    else System.out.println("Không tìm thấy, hoặc chi nhánh còn hàng trên kệ / hóa đơn / nhân viên.");
                    break;
                }
                case 4: {
                    ChiNhanh c = chonChiNhanh();
                    if (c != null) { cnHienTai = c; System.out.println("Đang làm việc tại: " + c.getTen()); }
                    break;
                }
                case 5: dsCN.docFile(hoiFile(FILE_CN)); break;
                case 6: dsCN.ghiFile(hoiFile("data/chinhanh_output.txt")); break;
                case 7: {
                    ChiNhanh c = dsCN.timTheoMa(TienIch.nhapChuoi("Mã chi nhánh cần sửa địa chỉ: "));
                    if (c == null) { System.out.println("Không tìm thấy chi nhánh."); break; }
                    String dc = TienIch.nhapChuoi("Địa chỉ mới: ");
                    if (dc.contains(";")) System.out.println("Địa chỉ không được chứa dấu ';'.");
                    else { c.setDiaChi(dc); System.out.println("Đã cập nhật địa chỉ."); }
                    break;
                }
                case 8: {
                    ChiNhanh c = chonChiNhanh();
                    if (c != null) dsCN.xuatNhanVien(c);
                    break;
                }
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0);
    }
    static void menuQuanLy() {
        int chon;
        do {
            System.out.println("\n" + ANSI + "=== QUẢN LÝ CHI NHÁNH: " + cnHienTai.getTen() + " | " + cnHienTai.getDiaChi() + " ===" + RESET);
            System.out.println("1. Mặt hàng   2. Khách hàng   3. Hóa đơn   4. Giỏ hàng");
            System.out.println("5. Xem kho (chỉ xem)   6. Nhân viên chi nhánh");
            System.out.println("0. Đăng xuất");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: menuMatHang(); break;
                case 2: menuKhachHang(); break;
                case 3: menuHoaDon(); break;
                case 4: menuGioHang(); break;
                case 5: menuXemKho(); break;
                case 6: dsCN.xuatNhanVien(cnHienTai); break;
                case 0: System.out.println("Đã đăng xuất."); break;
                default: System.out.println("Không được phép truy cập chức năng này.");
            }
        } while (chon != 0);
    }
    static void menuXemKho() {
        int chon;
        do {
            System.out.println("\n--- KHO CHUNG (CHỈ XEM) ---");
            System.out.println("1. Xem hàng tồn   2. Tra cứu theo mã   3. Kiểm tra sức chứa   0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: kho.xuat(); break;
                case 2: kho.timHangTon(); break;
                case 3: kho.kiemTraSucChua(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0);
    }
    static void menuBanHang() {
        int chon;
        do {
            System.out.println("\n" + ANSI + "=== NHÂN VIÊN BÁN HÀNG - " + cnHienTai.getTen() + " | " + cnHienTai.getDiaChi() + " ===" + RESET);
            System.out.println("1. Xem mặt hàng   2. Khách hàng   3. Hóa đơn   4. Giỏ hàng");
            System.out.println("0. Đăng xuất");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: cnHienTai.getDsmh().xuat(); break;
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
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: menuKho(); break;
                case 2: menuPhieuNhap(); break;
                case 3: menuNhaCungCap(); break;
                case 4: {   // nhân viên kho không thuộc chi nhánh: chọn chi nhánh để xem kệ
                    ChiNhanh c = chonChiNhanh();
                    if (c != null) c.getDsmh().xuat();
                    break;
                }
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
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: dsncc.xuat(); break;
                case 2: dsncc.them(); break;
                case 3: dsncc.sua(); break;
                case 4: dsncc.xoa(); break;
                case 5: dsncc.docFile(hoiFile("data/nhacungcap.txt")); break;
                case 6: dsncc.ghiFile(hoiFile("data/nhacungcap_output.txt")); break;
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
            System.out.println("6. Tổng lương cơ bản   7. Đọc file   8. Ghi file   9. Xem nhân viên theo chi nhánh   0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
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
                    // Sau thao tác import, tránh tự ghi đè file nguồn nếu còn sai định dạng.
                    coTheGhiNhanVien = false;
                    nvBanHang = null;
                    System.out.println("Đã đọc file; kết thúc phiên để xác thực lại với dữ liệu mới.");
                    dangDangNhap = null;
                    return;
                }
                case 8: {
                    String duongDan = hoiFile(FILE_NV);
                    boolean laFileGoc = java.nio.file.Path.of(duongDan).toAbsolutePath().normalize()
                        .equals(java.nio.file.Path.of(FILE_NV).toAbsolutePath().normalize());
                    if (laFileGoc && !coTheGhiNhanVien)
                        System.out.println("Từ chối ghi đè file nhân viên gốc định dạng cũ; hãy chọn file đầu ra khác.");
                    else dsnv.ghiFile(duongDan);
                    break;
                }
                case 9: dsCN.xuatNhanVienTatCaChiNhanh(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (chon != 0);
    }

    static NhanVienBanHang layNguoiBanHang() {
        if (dangDangNhap instanceof NhanVienBanHang)
            return (NhanVienBanHang) dangDangNhap;
        if (cnHienTai == null) return null;
        for (int i=0; i<dsnv.getSoLuong(); i++) {
            NhanVien n = dsnv.getDanhSach()[i];
            if (n instanceof NhanVienBanHang && cnHienTai.getMa().equalsIgnoreCase(n.getMaChiNhanh()))
                return (NhanVienBanHang) n;
        }
        return null;
    }
    static void thanhToanHoaDon() {
        if (!coChiNhanh()) return;
        DanhSachHoaDon dshd = cnHienTai.getDshd();
        String ma = TienIch.nhapChuoi("Mã hóa đơn thanh toán: ");
        HoaDon hd = dshd.timKiem(ma);
        if (hd == null) { System.out.println("Không tồn tại hóa đơn."); return; }
        if (hd.isDaThanhToan()) { System.out.println("Đã thanh toán trước đó."); return; }
        System.out.printf("Số tiền cần thanh toán: %.0f%n", hd.tinhThanhTien());
        int cach = TienIch.nhapSoNguyenKhongAm("1. Tiền mặt / 2. Thẻ / 0. Hủy: ");
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

    static void menuGioHang() {
        if (!coChiNhanh()) return;
        DanhSachMatHang dsmh = cnHienTai.getDsmh();
        DanhSachHoaDon dshd = cnHienTai.getDshd();
        NhanVienBanHang nhanVienLap = layNguoiBanHang();
        if (nhanVienLap == null) {
            System.out.println("Chi nhánh này cần ít nhất một tài khoản bán hàng trước khi lập hóa đơn.");
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
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: {
                    MatHang mh = dsmh.timKiem_hang(TienIch.nhapChuoi("Mã mặt hàng: "));
                    if (mh == null) {
                        System.out.println("Không tìm thấy mặt hàng trên kệ.");
                        break;
                    }
                    int sl = TienIch.nhapSoNguyenDuong("Số lượng: ");
                    gh.themVaoGio(mh, sl); break;
                }
                case 2: gh.xemGioHang(); break;
                case 3: {
                    String ma = TienIch.nhapChuoi("Mã mặt hàng: ");
                    int sl = TienIch.nhapSoNguyenKhongAm("Số lượng mới: ");
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
    static void khoiTaoChiNhanh() {
        try { java.nio.file.Files.createDirectories(java.nio.file.Path.of("data")); }
        catch (java.io.IOException e) { System.out.println("Không thể tạo thư mục data: " + e.getMessage()); }
        if (java.nio.file.Files.exists(java.nio.file.Path.of(FILE_CN))) dsCN.docFile(FILE_CN);
        if (dsCN.getSoLuong() == 0) {
            dsCN.them("CN01", "Chi nhánh 1", "Chưa cập nhật địa chỉ");
            dsCN.ghiFile(FILE_CN);
            System.out.println("Chưa có chi nhánh, đã tạo chi nhánh mặc định CN01.");
        }
    }

    static void ganChiNhanhMacDinh() {
        if (dsCN.getSoLuong() == 0) return;
        String macDinh = dsCN.get(0).getMa();
        int n = 0;
        for (int i = 0; i < dsnv.getSoLuong(); i++) {
            NhanVien nv = dsnv.getDanhSach()[i];
            if (nv.thuocChiNhanh() && nv.getMaChiNhanh().isEmpty()) { nv.setMaChiNhanh(macDinh); n++; }
        }
        if (n > 0) System.out.println("Đã gán " + n + " nhân viên chưa có chi nhánh vào " + macDinh
            + ". Admin có thể đổi trong mục Nhân sự > Sửa.");
    }
    static void khoiTaoNhanVien() {
        try { java.nio.file.Files.createDirectories(java.nio.file.Path.of("data")); }
        catch (java.io.IOException e) { System.out.println("Không thể tạo thư mục data: " + e.getMessage()); }
        java.nio.file.Path f = java.nio.file.Path.of(FILE_NV);
        if (java.nio.file.Files.exists(f)) {
            try {
                long soDong;
                try (java.util.stream.Stream<String> lines = java.nio.file.Files.lines(
                        f, java.nio.charset.StandardCharsets.UTF_8)) {
                    soDong = lines.filter(line -> !line.isBlank()).count();
                }
                dsnv.docFile(FILE_NV);
                if (soDong != dsnv.getSoLuong() || dsnv.soLuongAdmin() == 0 || dsnv.laDuLieuDinhDangCu()) {
                    coTheGhiNhanVien = false;
                    System.out.println("CẢNH BÁO: " + FILE_NV + " là định dạng cũ hoặc chứa dòng lỗi; "
                        + "không tự ghi đè khi thoát. Hãy kiểm tra file trước.");
                }
            } catch (java.io.IOException e) {
                coTheGhiNhanVien = false;
                System.out.println("Không kiểm tra được file nhân viên; đã khóa tự lưu để bảo vệ dữ liệu: "
                    + e.getMessage());
            }
        } else {
            System.out.println("Chưa có " + FILE_NV + ", khởi tạo tài khoản DEMO.");
            dsnv.them(new QuanLy("ADMIN", "Quản trị demo", "0900000001", "admin123", "Cả ngày", 16000000));
            dsnv.them(new QuanLyChiNhanh("QL01", "Quản lý CN01", "0900000004", "ql123", "Cả ngày", 12000000, "CN01"));
            dsnv.them(new NhanVienBanHang("BH01", "Nhân viên bán", "0900000002", "bh123", "Sáng", 8000000, "CN01"));
            dsnv.them(new NhanVienNhapKho("KHO01", "Nhân viên kho", "0900000003", "kho123", "Chiều", 8500000));
            dsnv.ghiFile(FILE_NV);
        }
        if (dsnv.soLuongAdmin() == 0)
            System.out.println("CẢNH BÁO: Không có tài khoản ADMIN trong file nhân viên!");
    }
    public static void main(String[] args) {
        TienIch.cauHinhUnicodeConsole();
        khoiTaoChiNhanh();
        dsnv.setChuanHoaChiNhanh(m -> { ChiNhanh c = dsCN.timTheoMa(m); return c == null ? null : c.getMa(); });
        khoiTaoNhanVien();
        ganChiNhanhMacDinh();
        int chon;
        do {
            menuChinh();
            chon = TienIch.nhapSoNguyenKhongAm("Chọn chức năng: ");
            switch (chon) {
                case 1: dangNhap(); break;
                case 2: {
                    ChiNhanh c = chonChiNhanh();
                    if (c != null) c.getDsmh().xuat();
                    break;
                }
                case 0:
                    dsCN.ghiFile(FILE_CN);
                    if (coTheGhiNhanVien) dsnv.ghiFile(FILE_NV);
                    else System.out.println("Bỏ qua lưu nhân viên vì dữ liệu gốc chưa tương thích.");
                    System.out.println("Tạm biệt!"); break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
}
