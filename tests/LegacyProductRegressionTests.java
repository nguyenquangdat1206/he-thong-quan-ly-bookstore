import interfaces.IThaoTacThucThe;
import mathang.*;
import kho.*;
import phieunhap.*;

import tienich.TienIch;
import java.io.ByteArrayInputStream;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Method;
import java.util.Scanner;
import java.util.Arrays;

/** Regression tests for product subtypes and the current warehouse/receipt APIs. */
public class LegacyProductRegressionTests {
    static int passed=0, failed=0;
    @FunctionalInterface interface Test { void run() throws Exception; }
    static void run(String name, Test test) {
        try { test.run();passed++;System.out.println("PASS LEGACY " + name); }
        catch(Throwable ex){failed++;System.err.println("FAIL LEGACY " + name + ": " + ex);ex.printStackTrace();}
    }
    static void check(boolean ok,String message){if(!ok)throw new AssertionError(message);}
    static void close(double actual,double expected){check(Math.abs(actual-expected)<0.00001,actual+" != "+expected);}
    static MatHang[] products(){return new MatHang[]{
        new DoChoi("DCH0101","Lego",100,3,"MakerD",8),
        new DungCuHocTap("DCT0101","Thuoc",100,4,"MakerH","Thước kẻ"),
        new TrangTri("TTR0101","Tranh",100,5,"MakerT"),
        new Vo("VO0101","Vo 96",100,6,96,"MakerV")
    };}
    static void useInput(String input, Test test) throws Exception {
        Scanner old=TienIch.sc;
        try {TienIch.sc=new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),StandardCharsets.UTF_8);test.run();}
        finally{TienIch.sc=old;}
    }
    public static void main(String[] args) throws Exception {
        run("IThaoTacThucThe retains all five original methods", ()->{
            for(String name:Arrays.asList("them","xoa","sua","timKiem","xuat")){
                Class<?>[] params=(name.equals("xoa")||name.equals("sua")||name.equals("timKiem"))?new Class<?>[]{String.class}:new Class<?>[]{};
                Method m=IThaoTacThucThe.class.getMethod(name,params);
                check(m.getReturnType()==void.class,"lost interface method "+name);
            }
        });
        run("restored products retain constructors, fields, category, price and original serialization", ()->{
            MatHang[] a=products();
            check(a[0] instanceof DoChoi && ((DoChoi)a[0]).getdotuoi()==8,"toy fields");
            check("MakerD".equals(((DoChoi)a[0]).getnhaSX()),"toy manufacturer");
            check("Thước kẻ".equals(((DungCuHocTap)a[1]).getLoaidungcu()),"tool type");
            check("MakerT".equals(((TrangTri)a[2]).getnhaSX()),"decor producer");
            check(((Vo)a[3]).getSotrang()==96,"notebook pages");
            close(a[0].tinhGiaBan(),156.0);
            close(a[1].tinhGiaBan(),132.16);
            close(a[2].tinhGiaBan(),142.8);
            close(a[3].tinhGiaBan(),126.5);
            check("Đồ chơi;DCH0101;Lego;100.0;3;8;MakerD".equals(a[0].toString()),"toy TXT format");
            check("Dụng cụ học tập;DCT0101;Thuoc;100.0;4;Thước kẻ;MakerH".equals(a[1].toString()),"tool TXT format");
            check("Trang trí;TTR0101;Tranh;100.0;5;MakerT".equals(a[2].toString()),"decor TXT format");
            check("Vở;VO0101;Vo 96;100.0;6;96;MakerV".equals(a[3].toString()),"notebook TXT format");
        });
        run("shelf transfer and TXT export preserve all four product types", ()->{
            Path f=Files.createTempFile("oop-v5-products-", ".txt");
            try {
                DanhSachMatHang list=new DanhSachMatHang();
                for(MatHang p:products()) TestSupport.stockShelf(list,p);
                list.ghiFile(f.toString());
                var rows=Files.readAllLines(f,StandardCharsets.UTF_8);
                check(rows.size()==4,"save count");
                int i=0;
                for(MatHang p:products()) {
                    MatHang copied=list.timKiem_hang(p.getMa());
                    check(copied.getClass()==p.getClass(),"shelf subtype");
                    check(p.toString().equals(copied.toString()),"data lost: "+p.getMa());
                    check(rows.get(i++).equals(p.toString()),"export fields: "+p.getMa());
                }
            }finally{Files.deleteIfExists(f);}
        });
        run("warehouse stock persistence preserves all four product types and counts", ()->{
            Path f=Files.createTempFile("oop-v5-warehouse-", ".txt");
            try {
                Kho warehouse=new Kho();
                for(MatHang p:products()) check(warehouse.tangTonKho(p,p.getSoLuong()),"stock add");
                check(warehouse.getTongSoLuongTon()==18,"total warehouse count");
                warehouse.ghiFile(f.toString());
                var rows=Files.readAllLines(f,StandardCharsets.UTF_8);
                check(rows.size()==4,"four warehouse rows");
                int i=0;
                for(MatHang p:products()) check(rows.get(i++).equals(p.toString()),"warehouse export: "+p.getMa());
            }finally{Files.deleteIfExists(f);}
        });
        run("purchase-note import/export supports all four types and supplier mapping", ()->{
            Path in=Files.createTempFile("oop-v5-purchase-", ".txt");
            Path out=Files.createTempFile("oop-v5-export-", ".txt");
            try {
                StringBuilder lines=new StringBuilder();int i=1;
                for(MatHang p:products())lines.append("PN").append(i++).append(";01/11/2026;").append(p).append(";CHO_DUYET\n");
                Files.writeString(in,lines,StandardCharsets.UTF_8);
                DanhSachPhieuNhap notes=new DanhSachPhieuNhap(); notes.docFile(in.toString());
                check(notes.getSLpn()==4,"four purchase notes");
                for(int k=1;k<=4;k++){
                    PhieuNhap pn=notes.timPhieu("PN"+k);
                    check(pn!=null && pn.getSoLuongNhap()==1,"purchase entry "+k);
                    check(pn.getChiTiet().getNhaCungCap()!=null,"supplier mapped "+k);
                }
                notes.ghiFile(out.toString());
                DanhSachPhieuNhap loaded=new DanhSachPhieuNhap();loaded.docFile(out.toString());
                check(loaded.getSLpn()==4,"four notes roundtripped");
                i=1;for(MatHang p:products()){
                    MatHang r=loaded.timPhieu("PN"+(i++)).getDSMH()[0];
                    check(r.getClass()==p.getClass(),"note class "+p.getMa());
                    check(r.toString().equals(p.toString()),"purchase field lost: "+p.getMa());
                }
            }finally{Files.deleteIfExists(in);Files.deleteIfExists(out);}
        });
        run("manual inventory menu creates restored toy and notebook", ()->{
            useInput("8\nDCH0102\nLego2\nMaker2\n6\n100\n7\n",()->{
                Kho warehouse=new Kho(); warehouse.them();
                check(warehouse.getSoLuongTon()==1,"one manual product");
                HangTonKho row=warehouse.getDanhSachTon()[0];
                check(row.getMatHang() instanceof DoChoi,"manual toy");
                check(row.getSoLuong()==7,"toy warehouse count");
            });
            useInput("11\nVO0102\nVo 200\n200\n100\n2\n",()->{
                MatHang mh=new PhieuNhap().nhapMH();
                check(mh instanceof Vo && ((Vo)mh).getSotrang()==200,"manual notebook");
            });
        });
        System.out.println("LEGACY PRODUCT TESTS: "+passed+" PASS, "+failed+" FAIL");
        if(failed!=0) System.exit(1);
    }
}
