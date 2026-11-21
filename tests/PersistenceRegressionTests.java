import chinhanh.*;
import hoadon.*;
import khachhang.*;
import mathang.*;
import phieunhap.*;
import thanhtoan.*;
import tienich.LuuTruPhien;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

/** Regression checks for complete, atomic and repeatable session restoration. */
public class PersistenceRegressionTests {
    static MatHang[] products() {
        return new MatHang[] {
            new Sach("SCH0101","Sách; tiếng Việt\nTập 1",100,3,"Author","Publisher","Science"),
            new SachGiaoKhoa("SGK0101","Textbook",100,3,"Math","Publisher"),
            new TieuThuyet("TTH0101","Novel",100,3,"Author","Publisher","Mystery"),
            new Truyen("TRU0101","Comic",100,3,"Author","Publisher","Adventure"),
            new BaoTapChi("BTC0101","Magazine",100,3,"Publisher","Science"),
            new VanPhongPham("VPP0101","Pen",100,3,"Maker","Pen"),
            new SPKhac("SPK0101","Other",100,3,"Maker"),
            new DoChoi("DCH0101","Toy",100,3,"Maker",6),
            new DungCuHocTap("DCT0101","Ruler",100,3,"Maker","Ruler"),
            new TrangTri("TTR0101","Decor",100,3,"Maker"),
            new Vo("VO0101","Notebook",100,3,96,"Maker")
        };
    }
    static LuuTruPhien.DuLieu data() {
        return new LuuTruPhien.DuLieu(Main.kho,Main.dskh,Main.dsncc,Main.dspn,Main.dsCN);
    }
    static void roundtrip(FullFunctionalAuditTests.Job job) throws Exception {
        Path path=Files.createTempFile("bookstore-session-", ".properties");
        try { current=path; job.run(); } finally { Files.deleteIfExists(path); current=null; }
    }
    static Path current;
    static void yes(boolean ok,String msg) { FullFunctionalAuditTests.yes(ok,msg); }
    static void reset() { FullFunctionalAuditTests.reset(); }

