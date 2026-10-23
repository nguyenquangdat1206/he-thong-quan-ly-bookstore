import java.io.File;
import mathang.DanhSachMatHang;
import kho.Kho;
import phieunhap.DanhSachPhieuNhap;
import hoadon.DanhSachHoaDon;
import hoadon.HoaDon;
import khachhang.DanhSachKhachHang;
import khachhang.KhachHang;
import nhanvien.*;
import thanhtoan.IThanhToan;
import thanhtoan.ThanhToanThe;
import tienich.TienIch;

public class Main {
    // 1. KHỞI TẠO CÁC ĐỐI TƯỢNG QUẢN LÝ TOÀN CỤC
    static Kho khoHang = new Kho();
    static DanhSachPhieuNhap dsPhieuNhap = new DanhSachPhieuNhap();
    static DanhSachMatHang dsHangHoa = new DanhSachMatHang();
    static DanhSachKhachHang dsKhachHang = new DanhSachKhachHang();
    static DanhSachHoaDon dsHoaDon = new DanhSachHoaDon(dsHangHoa, dsKhachHang);
    static DanhSachNhanVien dsNhanVien = new DanhSachNhanVien();

    // 2. BIẾN LƯU PHIÊN ĐĂNG NHẬP
    static NhanVien userHienTai = null;

    // Đường dẫn file mặc định
    static final String FILE_KHO = timDuongDan("kho_input.txt");
    static final String FILE_MAT_HANG = timDuongDan("mathang.txt");
    static final String FILE_PHIEU_NHAP = timDuongDan("phieunhap_input.txt");
    static final String FILE_KHACH_HANG = timDuongDan("khachhang.txt");
    static final String FILE_HOA_DON = timDuongDan("hoadon.txt");
    static final String FILE_NHAN_VIEN = timDuongDan("nhanvien.txt");

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   CHƯƠNG TRÌNH QUẢN LÝ CỬA HÀNG VĂN PHÒNG PHẨM");
        System.out.println("==================================================");

        // ================= NẠP DỮ LIỆU TỰ ĐỘNG =================
        System.out.println("\n[HỆ THỐNG] 1. Đang tải dữ liệu Kho từ " + FILE_KHO + " & " + FILE_MAT_HANG + "...");
        khoHang.docFile(FILE_KHO);
        if (new File(FILE_MAT_HANG).exists()) {
            khoHang.docFile(FILE_MAT_HANG);
        }

        System.out.println("\n[HỆ THỐNG] 2. Đang xuất hàng loạt (20%) từ Kho lên Kệ cửa hàng...");
        dsHangHoa.nhapHangLoat(khoHang);

        System.out.println("\n[HỆ THỐNG] 3. Đang tải dữ liệu Phiếu nhập từ " + FILE_PHIEU_NHAP + "...");
        dsPhieuNhap.docFile(FILE_PHIEU_NHAP);

        System.out.println("\n[HỆ THỐNG] 4. Đang tải dữ liệu Khách hàng từ " + FILE_KHACH_HANG + "...");
        dsKhachHang.docFile(FILE_KHACH_HANG);
        damBaoKhachHangMacDinh(); // Đảm bảo có KH00, KH01 để đọc hoadon.txt không bị lỗi

        System.out.println("\n[HỆ THỐNG] 5. Đang tải dữ liệu Hóa đơn từ " + FILE_HOA_DON + "...");
        dsHoaDon.docFile(FILE_HOA_DON);

        System.out.println("==================================================\n");

        // KHỞI TẠO TÀI KHOẢN NHÂN VIÊN MẪU
        khoiTaoNhanVienMau();

