import chinhanh.DanhSachChiNhanh;
import hoadon.ChiTietHoaDon;
import hoadon.DanhSachHoaDon;
import hoadon.HoaDon;
import khachhang.DanhSachKhachHang;
import khachhang.KhachHang;
import kho.Kho;
import mathang.*;
import nhacungcap.DanhSachNCC;
import nhanvien.*;
import phieunhap.DanhSachPhieuNhap;
import phieunhap.PhieuNhap.TrangThaiPN;
import thanhtoan.ThanhToanThe;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/** Check the supported TXT formats; the application has no full-session snapshot API. */
public class PersistenceRegressionTests {
    private static int passed, failed;
    @FunctionalInterface interface Task { void run(Path path) throws Exception; }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void test(String name, Task task) {
        Path path = null;
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            path = Files.createTempFile("bookstore-txt-", ".txt");
            System.setOut(output);
            task.run(path);
            passed++;
            original.println("PASS TXT " + name);
        } catch (Throwable error) {
            failed++;
            original.println("FAIL TXT " + name + ": " + error);
            original.print(bytes.toString(StandardCharsets.UTF_8));
            error.printStackTrace(original);
        } finally {
            System.setOut(original);
            if (path != null) {
                try { Files.deleteIfExists(path); }
                catch (Exception error) { throw new RuntimeException(error); }
            }
        }
    }

    private static Sach book(String id, int quantity) {
        return new Sach(id, "Sách tiếng Việt", 100, quantity, "Tác giả", "Nhà xuất bản", "Kỹ thuật");
    }

    private static MatHang[] products() {
        return new MatHang[] {
            book("SCH0101", 3),
            new SachGiaoKhoa("SGK0101", "Textbook", 100, 3, "Math", "Publisher"),
            new TieuThuyet("TTH0101", "Novel", 100, 3, "Author", "Publisher", "Mystery"),
            new Truyen("TRU0101", "Comic", 100, 3, "Author", "Publisher", "Adventure"),
            new BaoTapChi("BTC0101", "Magazine", 100, 3, "Publisher", "Science"),
            new VanPhongPham("VPP0101", "Pen", 100, 3, "Maker", "Pen"),
            new SPKhac("SPK0101", "Other", 100, 3, "Maker"),
            new DoChoi("DCH0101", "Toy", 100, 3, "Maker", 6),
            new DungCuHocTap("DCT0101", "Ruler", 100, 3, "Maker", "Ruler"),
            new TrangTri("TTR0101", "Decor", 100, 3, "Maker"),
            new Vo("VO0101", "Notebook", 100, 3, 96, "Maker")
        };
    }

    public static void main(String[] args) {
        test("branches retain UTF-8 addresses and accept legacy rows", path -> {
            Kho warehouse = new Kho();
            DanhSachKhachHang customers = new DanhSachKhachHang();
            DanhSachNhanVien staff = new DanhSachNhanVien();
            DanhSachChiNhanh branches = new DanhSachChiNhanh(warehouse, customers, staff);
            check(branches.them("CN01", "Chi nhánh Một", "Đường số 1"), "branch setup");
            branches.ghiFile(path.toString());
            Files.writeString(path, Files.readString(path) + "CN02;Chi nhánh Hai\n");
            DanhSachChiNhanh copy = new DanhSachChiNhanh(warehouse, customers, staff);
            copy.docFile(path.toString());
            copy.docFile(path.toString());
            check(copy.getSoLuong() == 2, "duplicate import added branches");
            check("Đường số 1".equals(copy.timTheoMa("cn01").getDiaChi()), "address lost");
            check(copy.timTheoMa("CN02").getDiaChi().isEmpty(), "legacy address");
        });
        test("branch TXT stores metadata and starts with empty shelves and invoices", path -> {
            Kho warehouse = new Kho();
            DanhSachKhachHang customers = new DanhSachKhachHang();
            DanhSachNhanVien staff = new DanhSachNhanVien();
            DanhSachChiNhanh branches = new DanhSachChiNhanh(warehouse, customers, staff);
            branches.them("CN01", "Branch", "Address");
            TestSupport.stockShelf(branches.get(0).getDsmh(), book("SCH0101", 3));
            branches.ghiFile(path.toString());
            check(Files.readString(path).strip().equals("CN01;Branch;Address"), "branch format");
            DanhSachChiNhanh copy = new DanhSachChiNhanh(warehouse, customers, staff);
            copy.docFile(path.toString());
            check(copy.get(0).getDsmh().getSoMH() == 0 && copy.get(0).getDshd().getSoLuong() == 0,
                    "branch metadata unexpectedly restores business data");
        });
        test("staff preserves four roles, passwords and branch assignments", path -> {
            DanhSachNhanVien staff = new DanhSachNhanVien();
            staff.them(new QuanLy("ADMIN", "Quản trị", "090", "pw", "Sáng", 100));
            staff.them(new QuanLyChiNhanh("QL01", "Quản lý", "091", "pw1", "Sáng", 90, "CN02"));
            staff.them(new NhanVienBanHang("BH01", "Bán hàng", "092", "pw2", "Chiều", 80, "CN01"));
            staff.them(new NhanVienNhapKho("KHO01", "Nhập kho", "093", "pw3", "Tối", 70));
            staff.ghiFile(path.toString());
            check(Files.readAllLines(path).stream().allMatch(row -> row.split(";", -1).length == 8),
                    "staff format must have eight columns");
            DanhSachNhanVien copy = new DanhSachNhanVien();
            copy.docFile(path.toString());
            check(copy.getSoLuong() == 4 && copy.dangNhap("BH01", "pw2") instanceof NhanVienBanHang,
                    "staff login roundtrip");
            check(copy.timTheoMa("QL01") instanceof QuanLyChiNhanh
                    && "CN02".equals(copy.timTheoMa("QL01").getMaChiNhanh()), "manager assignment");
            check(!copy.timTheoMa("KHO01").thuocChiNhanh(), "warehouse role must remain global");
        });
        test("customers use three-column UTF-8 TXT and repeated reads replace the list", path -> {
            Files.writeString(path, "KH01;Nguyễn Ánh;090\nKH02;Lê Bình;091\n");
            DanhSachKhachHang customers = new DanhSachKhachHang();
            customers.docFile(path.toString());
            customers.ghiFile(path.toString());
            DanhSachKhachHang copy = new DanhSachKhachHang();
            copy.docFile(path.toString());
            copy.docFile(path.toString());
            check(copy.getSoLuong() == 2, "customers duplicated");
            check("Nguyễn Ánh".equals(copy.timkiem_khachhang("KH01").getTenKH()), "UTF-8 name lost");
        });
        test("warehouse TXT records the remaining warehouse quantity", path -> {
            Kho warehouse = new Kho();
            warehouse.tangTonKho(book("SCH0101", 0), 10);
            DanhSachMatHang shelf = new DanhSachMatHang(warehouse);
            shelf.nhaptukho(warehouse, "SCH0101", 4);
            warehouse.ghiFile(path.toString());
            String[] fields = Files.readString(path).strip().split(";", -1);
            check(fields[1].equals("SCH0101") && fields[4].equals("6"), "warehouse export count");
            check(shelf.timKiem_hang("SCH0101").getSoLuong() == 4, "export changed shelf");
        });
        test("shelf TXT exports all eleven subtype formats", path -> {
            DanhSachMatHang shelf = new DanhSachMatHang();
            MatHang[] items = products();
            for (MatHang item : items) TestSupport.stockShelf(shelf, item);
            shelf.ghiFile(path.toString());
            var rows = Files.readAllLines(path);
            check(rows.size() == items.length, "missing exported product");
            for (int i = 0; i < items.length; i++) {
                check(rows.get(i).equals(items[i].toString()), "subtype format: " + items[i].getMa());
            }
        });
        test("receipt TXT roundtrip retains grouped lines, dates and status", path -> {
            Files.writeString(path,
                    "PN01;21/11/2026;Sách;SCH0101;First;100;3;Author;Publisher;Science;CHO_DUYET\n"
                    + "PN01;21/11/2026;Sách;SCH0102;Second;120;2;Author;Publisher;Science;CHO_DUYET\n");
            DanhSachPhieuNhap notes = new DanhSachPhieuNhap();
            notes.docFile(path.toString());
            notes.ghiFile(path.toString());
            DanhSachPhieuNhap copy = new DanhSachPhieuNhap();
            copy.docFile(path.toString());
            copy.docFile(path.toString());
            check(copy.getSLpn() == 1 && copy.timPhieu("PN01").getSoLuongNhap() == 2, "receipt grouping");
            check(copy.timPhieu("PN01").getChiTiet().getNgayNhap().equals(LocalDate.of(2026, 11, 21)),
                    "receipt date");
            Kho warehouse = new Kho();
            check(warehouse.nhapHangTuPhieu(copy.timPhieu("PN01")), "receipt intake");
            copy.ghiFileDaNhapKho(path.toString());
            DanhSachPhieuNhap approved = new DanhSachPhieuNhap();
            approved.docFile(path.toString());
            check(approved.timPhieu("PN01").getTrangThai() == TrangThaiPN.DA_DUYET, "approved status lost");
            check(!warehouse.nhapHangTuPhieu(approved.timPhieu("PN01"))
                    && warehouse.getTongSoLuongTon() == 5, "approved receipt imported twice");
        });
        test("supplier TXT updates the suppliers already identified by receipts", path -> {
            DanhSachNCC suppliers = new DanhSachNCC();
            String id = suppliers.timHoacTaoTheoTen("Nhà xuất bản").getMaNCC();
            Files.writeString(path, id + ";Nhà xuất bản;Hồ Chí Minh;090;test@example.vn\n");
            suppliers.docFile(path.toString());
            suppliers.ghiFile(path.toString());
            DanhSachNCC copy = new DanhSachNCC();
            copy.timHoacTaoTheoTen("Nhà xuất bản");
            copy.docFile(path.toString());
            check(copy.getSoLuongNCC() == 1 && "Hồ Chí Minh".equals(copy.timNCC(id).getDiaChi()),
                    "supplier details roundtrip");
        });
        test("invoice TXT retains payment and frozen prices without deducting stock again", path -> {
            DanhSachKhachHang customers = new DanhSachKhachHang();
            Files.writeString(path, "KH01;Khách;090\n");
            customers.docFile(path.toString());
            KhachHang customer = customers.timkiem_khachhang("KH01");
            DanhSachMatHang shelf = new DanhSachMatHang();
            TestSupport.stockShelf(shelf, book("SCH0101", 5));
            MatHang item = shelf.timKiem_hang("SCH0101");
            NhanVienBanHang seller = new NhanVienBanHang("BH01", "Nhân viên", "091", "pw", "Sáng", 100, "CN01");
            DanhSachNhanVien staff = new DanhSachNhanVien();
            staff.them(seller);
            HoaDon paid = new HoaDon("PAID", LocalDate.of(2026, 11, 21), customer, seller);
            check(paid.themChiTiet(new ChiTietHoaDon(2, item)), "invoice setup");
            double price = paid.getChiTiet()[0].getDonGia();
            paid.setHinhThucThanhToan(new ThanhToanThe("TX", "Bank"));
            check(paid.thanhToan(), "invoice payment");
            HoaDon pending = new HoaDon("PENDING", LocalDate.of(2026, 11, 21), customer, seller);
            check(pending.themChiTiet(new ChiTietHoaDon(1, item)), "pending invoice setup");
            DanhSachHoaDon invoices = new DanhSachHoaDon(shelf, customers, staff);
            check(invoices.them(paid) && invoices.them(pending), "invoice list setup");
            invoices.ghiFile(path.toString());
            item.setGiaNhap(999);
            DanhSachHoaDon copy = new DanhSachHoaDon(shelf, customers, staff);
            copy.docFile(path.toString());
            copy.docFile(path.toString());
            check(copy.getSoLuong() == 2 && copy.timKiem("PAID").isDaThanhToan()
                    && !copy.timKiem("PENDING").isDaThanhToan(), "invoice payment flags");
            check(copy.timKiem("PAID").getChiTiet()[0].getDonGia() == price, "historical price changed");
            check(item.getSoLuong() == 3, "reload deducted stock again");
        });
        System.out.printf("TXT PERSISTENCE TESTS: %d PASS, %d FAIL%n", passed, failed);
        if (failed > 0) System.exit(1);
    }
}
