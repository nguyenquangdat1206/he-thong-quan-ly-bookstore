import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Objects;

import nhanvien.*;
import mathang.*;
import kho.*;
import khachhang.*;
import hoadon.*;
import giohang.*;
import phieunhap.*;
import nhacungcap.*;
import thanhtoan.*;
import tienich.TienIch;
import tichhop.LuuTruMatHang;

/** No external dependency. Run with scripts/test.sh or test.bat. */
public class IntegrationTests {
    private static int ok, fail;
    private static void expect(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    private static void check(String name, ThrowingTask task) {
        try { task.run(); ok++; System.out.println("PASS " + name); }
        catch (Throwable t) { fail++; System.err.println("FAIL " + name + " : " + t); t.printStackTrace(System.err); }
    }
    @FunctionalInterface interface ThrowingTask { void run() throws Exception; }
    private static Sach sach(String ma,int soLuong) { return new Sach(ma,"Sách Java",20000,soLuong,"Tác giả","NXB","Kỹ thuật"); }
    public static void main(String[] args) throws Exception {
        check("TV5 - add role, search, login, reject duplicate", () -> {
            DanhSachNhanVien ds = new DanhSachNhanVien();
            expect(ds.them(new QuanLy("A","Quản lý","0900","p","Sáng",100)), "add admin");
            expect(!ds.them(new QuanLy("a","Duplicate","0900","p","Sáng",1)), "duplicate case insensitive");
            expect(ds.them(new NhanVienBanHang("B","Bán hàng","0901","p2","Chiều",50)), "add sales");
            expect(ds.them(new NhanVienNhapKho("C","Kho hàng","0902","p3","Tối",70)), "add stock and grow array");
            expect(ds.getSoLuong()==3, "size");
            expect(ds.dangNhap("B","wrong")==null, "wrong password should fail");
            expect(ds.dangNhap("b","p2") instanceof NhanVienBanHang,"sales login");
            expect("NHAP_KHO".equals(ds.dangNhap("C","p3").getQuyen()),"stock role");
            expect(ds.timTheoMa("missing")==null, "missing");
            expect(ds.tongLuongCoBan()==220,"total payroll");
        });
        check("TV5 - update salary, name, shift and password", () -> {
            DanhSachNhanVien ds = new DanhSachNhanVien();
            ds.them(new NhanVienBanHang("B","Old","0901","old","Sáng",100));
            expect(!ds.sua("B", "New",null,null,-1.0,null),"negative salary rejected");
            expect(ds.sua("B","Tên mới","0911","Tối",150.0,"new"),"update");
            expect(ds.dangNhap("B","old")==null,"old password expired");
            expect(ds.dangNhap("B","new")!=null,"new password valid");
            expect(ds.timTheoMa("B").getLuongCoBan()==150,"new salary");
            expect("Tên mới".equals(ds.timTheoMa("B").getHoTen()),"unicode name");
        });
        check("TV5 - deletion shift, no missing, protect last admin", () -> {
            DanhSachNhanVien ds = new DanhSachNhanVien();
            ds.them(new QuanLy("ADMIN","Admin","0900","p","Sáng",100));
            ds.them(new NhanVienBanHang("A","Sales A","0901","p","Sáng",100));
            ds.them(new NhanVienBanHang("B","Sales B","0902","p","Sáng",100));
            expect(!ds.xoa("ADMIN"), "last admin protected");
            expect(ds.xoa("A"),"remove middle");
            expect(ds.getSoLuong()==2 && "B".equals(ds.getDanhSach()[1].getMaNguoi()),"array shifted");
            expect(!ds.xoa("UNKNOWN"),"not found rejected");
            ds.them(new QuanLy("ADMIN2","Second","0910","p","Sáng",100));
            expect(ds.xoa("ADMIN"),"admin removal allowed when another remains");
        });
        check("TV5 - UTF8 file read-write, invalid and duplicate rows", () -> {
            Path f = Files.createTempFile("nhanvien-", ".txt");
            DanhSachNhanVien ds = new DanhSachNhanVien();
            ds.them(new QuanLy("ADM","Nguyễn Ánh","0900","pw","Cả ngày",1300000));
            ds.them(new NhanVienNhapKho("KH","Lê Bình","0901","pw2","Chiều",800000));
            ds.ghiFile(f.toString());
            DanhSachNhanVien r = new DanhSachNhanVien();
            r.docFile(f.toString());
            expect(r.getSoLuong()==2, "read 2");
            expect("Nguyễn Ánh".equals(r.timTheoMa("ADM").getHoTen()), "UTF8 roundtrip");
            expect("NHAP_KHO".equals(r.timTheoMa("KH").getQuyen()), "subclass restored");
            Files.writeString(f, Files.readString(f,StandardCharsets.UTF_8)
                + "broken;row\nADM;Duplicate;0900;pw;Sáng;100;ADMIN\nBAD;Bad;0901;pw;Sáng;-1;ADMIN\n", StandardCharsets.UTF_8);
            r.docFile(f.toString());
            expect(r.getSoLuong()==2,"invalid rows skipped");
            Files.deleteIfExists(f);
        });
        check("TV5 - legacy Java-text staff import safely without executing code", () -> {
            Path f = Files.createTempFile("staff-legacy-", ".txt");
            try {
                String legacy = "dsNhanVien.them(new QuanLy(\"admin\",\"Quản trị\",\"0900\",\"demo\",\"Sáng\",100));\n"
                    + "dsNhanVien.them(new NhanVienBanHang(\"bh01\",\"Bán hàng\",\"0901\",\"demo2\",\"Chiều\",60));\n"
                    + "dsNhanVien.them(new NhanVienNhapKho(\"kho01\",\"Nhập kho\",\"0902\",\"demo3\",\"Chiều\",70));\n";
                Files.writeString(f, legacy, StandardCharsets.UTF_8);
                DanhSachNhanVien ds = new DanhSachNhanVien();
                ds.docFile(f.toString());
                expect(ds.getSoLuong()==3 && ds.laDuLieuDinhDangCu(), "recognize legacy records");
                expect(ds.dangNhap("admin","demo")!=null, "legacy admin login");
                expect(ds.soLuongAdmin()==1, "admin privilege preserved");
                expect(Files.readString(f).equals(legacy),"source file preserved byte for byte");
                Files.writeString(f, legacy + "System.exit(99);\n");
                ds.docFile(f.toString());
                expect(ds.getSoLuong()==3, "unexpected Java code ignored, never executed");
            } finally { Files.deleteIfExists(f); }
        });
        check("TV1/TV5 - console role gate", () -> {
            NhanVien q = new QuanLy("A","Admin","0900","pw","Sáng",100);
            NhanVien b = new NhanVienBanHang("B","Sale","0901","pw","Sáng",100);
            NhanVien k = new NhanVienNhapKho("K","Kho","0902","pw","Sáng",100);
            expect(Main.duocPhep(q,"BAN_HANG") && Main.duocPhep(q,"NHAP_KHO"),"admin all");
            expect(Main.duocPhep(b,"BAN_HANG") && !Main.duocPhep(b,"NHAP_KHO"),"sales no stock");
            expect(Main.duocPhep(k,"NHAP_KHO") && !Main.duocPhep(k,"BAN_HANG"),"stock no sales");
            expect(!Main.duocPhep(null,"ADMIN"),"guest forbidden");
        });
        check("TV2/TV4 - capacity, stock to shelf, reserve counts", () -> {
            Kho kho = new Kho(); DanhSachMatHang ds = new DanhSachMatHang();
            Sach s = sach("S10",0);
            expect(kho.tangTonKho(s,10),"stock 10");
            expect(!kho.tangTonKho(s,10000),"reject beyond capacity");
            ds.nhapTuKho(kho,"S10",4);
            expect(ds.getSoMH()==1,"shelf entry");
            expect(ds.timKiem_hang("S10").getSoLuong()==4,"4 on shelf");
            expect(kho.getDanhSachTon()[0].getSoLuong()==6,"6 in warehouse");
            ds.nhapTuKho(kho,"S10",20);
            expect(kho.getDanhSachTon()[0].getSoLuong()==6,"no overselling stock");
        });
        check("TV4 - stock file records WAREHOUSE not SHELF quantity", () -> {
            Kho kho = new Kho(); DanhSachMatHang ds = new DanhSachMatHang();
            kho.tangTonKho(sach("SAVE01",0),25);
            ds.nhapTuKho(kho,"SAVE01",10);
            Path f = Files.createTempFile("kho-", ".txt");
            kho.ghiFile(f.toString());
            expect(Files.readString(f,StandardCharsets.UTF_8).contains(";15;"),"saved 15 in warehouse");
            Kho loaded = new Kho(); loaded.docFile(f.toString());
            expect(loaded.getDanhSachTon()[0].getSoLuong()==15,"reload 15");
            Files.deleteIfExists(f);
        });
        check("TV4 - sample warehouse load", () -> {
            Kho kho = new Kho(); kho.docFile("tests/fixtures/kho/kho_input.txt");
            expect(kho.getSoLuongTon()>=5, "at least 5 sample items");
            expect(kho.getTongSoLuongTon()>0,"positive stock");
        });
        check("TV3 - cart add, duplicate accumulation, invalid quantity", () -> {
            MatHang mh = sach("S01",6); GioHang gio = new GioHang(new KhachHang("K01","Khách","0900"));
            gio.themVaoGio(mh,2);gio.themVaoGio(mh,3);
            expect(gio.getSoDong()==1,"accumulate same item");
            expect(gio.tongTien()==5*mh.tinhGiaBan(),"total");
            gio.themVaoGio(mh,2);
            expect(gio.tongTien()==5*mh.tinhGiaBan(),"reject overstock");
            gio.capNhatSoLuong("S01",6);
            expect(gio.tongTien()==6*mh.tinhGiaBan(),"change quantity");
            gio.capNhatSoLuong("S01",0);
            expect(gio.isRong(), "remove last item");
        });
        check("TV3 - cart delete first/middle/last", () -> {
            GioHang gio = new GioHang(new KhachHang("K01","Khách","0900"));
            gio.themVaoGio(sach("A",10),1);
            gio.themVaoGio(sach("B",10),1);
            gio.themVaoGio(sach("C",10),1);
            gio.xoaKhoiGio("B"); expect(gio.getSoDong()==2,"delete middle");
            gio.xoaKhoiGio("A"); expect(gio.getSoDong()==1,"delete first");
            gio.xoaKhoiGio("C"); expect(gio.isRong(),"delete last");
        });
        check("TV3 - checkout, duplicate order, insufficient/sufficient cash, stock deduction", () -> {
            Sach s=sach("SKU",5); KhachHang kh=new KhachHang("KH","Khách","0900");
            DanhSachMatHang ds=new DanhSachMatHang(); Kho kho=new Kho();
            kho.tangTonKho(s,5);ds.nhapTuKho(kho,"SKU",5);
            Path customers = Files.createTempFile("customer-", ".txt");
            Files.writeString(customers,"KH;Khách;0900\n", StandardCharsets.UTF_8);
            DanhSachKhachHang dskh = new DanhSachKhachHang(); dskh.docFile(customers.toString());
            DanhSachHoaDon dshd=new DanhSachHoaDon(ds,dskh);
            GioHang gio = new GioHang(kh);gio.themVaoGio(s,3);
            NhanVienBanHang sale = new NhanVienBanHang("NV","Bán","0901","pw","Sáng",100);
            HoaDon hd=gio.xacNhanMua(dshd,"HD1",sale);
            expect(hd!=null && gio.isRong(),"new invoice clears cart");
            expect(s.getSoLuong()==5,"unpaid order doesn't deduct stock");
            GioHang gio2 = new GioHang(kh);gio2.themVaoGio(s,1);
            expect(gio2.xacNhanMua(dshd,"HD1",sale)==null,"duplicate invoice rejected");
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1));
            expect(!hd.thanhToan() && s.getSoLuong()==5,"insufficient cash no deduction");
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000000));
            expect(hd.thanhToan() && hd.isDaThanhToan(),"paid");
            expect(s.getSoLuong()==2,"deduct exactly 3");
            expect(!hd.thanhToan() && s.getSoLuong()==2,"no double charge");
            Path invoices=Files.createTempFile("invoice-", ".txt");
            dshd.ghiFile(invoices.toString());
            DanhSachHoaDon reloaded=new DanhSachHoaDon(ds,dskh);
            reloaded.docFile(invoices.toString(),dskh,ds);
            expect(reloaded.getSoLuong()==1 && reloaded.timKiem("HD1").isDaThanhToan(),"persist settled invoice");
            expect(s.getSoLuong()==2,"reload does not decrement again");
            Files.deleteIfExists(invoices);Files.deleteIfExists(customers);
        });
        check("TV4 - purchase note removes middle correctly", () -> {
            PhieuNhap pn = new PhieuNhap();
            pn.them(sach("A",1));pn.them(sach("B",1));pn.them(sach("C",1));
            Scanner original = TienIch.sc;
            try { TienIch.sc = new Scanner(new ByteArrayInputStream("B\n".getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
                pn.xoa();
            } finally { TienIch.sc=original; }
            expect(pn.getSoLuongNhap()==2 && Objects.equals(pn.getDSMH()[1].getMa(),"C"),"array shift correct");
        });
        check("TV4 - receipt to warehouse and no double processing check", () -> {
            Kho k=new Kho(); PhieuNhap pn=new PhieuNhap();
            pn.them(sach("NEW",4));
            expect(k.nhapHangTuPhieu(pn),"stock updated");
            expect(pn.getTrangThai()==PhieuNhap.TrangThaiPN.DA_DUYET,"approved");
            expect(k.getTongSoLuongTon()==4,"4 stock");
            expect(!k.nhapHangTuPhieu(pn),"same receipt cannot be applied twice");
            expect(k.getTongSoLuongTon()==4,"no double stock increment");
        });
        check("TV4 - supplier file read-write", () -> {
            Path p=Files.createTempFile("ncc-", ".txt");
            Files.writeString(p,"NCC1;Nhà sách;HCM;0900;mail@test.vn\n",StandardCharsets.UTF_8);
            DanhSachNCC n=new DanhSachNCC();n.docFile(p.toString());
            expect(n.timNCC("NCC1")!=null,"loaded");
            n.ghiFile(p.toString());
            expect(Files.readString(p,StandardCharsets.UTF_8).contains("NCC1;"),"saved");
            Files.deleteIfExists(p);
        });
        check("TV4 - interactive add warehouse item and delete middle", () -> {
            Kho k = new Kho();
            Scanner original = TienIch.sc;
            // Loại 1 Sách; ma, ten, theloai, tacgia, NXB, gia, soLuongQuay, soLuongNhapKho.
            String input = "1\nSNEW\nSách mới\nKỹ thuật\nTác giả\nNXB\n10000\n0\n7\n";
            try { TienIch.sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
                k.them();
            } finally { TienIch.sc=original; }
            expect(k.getSoLuongTon()==1,"interactive item imported");
            expect(k.getTongSoLuongTon()==7,"7 stocked");
            k.tangTonKho(sach("B",0),3);
            k.tangTonKho(sach("C",0),4);
            try { TienIch.sc = new Scanner(new ByteArrayInputStream("B\n".getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);
                k.xoa();
            } finally { TienIch.sc=original; }
            expect(k.getSoLuongTon()==2,"remove middle and shift");
            expect("C".equals(k.getDanhSachTon()[1].getMa()),"last item shifted");
        });
        check("TV2/TV5 - all seven product types TXT roundtrip, duplicate prevention", () -> {
            DanhSachMatHang a = new DanhSachMatHang();
            expect(a.themTuFile(new Sach("S1","Sách",100,2,"A","NXB","T")), "sach");
            expect(a.themTuFile(new SachGiaoKhoa("G1","SGK",100,3,"Toán","NXB")), "SGK");
            expect(a.themTuFile(new TieuThuyet("T1","Novel",100,4,"Tác giả","NXB","Văn")), "Novel");
            expect(a.themTuFile(new Truyen("TR1","Comic",100,5,"A","NXB","Comic")), "Truyen");
            expect(a.themTuFile(new VanPhongPham("V1","Bút",100,6,"Thiên Long","Mực")), "VPP");
            expect(a.themTuFile(new BaoTapChi("B1","Báo",100,7,"NXB báo","Khoa học")), "Báo");
            expect(a.themTuFile(new SPKhac("K1","SP",100,8,"ABC")), "Khác");
            Path f = Files.createTempFile("mathang-", ".txt");
            expect(LuuTruMatHang.ghiFile(a,f.toString())==7,"wrote 7");
            DanhSachMatHang b = new DanhSachMatHang();
            expect(LuuTruMatHang.docFile(b,f.toString())==7,"read 7");
            expect(b.getSoMH()==7,"size 7");
            expect(b.timKiem_hang("V1") instanceof VanPhongPham, "VPP type");
            expect("Mực".equals(((VanPhongPham)b.timKiem_hang("V1")).getLoaiVPP()), "VPP subtype field");
            expect("NXB báo".equals(((BaoTapChi)b.timKiem_hang("B1")).getNhaXuatBan()), "Báo subtype field");
            expect(LuuTruMatHang.docFile(b,f.toString())==0,"duplicate import avoided");
            Files.deleteIfExists(f);
        });
        System.out.printf("%nTEST RESULT: %d PASS, %d FAIL%n",ok,fail);
        if (fail>0) System.exit(1);
    }
}
