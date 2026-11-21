import chinhanh.*;
import hoadon.*;
import khachhang.*;
import kho.*;
import mathang.*;
import nhacungcap.*;
import nhanvien.*;
import phieunhap.*;
import thanhtoan.*;
import tienich.*;

import java.nio.file.*;
import java.time.LocalDate;

/** Adversarial regressions found by reviewing production data and all source modules. */
public class DeepAuditRegressionTests {
    static void test(String name, FullFunctionalAuditTests.Job job) { FullFunctionalAuditTests.test(name, job); }
    static void yes(boolean ok, String message) { FullFunctionalAuditTests.yes(ok, message); }
    static Sach book(String code, int qty) { return FullFunctionalAuditTests.book(code, qty); }
    static PhieuNhap note(MatHang... items) {
        PhieuNhap result = new PhieuNhap(); result.setMaPN("PNDEEP");
        result.setChiTiet(new ChiTietPhieuNhap(LocalDate.now(), new NhaCungCap("NCC0", "Publisher", "", "", "")));
        for (MatHang item : items) result.them(item);
        return result;
    }
    static DanhSachHoaDon invoices(DanhSachMatHang shelf) {
        return new DanhSachHoaDon(shelf, new DanhSachKhachHang());
    }
    static void withFile(String content, java.util.function.Consumer<Path> job) throws Exception {
        Path path = FullFunctionalAuditTests.file(content);
        try { job.accept(path); } finally { Files.deleteIfExists(path); }
    }
    public static void main(String[] args) throws Exception {
        test("DEEP manual novel is entered once", () -> {
            PhieuNhap pn = note();
            FullFunctionalAuditTests.input("3\nTTH0101\nNovel\nGenre\nAuthor\n100\n2\n", pn::them);
            yes(pn.getSoLuongNhap() == 1 && pn.getDSMH()[0] instanceof TieuThuyet, "novel input repeated");
        });
        test("DEEP manual comic is entered once", () -> {
            PhieuNhap pn = note();
            FullFunctionalAuditTests.input("4\nTRU0101\nComic\nGenre\nAuthor\n100\n2\n", pn::them);
            yes(pn.getSoLuongNhap() == 1 && pn.getDSMH()[0] instanceof Truyen, "comic input repeated");
        });
        test("DEEP stock import rejects overflowing total atomically", () -> {
            Kho kho = new Kho();
            PhieuNhap pn = note(book("SCH0101", 5), book("SCH0102", Integer.MAX_VALUE), book("SCH0103", Integer.MAX_VALUE));
            yes(!kho.nhapHangTuPhieu(pn), "overflowing receipt approved");
            yes(kho.getTongSoLuongTon() == 0 && pn.getTrangThai() == PhieuNhap.TrangThaiPN.CHO_DUYET, "partial import");
        });
        test("DEEP empty or zero quantity receipt cannot be approved", () -> {
            for (PhieuNhap pn : new PhieuNhap[]{note(), note(book("SCH0101", 0))})
                yes(!new Kho().nhapHangTuPhieu(pn) && pn.getTrangThai() == PhieuNhap.TrangThaiPN.CHO_DUYET, "empty receipt approved");
        });
        test("DEEP warehouse owns product metadata independently of receipt", () -> {
            MatHang source = book("SCH0101", 5); Kho kho = new Kho();
            yes(kho.nhapHangTuPhieu(note(source)), "stock setup");
            source.setTen("Changed"); source.setMa("SCH0102");
            yes("SCH0101".equals(kho.getDanhSachTon()[0].getMa()), "warehouse SKU changed through receipt");
        });
        test("DEEP negative stock decrement never increases inventory", () -> {
            HangTonKho row = new HangTonKho(book("SCH0101", 5), 5);
            yes(!row.giamSoLuong(-2) && row.getSoLuong() == 5, "negative decrement accepted");
        });
        test("DEEP actual repository receipts load all 50 rows", () -> {
            DanhSachPhieuNhap list = new DanhSachPhieuNhap(); list.docFile("data/phieunhap_input.txt");
            int rows = 0; for (int i = 0; i < list.getSLpn(); i++) rows += list.getMangPhieuNhap()[i].getSoLuongNhap();
            yes(list.getSLpn() == 13 && rows == 50 && list.getDanhSachNCC().getSoLuongNCC() == 7, "real data rejected");
        });
        test("DEEP actual warehouse TXT loads once with all metadata", () -> {
            Kho kho = new Kho(); kho.docFile("data/kho_input.txt");
            int total = kho.getTongSoLuongTon();
            yes(kho.getSoLuongTon() == 50 && total > 0, "warehouse data unavailable");
            kho.docFile("data/kho_input.txt"); yes(kho.getSoLuongTon() == 50 && kho.getTongSoLuongTon() == total, "warehouse duplicated");
            for (MatHang item : PersistenceRegressionTests.products()) {
                MatHang copy = BoChuyenDoiMatHang.docDong(item.toString().replace("Sách; tiếng Việt\nTập 1", "Book"));
                yes(copy.getClass() == item.getClass() && copy.getSoLuong() == item.getSoLuong(), "TXT subtype lost");
            }
        });
        test("DEEP malformed warehouse rows do not hide valid following rows", () -> {
            withFile("BROKEN\nVở;VO0101;Notebook;100;2;0;Maker\nKhác;SPK0101;Item;100;2;Maker\n", path -> {
                Kho kho = new Kho(); kho.docFile(path.toString());
                yes(kho.getSoLuongTon() == 1 && kho.getTongSoLuongTon() == 2, "invalid stock imported or valid row skipped");
            });
        });
        test("DEEP blank receipt ID is skipped without partial supplier", () -> withFile(
                ";21/11/2026;Sách;SCH0101;Book;100;2;Author;Publisher;Genre;CHO_DUYET\n", path -> {
            DanhSachPhieuNhap list = new DanhSachPhieuNhap(); list.docFile(path.toString());
            yes(list.getSLpn() == 0 && list.getDanhSachNCC().getSoLuongNCC() == 0, "blank ID imported");
        }));
        test("DEEP zero-capacity receipt list grows on file import", () -> withFile(
                "PN1;21/11/2026;Khác;SPK0101;Item;100;2;Maker;CHO_DUYET\n", path -> {
            DanhSachPhieuNhap list = new DanhSachPhieuNhap(0); list.docFile(path.toString());
            yes(list.getSLpn() == 1, "empty array never grows");
        }));
        test("DEEP invalid notebook pages rejected before entering session", () -> withFile(
                "PN1;21/11/2026;Vở;VO0101;Notebook;100;2;0;Maker;CHO_DUYET\n", path -> {
            DanhSachPhieuNhap list = new DanhSachPhieuNhap(); list.docFile(path.toString());
            yes(list.getSLpn() == 0, "zero pages poison later session save");
        }));
        test("DEEP processed receipt cannot gain unstocked rows on reread", () -> withFile(
                "PN1;21/11/2026;Khác;SPK0101;One;100;2;Maker;DA_DUYET\n", path -> {
            DanhSachPhieuNhap list = new DanhSachPhieuNhap(); list.docFile(path.toString());
            try { Files.writeString(path, "PN1;21/11/2026;Khác;SPK0102;Two;100;2;Maker;DA_DUYET\n"); }
            catch (java.io.IOException e) { throw new RuntimeException(e); }
            list.docFile(path.toString()); yes(list.timPhieu("PN1").getSoLuongNhap() == 1, "processed receipt changed");
        }));
        test("DEEP deleted shelf item cannot be sold from stale reference", () -> {
            DanhSachMatHang shelf = new DanhSachMatHang(); shelf.khoiPhuc(book("SCH0101", 5));
            HoaDon hd = FullFunctionalAuditTests.order("HD1", shelf.timKiem_hang("SCH0101"), 2);
            invoices(shelf).them(hd); shelf.xoa("SCH0101");
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); yes(!hd.thanhToan(), "charged for deleted shelf stock");
        });
        test("DEEP imported pending invoice can be paid after shelf is stocked", () -> {
            DanhSachMatHang shelf = new DanhSachMatHang(); DanhSachKhachHang customers = new DanhSachKhachHang();
            customers.them(new KhachHang("KH01", "Alice", "090")); DanhSachHoaDon list = new DanhSachHoaDon(shelf, customers);
            withFile("HD1;2026-11-21;KH01\n1\nSCH0101;2;150\n", p -> list.docFile(p.toString()));
            shelf.khoiPhuc(book("SCH0101", 5)); HoaDon hd = list.timKiem("HD1");
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000));
            yes(hd.thanhToan() && shelf.timKiem_hang("SCH0101").getSoLuong() == 3, "pending invoice never reconnects to stock");
        });
        test("DEEP invoice history survives shelf rename and restart", () -> {
            FullFunctionalAuditTests.reset(); DanhSachMatHang shelf = Main.cnHienTai.getDsmh();
            shelf.khoiPhuc(book("SCH0101", 5)); MatHang live = shelf.timKiem_hang("SCH0101");
            HoaDon hd = FullFunctionalAuditTests.order("HISTORY", live, 2);
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); yes(hd.thanhToan(), "payment");
            Main.cnHienTai.getDshd().them(hd); live.setMa("SCH0102"); live.setTen("Renamed");
            Path path = Files.createTempFile("deep-session-", ".properties");
            try {
                LuuTruPhien.ghi(path, PersistenceRegressionTests.data(), Main.dsnv);
                HoaDon restored = LuuTruPhien.doc(path, Main.dsnv).chiNhanh.get(0).getDshd().timKiem("HISTORY");
                yes("SCH0101".equals(restored.getChiTiet()[0].getMathang().getMa()), "historical SKU rewritten");
                yes("Book SCH0101".equals(restored.getChiTiet()[0].getMathang().getTen()), "historical name rewritten");
            } finally { Files.deleteIfExists(path); }
        });
        test("DEEP blank invoice code cannot poison persistence", () -> {
            yes(!invoices(new DanhSachMatHang()).them(FullFunctionalAuditTests.order(" ", book("SCH0101", 5), 1)), "blank invoice accepted");
        });
        test("DEEP revenue report grows beyond 1000 unique products", () -> {
            DanhSachHoaDon list = invoices(new DanhSachMatHang());
            for (int i = 0; i < 1001; i++) {
                HoaDon hd = FullFunctionalAuditTests.order("HD" + i, book(String.format("SCH%04d", i), 1), 1);
                hd.danhDauDaThanhToan(); list.them(hd);
            }
            list.thongKeDoanhThu();
        });
        test("DEEP staff and branch UTF-8 BOM never changes IDs", () -> {
            withFile("\uFEFFADMIN;Admin;090;test;Morning;100;ADMIN;\n", path -> {
                DanhSachNhanVien list = new DanhSachNhanVien(); list.docFile(path.toString());
                yes(list.dangNhap("ADMIN", "test") != null, "BOM in staff ID");
            });
            withFile("\uFEFFCN01;Branch;Address\n", path -> {
                DanhSachChiNhanh list = new DanhSachChiNhanh(new Kho(), new DanhSachKhachHang(), new DanhSachNhanVien());
                list.docFile(path.toString()); yes(list.timTheoMa("CN01") != null, "BOM in branch ID");
            });
        });
        test("DEEP staff codes are trimmed before duplicate detection", () -> withFile(
                " ADMIN ;Admin;090;test;Morning;100;ADMIN;\nADMIN;Other;091;test;Morning;100;ADMIN;\n", path -> {
            DanhSachNhanVien list = new DanhSachNhanVien(); list.docFile(path.toString());
            yes(list.getSoLuong() == 1 && list.dangNhap("ADMIN", "test") != null, "inaccessible/duplicate staff ID");
        }));
        test("DEEP malformed properties are reported as IO failures", () -> {
            Path path = FullFunctionalAuditTests.file("bad=\\uZZZZ\n");
            try {
                boolean rejected = false;
                try { LuuTruPhien.doc(path, new DanhSachNhanVien()); } catch (java.io.IOException expected) { rejected = true; }
                yes(rejected, "malformed properties not rejected");
            } finally { Files.deleteIfExists(path); }
        });
        test("DEEP large prices never saturate to long or turn NaN into free goods", () -> {
            Sach high = new Sach("SCH0101", "High", 1e18, 2, "A", "P", "G");
            yes(high.tinhGiaBan() == 1.5e18, "large selling price saturated");
            high.setGiaNhap(Double.NaN);
            HoaDon hd = new HoaDon("INVALID", LocalDate.now(), new KhachHang("KH1", "A", "0"), null);
            yes(!hd.themChiTiet(new ChiTietHoaDon(1, high)), "NaN product became free goods");
        });
        test("DEEP paid invoice locks detail quantity and preserves customer identity", () -> {
            KhachHang customer = new KhachHang("KH1", "Original", "090");
            HoaDon hd = new HoaDon("PAID", LocalDate.now(), customer, null);
            hd.themChiTiet(new ChiTietHoaDon(1, book("SCH0101", 5)));
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); yes(hd.thanhToan(), "payment");
            customer.setTenKH("Renamed"); customer.setSoDienThoai("091");
            yes("Original".equals(hd.getKhachHang().getTenKH()), "customer history changed");
            FullFunctionalAuditTests.rejects(() -> hd.getChiTiet()[0].setsoLuong(999));
            hd.getChiTiet()[0] = null; yes(hd.getSoLuongChiTiet() == 1 && hd.tinhThanhTien() == 150, "detail array escaped");
        });
        test("DEEP failed customer read preserves complete prior list", () -> {
            DanhSachKhachHang list = new DanhSachKhachHang(); list.them(new KhachHang("OLD", "Old", "090"));
            Path path = Files.createTempFile("bad-utf8-", ".txt");
            try {
                Files.write(path, new byte[]{(byte) 0xc3, (byte) 0x28});
                list.docFile(path.toString()); yes(list.getSoLuong() == 1 && list.timkiem_khachhang("OLD") != null, "read failure erased customers");
            } finally { Files.deleteIfExists(path); }
        });
        test("DEEP failed staff import keeps active authenticated session", () -> {
            FullFunctionalAuditTests.reset(); NhanVien authenticated = Main.dangDangNhap;
            Path missing = Path.of(System.getProperty("java.io.tmpdir"), "bookstore-missing-" + System.nanoTime());
            FullFunctionalAuditTests.input("7\n" + missing + "\n0\n", Main::menuNhanSu);
            yes(Main.dangDangNhap == authenticated && Main.dsnv.getSoLuong() == 5, "failed import logged out/erased staff");
            withFile("broken\n", path -> {
                Main.dsnv.docFile(path.toString()); yes(Main.dsnv.getSoLuong() == 5 && !Main.dsnv.docLanCuoiThanhCong(), "invalid import replaced staff");
            });
        });
        test("DEEP invalid export fields preserve previous staff file", () -> {
            DanhSachNhanVien list = new DanhSachNhanVien();
            list.them(new QuanLy("ADMIN", "Bad;Name", "090", "pw", "Morning", 100));
            withFile("PRIOR CONTENT\n", path -> {
                list.ghiFile(path.toString());
                try { yes("PRIOR CONTENT\n".equals(Files.readString(path)), "prior file truncated"); }
                catch (java.io.IOException e) { throw new RuntimeException(e); }
            });
        });
        test("DEEP malformed path retries and branch IDs cannot become paths", () -> {
            FullFunctionalAuditTests.input("bad\u0000path\n.\n", () -> yes("data/test.txt".equals(TienIch.nhapDuongDan("data/test.txt")), "path retry"));
            DanhSachChiNhanh list = new DanhSachChiNhanh(new Kho(), new DanhSachKhachHang(), new DanhSachNhanVien());
            yes(!list.them("../X", "Branch") && !list.them("..\\X", "Branch"), "branch escaped data folder");
        });
        System.out.println("DEEP AUDIT: " + FullFunctionalAuditTests.passed + " PASS, " + FullFunctionalAuditTests.failed + " FAIL");
        if (FullFunctionalAuditTests.failed > 0) System.exit(1);
    }
}
