package chaychuongtrinh;

import danhsach.*;
import thucthe.*;
import tienich.QuanLyFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    static DanhSachSanPham dsSP = new DanhSachSanPham();
    static DanhSachKhachHang dsKH = new DanhSachKhachHang();
    static DanhSachNhanVien dsNV = new DanhSachNhanVien();
    static DanhSachHoaDon dsHD = new DanhSachHoaDon();
    static DanhSachMaGiamGia dsMGM = new DanhSachMaGiamGia();
    static DanhSachLichSu dsLS = new DanhSachLichSu();
    static Scanner sc = new Scanner(System.in);

    // ================= CÁC HÀM TIỆN ÍCH DÙNG CHUNG =================

    private static int nhapSoNguyen(String thongBao) {
        while (true) {
            try {
                System.out.print(thongBao);
                int so = Integer.parseInt(sc.nextLine());
                if (so >= 0) return so;
                System.out.println("Lỗi: Vui lòng nhập số dương (Nhập 0 để hủy hoặc bỏ qua)!");
            } catch (Exception e) {
                System.out.println("Lỗi: Sai định dạng! Vui lòng chỉ nhập số, không nhập chữ.");
            }
        }
    }

    private static String nhapChuoi(String thongBao) {
        System.out.print(thongBao);
        return sc.nextLine().trim();
    }

    private static String chuanHoaTen(String str) {
        String[] words = str.trim().replaceAll("\\s+", " ").split(" ");
        StringBuilder res = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                res.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1).toLowerCase()).append(" ");
            }
        }
        return res.toString().trim();
    }

    private static String layVietTat(String str) {
        String[] words = str.split(" ");
        StringBuilder res = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) res.append(Character.toUpperCase(w.charAt(0)));
        }
        return res.toString();
    }

    public static String layThoiGianThuc() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dtf.format(LocalDateTime.now());
    }

    public static void log(String taiKhoan, String hanhDong) {
        dsLS.ghiLog(new LichSuHoatDong(layThoiGianThuc(), taiKhoan, hanhDong));
    }

    // ================= HÀM MAIN VÀ KHỞI ĐỘNG =================

    public static void main(String[] args) {
        QuanLyFile.docFileSanPham(dsSP, "sanpham.txt");
        QuanLyFile.docFileKhachHang(dsKH, "khachhang.txt");
        khoiTaoDuLieuMau();

        int chon;
        do {
            System.out.println("\n===================================================");
            System.out.println("║       CHÀO MỪNG ĐẾN VỚI HỆ THỐNG BOOKSTORE      ║");
            System.out.println("===================================================");
            System.out.println("1. Đăng nhập hệ thống");
            System.out.println("2. Đăng ký tài khoản Khách hàng mới");
            System.out.println("3. Xem danh sách Sản phẩm (Dành cho khách vãng lai)");
            System.out.println("0. Thoát chương trình (Lưu dữ liệu)");
            System.out.println("===================================================");
            chon = nhapSoNguyen("Mời bạn chọn chức năng (0-3): ");

            switch (chon) {
                case 1: xuLyDangNhap(); break;
                case 2: xuLyDangKyKhachHang(); break;
                case 3: xemVaLocSanPham(); break; // <-- Đã đổi thành menu Lọc/Tìm kiếm
                case 0:
                    QuanLyFile.ghiFileSanPham(dsSP, "sanpham.txt");
                    QuanLyFile.ghiFileKhachHang(dsKH, "khachhang.txt");
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống!");
                    break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }

    private static void xuLyDangNhap() {
        String taiKhoan = nhapChuoi("Nhập SĐT (hoặc Mã NV) (Nhập 0 để hủy): ");
        if (taiKhoan.equals("0")) return;
        String matKhau = nhapChuoi("Nhập Mật khẩu: ");

        NhanVien nv = dsNV.dangNhap(taiKhoan, matKhau);
        if (nv != null) {
            String chucVu = nv instanceof QuanLy ? "Quản Trị Viên" :
                    (nv instanceof NhanVienBanHang ? "Nhân Viên Bán Hàng" : "Nhân Viên Kho");
            System.out.println("\n>>> Đăng nhập thành công! Chào " + chucVu + ": " + nv.getHoTen());
            log(nv.getMaNguoi(), "Đăng nhập hệ thống");

            if (nv instanceof QuanLy) menuAdmin(nv);
            else if (nv instanceof NhanVienBanHang) menuBanHang(nv);
            else menuKho(nv);
            return;
        }

        KhachHang kh = dsKH.dangNhap(taiKhoan, matKhau);
        if (kh != null) {
            System.out.println("\n>>> Đăng nhập thành công! Xin chào Khách hàng: " + kh.getHoTen());
            log(kh.getSoDienThoai(), "Đăng nhập hệ thống");
            menuKhachHang(kh);
            return;
        }
        System.out.println("Sai tài khoản hoặc mật khẩu!");
    }

    // ================= TÍNH NĂNG MỚI: MENU LỌC & TÌM KIẾM =================
    private static void xemVaLocSanPham() {
        System.out.println("\n--- TÙY CHỌN XEM SẢN PHẨM ---");
        System.out.println("1. Xem tất cả sản phẩm");
        System.out.println("2. Lọc theo danh mục (Sách / VPP)");
        System.out.println("3. Tìm kiếm theo tên sản phẩm");
        System.out.println("0. Quay lại");

        int chon = nhapSoNguyen("Bạn muốn xem theo cách nào? (0-3): ");
        if (chon == 1) {
            dsSP.xuat();
        } else if (chon == 2) {
            int loai = nhapSoNguyen("Chọn danh mục (1. Sách | 2. Văn phòng phẩm): ");
            dsSP.locTheoLoai(loai);
        } else if (chon == 3) {
            String tuKhoa = nhapChuoi("Nhập từ khóa (VD: Toán, Java, Bút...): ");
            dsSP.timKiemTheoTen(tuKhoa);
        }
    }

    // ================= CÁC MENU PHÂN QUYỀN RÕ RÀNG =================

    private static void menuKhachHang(KhachHang kh) {
        GioHang gioHang = new GioHang(kh);
        int chon;
        do {
            System.out.println("\n--- CỔNG MUA SẮM KHÁCH HÀNG ---");
            System.out.println("1. Xem & Tìm kiếm Sản phẩm");
            System.out.println("2. Thêm sản phẩm vào Giỏ hàng");
            System.out.println("3. Xem & Chỉnh sửa Giỏ hàng");
            System.out.println("4. Thanh toán");
            System.out.println("0. Đăng xuất");
            chon = nhapSoNguyen("Chọn (0-4): ");

            switch (chon) {
                case 1: xemVaLocSanPham(); break; // <-- Đã đổi
                case 2:
                    String ma = nhapChuoi("Nhập mã SP (Nhập 0 để hủy): ");
                    if (ma.equals("0")) break;
                    int viTri = dsSP.timKiemViTri(ma);
                    if (viTri != -1) {
                        int sl = nhapSoNguyen("Nhập số lượng: ");
                        if (sl <= 0) {
                            System.out.println("Lỗi: Số lượng mua phải lớn hơn 0!");
                        } else if (sl <= dsSP.getDanhSach()[viTri].getSoLuongQuay()) {
                            gioHang.themVaoGio(dsSP.getDanhSach()[viTri], sl);
                        } else {
                            System.out.println("Xin lỗi, quầy chỉ còn " + dsSP.getDanhSach()[viTri].getSoLuongQuay());
                        }
                    } else {
                        System.out.println("Không tìm thấy sản phẩm! (Lưu ý chữ Hoa/Thường)");
                    }
                    break;
                case 3:
                    if (gioHang.getSoLuongMon() == 0) {
                        System.out.println("Giỏ hàng đang trống!"); break;
                    }
                    gioHang.xemGioHang();
                    System.out.println("\n1. Đổi số lượng | 2. Xóa SP khỏi giỏ | 0. Thoát");
                    int chonGio = nhapSoNguyen("Chọn: ");
                    if (chonGio == 1) {
                        String maSua = nhapChuoi("Nhập mã SP: ");
                        int slMoi = nhapSoNguyen("Số lượng mới (Nhập 0 để xóa): ");
                        gioHang.capNhatSoLuong(maSua, slMoi);
                    } else if (chonGio == 2) {
                        gioHang.xoaKhoiGio(nhapChuoi("Nhập mã SP cần xóa: "));
                    }
                    break;
                case 4: thanhToan(kh, gioHang); break;
                case 0: System.out.println("Đã đăng xuất!"); break;
            }
        } while (chon != 0);
    }

    private static void menuBanHang(NhanVien nv) {
        int chon;
        do {
            System.out.println("\n--- BẢNG ĐIỀU KHIỂN BÁN HÀNG ---");
            System.out.println("1. Xem & Tìm kiếm Hàng hóa");
            System.out.println("2. Chuyển hàng từ Kho lên Quầy");
            System.out.println("3. Xem lịch sử Hóa đơn bán ra");
            System.out.println("0. Đăng xuất");
            chon = nhapSoNguyen("Chọn (0-3): ");

            switch (chon) {
                case 1: xemVaLocSanPham(); break; // <-- Đã đổi
                case 2: chuyenHangLenQuay(); break;
                case 3: xemDanhSachHoaDon(); break;
                case 0: System.out.println("Đã đăng xuất!"); break;
            }
        } while (chon != 0);
    }

    private static void menuKho(NhanVien nv) {
        int chon;
        do {
            System.out.println("\n--- BẢNG ĐIỀU KHIỂN KHO HÀNG ---");
            System.out.println("1. Xem & Tìm kiếm Hàng hóa");
            System.out.println("2. Nhập thêm số lượng (Tăng Tồn Kho cho SP hiện có)");
            System.out.println("0. Đăng xuất");
            chon = nhapSoNguyen("Chọn (0-2): ");

            switch (chon) {
                case 1: xemVaLocSanPham(); break; // <-- Đã đổi
                case 2:
                    String ma = nhapChuoi("Nhập mã SP cần tăng tồn kho (Nhập 0 để hủy): ");
                    if(ma.equals("0")) break;
                    int vt = dsSP.timKiemViTri(ma);
                    if(vt != -1) {
                        int sl = nhapSoNguyen("Nhập số lượng vừa nhập về: ");
                        if(sl > 0) {
                            SanPham sp = dsSP.getDanhSach()[vt];
                            sp.setSoLuongKho(sp.getSoLuongKho() + sl);
                            System.out.println("Đã cộng thêm " + sl + " vào kho cho SP: " + sp.getTenSP());
                            log(nv.getMaNguoi(), "Nhập " + sl + " kho cho SP " + ma);
                        }
                    } else System.out.println("Không tìm thấy SP!");
                    break;
                case 0: System.out.println("Đã đăng xuất!"); break;
            }
        } while (chon != 0);
    }

    private static void menuAdmin(NhanVien nv) {
        int chon;
        do {
            System.out.println("\n--- BẢNG ĐIỀU KHIỂN SẾP TỔNG (ADMIN) ---");
            System.out.println("1. Xem & Tìm kiếm Hàng hóa");
            System.out.println("2. Quản lý Sản phẩm (Thêm SP Mới / Sửa Thông Tin)");
            System.out.println("3. Quản lý Nhân sự (Xem / Thêm / Xóa)");
            System.out.println("4. Xem Hóa Đơn & Doanh Thu");
            System.out.println("5. Chuyển hàng từ Kho lên Quầy");
            System.out.println("6. Xem Lịch sử Hệ thống (Audit Log)");
            System.out.println("0. Đăng xuất");
            chon = nhapSoNguyen("Chọn (0-6): ");

            switch (chon) {
                case 1: xemVaLocSanPham(); break; // <-- Đã đổi
                case 2: quanLySanPhamAdmin(); break;
                case 3: quanLyNhanSuAdmin(); break;
                case 4: xemDanhSachHoaDon(); break;
                case 5: chuyenHangLenQuay(); break;
                case 6: dsLS.xuatLichSuToanHeThong(); break;
                case 0: System.out.println("Đã đăng xuất!"); break;
            }
        } while (chon != 0);
    }

    // ================= CÁC HÀM XỬ LÝ CHUYÊN SÂU =================

    private static void xuLyDangKyKhachHang() {
        System.out.println("\n--- ĐĂNG KÝ TÀI KHOẢN KHÁCH HÀNG ---");
        String sdt = "";
        while (true) {
            sdt = nhapChuoi("Nhập SĐT (10 số, bắt đầu bằng 0) (Nhập 0 để hủy): ");
            if (sdt.equals("0")) return;
            if (!sdt.matches("^0\\d{9}$")) {
                System.out.println("Lỗi: SĐT không hợp lệ!"); continue;
            }
            if (dsKH.timKiemViTri(sdt) != -1) System.out.println("Lỗi: SĐT đã tồn tại!");
            else break;
        }

        String ho = nhapChuoi("Nhập Họ và Tên lót (Chỉ chữ cái) (Nhập 0 để hủy): ");
        if (ho.equals("0")) return;
        String ten = nhapChuoi("Nhập Tên: ");

        String hoTenChuan = chuanHoaTen(ho + " " + ten);
        if (!hoTenChuan.matches("^[\\p{L}\\s]+$")) {
            System.out.println("Lỗi: Tên chứa ký tự đặc biệt. Đã hủy đăng ký!");
            return;
        }

        String matKhau = nhapChuoi("Nhập Mật khẩu: ");
        String maKH = "KH" + String.format("%03d", dsKH.getSoLuong() + 1);
        dsKH.them(new KhachHang(maKH, hoTenChuan, sdt, matKhau, 0, "Đồng"));
        System.out.println(">>> Đăng ký thành công! Xin chào " + hoTenChuan);
    }

    private static void thanhToan(KhachHang kh, GioHang gioHang) {
        if (gioHang.getSoLuongMon() == 0) {
            System.out.println("Giỏ hàng trống!"); return;
        }
        gioHang.xemGioHang();
        double tongTien = gioHang.tinhTongTien();

        double giamGiaHang = kh.getHangThanhVien().equals("Kim Cương") ? 0.08 :
                (kh.getHangThanhVien().equals("Vàng") ? 0.05 :
                        (kh.getHangThanhVien().equals("Bạc") ? 0.02 : 0));
        tongTien -= (tongTien * giamGiaHang);

        String maVoucher = nhapChuoi("\nNhập Mã giảm giá (Enter hoặc 0 để bỏ qua): ");
        if (!maVoucher.isEmpty() && !maVoucher.equals("0")) {
            tongTien -= (tongTien * dsMGM.apDungMaGiamGia(maVoucher));
        }

        System.out.printf(">>> TỔNG TIỀN CUỐI CÙNG: %.2f\n", tongTien);
        String xacNhan = nhapChuoi("Xác nhận thanh toán? (Y/N): ");
        if (!xacNhan.equalsIgnoreCase("Y")) {
            System.out.println("Đã hủy thanh toán."); return;
        }

        for (int i = 0; i < gioHang.getSoLuongMon(); i++) {
            ChiTietHoaDon ct = gioHang.getDanhSachChiTiet()[i];
            ct.getSanPham().setSoLuongQuay(ct.getSanPham().getSoLuongQuay() - ct.getSoLuongMua());
        }

        HoaDon hd = new HoaDon(kh, layThoiGianThuc(), gioHang.getDanhSachChiTiet(), gioHang.getSoLuongMon(), tongTien);
        dsHD.them(hd);
        kh.setDiemTichLuy(kh.getDiemTichLuy() + (int)(tongTien / 10000));
        kh.capNhatHangThanhVien();
        hd.xuatHoaDon();
        gioHang.lamTrongGio();
        log(kh.getSoDienThoai(), "Thanh toán HD: " + hd.getMaHoaDon());
    }

    private static void xemDanhSachHoaDon() {
        if (dsHD.getSoLuong() == 0) {
            System.out.println("Chưa có hóa đơn nào trong hệ thống!"); return;
        }
        System.out.println("\n================ LỊCH SỬ HÓA ĐƠN ================");
        System.out.printf("%-7s | %-20s | %-20s | %-15s\n", "Mã HD", "Ngày Lập", "Khách Hàng", "Tổng Tiền");
        System.out.println("-----------------------------------------------------------------");
        for (int i = 0; i < dsHD.getSoLuong(); i++) {
            HoaDon hd = dsHD.getDanhSach()[i];
            System.out.printf("%-7s | %-20s | %-20s | %-15.2f\n",
                    hd.getMaHoaDon(), hd.getNgayLap(), hd.getKhachHang().getHoTen(), hd.getTongTienThanhToan());
        }

        while (true) {
            String ma = nhapChuoi("\nNhập Mã HD để xem chi tiết (Nhập 0 để thoát): ");
            if (ma.equals("0")) break;

            boolean timThay = false;
            for (int i = 0; i < dsHD.getSoLuong(); i++) {
                if (dsHD.getDanhSach()[i].getMaHoaDon().equalsIgnoreCase(ma)) {
                    dsHD.getDanhSach()[i].xuatHoaDon();
                    timThay = true; break;
                }
            }
            if (!timThay) System.out.println("Không tìm thấy mã Hóa đơn!");
        }
    }

    private static void chuyenHangLenQuay() {
        String ma = nhapChuoi("\nNhập mã SP cần xuất lên quầy (Nhập 0 để hủy): ");
        if (ma.equals("0")) return;
        int vt = dsSP.timKiemViTri(ma);
        if (vt != -1) {
            SanPham sp = dsSP.getDanhSach()[vt];
            System.out.println("Tồn KHO: " + sp.getSoLuongKho() + " | Tồn QUẦY: " + sp.getSoLuongQuay());
            int sl = nhapSoNguyen("Nhập SL chuyển lên (Nhập 0 để hủy): ");
            if (sl == 0) return;
            if (sl > 0 && sl <= sp.getSoLuongKho()) {
                sp.setSoLuongKho(sp.getSoLuongKho() - sl);
                sp.setSoLuongQuay(sp.getSoLuongQuay() + sl);
                System.out.println(">>> Đã chuyển thành công!");
                log("HỆ THỐNG", "Chuyển " + sl + " SP " + ma + " lên quầy");
            } else System.out.println("Lỗi: Không đủ hàng trong kho!");
        } else System.out.println("Không tìm thấy sản phẩm!");
    }

    // --- CÁC HÀM ADMIN CON ---

    private static void quanLySanPhamAdmin() {
        System.out.println("\n1. Thêm SP Mới | 2. Sửa Tên/Giá SP | 0. Thoát");
        int chon = nhapSoNguyen("Chọn: ");
        if (chon == 1) {
            int loai = nhapSoNguyen("Chọn loại (1. Sách | 2. VPP): ");
            if (loai == 0) return;
            String ma = nhapChuoi("Mã SP (VD: S05): ");
            String ten = nhapChuoi("Tên SP: ");
            double gia = nhapSoNguyen("Đơn giá: ");
            int kho = nhapSoNguyen("SL Kho: ");

            if (loai == 1) {
                dsSP.them(new Sach(ma, ten, gia, kho, 0, nhapChuoi("Tác giả: "), nhapChuoi("NXB: "), nhapSoNguyen("Năm XB: ")));
            } else if (loai == 2) {
                dsSP.them(new VanPhongPham(ma, ten, gia, kho, 0, nhapChuoi("Thương hiệu: "), nhapChuoi("Màu sắc: ")));
            }
            System.out.println("Đã thêm Sản phẩm mới!");
            log("ADMIN", "Thêm SP mới: " + ma);
        } else if (chon == 2) {
            String ma = nhapChuoi("Nhập mã SP cần sửa (Nhập 0 để hủy): ");
            if(ma.equals("0")) return;
            int vt = dsSP.timKiemViTri(ma);
            if (vt != -1) {
                String tenMoi = nhapChuoi("Tên mới (Enter để giữ nguyên): ");
                if (!tenMoi.isEmpty()) dsSP.getDanhSach()[vt].setTenSP(tenMoi);
                System.out.println("Sửa thành công!");
            } else System.out.println("Không tìm thấy SP!");
        }
    }

    private static void quanLyNhanSuAdmin() {
        System.out.println("\n1. Xem Danh sách NV | 2. Thêm NV mới | 3. Xóa NV | 0. Thoát");
        int chon = nhapSoNguyen("Chọn: ");
        if (chon == 1) dsNV.xuat();
        else if (chon == 2) {
            String ho = nhapChuoi("Nhập Họ và Tên lót (VD: Võ Trần) (Nhập 0 để hủy): ");
            if (ho.equals("0")) return;
            String ten = nhapChuoi("Nhập Tên (VD: Bình): ");
            String hoTenChuan = chuanHoaTen(ho + " " + ten);

            String sdt = "";
            while (true) {
                sdt = nhapChuoi("Nhập SĐT (10 số): ");
                if (sdt.matches("^0\\d{9}$")) break;
                System.out.println("Lỗi SĐT!");
            }

            int vt = nhapSoNguyen("Vị trí (1. Bán hàng | 2. Nhập kho): ");
            if (vt == 0) return;

            String tienTo = (vt == 1) ? "NVBH" : "NVNK";
            String maGoc = tienTo + layVietTat(hoTenChuan);
            String maNV = maGoc;
            int count = 1;
            while (dsNV.timKiemViTri(maNV) != -1) {
                maNV = maGoc + count++;
            }
            System.out.println("=> Hệ thống cấp tự động Mã Đăng Nhập: " + maNV);
            String mk = nhapChuoi("Cấp Mật khẩu: ");
            double luong = nhapSoNguyen("Lương cơ bản: ");

            if (vt == 1) dsNV.them(new NhanVienBanHang(maNV, hoTenChuan, sdt, mk, "Hành chính", luong));
            else dsNV.them(new NhanVienNhapKho(maNV, hoTenChuan, sdt, mk, "Hành chính", luong));
            System.out.println("Đã thêm nhân viên thành công!");
        }
        else if (chon == 3) {
            String ma = nhapChuoi("Nhập Mã NV cần xóa (Nhập 0 để hủy): ");
            if (!ma.equals("0")) dsNV.xoa(ma);
        }
    }

    private static void khoiTaoDuLieuMau() {
        if (dsNV.getSoLuong() == 0) {
            dsNV.them(new QuanLy("ADMIN", "Quản Trị Viên", "0999", "admin", "Full", 20000));
            dsNV.them(new NhanVienBanHang("NVBHTVA", "Trần Văn An", "0987654321", "BH123", "Sáng", 15000));
            dsNV.them(new NhanVienNhapKho("NVNKNVA", "Nguyễn Văn A", "0123456789", "NK123", "Tối", 15000));
        }
        if (dsKH.getSoLuong() == 0) {
            dsKH.them(new KhachHang("KH01", "Nguyễn Khách Vip", "0123456789", "123", 60, "Bạc"));
        }
        if (dsSP.getSoLuong() == 0) {
            dsSP.them(new Sach("S01", "Lập trình Java", 150000, 50, 10, "James", "NXB IT", 2023));
            dsSP.them(new VanPhongPham("V01", "Bút bi Thiên Long", 5000, 200, 50, "Thiên Long", "Xanh"));
        }
        if (dsMGM.getSoLuong() == 0) dsMGM.them(new MaGiamGia("SALE10", 0.1f, 5));
    }
}3