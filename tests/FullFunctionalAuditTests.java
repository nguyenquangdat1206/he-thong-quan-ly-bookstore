import chinhanh.*;
import giohang.*;
import hoadon.*;
import khachhang.*;
import kho.*;
import mathang.*;
import nhacungcap.*;
import nhanvien.*;
import phieunhap.*;
import thanhtoan.*;
import tienich.TienIch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Scanner;

/** Extended functional checks. Failures deliberately remain visible for investigation. */
public class FullFunctionalAuditTests {
    interface Job { void run() throws Exception; }
    static int passed, failed;
    static final LocalDate DATE = LocalDate.of(2026, 11, 21);

    static void yes(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    static void eq(double actual, double expected) {
        yes(Math.abs(actual - expected) < 0.00001, actual + " != " + expected);
    }
    static void rejects(Job job) throws Exception {
        try { job.run(); }
        catch (IllegalArgumentException | IllegalStateException expected) { return; }
        throw new AssertionError("invalid operation accepted");
    }
    static String capture(Job job) throws Exception {
        PrintStream old = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream sink = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setOut(sink);
            job.run();
        } finally { System.setOut(old); }
        return bytes.toString(StandardCharsets.UTF_8);
    }
    static String input(String text, Job job) throws Exception {
        Scanner old = TienIch.sc;
        try (Scanner scanner = new Scanner(text)) {
            TienIch.sc = scanner;
            return capture(job);
        } finally { TienIch.sc = old; }
    }
    static void test(String name, Job job) {
        try { capture(job); passed++; System.out.println("PASS AUDIT " + name); }
        catch (Exception | AssertionError error) {
            failed++;
            System.out.println("FAIL AUDIT " + name + " : " + error);
        }
    }
    static Sach book(String id, int qty) {
        return new Sach(id, "Book " + id, 100, qty, "Author", "Publisher", "Science");
    }
    static NhanVienBanHang seller(String id, String branch) {
        return new NhanVienBanHang(id, "Seller " + id, "090", "pw", "Morning", 100, branch);
    }
    static HoaDon order(String id, MatHang item, int qty) {
        HoaDon result = new HoaDon(id, DATE, new KhachHang("KH01", "Alice", "090"), seller("BH01", "CN01"));
        yes(result.themChiTiet(new ChiTietHoaDon(qty, item)), "order setup");
        return result;
    }
    static Path file(String data) throws Exception {
        Path path = Files.createTempFile("bookstore-functional-", ".txt");
        Files.writeString(path, data, StandardCharsets.UTF_8);
        return path;
    }
    static DanhSachPhieuNhap receipts() throws Exception {
        Path path = file("PN01;21/11/2026;Sách;SCH0101;Book;100;5;Author;Publisher;Science;CHO_DUYET\n");
        try {
            DanhSachPhieuNhap list = new DanhSachPhieuNhap(); list.docFile(path.toString()); return list;
        } finally { Files.deleteIfExists(path); }
    }
    static void reset() {
        Main.kho = new Kho(); Main.dskh = new DanhSachKhachHang(); Main.dsnv = new DanhSachNhanVien();
        Main.dsCN = new DanhSachChiNhanh(Main.kho, Main.dskh, Main.dsnv);
        Main.dsCN.them("CN01", "Branch One", "Address One"); Main.dsCN.them("CN02", "Branch Two", "Address Two");
        Main.dsnv.them(new QuanLy("ADMIN", "Admin", "090", "pw", "Morning", 100));
        Main.dsnv.them(new QuanLyChiNhanh("QL01", "Manager", "090", "pw", "Morning", 100, "CN01"));
        Main.dsnv.them(seller("BH01", "CN01")); Main.dsnv.them(seller("BH02", "CN02"));
        Main.dsnv.them(new NhanVienNhapKho("KHO01", "Warehouse", "090", "pw", "Morning", 100));
        Main.dsncc = new DanhSachNCC(); Main.dspn = new DanhSachPhieuNhap(Main.dsncc);
        Main.cnHienTai = Main.dsCN.get(0); Main.dangDangNhap = Main.dsnv.timTheoMa("ADMIN");
        Main.nvBanHang = null; Main.coTheGhiNhanVien = true;
        Main.dsnv.setChuanHoaChiNhanh(id -> {
            ChiNhanh branch = Main.dsCN.timTheoMa(id); return branch == null ? null : branch.getMa();
        });
    }
    public static void main(String[] args) throws Exception {
        test("BRANCH create, trim, duplicate, invalid fields", () -> {
            reset(); yes(!Main.dsCN.them(" cn01 ", "Other"), "duplicate branch");
            yes(!Main.dsCN.them("", "Empty") && !Main.dsCN.them("CN;X", "Bad"), "invalid branch");
            yes(Main.dsCN.them(" CN03 ", " Branch Three ", null), "valid branch");
            yes("Branch Three".equals(Main.dsCN.timTheoMa("cn03").getTen()), "trim/name");
        });
        test("BRANCH grows past initial four slots", () -> {
            reset(); for (int i=3;i<=20;i++) yes(Main.dsCN.them("CN"+i,"Branch "+i), "growth");
            yes(Main.dsCN.getSoLuong()==20 && Main.dsCN.timTheoMa("CN20")!=null,"last branch");
        });
        test("BRANCH delete empty branch only", () -> {
            reset(); yes(!Main.dsCN.xoa("CN01"), "deleted branch with employees");
            Main.dsCN.them("EMPTY","Empty"); yes(Main.dsCN.xoa("empty"),"empty delete");
            yes(!Main.dsCN.xoa("MISSING"),"missing delete");
        });
        test("BRANCH cannot delete shelf or invoice data", () -> {
            reset(); Main.dsCN.them("CN03","Three"); ChiNhanh branch=Main.dsCN.timTheoMa("CN03");
            TestSupport.stockShelf(branch.getDsmh(),book("SCH0101",2)); yes(!Main.dsCN.xoa("CN03"),"shelf deleted");
            branch.getDsmh().xoa("SCH0101"); branch.getDshd().them(order("HD01",book("SCH0102",2),1));
            yes(!Main.dsCN.xoa("CN03"),"invoice branch deleted");
        });
        test("BRANCH UTF-8 save, reload and duplicate import", () -> {
            reset(); Main.dsCN.them("CN03","Chi nhánh thử","Đường số 3"); Path path=file("");
            try {
                Main.dsCN.ghiFile(path.toString()); DanhSachChiNhanh copy=new DanhSachChiNhanh(Main.kho,Main.dskh,Main.dsnv);
                copy.docFile(path.toString()); copy.docFile(path.toString());
                yes(copy.getSoLuong()==3 && "Đường số 3".equals(copy.timTheoMa("CN03").getDiaChi()),"roundtrip");
            } finally { Files.deleteIfExists(path); }
        });
        test("BRANCH same SKU stays separate and warehouse totals conserved", () -> {
            reset(); Main.kho.tangTonKho(book("SCH0101",0),20);
            Main.dsCN.get(0).getDsmh().nhaptukho(Main.kho,"SCH0101",4);
            Main.dsCN.get(1).getDsmh().nhaptukho(Main.kho,"SCH0101",6);
            MatHang a=Main.dsCN.get(0).getDsmh().timKiem_hang("SCH0101"), b=Main.dsCN.get(1).getDsmh().timKiem_hang("SCH0101");
            yes(a!=b && a.getSoLuong()==4 && b.getSoLuong()==6 && Main.kho.getTongSoLuongTon()==10,"isolation");
            HoaDon hd=order("HD01",a,2); hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); yes(hd.thanhToan(),"pay");
            yes(a.getSoLuong()==2 && b.getSoLuong()==6 && Main.kho.getTongSoLuongTon()==10,"cross branch deduction");
        });
        test("BRANCH invoice IDs may repeat across branches", () -> {
            reset(); yes(Main.dsCN.get(0).getDshd().them(order("HD01",book("SCH0101",5),1)),"first");
            yes(Main.dsCN.get(1).getDshd().them(order("HD01",book("SCH0101",5),1)),"other branch");
        });
        test("STAFF all four roles and branch membership", () -> {
            reset(); yes(Main.dsnv.getSoLuong()==5 && Main.dsnv.demTheoChiNhanh("CN01")==2,"role count");
            yes(!Main.dsnv.timTheoMa("ADMIN").thuocChiNhanh() && !Main.dsnv.timTheoMa("KHO01").thuocChiNhanh(),"global roles");
            yes(Main.dsnv.dangNhap(" bh02 ","pw")!=null && Main.dsnv.dangNhap("BH02","bad")==null,"login");
        });
        test("STAFF rejects NaN salary without partial name update", () -> {
            reset(); yes(!Main.dsnv.sua("BH01","Changed",null,null,Double.NaN,null),"NaN salary accepted");
            yes("Seller BH01".equals(Main.dsnv.timTheoMa("BH01").getHoTen()),"partial update");
        });
        test("STAFF edit validates branch before updating fields", () -> {
            reset(); input("BH01\nChanged\n\n\n\n\nNO_BRANCH\n",Main.dsnv::sua);
            yes("Seller BH01".equals(Main.dsnv.timTheoMa("BH01").getHoTen()),"partial edit");
        });
        test("STAFF eight-column file preserves manager and branch assignments", () -> {
            reset(); Path path=file(""); try {
                Main.dsnv.ghiFile(path.toString()); DanhSachNhanVien copy=new DanhSachNhanVien(); copy.docFile(path.toString());
                yes(copy.getSoLuong()==5 && "CN02".equals(copy.timTheoMa("BH02").getMaChiNhanh()),"assignment");
                yes(copy.timTheoMa("QL01") instanceof QuanLyChiNhanh,"manager role");
            } finally { Files.deleteIfExists(path); }
        });
        test("MAIN admin chooses branch and session clears on logout", () -> {
            reset(); String out=input("ADMIN\npw\nCN02\n0\n",Main::dangNhap);
            yes(out.contains("Branch Two") && Main.dangDangNhap==null && Main.cnHienTai==null,"session cleanup");
        });
        test("MAIN branch manager uses assigned branch only", () -> {
            reset(); String out=input("QL01\npw\n6\n0\n",Main::dangNhap);
            yes(out.contains("QUẢN LÝ CHI NHÁNH: Branch One") && out.contains("Seller BH01") && !out.contains("Seller BH02"),"wrong branch staff");
        });
        test("MAIN seller cannot access admin actions", () -> {
            reset(); String out=input("BH02\npw\n9\n0\n",Main::dangNhap);
            yes(out.contains("BÁN HÀNG - Branch Two") && out.contains("Không được phép"),"seller gate");
            yes(!out.contains("--- CHI NHÁNH ---"),"admin menu exposed");
        });
        test("MAIN warehouse cannot access HR", () -> {
            reset(); String out=input("KHO01\npw\n7\n0\n",Main::dangNhap);
            yes(out.contains("Không được phép") && !out.contains("QUẢN LÝ NHÂN SỰ"),"warehouse gate");
        });
        test("MAIN invalid branch account cannot log in", () -> {
            reset(); Main.dsnv.timTheoMa("BH01").setMaChiNhanh("MISSING"); Main.dangDangNhap=null;
            String out=input("BH01\npw\n",Main::dangNhap); yes(out.contains("chưa được gán") && Main.dangDangNhap==null,"invalid login");
        });
        test("MAIN admin may cancel branch and gets a guard for shelf", () -> {
            reset(); String out=input("ADMIN\npw\n0\n3\n0\n",Main::dangNhap);
            yes(out.contains("Chưa chọn chi nhánh"),"missing branch guard");
        });
        test("MAIN HR protects current admin", () -> {
            reset(); input("4\nADMIN\n0\n",Main::menuNhanSu); yes(Main.dsnv.timTheoMa("ADMIN")!=null,"self deleted");
        });
        test("MAIN checkout requires a seller at selected branch", () -> {
            reset(); Main.dsCN.them("CN03","Empty"); Main.cnHienTai=Main.dsCN.timTheoMa("CN03");
            yes(input("",Main::menuGioHang).contains("ít nhất một tài khoản bán hàng"),"seller guard");
        });
        test("WAREHOUSE exact capacity and refusal leave totals intact", () -> {
            Kho k=new Kho(); yes(k.tangTonKho(book("SCH0101",0),10000),"capacity boundary");
            yes(!k.tangTonKho(book("SCH0102",0),1) && k.getTongSoLuongTon()==10000,"over capacity");
        });
        test("WAREHOUSE interactive edit respects capacity", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),5); input("SCH0101\n10001\n",k::sua);
            yes(k.getTongSoLuongTon()==5,"capacity edit"); input("SCH0101\n0\n",k::sua); yes(k.getTongSoLuongTon()==0,"zero adjustment");
        });
        test("WAREHOUSE missing delete preserves rows", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),5); input("MISSING\n",k::xoa); yes(k.getTongSoLuongTon()==5,"missing delete");
        });
        test("WAREHOUSE rejects case-variant duplicate SKU as a new row", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),3); k.tangTonKho(book("sch0101",0),2);
            yes(k.getSoLuongTon()==1 && k.getTongSoLuongTon()==5,"same SKU split by case");
        });
        test("SHELF negative transfer cannot create negative stock", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),5); DanhSachMatHang shelf=new DanhSachMatHang(k);
            shelf.nhaptukho(k,"SCH0101",-2); yes(k.getTongSoLuongTon()==5 && shelf.getSoMH()==0,"negative transfer altered stock");
        });
        test("SHELF unknown SKU and excessive transfer do nothing", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),5); DanhSachMatHang shelf=new DanhSachMatHang(k);
            shelf.nhaptukho(k,"NO",1); shelf.nhaptukho(k,"SCH0101",6);
            yes(k.getTongSoLuongTon()==5 && shelf.getSoMH()==0,"rejected transfer changed state");
        });
        test("SHELF bulk transfer, return and metadata remain consistent", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),20); DanhSachMatHang shelf=new DanhSachMatHang(k);
            shelf.nhapHangLoat(k); yes(shelf.timKiem_hang("SCH0101").getSoLuong()==4 && k.getTongSoLuongTon()==16,"bulk");
            yes(shelf.traHangVeKho(k,"SCH0101",4) && k.getTongSoLuongTon()==20,"return");
            yes(!shelf.traHangVeKho(k,"SCH0101",1),"overreturn");
        });
        test("SHELF search filter and pending warehouse report", () -> {
            Kho k=new Kho(); k.tangTonKho(book("SCH0101",0),20); k.tangTonKho(book("SCH0102",0),3);
            DanhSachMatHang shelf=new DanhSachMatHang(k); shelf.nhaptukho(k,"SCH0101",2);
            yes(input("1\n1\nAuthor\n",shelf::timkiem_nangcao).contains("Book SCH0101"),"filter");
            String out=capture(() -> shelf.thongKeHangChuaLenKe(k)); yes(out.contains("SCH0102") && !out.contains("SCH0101"),"pending stock");
        });
        test("CUSTOMER search by code, partial name and phone", () -> {
            DanhSachKhachHang list=new DanhSachKhachHang(); input("KH01\nAlice\n090123\n",list::them);
            yes(input("1\nKH01\n",list::timKiem).contains("Alice"),"code search");
            yes(input("2\nLIC\n",list::timKiem).contains("Alice"),"name search");
            yes(input("3\n012\n",list::timKiem).contains("Alice"),"phone search");
        });
        test("CUSTOMER edit and delete missing/known IDs", () -> {
            DanhSachKhachHang list=new DanhSachKhachHang(); input("KH01\nAlice\n090\n",list::them);
            input("1\nBob\n2\n091\n0\n",() -> list.sua("KH01")); yes("Bob".equals(list.timkiem_khachhang("KH01").getTenKH()),"edit");
            list.xoa("NO"); yes(list.getSoLuong()==1,"missing delete"); list.xoa("kh01"); yes(list.getSoLuong()==0,"delete");
        });
        test("CUSTOMER BOM, duplicate and malformed rows", () -> {
            Path path=file("\uFEFFKH01;Alice;090\nbroken\nkh01;Duplicate;091\nKH02;Bob;092\n");
            try { DanhSachKhachHang list=new DanhSachKhachHang(); list.docFile(path.toString()); yes(list.getSoLuong()==2,"bad rows"); }
            finally { Files.deleteIfExists(path); }
        });
        test("CUSTOMER UTF-8 save and reload", () -> {
            DanhSachKhachHang list=new DanhSachKhachHang(); input("KH01\nNguyễn An\n090\n",list::them); Path path=file("");
            try { list.ghiFile(path.toString()); DanhSachKhachHang copy=new DanhSachKhachHang(); copy.docFile(path.toString()); yes("Nguyễn An".equals(copy.getDs()[0].getTenKH()),"UTF8"); }
            finally { Files.deleteIfExists(path); }
        });
        test("SUPPLIER manual create, edit and delete", () -> {
            DanhSachNCC list=new DanhSachNCC(); input("NCCX\nSupplier\nAddress\n090\na@b.vn\n",list::them);
            input("NCCX\nChanged\nNew Address\n091\nb@c.vn\n",list::sua); yes("Changed".equals(list.timNCC("NCCX").getTenNCC()),"edit");
            input("NCCX\n",list::xoa); yes(list.getSoLuongNCC()==0,"delete");
        });
        test("SUPPLIER generated ID avoids collision after deletion", () -> {
            DanhSachNCC list=new DanhSachNCC(); list.timHoacTaoTheoTen("A"); list.timHoacTaoTheoTen("B"); list.timHoacTaoTheoTen("C");
            input("NCC1\n",list::xoa); NhaCungCap added=list.timHoacTaoTheoTen("D"); yes(!"NCC2".equals(added.getMaNCC()),"reused existing code");
        });
        test("RECEIPT delete existing ID from list", () -> {
            DanhSachPhieuNhap list=receipts(); input("PN01\n",list::xoa); yes(list.getSLpn()==0,"not deleted");
        });
        test("RECEIPT delete missing ID leaves valid receipt intact", () -> {
            DanhSachPhieuNhap list=receipts(); input("MISSING\n",list::xoa); yes(list.getSLpn()==1 && list.timPhieu("PN01")!=null,"missing delete mutated state");
        });
        test("RECEIPT select existing ID for editing", () -> {
            DanhSachPhieuNhap list=receipts(); input("PN01\n4\n",list::sua); yes(list.getSLpn()==1,"edit changed count");
        });
        test("RECEIPT editing a missing ID preserves the list without entering an editor", () -> {
            DanhSachPhieuNhap list=receipts();
            String out=input("MISSING\n",list::sua);
            yes(list.getSLpn()==1 && list.timPhieu("PN01")!=null,"missing edit changed receipts");
            yes(out.contains("Không có mã phiếu nhập") && !out.contains("MENU SỬA"),"missing edit was not rejected");
        });
        test("RECEIPT product editor accepts zero to exit", () -> {
            PhieuNhap note=receipts().timPhieu("PN01"); input("SCH0101\n0\n",note::sua); yes(note.getSoLuongNhap()==1,"exit changed rows");
        });
        test("RECEIPT TXT restores supplier from the product publisher", () -> {
            DanhSachPhieuNhap list=receipts();
            ((Sach)list.timPhieu("PN01").getDSMH()[0]).setNhaXuatBan("Renamed");
            Path path=file("");
            try {
                list.ghiFile(path.toString()); DanhSachPhieuNhap copy=new DanhSachPhieuNhap(); copy.docFile(path.toString());
                yes("Renamed".equals(copy.timPhieu("PN01").getChiTiet().getNhaCungCap().getTenNCC()),"supplier rename lost on reload");
            } finally { Files.deleteIfExists(path); }
        });
        test("RECEIPT approved and canceled entries are never applied", () -> {
            PhieuNhap note=receipts().timPhieu("PN01"); Kho k=new Kho(); note.setTrangThai(PhieuNhap.TrangThaiPN.DA_HUY);
            yes(!k.nhapHangTuPhieu(note),"canceled stock"); note.setTrangThai(PhieuNhap.TrangThaiPN.DA_DUYET);
            yes(!k.nhapHangTuPhieu(note) && k.getTongSoLuongTon()==0,"processed stock");
        });
        test("CART empty and invalid product rejected", () -> {
            GioHang cart=new GioHang(new KhachHang("KH01","Alice","090")); DanhSachHoaDon list=new DanhSachHoaDon(new DanhSachMatHang(),new DanhSachKhachHang());
            cart.themVaoGio(null,1); cart.themVaoGio(book("SCH0101",2),0); yes(cart.isRong() && cart.xacNhanMua(list,"HD01",seller("BH01","CN01"))==null,"empty checkout");
        });
        test("CART resizes beyond five rows and keeps quantities", () -> {
            GioHang cart=new GioHang(new KhachHang("KH01","Alice","090"));
            for(int i=1;i<=12;i++) cart.themVaoGio(book("SCH"+String.format("%04d",i),3),2);
            yes(cart.getSoDong()==12,"cart growth"); eq(cart.tongTien(),3600);
        });
        test("CART rejects integer-overflow quantity addition", () -> {
            GioHang cart=new GioHang(new KhachHang("KH01","Alice","090")); MatHang item=book("SCH0101",5);
            cart.themVaoGio(item,2); cart.themVaoGio(item,Integer.MAX_VALUE); eq(cart.tongTien(),300);
        });
        test("INVOICE two pending orders cannot oversell when paid", () -> {
            MatHang item=book("SCH0101",5); HoaDon a=order("A",item,4), b=order("B",item,4);
            a.setHinhThucThanhToan(new ThanhToanTienMat(1000)); b.setHinhThucThanhToan(new ThanhToanTienMat(1000));
            yes(a.thanhToan() && !b.thanhToan() && item.getSoLuong()==1,"oversold");
        });
        test("INVOICE editable before payment and immutable after payment", () -> {
            MatHang item=book("SCH0101",10); HoaDon hd=order("A",item,2); hd.suaSoLuongChiTiet(0,3); eq(hd.tinhThanhTien(),450);
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); yes(hd.thanhToan(),"pay");
            rejects(() -> hd.suaSoLuongChiTiet(0,1)); rejects(() -> hd.xoaChiTiet(0));
            yes(!hd.themChiTiet(new ChiTietHoaDon(1,item)),"paid append");
        });
        test("INVOICE invalid line positions and final line deletion rejected", () -> {
            HoaDon hd=order("A",book("SCH0101",5),2); rejects(() -> hd.suaSoLuongChiTiet(-1,1)); rejects(() -> hd.suaSoLuongChiTiet(0,0)); rejects(() -> hd.xoaChiTiet(0));
        });
        test("INVOICE paid deletion blocked by list", () -> {
            reset(); HoaDon hd=order("A",book("SCH0101",5),2); hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); hd.thanhToan();
            Main.cnHienTai.getDshd().them(hd); input("A\n",Main.cnHienTai.getDshd()::xoa); yes(Main.cnHienTai.getDshd().getSoLuong()==1,"paid delete");
        });
        test("PAYMENT cash exact amount and insufficient cash", () -> {
            yes(!new ThanhToanTienMat(99).xuLyThanhToan(100),"short cash accepted"); yes(new ThanhToanTienMat(100).xuLyThanhToan(100),"exact cash rejected");
        });
        test("PAYMENT cash NaN must not mark invoice paid", () -> {
            MatHang item=book("SCH0101",5); HoaDon hd=order("A",item,2); hd.setHinhThucThanhToan(new ThanhToanTienMat(Double.NaN));
            yes(!hd.thanhToan() && !hd.isDaThanhToan() && item.getSoLuong()==5,"NaN cash marked paid");
        });
        test("PAYMENT rejects nonfinite and negative cash and totals", () -> {
            for (double invalid : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -1}) {
                yes(!new ThanhToanTienMat(invalid).xuLyThanhToan(100),"invalid cash accepted: " + invalid);
                yes(!new ThanhToanTienMat(100).xuLyThanhToan(invalid),"invalid total accepted: " + invalid);
            }
        });
        test("PAYMENT invalid card and valid card", () -> {
            yes(!new ThanhToanThe("", "Bank").xuLyThanhToan(100),"empty transaction");
            yes(!new ThanhToanThe("TX", " ").xuLyThanhToan(100),"empty bank");
            yes(new ThanhToanThe("TX","Bank").xuLyThanhToan(100),"valid card");
        });
        test("MAIN cash entry rejects nonfinite and negative amounts", () -> {
            double[] result=new double[1]; input("NaN\nInfinity\n-1\n100\n",() -> result[0]=Main.nhapSoTienKhongAm("Cash")); eq(result[0],100);
        });
        test("UTIL price entry rejects Infinity", () -> {
            double[] result=new double[1]; input("Infinity\n100\n",() -> result[0]=TienIch.nhapGiaTien("Price")); eq(result[0],100);
        });
        test("INVOICE saved paid state reloads without deducting stock", () -> {
            reset(); input("KH01\nAlice\n090\n",Main.dskh::them); TestSupport.stockShelf(Main.cnHienTai.getDsmh(),book("SCH0101",5));
            MatHang item=Main.cnHienTai.getDsmh().timKiem_hang("SCH0101"); HoaDon hd=order("A",item,2);
            hd.setHinhThucThanhToan(new ThanhToanThe("TX","Bank")); hd.thanhToan(); Main.cnHienTai.getDshd().them(hd); Path path=file("");
            try {
                Main.cnHienTai.getDshd().ghiFile(path.toString()); DanhSachHoaDon copy=new DanhSachHoaDon(Main.cnHienTai.getDsmh(),Main.dskh,Main.dsnv);
                copy.docFile(path.toString()); copy.docFile(path.toString());
                yes(copy.getSoLuong()==1 && copy.timKiem("A").isDaThanhToan() && item.getSoLuong()==3,"paid reload");
                yes(copy.timKiem("A").getNhanVienLap()==Main.dsnv.timTheoMa("BH01"),"seller identity");
            } finally { Files.deleteIfExists(path); }
        });
        test("INVOICE malformed record does not discard later valid invoice", () -> {
            reset(); input("KH01\nAlice\n090\n",Main.dskh::them);
            Path path=file("BROKEN\nHDVALID;2026-11-21;KH01;BH01;Seller;1\n1\nSCH0101;2;150\n");
            try { Main.cnHienTai.getDshd().docFile(path.toString()); yes(Main.cnHienTai.getDshd().timKiem("HDVALID")!=null,"valid record after malformed row lost"); }
            finally { Files.deleteIfExists(path); }
        });
        test("INVOICE invalid date, count and detail do not leave partial invoices", () -> {
            for (String invalid : new String[]{
                    "BAD;not-a-date;KH01\n1\nSCH0101;1;150\n",
                    "BAD;2026-11-21;KH01\nnot-a-count\n",
                    "BAD;2026-11-21;KH01\n2147483647\n",
                    "BAD;2026-11-21;KH01\n2\nSCH0101;1;150\nSCH0102;invalid;150\n"}) {
                reset(); input("KH01\nAlice\n090\n",Main.dskh::them);
                Path path=file(invalid + "VALID;2026-11-21;KH01;BH01;Seller;1\n1\nSCH0101;2;150\n");
                try {
                    Main.cnHienTai.getDshd().docFile(path.toString());
                    yes(Main.cnHienTai.getDshd().getSoLuong()==1
                            && Main.cnHienTai.getDshd().timKiem("VALID")!=null,"partial or missing invoice after bad record");
                } finally { Files.deleteIfExists(path); }
            }
        });
        test("REPORT paid revenue, pending total and customer ranking", () -> {
            reset(); input("KH01\nAlice\n090\n",Main.dskh::them); MatHang item=book("SCH0101",10);
            HoaDon paid=order("PAID",item,2), pending=order("PENDING",item,3); paid.setHinhThucThanhToan(new ThanhToanThe("TX","Bank")); paid.thanhToan();
            DanhSachHoaDon list=Main.cnHienTai.getDshd(); list.them(paid); list.them(pending);
            String out=capture(list::thongKeDoanhThu); yes(out.matches("(?s).*Doanh thu thực thu:\\s+300.*") && out.matches("(?s).*Tổng tiền chờ thanh toán:\\s+450.*"),"totals");
            out=capture(list::thongKeTopKhachHang); yes(out.contains("Alice") && out.contains("Tổng chi: 300"),"customer rank");
        });
        test("REPORT invoice filters by paid state, employee and date", () -> {
            reset(); DanhSachHoaDon list=Main.cnHienTai.getDshd(); list.them(order("FINDME",book("SCH0101",5),2));
            yes(input("3\n",list::traCuuVaLoc).contains("FINDME"),"pending filter");
            yes(input("4\nBH01\n",list::traCuuVaLoc).contains("FINDME"),"employee filter");
            yes(input("5\n21/11/2026\n21/11/2026\n",list::traCuuVaLoc).contains("FINDME"),"date boundary");
            yes(input("5\n22/11/2026\n21/11/2026\n",list::traCuuVaLoc).contains("Khoảng ngày không hợp lệ"),"reversed date");
        });
        System.out.println("FULL FUNCTIONAL AUDIT: " + passed + " PASS, " + failed + " FAIL");
        if (failed>0) System.exit(1);
    }
}
