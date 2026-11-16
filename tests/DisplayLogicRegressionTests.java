import giohang.ChiTietGioHang;
import giohang.GioHang;
import hoadon.ChiTietHoaDon;
import hoadon.DanhSachHoaDon;
import hoadon.HoaDon;
import interfaces.IThaoTacThucThe;
import khachhang.DanhSachKhachHang;
import khachhang.KhachHang;
import kho.HangTonKho;
import kho.Kho;
import mathang.*;
import phieunhap.DanhSachPhieuNhap;
import phieunhap.PhieuNhap;
import tienich.TienIch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Regression checks for console display, input rules and warehouse/shelf ownership. */
public class DisplayLogicRegressionTests {
    private static int passed, failed;

    @FunctionalInterface interface Task { void run() throws Exception; }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void test(String name, Task task) {
        try {
            task.run();
            passed++;
            System.out.println("PASS DISPLAY " + name);
        } catch (Throwable error) {
            failed++;
            System.err.println("FAIL DISPLAY " + name + ": " + error);
            error.printStackTrace(System.err);
        }
    }

    private static String capture(Task task) throws Exception {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(output);
            task.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static void withInput(String input, Task task) throws Exception {
        Scanner original = TienIch.sc;
        try {
            TienIch.sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8);
            task.run();
        } finally {
            TienIch.sc = original;
        }
    }

    private static Sach book(String code, int quantity) {
        return new Sach(code, "Book", 100, quantity, "Author", "Publisher", "Genre");
    }