        // ================= VÒNG LẶP HỆ THỐNG & ĐĂNG NHẬP =================
        boolean chayChuongTrinh = true;
        while (chayChuongTrinh) {
            while (userHienTai == null) {
                System.out.println("\n--- VUI LÒNG ĐĂNG NHẬP (Gõ 'exit' ở tài khoản để tắt máy) ---");
                String tk = TienIch.nhapChuoi("Tài khoản (Mã NV): ");
                if (tk.equalsIgnoreCase("exit")) {
                    System.out.println("Đã đóng chương trình.");
                    return;
                }
                String mk = TienIch.nhapChuoi("Mật khẩu: ");

                userHienTai = dsNhanVien.dangNhap(tk, mk);

                if (userHienTai == null) {
                    System.out.println("[LỖI] Sai tài khoản hoặc mật khẩu! (Gợi ý: admin/123, bh01/123, kho01/123)");
                }
            }

            System.out.println("\n>>> ĐĂNG NHẬP THÀNH CÔNG!");
            System.out.println("Xin chào: " + userHienTai.getHoTen() + " | Quyền: " + userHienTai.getQuyen());
            dungManHinh();

            // ================= MENU CHÍNH ĐỘNG THEO ROLE =================
            boolean trongPhienLamViec = true;
            while (trongPhienLamViec) {
                String quyen = userHienTai.getQuyen();
                int stt = 1;

                System.out.println("\n=================== MENU CHÍNH (" + quyen + ") ===================");
                System.out.println((stt++) + ". Quản lý Mặt Hàng (Kệ cửa hàng)");

                if (quyen.equals("ADMIN") || quyen.equals("NHAP_KHO")) {
                    System.out.println((stt++) + ". Quản lý Kho Hàng");
                    System.out.println((stt++) + ". Quản lý Phiếu Nhập Kho");
                }
                if (quyen.equals("ADMIN") || quyen.equals("BAN_HANG")) {
                    System.out.println((stt++) + ". Quản lý Bán Hàng & Hóa Đơn");
                    System.out.println((stt++) + ". Quản lý Khách Hàng");
                }
                if (quyen.equals("ADMIN")) {
                    System.out.println((stt++) + ". Quản lý Nhân Sự");
                }

                int nutDangXuat = stt++;
                int nutThoatHan = stt;
                System.out.println(nutDangXuat + ". Lưu dữ liệu & Đăng xuất (Đổi tài khoản)");
                System.out.println(nutThoatHan + ". Lưu dữ liệu & Thoát chương trình");
                System.out.println("==============================================================");

                int chon = nhapSoTuKhoang("Nhập lựa chọn (1 - " + nutThoatHan + "): ", 1, nutThoatHan);

                if (chon == 1) {
                    menuMatHang();
                } else if (chon == nutDangXuat) {
                    luuToanBoDuLieu();
                    System.out.println("Đã đăng xuất tài khoản: " + userHienTai.getMaNguoi());
                    userHienTai = null;
                    trongPhienLamViec = false;
                } else if (chon == nutThoatHan) {
                    luuToanBoDuLieu();
                    System.out.println("Tạm biệt " + userHienTai.getHoTen() + "! Hẹn gặp lại.");
                    userHienTai = null;
                    trongPhienLamViec = false;
                    chayChuongTrinh = false;
                } else {
                    if (quyen.equals("ADMIN")) {
                        switch (chon) {
                            case 2: menuKho(); break;
                            case 3: menuPhieuNhap(); break;
                            case 4: menuHoaDon(); break;
                            case 5: menuKhachHang(); break;
                            case 6: menuNhanSu(); break;
                        }
                    } else if (quyen.equals("BAN_HANG")) {
                        switch (chon) {
                            case 2: menuHoaDon(); break;
                            case 3: menuKhachHang(); break;
                        }
                    } else if (quyen.equals("NHAP_KHO")) {
                        switch (chon) {
                            case 2: menuKho(); break;
                            case 3: menuPhieuNhap(); break;
                        }
                    }
                }
            }
        }
    }

    // ================= SUB-MENU 1: QUẢN LÝ MẶT HÀNG TRÊN KỆ =================
    private static void menuMatHang() {
        boolean tiepTuc = true;
        String quyen = userHienTai.getQuyen();

        while (tiepTuc) {
            System.out.println("\n========== MODULE: QUẢN LÝ MẶT HÀNG (KỆ BÁN) ==========");
            System.out.println("1. Xem số lượng loại mặt hàng đang bày bán");
            System.out.println("2. Xuất danh sách mặt hàng trên kệ");
            System.out.println("3. Tìm kiếm mặt hàng theo mã");
            System.out.println("4. Bộ lọc tìm kiếm nâng cao (Theo danh mục)");
            if (quyen.equals("ADMIN") || quyen.equals("NHAP_KHO")) {
                System.out.println("5. Nhập hàng loạt mặt hàng từ Kho lên kệ (20%)");
                System.out.println("6. Nhập một mặt hàng cụ thể từ Kho lên kệ");
            }
            if (quyen.equals("ADMIN")) {
                System.out.println("7. Thêm mới mặt hàng trực tiếp lên kệ");
                System.out.println("8. Sửa thông tin mặt hàng trên kệ");
                System.out.println("9. Xóa mặt hàng khỏi kệ");
            }
            System.out.println("0. Trở về Menu Chính");
            System.out.println("=======================================================");

            int chon = TienIch.nhapSoNguyenDuong("Nhập lựa chọn: ");

            switch (chon) {
                case 1:
                    System.out.println("Số lượng đầu mục mặt hàng đang bày bán trên kệ: " + dsHangHoa.getSoMH());
                    dungManHinh();
                    break;
                case 2:
                    dsHangHoa.xuat();
                    dungManHinh();
                    break;
                case 3:
                    dsHangHoa.timkiem();
                    dungManHinh();
                    break;
                case 4:
                    dsHangHoa.timkiem_nangcao();
                    dungManHinh();
                    break;
                case 5:
                    if (quyen.equals("BAN_HANG")) {
                        System.out.println("[CẢNH BÁO] Nhân viên Bán hàng không có quyền rút hàng từ Kho!");
                    } else {
                        dsHangHoa.nhapHangLoat(khoHang);
                    }
                    dungManHinh();
                    break;
                case 6:
                    if (quyen.equals("BAN_HANG")) {
                        System.out.println("[CẢNH BÁO] Nhân viên Bán hàng không có quyền rút hàng từ Kho!");
                    } else {
                        String maCanLay = TienIch.nhapChuoi("Nhập mã hàng cần lấy từ Kho: ");
                        int soLuongLay = TienIch.nhapSoNguyenDuong("Nhập số lượng cần lấy: ");
                        dsHangHoa.nhapTuKho(khoHang, maCanLay, soLuongLay);
                    }
                    dungManHinh();
                    break;
                case 7:
                    if (!quyen.equals("ADMIN")) {
                        System.out.println("[CẢNH BÁO] Chỉ Quản lý (ADMIN) mới có quyền này!");
                    } else {
                        dsHangHoa.them();
                    }
                    dungManHinh();
                    break;
                case 8:
                    if (!quyen.equals("ADMIN")) {
                        System.out.println("[CẢNH BÁO] Chỉ Quản lý (ADMIN) mới có quyền này!");
                    } else {
                        dsHangHoa.sua();
                    }
                    dungManHinh();
                    break;
                case 9:
                    if (!quyen.equals("ADMIN")) {
                        System.out.println("[CẢNH BÁO] Chỉ Quản lý (ADMIN) mới có quyền này!");
                    } else {
                        dsHangHoa.xoa();
                    }
                    dungManHinh();
                    break;
                case 0:
                    tiepTuc = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    break;
            }
        }
    }

    // ================= SUB-MENU 2: QUẢN LÝ KHO HÀNG =================
    private static void menuKho() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n========== MODULE: QUẢN LÝ KHO ==========");
            System.out.println("1. Xem hàng tồn trong kho");
            System.out.println("2. Thêm 1 mặt hàng vào kho");
            System.out.println("3. Nhập nhiều mặt hàng vào kho");
            System.out.println("4. Sửa số lượng tồn");
            System.out.println("5. Xóa mặt hàng khỏi kho");
            System.out.println("6. Tra cứu chi tiết theo mã");
            System.out.println("7. Kiểm tra sức chứa của kho");
            System.out.println("8. Đọc kho từ file");
            System.out.println("9. Ghi kho ra file");
            System.out.println("0. Trở về Menu Chính");
            System.out.println("=========================================");

            int chon = TienIch.nhapSoNguyenDuong("Chọn chức năng: ");
            switch (chon) {
                case 1: khoHang.xuat(); dungManHinh(); break;
                case 2: khoHang.them(); dungManHinh(); break;
                case 3: khoHang.nhap(); dungManHinh(); break;
                case 4: khoHang.sua(); dungManHinh(); break;
                case 5: khoHang.xoa(); dungManHinh(); break;
                case 6: khoHang.timHangTon(); dungManHinh(); break;
                case 7: khoHang.kiemTraSucChua(); dungManHinh(); break;
                case 8: khoHang.docFile(hoiDuongDan(FILE_KHO)); dungManHinh(); break;
                case 9: khoHang.ghiFile(hoiDuongDan("kho_output.txt")); dungManHinh(); break;
                case 0: tiepTuc = false; break;
                default: System.out.println("Lựa chọn không hợp lệ!"); break;
            }
        }
    }

    // ================= SUB-MENU 3: QUẢN LÝ PHIẾU NHẬP =================
    private static void menuPhieuNhap() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n========== MODULE: QUẢN LÝ PHIẾU NHẬP ==========");
            System.out.println("1. Xem danh sách phiếu nhập");
            System.out.println("2. Thêm 1 phiếu nhập");
            System.out.println("3. Thêm nhiều phiếu nhập");
            System.out.println("4. Sửa phiếu nhập");
            System.out.println("5. Xóa phiếu nhập");
            System.out.println("6. Duyệt & Nhập hàng từ phiếu vào Kho");
            System.out.println("7. Xem hàng tồn trong Kho");
            System.out.println("8. Đọc phiếu nhập từ file");
            System.out.println("9. Ghi phiếu nhập ra file");
            System.out.println("0. Trở về Menu Chính");
            System.out.println("================================================");

            int chon = TienIch.nhapSoNguyenDuong("Chọn chức năng: ");
            switch (chon) {
                case 1: dsPhieuNhap.xuat(); dungManHinh(); break;
                case 2: dsPhieuNhap.them(); dungManHinh(); break;
                case 3: dsPhieuNhap.nhap(); dungManHinh(); break;
                case 4: dsPhieuNhap.sua(); dungManHinh(); break;
                case 5: dsPhieuNhap.xoa(); dungManHinh(); break;
                case 6: dsPhieuNhap.nhapKho(khoHang); dungManHinh(); break;
                case 7: khoHang.xuat(); dungManHinh(); break;
                case 8: dsPhieuNhap.docFile(hoiDuongDan(FILE_PHIEU_NHAP)); dungManHinh(); break;
                case 9: dsPhieuNhap.ghiFile(hoiDuongDan("phieunhap_output.txt")); dungManHinh(); break;
                case 0: tiepTuc = false; break;
                default: System.out.println("Lựa chọn không hợp lệ!"); break;
            }
        }
    }

    // ================= SUB-MENU 4: BÁN HÀNG & HÓA ĐƠN =================
    private static void menuHoaDon() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n========== MODULE: BÁN HÀNG & HÓA ĐƠN ==========");
            System.out.println("1. Lập Hóa Đơn Bán Hàng mới");
            System.out.println("2. Thanh toán Hóa Đơn");
            System.out.println("3. Sửa Hóa Đơn (Chưa thanh toán)");
            System.out.println("4. Xóa Hóa Đơn (Chưa thanh toán)");
            System.out.println("5. Tìm kiếm Hóa Đơn theo mã");
            System.out.println("6. Xem toàn bộ danh sách Hóa Đơn");
            System.out.println("7. Thống kê tổng doanh thu");
            System.out.println("0. Trở về Menu Chính");
            System.out.println("================================================");

            int chon = TienIch.nhapSoNguyenDuong("Chọn chức năng: ");
            switch (chon) {
                case 1:
                    lapHoaDonMoi();
                    dungManHinh();
                    break;
                case 2:
                    String maHDThanhToan = TienIch.nhapChuoi("Nhập mã hóa đơn cần thanh toán: ");
                    HoaDon hdTT = dsHoaDon.timKiem(maHDThanhToan);
                    if (hdTT == null) {
                        System.out.println("Không tìm thấy hóa đơn có mã: " + maHDThanhToan);
                    } else {
                        thucHienThanhToan(hdTT);
                    }
                    dungManHinh();
                    break;
                case 3:
                    dsHoaDon.sua();
                    dungManHinh();
                    break;
                case 4:
                    dsHoaDon.xoa();
                    dungManHinh();
                    break;
                case 5:
                    String maTim = TienIch.nhapChuoi("Nhập mã hóa đơn cần tìm: ");
                    HoaDon hdTim = dsHoaDon.timKiem(maTim);
                    if (hdTim != null) {
                        hdTim.xuat();
                    } else {
                        System.out.println("Không tìm thấy hóa đơn có mã: " + maTim);
                    }
                    dungManHinh();
                    break;
                case 6:
                    dsHoaDon.xuat();
                    dungManHinh();
                    break;
                case 7:
                    thongKeDoanhThu();
                    dungManHinh();
                    break;
                case 0:
                    tiepTuc = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    break;
            }
        }
    }

    private static void lapHoaDonMoi() {
        String maHD;
        while (true) {
            maHD = TienIch.nhapChuoi("Nhập mã hóa đơn mới (VD: HD03): ");
            if (dsHoaDon.timKiem(maHD) != null) {
                System.out.println("Mã hóa đơn đã tồn tại! Vui lòng nhập mã khác.");
            } else {
                break;
            }
        }

        String maKH = TienIch.nhapChuoi("Nhập mã khách hàng mua (Nhập 'KH00' nếu là khách lẻ): ");
        KhachHang kh = dsKhachHang.timkiem_khachhang(maKH);
        if (kh == null) {
            if (maKH.equalsIgnoreCase("KH00")) {
                kh = new KhachHang("KH00", "Khách Vãng Lai", "0000000000");
                dsKhachHang.
                        timkiem_khachhang("KH00");
            } else {
                System.out.println("Khách hàng chưa có trong hệ thống. Vui lòng nhập thông tin khách mới:");
                dsKhachHang.them();
                kh = dsKhachHang.timkiem_khachhang(maKH);
                if (kh == null) {
                    kh = new KhachHang(maKH, "Khách Hàng Mới", "0000000000");
                }
            }
        }

        NhanVienBanHang nvLap;
        if (userHienTai instanceof NhanVienBanHang) {
            nvLap = (NhanVienBanHang) userHienTai;
        } else {
            nvLap = new NhanVienBanHang(
                    userHienTai.getMaNguoi(),
                    userHienTai.getHoTen(),
                    userHienTai.getSoDienThoai(),
                    userHienTai.getMatKhau(),
                    userHienTai.getCaLamViec(),
                    userHienTai.getLuongCoBan()
            );
        }

        HoaDon hdMoi = dsHoaDon.taoHoaDon(maHD, kh, nvLap);

        if (hdMoi != null) {
            String hoiTT = TienIch.nhapChuoi("Bạn có muốn thanh toán hóa đơn này ngay không? (c/k): ");
            if (hoiTT.equalsIgnoreCase("c")) {
                thucHienThanhToan(hdMoi);
            } else {
                System.out.println("Hóa đơn đã được lưu ở trạng thái Chờ thanh toán (Có thể Sửa/Xóa).");
            }
        }
    }

    private static void thucHienThanhToan(HoaDon hd) {
        if (hd.isDaThanhToan()) {
            System.out.println("Hóa đơn này đã được thanh toán trước đó!");
            return;
        }
        System.out.println("\n--- THANH TOÁN HÓA ĐƠN " + hd.getMaHD() + " ---");
        System.out.println("Tổng tiền cần thanh toán: " + hd.tinhThanhTien());
        System.out.println("1. Thanh toán Tiền mặt");
        System.out.println("2. Thanh toán Thẻ ngân hàng");
        System.out.println("0. Hủy (Chưa thanh toán)");

        int loaiTT = nhapSoTuKhoang("Chọn hình thức thanh toán (0-2): ", 0, 2);
        if (loaiTT == 1) {
            double tienKhachDua = TienIch.nhapGiaTien("Nhập số tiền khách đưa: ");
            IThanhToan ttTienMat = new IThanhToan() {
                @Override
                public boolean xuLyThanhToan(double soTien) {
                    if (tienKhachDua < soTien) {
                        System.out.println("Số tiền khách đưa không đủ! Cần " + soTien + ", đưa " + tienKhachDua);
                        return false;
                    }
                    double tienThua = tienKhachDua - soTien;
                    System.out.println("Thanh toán tiền mặt thành công. Tiền thối lại " + tienThua);
                    return true;
                }
            };
            hd.setHinhThucThanhToan(ttTienMat);
            hd.thanhToan();
        } else if (loaiTT == 2) {
            String nganHang = TienIch.nhapChuoi("Nhập tên ngân hàng: ");
            String maGD = TienIch.nhapChuoi("Nhập mã giao dịch: ");
            ThanhToanThe ttThe = new ThanhToanThe(maGD, nganHang);
            hd.setHinhThucThanhToan(ttThe);
            hd.thanhToan();
        } else {
            System.out.println("Đã tạm hoãn thanh toán.");
        }
    }

    private static void thongKeDoanhThu() {
        double tongDaTT = 0;
        double tongChuaTT = 0;
        int soHDDaTT = 0;
        int soHDChuaTT = 0;

        for (int i = 0; i < dsHoaDon.getSoLuong(); i++) {
            HoaDon hd = dsHoaDon.getDs()[i];
            if (hd.isDaThanhToan()) {
                tongDaTT += hd.tinhThanhTien();
                soHDDaTT++;
            } else {
                tongChuaTT += hd.tinhThanhTien();
                soHDChuaTT++;
            }
        }
        System.out.println("\n========== THỐNG KÊ DOANH THU ==========");
        System.out.println("- Tổng số hóa đơn trong hệ thống : " + dsHoaDon.getSoLuong());
        System.out.println("- Hóa đơn đã thanh toán trong ca : " + soHDDaTT + " HĐ | Doanh thu: " + tongDaTT);
        System.out.println("- Hóa đơn lưu lịch sử / chờ thu  : " + soHDChuaTT + " HĐ | Giá trị  : " + tongChuaTT);
        System.out.println("- TỔNG GIÁ TRỊ TOÀN BỘ HÓA ĐƠN   : " + (tongDaTT + tongChuaTT));
        System.out.println("========================================");
    }

    // ================= SUB-MENU 5: QUẢN LÝ KHÁCH HÀNG =================
    private static void menuKhachHang() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n========== MODULE: QUẢN LÝ KHÁCH HÀNG ==========");
            System.out.println("1. Thêm 1 khách hàng mới (Kiểm tra trùng mã)");
            System.out.println("2. Nhập nhiều khách hàng");
            System.out.println("3. Xem danh sách khách hàng");
            System.out.println("4. Sửa thông tin khách hàng");
            System.out.println("5. Xóa khách hàng");
            System.out.println("6. Tìm kiếm khách hàng theo mã");
            System.out.println("0. Trở về Menu Chính");
            System.out.println("================================================");

            int chon = TienIch.nhapSoNguyenDuong("Chọn chức năng: ");
            switch (chon) {
                case 1: dsKhachHang.them(); dungManHinh(); break;
                case 2: dsKhachHang.nhap(); dungManHinh(); break;
                case 3: dsKhachHang.xuat(); dungManHinh(); break;
                case 4: dsKhachHang.sua(); dungManHinh(); break;
                case 5: dsKhachHang.xoa(); dungManHinh(); break;
                case 6:
                    String maKH = TienIch.nhapChuoi("Nhập mã khách hàng cần tìm: ");
                    KhachHang kh = dsKhachHang.timkiem_khachhang(maKH);
                    if (kh != null) {
                        kh.xuat();
                    } else {
                        System.out.println("Không tìm thấy khách hàng có mã: " + maKH);
                    }
                    dungManHinh();
                    break;
                case 0: tiepTuc = false; break;
                default: System.out.println("Lựa chọn không hợp lệ!"); break;
            }
        }
    }

    // ================= SUB-MENU 6: QUẢN LÝ NHÂN SỰ =================
    private static void menuNhanSu() {
        boolean tiepTuc = true;
        while (tiepTuc) {
            System.out.println("\n========== MODULE: QUẢN LÝ NHÂN SỰ ==========");
            System.out.println("1. Thêm nhân viên mới (Cấp tài khoản)");
            System.out.println("2. Xem danh sách nhân viên");
            System.out.println("3. Sửa thông tin nhân viên");
            System.out.println("4. Xóa nhân viên");
            System.out.println("5. Tìm kiếm nhân viên theo mã");
            System.out.println("0. Trở về Menu Chính");
            System.out.println("=============================================");

            int chon = TienIch.nhapSoNguyenDuong("Chọn chức năng: ");
            switch (chon) {
                case 1:
                    System.out.println("\n--- TIẾN HÀNH CẤP TÀI KHOẢN MỚI ---");
                    System.out.println("1. Cấp quyền Quản Lý (ADMIN)");
                    System.out.println("2. Cấp quyền Bán Hàng (BAN_HANG)");
                    System.out.println("3. Cấp quyền Nhập Kho (NHAP_KHO)");
                    int loaiNV = nhapSoTuKhoang("Chọn chức vụ (1-3): ", 1, 3);

                    String ma;
                    while (true) {
                        ma = TienIch.nhapChuoi("Nhập mã NV (Tên đăng nhập): ");
                        if (dsNhanVien.timKiemViTri(ma) != -1) {
                            System.out.println("Mã nhân viên đã tồn tại! Vui lòng nhập mã khác.");
                        } else {
                            break;
                        }
                    }
                    String ten = TienIch.nhapChuoi("Nhập họ và tên: ");
                    String sdt = TienIch.nhapChuoi("Nhập số điện thoại: ");
                    String mk = TienIch.nhapChuoi("Nhập mật khẩu: ");
                    String ca = TienIch.nhapChuoi("Nhập ca làm việc (Sáng/Chiều/Tối): ");
                    double luong = TienIch.nhapGiaTien("Nhập mức lương cơ bản: ");

                    if (loaiNV == 1) {
                        dsNhanVien.them(new QuanLy(ma, ten, sdt, mk, ca, luong));
                    } else if (loaiNV == 2) {
                        dsNhanVien.them(new NhanVienBanHang(ma, ten, sdt, mk, ca, luong));
                    } else {
                        dsNhanVien.them(new NhanVienNhapKho(ma, ten, sdt, mk, ca, luong));
                    }
                    System.out.println(">>> Đã cấp tài khoản thành công cho nhân viên: " + ten);
                    dungManHinh();
                    break;
                case 2:
                    dsNhanVien.xuat();
                    dungManHinh();
                    break;
                case 3:
                    String maSua = TienIch.nhapChuoi("Nhập mã NV cần sửa: ");
                    dsNhanVien.sua(maSua);
                    dungManHinh();
                    break;
                case 4:
                    String maXoa = TienIch.nhapChuoi("Nhập mã NV cần xóa: ");
                    dsNhanVien.xoa(maXoa);
                    dungManHinh();
                    break;
                case 5:
                    String maTim = TienIch.nhapChuoi("Nhập mã NV cần tìm: ");
                    dsNhanVien.timKiem(maTim);
                    dungManHinh();
                    break;
                case 0:
                    tiepTuc = false;
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    break;
            }
        }
    }

    // ================= CÁC HÀM HỖ TRỢ HỆ THỐNG =================
    private static String timDuongDan(String tenFile) {
        String[] cacThuMuc = { "data/" + tenFile, tenFile, "src/data/" + tenFile, "src/" + tenFile };
        for (String path : cacThuMuc) {
            if (new File(path).exists()) {
                return path;
            }
        }
        return "data/" + tenFile;
    }

    // Đảm bảo KH00 và KH01 luôn tồn tại để đọc file hoadon.txt không bị lỗi
    private static void damBaoKhachHangMacDinh() {
        boolean canCapNhatFile = false;
        if (dsKhachHang.timkiem_khachhang("KH00") == null) {
            canCapNhatFile = true;
        }
        if (dsKhachHang.timkiem_khachhang("KH01") == null) {
            canCapNhatFile = true;
        }
        if (canCapNhatFile) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(FILE_KHACH_HANG, true))) {
                if (dsKhachHang.timkiem_khachhang("KH00") == null) {
                    pw.println("KH00;Khách Vãng Lai;0000000000");
                }
                if (dsKhachHang.timkiem_khachhang("KH01") == null) {
                    pw.println("KH01;Trần Thị B;0912345678");
                }
            } catch (Exception e) {
                // Bỏ qua nếu chưa tạo được file
            }
            dsKhachHang.docFile(FILE_KHACH_HANG);
        }
    }

    private static void khoiTaoNhanVienMau() {
        if (dsNhanVien.timKiemViTri("admin") == -1) {
            dsNhanVien.them(new QuanLy("admin", "Nguyễn Quang Đạt", "0900000000", "123", "Sáng", 20000000));
        }
        if (dsNhanVien.timKiemViTri("bh01") == -1) {
            dsNhanVien.them(new NhanVienBanHang("bh01", "Lê Bán Hàng", "0911111111", "123", "Chiều", 10000000));
        }
        if (dsNhanVien.timKiemViTri("kho01") == -1) {
            dsNhanVien.them(new NhanVienNhapKho("kho01", "Trần Thủ Kho", "0922222222", "123", "Tối", 12000000));
        }
    }

    private static void luuToanBoDuLieu() {
        System.out.println("\n[HỆ THỐNG] Đang lưu dữ liệu xuống ổ cứng...");
        khoHang.ghiFile("kho_output.txt");
        dsPhieuNhap.ghiFile("phieunhap_output.txt");
        dsKhachHang.ghiFile("khachhang_output.txt"); // Ghi ra file output riêng biệt
        dsHoaDon.ghiFile("hoadon_output.txt");       // Ghi ra file output riêng biệt
        System.out.println("[HỆ THỐNG] Hoàn tất lưu dữ liệu!");
    }

    private static String hoiDuongDan(String macDinh) {
        String s = TienIch.nhapChuoi("Nhập đường dẫn file (gõ . để dùng mặc định '" + macDinh + "'): ");
        return s.equals(".") ? macDinh : s;
    }

    private static int nhapSoTuKhoang(String thongBao, int min, int max) {
        while (true) {
            int n = TienIch.nhapSoNguyenDuong(thongBao);
            if (n >= min && n <= max) {
                return n;
            }
            System.out.println("LỖI: Vui lòng nhập số trong khoảng từ " + min + " đến " + max + "!");
        }
    }

    private static void dungManHinh() {
        System.out.print("\nNhấn Enter để tiếp tục...");
        TienIch.sc.nextLine();
    }
}