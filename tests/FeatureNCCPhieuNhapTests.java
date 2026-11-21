import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import nhacungcap.*;
import phieunhap.*;
import phieunhap.PhieuNhap.TrangThaiPN;
import mathang.*;
import kho.*;
import tienich.TienIch;

/** Bài kiểm thử hồi quy dành riêng cho phần bổ sung TV4 -> TV5. Không cần thư viện ngoài. */
public class FeatureNCCPhieuNhapTests {
    static int passed, failed;
    interface Task { void run() throws Exception; }
    static void test(String name, Task job) { try {job.run();passed++;System.out.println("PASS " + name);}catch(Throwable e){failed++;System.err.println("FAIL " + name + " : " + e);e.printStackTrace();}}
    static void yes(boolean x,String msg){ if(!x)throw new AssertionError(msg); }
    static Path txt(String body) throws IOException {Path f=Files.createTempFile("pn-regression-", ".txt");Files.writeString(f,body,StandardCharsets.UTF_8);return f;}
    static String line(String pn,String date,String type,String ma,String params,String status){ return pn+";"+date+";"+type+";"+ma+";Test;100;3;"+params+";"+status+"\n"; }
    static final String BASIC = line("PN100","01/10/2026","Sách","SCH0100","Author;Publisher;Drama","CHO_DUYET");
    public static void main(String[] args) throws Exception {
        test("01 - shared supplier ref exposed",()->{DanhSachNCC n=new DanhSachNCC(); DanhSachPhieuNhap d=new DanhSachPhieuNhap(n);yes(d.getDanhSachNCC()==n,"not shared");});
        test("02 - create supplier with code and count",()->{DanhSachNCC n=new DanhSachNCC(); NhaCungCap a=n.timHoacTaoTheoTen("NXB Trẻ");yes("NCC0".equals(a.getMaNCC())&&n.getSoLuongNCC()==1,"first NCC");});
        test("03 - case-insensitive/trim name dedup",()->{DanhSachNCC n=new DanhSachNCC();NhaCungCap a=n.timHoacTaoTheoTen(" Trẻ  ");yes(a==n.timHoacTaoTheoTen("trẻ")&&n.getSoLuongNCC()==1,"duplicate");});
        test("04 - auto-generated code avoids manual conflict",()->{DanhSachNCC n=new DanhSachNCC();n.timHoacTaoTheoTen("A");NhaCungCap b=n.timHoacTaoTheoTen("B");yes("NCC1".equals(b.getMaNCC()),"second code");});
        test("05 - max 100 supplier expands array safely",()->{DanhSachNCC n=new DanhSachNCC();for(int i=0;i<110;i++)n.timHoacTaoTheoTen("N"+i);yes(n.getSoLuongNCC()==110&&n.getDsNCC().length>=110,"capacity");});
        test("06 - reject empty supplier name",()->{DanhSachNCC n=new DanhSachNCC();try{n.timHoacTaoTheoTen("  ");throw new AssertionError("missing validation");}catch(IllegalArgumentException ok){}yes(n.getSoLuongNCC()==0,"modified");});
        test("07 - sample input all four receipts & 10 product lines",()->{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");yes(d.getSLpn()==4,"number notes"); int lines=0;for(String s:new String[]{"PN001","PN002","PN003","PN004"})lines+=d.timPhieu(s).getSoLuongNhap();yes(lines==10,"number details: "+lines);yes(d.getDanhSachNCC().getSoLuongNCC()==4,"all supplier names");});
        test("08 - correct category, subtype & PN supplier relationship",()->{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");PhieuNhap pn=d.timPhieu("PN001");yes(pn.getDSMH()[0] instanceof Sach&&pn.getDSMH()[1] instanceof VanPhongPham,"categories");yes("Kim Đồng".equals(pn.getChiTiet().getNhaCungCap().getTenNCC()),"first supplier retained");yes(pn.getChiTiet().getNhaCungCap()==d.getDanhSachNCC().timNCC("NCC0"),"supplier pointer");});
        test("09 - repeated read idempotent incl suppliers",()->{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");yes(d.getSLpn()==4&&d.timPhieu("PN003").getSoLuongNhap()==3,"duplicates");yes(d.getDanhSachNCC().getSoLuongNCC()==4,"duplicate supplier");});
        test("10 - export/import preserves date status and count",()->{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");Path f=Files.createTempFile("pnpersist", ".txt");try{d.ghiFile(f.toString());String saved=Files.readString(f);yes(saved.contains("15/09/2025"),"date format");DanhSachPhieuNhap re=new DanhSachPhieuNhap();re.docFile(f.toString());yes(re.getSLpn()==4&&re.timPhieu("PN003").getSoLuongNhap()==3,"roundtrip");yes(re.timPhieu("PN002").getTrangThai()==TrangThaiPN.DA_DUYET,"status");yes(re.timPhieu("PN004").getTrangThai()==TrangThaiPN.DA_HUY,"canceled");}finally{Files.delete(f);}});
        test("11a - dotted date is rejected without partial state",()->{Path f=txt(BASIC.replace("01/10/2026","01/10.2026"));try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==0 && d.getDanhSachNCC().getSoLuongNCC()==0,"invalid date created data");}finally{Files.delete(f);}});
        test("11 - ISO date is rejected; slash date is accepted",()->{Path f=txt(line("ISO","2026-10-27","Khác","SPK0100","OEM","CHO_DUYET")+BASIC);try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==1 && d.timPhieu("ISO")==null && d.timPhieu("PN100").getChiTiet().getNgayNhap().equals(LocalDate.of(2026,10,1)),"date format validation");}finally{Files.delete(f);}});
        test("12 - bad rows skipped no half-created receipt/NCC",()->{Path f=txt("abc\n"+line("PN01","45/13/2026","Khác","SPK0101","OEM","CHO_DUYET")+line("PN02","01/01/2026","Khác","SPK0102","OEM","WRONG")+line("PN03","01/01/2026","Khác","SPK0103","OEM","CHO_DUYET").replace(";100;3;",";NaN;3;")+line("PN04","01/01/2026","Khác","SPK0104","OEM","CHO_DUYET").replace(";100;3;",";100;-3;"));try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==0&&d.getDanhSachNCC().getSoLuongNCC()==0,"bad record changed state");}finally{Files.delete(f);}});
        test("13 - valid row still read after invalid rows",()->{Path f=txt("broken\n"+BASIC+"broken-2\n");try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==1&&d.timPhieu("PN100").getSoLuongNhap()==1,"mixed rows");}finally{Files.delete(f);}});
        test("14 - reject duplicate SKU within a receipt",()->{Path f=txt(BASIC+BASIC.replace("Test","Duplicated"));try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==1&&d.timPhieu("PN100").getSoLuongNhap()==1,"duplicate SKU");}finally{Files.delete(f);}});
        test("15 - duplicate receipt conflicting date/status excluded",()->{Path f=txt(BASIC+line("PN100","02/10/2026","Sách","SCH0101","Author;Publisher;Drama","CHO_DUYET")+line("PN100","01/10/2026","Sách","SCH0102","Author;Publisher;Drama","DA_HUY"));try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==1&&d.timPhieu("PN100").getSoLuongNhap()==1,"conflicting rows");}finally{Files.delete(f);}});
        test("16 - conflicting supplier row is rejected without partial state",()->{Path f=txt(BASIC+line("PN100","01/10/2026","Khác","SPK0105","OEM","CHO_DUYET"));try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getDanhSachNCC().getSoLuongNCC()==1,"rejected row created supplier");yes(d.timPhieu("PN100").getSoLuongNhap()==1,"conflicting row accepted");yes("Publisher".equals(d.timPhieu("PN100").getChiTiet().getNhaCungCap().getTenNCC()),"NCC overwritten");}finally{Files.delete(f);}});
        test("17 - supplier file updates over 100 existing entries and skips unknown codes",()->{Path f=Files.createTempFile("ncc", ".txt");StringBuilder s=new StringBuilder("bad\nUNKNOWN;Other;HCM;01;mail\n");DanhSachNCC n=new DanhSachNCC();for(int i=0;i<102;i++){n.timHoacTaoTheoTen("N"+i);s.append("NCC").append(i).append(";N").append(i).append(";HCM;01;mail\n");}Files.writeString(f,s.toString());try{n.docFile(f.toString());n.docFile(f.toString());yes(n.getSoLuongNCC()==102 && n.timNCC("UNKNOWN")==null,"supplier capacity/idempotency");yes("HCM".equals(n.timNCC("NCC101").getDiaChi()),"last supplier not updated");}finally{Files.delete(f);}});
        test("18 - automatic codes sequential when adding",()->{DanhSachNCC n=new DanhSachNCC();n.timHoacTaoTheoTen("A");n.timHoacTaoTheoTen("B");n.timHoacTaoTheoTen("C");NhaCungCap n3=n.timHoacTaoTheoTen("D");yes(n3.getMaNCC().equals("NCC3"),"unexpected code");});
        test("19 - processed receipt does not double-update stock",()->{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");Kho kho=new Kho();PhieuNhap p=d.timPhieu("PN001");yes(kho.nhapHangTuPhieu(p),"first stock");int total=kho.getTongSoLuongTon();yes(!kho.nhapHangTuPhieu(p)&&kho.getTongSoLuongTon()==total,"double stock");});
        test("20 - file not found keeps prior data",()->{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile("tests/fixtures/phieunhap/phieunhap_input.txt");d.docFile("/tmp/never-exists-OOP-2026-foo.txt");yes(d.getSLpn()==4,"discarded input on missing file");});
        test("21 - manual add sets receipt ID/date/NCC & supplier list",()->{DanhSachNCC n=new DanhSachNCC();DanhSachPhieuNhap d=new DanhSachPhieuNhap(n);Scanner old=TienIch.sc;String input="PNMAN\n02/10/2026\nNhà Xuất Bản A\n1\n1\nSCH0103\nSach Test\nKỹ thuật\nTác giả\n100\n4\n";try{TienIch.sc=new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);d.them();}finally{TienIch.sc=old;}yes(d.getSLpn()==1,"manual receipt count");yes("PNMAN".equals(d.timPhieu("PNMAN").getMa())&&d.timPhieu("PNMAN").getChiTiet().getNgayNhap().equals(LocalDate.of(2026,10,2)),"id/date");yes(n.getSoLuongNCC()==1&&d.timPhieu("PNMAN").getChiTiet().getNhaCungCap()==n.getDsNCC()[0],"NCC");});
        test("22 - read UTF-8 BOM and mixed-case ID",()->{Path f=txt("\uFEFF"+BASIC+BASIC.replace("PN100","pn100"));try{DanhSachPhieuNhap d=new DanhSachPhieuNhap();d.docFile(f.toString());yes(d.getSLpn()==1&&d.timPhieu("pn100").getSoLuongNhap()==1,"duplicate IDs case insensitive");}finally{Files.delete(f);}});
        test("23 - date input retries invalid dates and supports optional blank",()->{
            Scanner old = TienIch.sc;
            String input = "31/02/2026\n02/10/2026\n\n";
            try {
                TienIch.sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
                yes(TienIch.nhapNgay("Date: ").equals(LocalDate.of(2026,10,2)), "date retry failed");
                yes(TienIch.nhapNgay("Optional date: ", true)==null, "optional blank rejected");
            }
            finally { TienIch.sc = old; }
        });
        test("24 - manual supplier is inherited by product and survives reload",()->{
            Path saved = Files.createTempFile("pn-supplier-", ".txt");
            Scanner old = TienIch.sc;
            try {
                DanhSachPhieuNhap d = new DanhSachPhieuNhap();
                String input = "PNMAN\n02/10/2026\nOther Supplier\n1\n7\nSPK0107\nItem\n100\n2\n";
                try { TienIch.sc = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8); d.them(); }
                finally { TienIch.sc = old; }
                yes("Other Supplier".equals(((SPKhac)d.timPhieu("PNMAN").getDSMH()[0]).getnhaSX()), "supplier not inherited");
                d.ghiFile(saved.toString());
                DanhSachPhieuNhap loaded = new DanhSachPhieuNhap(); loaded.docFile(saved.toString());
                yes("Other Supplier".equals(loaded.timPhieu("PNMAN").getChiTiet().getNhaCungCap().getTenNCC()), "supplier lost on reload");
            } finally { TienIch.sc = old; Files.deleteIfExists(saved); }
        });
        test("25 - automatic import skips processed receipts and exports approved ones",()->{
            Path f = txt(BASIC);
            Path saved = Files.createTempFile("pn-approved-", ".txt");
            try {
                DanhSachPhieuNhap d = new DanhSachPhieuNhap(); d.docFile(f.toString());
                Kho kho = new Kho();
                d.tuDongNhapTatCaVaoKho(kho, saved.toString());
                d.tuDongNhapTatCaVaoKho(kho, saved.toString());
                yes(kho.getTongSoLuongTon()==3, "stock doubled");
                DanhSachPhieuNhap loaded = new DanhSachPhieuNhap(); loaded.docFile(saved.toString());
                yes(loaded.getSLpn()==1 && loaded.timPhieu("PN100").getTrangThai()==TrangThaiPN.DA_DUYET, "approved export");
            } finally { Files.deleteIfExists(f); Files.deleteIfExists(saved); }
        });
        System.out.println("NEW FEATURE TESTS: "+passed+" PASS, "+failed+" FAIL");if(failed!=0)System.exit(1);
    }
}