    public static void main(String[] args) {
        test("UI-01 warehouse search shows product fields", () -> {
            Kho warehouse = new Kho();
            warehouse.tangTonKho(book("SCH0101", 0), 4);
            String out = capture(() -> withInput("SCH0101\n", warehouse::timHangTon));
            check(out.contains("Book") && out.contains("SCH0101"), "missing product fields");
            check(!out.contains("kho.HangTonKho@"), "Object.toString leaked into menu");
        });

        test("UI-02 every product exposes its actual tax and profit", () -> {
            MatHang[] items = {
                book("SCH0101", 5),
                new SachGiaoKhoa("SGK0101", "Textbook", 100, 5, "Math", "Publisher"),
                new TieuThuyet("TTH0101", "Novel", 100, 5, "Author", "Publisher", "Genre"),
                new Truyen("TRU0101", "Comic", 100, 5, "Author", "Publisher", "Genre"),
                new BaoTapChi("BTC0101", "Magazine", 100, 5, "Publisher", "Science"),
                new VanPhongPham("VPP0101", "Pen", 100, 5, "Maker", "Pen"),
                new SPKhac("SPK0101", "Other", 100, 5, "Maker"),
                new DoChoi("DCH0101", "Toy", 100, 5, "Maker", 6),
                new DungCuHocTap("DCT0101", "Ruler", 100, 5, "Maker", "Ruler"),
                new TrangTri("TTR0101", "Decor", 100, 5, "Maker"),
                new Vo("VO0101", "Notebook", 100, 5, 96, "Maker")
            };
            double[] taxes = {25, 0, 20, 25, 10, 15, 15, 20, 12, 20, 10};
            double[] profits = {20, 0, 20, 20, 18, 20, 15, 30, 18, 19, 15};
            for (int i = 0; i < items.length; i++) {
                check(items[i].getThue() == taxes[i], "tax for " + items[i].getMa());
                check(items[i].getLoiNhuan() == profits[i], "profit for " + items[i].getMa());
            }
            GioHang cart = new GioHang(new KhachHang("KH", "Customer", "090"));
            cart.themVaoGio(items[5], 1);
            check(capture(cart::xemGioHang).contains("Thuế (15%)"), "cart displays zero tax");
        });

        test("UI-03 warehouse header and data columns align", () -> {
            Kho warehouse = new Kho();
            warehouse.tangTonKho(book("SCH0101", 0), 3);
            String out = capture(warehouse::xuat);
            String header = null, row = null;
            for (String line : out.split("\\R")) {
                if (line.startsWith("|Mã")) header = line;
                if (line.startsWith("|SCH0101")) row = line;
            }
            check(header != null && row != null, "missing table lines");
            check(header.indexOf('|', 22) == row.indexOf('|', 22), "name column width differs");
        });

        test("UI-04 purchase notes show supplier name and status", () -> {
            DanhSachPhieuNhap notes = new DanhSachPhieuNhap();
            capture(() -> notes.docFile("tests/fixtures/phieunhap/phieunhap_input.txt"));
            String out = capture(notes::xuat);
            check(out.contains("Trạng thái") && out.contains("CHO_DUYET"), "status column missing");
            check(out.contains("Kim Đồng") && !out.contains("NCC0;Kim Đồng"), "supplier serialized in menu");
        });

        test("UI-05 notebook input prompts once per field", () -> {
            Vo notebook = new Vo();
            String out = capture(() -> withInput("VO0101\nNotebook\n96\nMaker\n100\n1\n", notebook::nhap));
            check(out.split("Nhà sản xuất:", -1).length == 2, "maker prompt repeated");
            check(out.split("Giá nhập hàng:", -1).length == 2, "price prompt repeated");
        });

        test("UI-06 to UI-08 subtype details have their labels", () -> {
            check(capture(() -> book("SCH0101", 1).xuat()).contains("Tác giả: Author"), "book author missing");
            TieuThuyet novel = new TieuThuyet("TTH0101", "Novel", 100, 1,
                    "Author", "Publisher", "Mystery");
            check(capture(novel::xuat).contains("Thể loại: Mystery"), "novel genre missing");
            DungCuHocTap tool = new DungCuHocTap("DCT0101", "Ruler", 100, 1, "Maker", "Ruler");
            check(capture(tool::xuat).contains("Loại dụng cụ: Ruler"), "tool label malformed");
        });

        test("UI-09 invoice detail prints a name and code", () -> {
            ChiTietHoaDon detail = new ChiTietHoaDon(1, book("SCH0101", 2));
            String out = capture(detail::xuat);
            check(out.contains("Mặt hàng: Book (SCH0101)"), "serialized product shown");
            check(!out.contains("Sách;SCH0101"), "TXT row leaked into display");
        });

        test("UI-10 edit and search prompts are punctuated", () -> {
            DanhSachMatHang catalog = new DanhSachMatHang();
            String edit = capture(() -> withInput("MISSING\n", catalog::sua));
            String search = capture(() -> withInput("MISSING\n", catalog::timkiem));
            check(edit.contains("Nhập mã cần sửa: "), "edit prompt");
            check(search.contains("Nhập mã hàng cần tìm: "), "search prompt");
        });

        test("LG-01 transfer to shelf copies product metadata", () -> {
            Kho warehouse = new Kho();
            Sach original = book("SCH0101", 0);
            warehouse.tangTonKho(original, 20);
            DanhSachMatHang shelf = new DanhSachMatHang();
            capture(() -> shelf.nhapTuKho(warehouse, "SCH0101", 5));
            MatHang onShelf = shelf.timKiem_hang("SCH0101");
            HangTonKho inWarehouse = warehouse.getDanhSachTon()[0];
            check(onShelf != inWarehouse.getMatHang(), "shared object after single transfer");
            onShelf.setMa("SCH0102");
            onShelf.setGiaNhap(999);
            check("SCH0101".equals(inWarehouse.getMa()) && inWarehouse.getMatHang().getGiaNhap() == 100,
                    "shelf edit mutated warehouse metadata");
            check(inWarehouse.getSoLuong() == 15, "warehouse count changed");

            Kho bulkWarehouse = new Kho();
            DoChoi toy = new DoChoi("DCH0101", "Toy", 100, 0, "Maker", 6);
            bulkWarehouse.tangTonKho(toy, 20);
            DanhSachMatHang bulkShelf = new DanhSachMatHang();
            capture(() -> bulkShelf.nhapHangLoat(bulkWarehouse));
            MatHang copied = bulkShelf.timKiem_hang("DCH0101");
            check(copied instanceof DoChoi && copied != bulkWarehouse.getDanhSachTon()[0].getMatHang(),
                    "bulk transfer lost subtype or shared reference");
            check(((DoChoi) copied).getdotuoi() == 6 && bulkWarehouse.getDanhSachTon()[0].getSoLuong() == 16,
                    "bulk transfer lost subtype data or count");
        });

        test("LG-02 bulk customer input rejects duplicate codes", () -> {
            DanhSachKhachHang customers = new DanhSachKhachHang();
            String input = "2\nKH01\nAlice\n090\nKH01\nDuplicate\n091\nKH02\nBob\n092\n";
            capture(() -> withInput(input, customers::nhap));
            check(customers.getSoLuong() == 2, "incorrect customer count");
            check("Alice".equals(customers.timkiem_khachhang("KH01").getTenKH()), "duplicate replaced first");
            check("Bob".equals(customers.timkiem_khachhang("KH02").getTenKH()), "retry was not accepted");
        });

        test("LG-03 positive input rejects zero but menu accepts it", () -> {
            final int[] values = new int[2];
            String out = capture(() -> withInput("0\n-1\n3\n", () ->
                    values[0] = TienIch.nhapSoNguyenDuong("Số lượng: ")));
            withInput("0\n", () -> values[1] = TienIch.nhapSoNguyenKhongAm("Chọn: "));
            check(values[0] == 3 && values[1] == 0, "zero rule not separated");
            check(out.contains("lớn hơn 0"), "invalid positive value had no feedback");
        });

        test("LG-03 zero pages, age and new warehouse quantity are rejected", () -> {
            Vo notebook = new Vo();
            capture(() -> withInput("VO0101\nNotebook\n0\n96\nMaker\n100\n1\n", notebook::nhap));
            check(notebook.getSotrang() == 96, "zero pages accepted");
            DoChoi toy = new DoChoi();
            capture(() -> withInput("DCH0101\nToy\nMaker\n0\n6\n100\n1\n", toy::nhap));
            check(toy.getdotuoi() == 6, "zero age accepted");
            HangTonKho stock = new HangTonKho();
            final HangTonKho[] created = {null};
            String input = "1\nSCH0101\nBook\nGenre\nAuthor\nPublisher\n100\n0\n0\n7\n";
            capture(() -> withInput(input, () -> created[0] = stock.taoHangTonMoi()));
            check(created[0] != null && created[0].getSoLuong() == 7, "zero warehouse intake accepted");
        });

        test("LG-03 purchase note rejects a zero-quantity item", () -> {
            PhieuNhap note = new PhieuNhap();
            String input = "1\nSCH0101\nBook\nGenre\nAuthor\nPublisher\n100\n0\n"
                    + "1\nSCH0102\nBook 2\nGenre\nAuthor\nPublisher\n100\n2\n";
            capture(() -> withInput(input, note::them));
            check(note.getSoLuongNhap() == 1 && note.getDSMH()[0].getSoLuong() == 2,
                    "zero-quantity item stored in purchase note");
        });

        test("OOP-01 cart uses ChiTietGioHang and preserves unit price", () -> {
            check(GioHang.class.getDeclaredField("ds").getType().getComponentType() == ChiTietGioHang.class,
                    "cart detail class remains unused");
            Sach product = book("SCH0101", 2);
            GioHang cart = new GioHang(new KhachHang("KH", "Customer", "090"));
            capture(() -> cart.themVaoGio(product, 1));
            double priceAtAdd = cart.tongTien();
            product.setGiaNhap(200);
            check(cart.tongTien() == priceAtAdd, "cart unit price changed after product edit");
            DanhSachHoaDon invoices = new DanhSachHoaDon(new DanhSachMatHang(), new DanhSachKhachHang());
            HoaDon invoice = cart.xacNhanMua(invoices, "HD01", null);
            check(invoice != null && invoice.getChiTiet()[0].getDonGia() == priceAtAdd,
                    "invoice did not preserve cart price");
        });

        test("OOP-02 legacy entity interface has a real implementation", () -> {
            DanhSachMatHang catalog = new DanhSachMatHang();
            catalog.themTuFile(book("SCH0101", 2));
            IThaoTacThucThe operations = catalog;
            check(capture(() -> operations.timKiem("SCH0101")).contains("Book"), "interface search failed");
            capture(() -> operations.xoa("SCH0101"));
            check(catalog.getSoMH() == 0, "interface deletion failed");
        });

        System.out.printf("DISPLAY/LOGIC TESTS: %d PASS, %d FAIL%n", passed, failed);
        if (failed > 0) System.exit(1);
    }
}