    public static void main(String[] args) throws Exception {
        FullFunctionalAuditTests.test("SESSION all eleven subtypes and escaped fields", () -> roundtrip(() -> {
            reset();
            for(MatHang item:products()) {
                Main.kho.tangTonKho(item,3);
                Main.dsCN.get(0).getDsmh().nhaptukho(Main.kho,item.getMa(),2);
                Main.dsCN.get(1).getDsmh().nhaptukho(Main.kho,item.getMa(),1);
            }
            LuuTruPhien.ghi(current,data(),Main.dsnv);
            LuuTruPhien.DuLieu copy=LuuTruPhien.doc(current,Main.dsnv);
            yes(copy.kho.getTongSoLuongTon()==0,"zero warehouse balance was not preserved");
            for(MatHang expected:products()) {
                MatHang a=copy.chiNhanh.get(0).getDsmh().timKiem_hang(expected.getMa());
                MatHang b=copy.chiNhanh.get(1).getDsmh().timKiem_hang(expected.getMa());
                yes(a.getClass()==expected.getClass() && b.getClass()==expected.getClass(),"subtype");
                expected.setSoLuong(2); yes(a.toString().equals(expected.toString()),"metadata changed");
                yes(a!=b && b.getSoLuong()==1,"cross branch sharing");
            }
        }));
        FullFunctionalAuditTests.test("SESSION paid and pending invoices retain stock and saved unit price", () -> roundtrip(() -> {
            reset(); Main.dskh.them(new KhachHang("KH01","Alice","090"));
            TestSupport.stockShelf(Main.cnHienTai.getDsmh(),FullFunctionalAuditTests.book("SCH0101",10));
            MatHang item=Main.cnHienTai.getDsmh().timKiem_hang("SCH0101");
            HoaDon paid=FullFunctionalAuditTests.order("PAID",item,2), pending=FullFunctionalAuditTests.order("PENDING",item,1);
            paid.setHinhThucThanhToan(new ThanhToanThe("TX","Bank")); paid.thanhToan();
            Main.cnHienTai.getDshd().them(paid); Main.cnHienTai.getDshd().them(pending);
            item.setGiaNhap(999); LuuTruPhien.ghi(current,data(),Main.dsnv);
            for(int i=0;i<2;i++) {
                LuuTruPhien.DuLieu copy=LuuTruPhien.doc(current,Main.dsnv); ChiNhanh branch=copy.chiNhanh.get(0);
                HoaDon a=branch.getDshd().timKiem("PAID"), b=branch.getDshd().timKiem("PENDING");
                yes(a.isDaThanhToan() && !b.isDaThanhToan(),"paid state");
                yes(branch.getDsmh().timKiem_hang("SCH0101").getSoLuong()==8,"repeated load deducted stock");
                FullFunctionalAuditTests.eq(a.tinhThanhTien(),300);
                yes(a.getNhanVienLap()==Main.dsnv.timTheoMa("BH01"),"seller pointer");
                b.setHinhThucThanhToan(new ThanhToanTienMat(1000)); yes(b.thanhToan(),"pending checkout");
                yes(branch.getDsmh().timKiem_hang("SCH0101").getSoLuong()==7,"pending stock reference");
            }
        }));
        FullFunctionalAuditTests.test("SESSION deleted customer and product retain invoice history", () -> roundtrip(() -> {
            reset(); HoaDon hd=FullFunctionalAuditTests.order("HISTORY",FullFunctionalAuditTests.book("SCH0101",5),2);
            hd.setHinhThucThanhToan(new ThanhToanTienMat(1000)); hd.thanhToan(); Main.cnHienTai.getDshd().them(hd);
            LuuTruPhien.ghi(current,data(),Main.dsnv); HoaDon copy=LuuTruPhien.doc(current,Main.dsnv).chiNhanh.get(0).getDshd().timKiem("HISTORY");
            yes(copy.isDaThanhToan() && "Alice".equals(copy.getKhachHang().getTenKH()),"customer history");
            yes("Book SCH0101".equals(copy.getChiTiet()[0].getMathang().getTen()),"product history");
        }));
        FullFunctionalAuditTests.test("SESSION renamed supplier preserves receipt reference and contacts", () -> roundtrip(() -> {
            reset(); Main.dspn=FullFunctionalAuditTests.receipts(); Main.dsncc=Main.dspn.getDanhSachNCC();
            Main.dsncc.timNCC("NCC0").setTenNCC("Renamed"); Main.dsncc.timNCC("NCC0").setDiaChi("Địa chỉ; số 1");
            Main.kho.nhapHangTuPhieu(Main.dspn.timPhieu("PN01"));
            LuuTruPhien.ghi(current,data(),Main.dsnv); LuuTruPhien.DuLieu copy=LuuTruPhien.doc(current,Main.dsnv);
            yes(copy.phieuNhap.timPhieu("PN01").getChiTiet().getNhaCungCap()==copy.nhaCungCap.timNCC("NCC0"),"supplier pointer");
            yes("Địa chỉ; số 1".equals(copy.nhaCungCap.timNCC("NCC0").getDiaChi()),"contact data");
            yes(!copy.kho.nhapHangTuPhieu(copy.phieuNhap.timPhieu("PN01")) && copy.kho.getTongSoLuongTon()==5,"receipt replay");
        }));
        FullFunctionalAuditTests.test("SESSION reused customer and supplier IDs retain old invoice and receipt history", () -> roundtrip(() -> {
            reset(); KhachHang old=new KhachHang("KH01","Alice","090"); Main.dskh.them(old);
            Main.cnHienTai.getDshd().them(FullFunctionalAuditTests.order("OLD",FullFunctionalAuditTests.book("SCH0101",5),1));
            Main.dskh.xoa("KH01"); Main.dskh.them(new KhachHang("KH01","New Customer","091"));
            Main.dspn=FullFunctionalAuditTests.receipts(); Main.dsncc=Main.dspn.getDanhSachNCC();
            FullFunctionalAuditTests.input("NCC0\n",Main.dsncc::xoa); Main.dsncc.timHoacTaoTheoTen("New Supplier");
            LuuTruPhien.ghi(current,data(),Main.dsnv); LuuTruPhien.DuLieu copy=LuuTruPhien.doc(current,Main.dsnv);
            yes("New Customer".equals(copy.khachHang.timkiem_khachhang("KH01").getTenKH()),"current customer");
            yes("Alice".equals(copy.chiNhanh.get(0).getDshd().timKiem("OLD").getKhachHang().getTenKH()),"invoice customer history overwritten");
            yes("Publisher".equals(copy.phieuNhap.timPhieu("PN01").getChiTiet().getNhaCungCap().getTenNCC()),"receipt supplier history overwritten");
            yes("New Supplier".equals(copy.nhaCungCap.timNCC("NCC0").getTenNCC()),"current supplier");
        }));
        FullFunctionalAuditTests.test("SESSION malformed file rejects complete load", () -> roundtrip(() -> {
            reset(); Files.writeString(current,"version=1\nkho.count=0\nkh.count=0\nncc.count=0\npn.count=0\ncn.count=1\n");
            byte[] original=Files.readAllBytes(current); boolean rejected=false;
            try { LuuTruPhien.doc(current,Main.dsnv); } catch(IOException expected) { rejected=true; }
            yes(rejected && java.util.Arrays.equals(original,Files.readAllBytes(current)),"corrupt snapshot modified or accepted");
            yes(Main.dsCN.getSoLuong()==2,"live data mutated during failed restore");
        }));
        FullFunctionalAuditTests.test("SESSION failed validation preserves prior good snapshot", () -> roundtrip(() -> {
            reset(); TestSupport.stockShelf(Main.cnHienTai.getDsmh(),FullFunctionalAuditTests.book("SCH0101",5));
            LuuTruPhien.ghi(current,data(),Main.dsnv); byte[] original=Files.readAllBytes(current);
            Main.cnHienTai.getDsmh().timKiem_hang("SCH0101").setSoLuong(-1); boolean rejected=false;
            try { LuuTruPhien.ghi(current,data(),Main.dsnv); } catch(IOException expected) { rejected=true; }
            yes(rejected && java.util.Arrays.equals(original,Files.readAllBytes(current)),"prior save overwritten");
        }));
        FullFunctionalAuditTests.test("SESSION unknown version and invalid numeric fields rejected", () -> roundtrip(() -> {
            reset(); LuuTruPhien.ghi(current,data(),Main.dsnv); Properties p=new Properties();
            try(java.io.Reader reader=Files.newBufferedReader(current)) { p.load(reader); }
            for(String key:new String[]{"version","kho.count","cn.count"}) {
                Properties bad=new Properties(); bad.putAll(p); bad.setProperty(key,"-1");
                try(java.io.Writer writer=Files.newBufferedWriter(current)) { bad.store(writer,"test"); }
                boolean rejected=false; try { LuuTruPhien.doc(current,Main.dsnv); } catch(IOException expected) { rejected=true; }
                yes(rejected,"invalid field accepted: "+key);
            }
        }));
        FullFunctionalAuditTests.test("RECEIPT processed note cannot be edited or deleted", () -> {
            DanhSachPhieuNhap list=FullFunctionalAuditTests.receipts(); new kho.Kho().nhapHangTuPhieu(list.timPhieu("PN01"));
            FullFunctionalAuditTests.input("PN01\n",list::sua); FullFunctionalAuditTests.input("PN01\n",list::xoa);
            yes(list.getSLpn()==1 && list.timPhieu("PN01").getTrangThai()==PhieuNhap.TrangThaiPN.DA_DUYET,"processed note changed");
        });
        FullFunctionalAuditTests.test("RECEIPT cancellation returns to menu and never adds stock", () -> {
            DanhSachPhieuNhap list=FullFunctionalAuditTests.receipts(); FullFunctionalAuditTests.input("PN01\n3\n3\n4\n",list::sua);
            yes(list.timPhieu("PN01").getTrangThai()==PhieuNhap.TrangThaiPN.DA_HUY,"cancel status");
            yes(!new kho.Kho().nhapHangTuPhieu(list.timPhieu("PN01")),"canceled note stocked");
        });
        FullFunctionalAuditTests.test("INVOICE missing count, invalid detail and truncation recover next header", () -> {
            for(String bad:new String[]{"BAD;2026-11-21;KH01;BH01;Seller;0\n", "BAD;2026-11-21;KH01\n2\nSCH0101;1;150\n", "BAD;2026-11-21;KH01\n1\nSCH0101;-1;150\n"}) {
                reset(); Main.dskh.them(new KhachHang("KH01","Alice","090"));
                Path path=FullFunctionalAuditTests.file(bad+"GOOD;2026-11-21;KH01;BH01;Seller;1\n1\nSCH0101;2;150\n");
                try { Main.cnHienTai.getDshd().docFile(path.toString()); yes(Main.cnHienTai.getDshd().getSoLuong()==1 && Main.cnHienTai.getDshd().timKiem("GOOD")!=null,"later invoice lost"); }
                finally { Files.deleteIfExists(path); }
            }
        });
        System.out.println("PERSISTENCE REGRESSION: "+FullFunctionalAuditTests.passed+" PASS, "+FullFunctionalAuditTests.failed+" FAIL");
        if(FullFunctionalAuditTests.failed>0) System.exit(1);
    }
}
