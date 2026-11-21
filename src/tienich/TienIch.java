package tienich;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import interfaces.IGetMa;

public class TienIch {
    public static Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);


    public static void cauHinhUnicodeConsole() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
        sc = new Scanner(System.in, StandardCharsets.UTF_8);
    }
    public static int nhapSoNguyenDuong(String thongbao) {
        return nhapSoNguyen(thongbao, 1);
    }


    public static int nhapSoNguyenKhongAm(String thongbao) {
        return nhapSoNguyen(thongbao, 0);
    }

    private static int nhapSoNguyen(String thongbao, int nhoNhat) {
        while (true) {
            System.out.println(thongbao);
            try {
                int num = Integer.parseInt(sc.nextLine().trim());
                if (num >= nhoNhat) return num;
            } catch (NumberFormatException ignored) { }
            System.out.println(nhoNhat == 0
                    ? "LỖI: Vui lòng nhập số nguyên không âm!"
                    : "LỖI: Vui lòng nhập số nguyên lớn hơn 0!");
        }
    }
//nhập giá: số thực, >0
    public static double nhapGiaTien(String thongbao){
        double num=0;
        while(true){
            try{
                System.out.println(thongbao);
                num=Double.parseDouble(sc.nextLine());
                if(Double.isFinite(num) && num>0)
                    break;
            } 
            catch(NumberFormatException e){
                System.out.println("LỖI: Vui lòng nhập lại giá tiền!");
           }
        }
        return num;
    }
//nhập chuỗi: bỏ khoảng cách đầu và cuối chuỗi, đảm bảo chuỗi kh 
    public static String nhapChuoi(String thongbao){
        String input;
        while(true){
            System.out.println(thongbao);
            input=sc.nextLine().trim();
            if(!input.isEmpty())
                return input;
            else 
                System.out.println("LỖI: Chuỗi không được để trống!");
        }
    }
//nhập ngày: dùng DateTimeFormatter để chuyển kiểu quốc tế yyyy-MM-dd theo định dạng dd/MM/yyyy
    public static LocalDate nhapNgay(String thongbao){
        return nhapNgay(thongbao, false);
    }

    public static LocalDate nhapNgay(String thongbao, boolean choPhepTrong){
        String input;
        while(true){
            System.out.println(thongbao);
            input=sc.nextLine().trim();
            if(input.isEmpty()) {
                if (choPhepTrong) return null;
                System.out.println("LỖI: Ngày không được để trống!");
                continue;
            }
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);
                return LocalDate.parse(input,formatter);
            }
            catch (DateTimeParseException e){
                System.out.println("LỖI! Vui lòng nhập theo chuẩn dd/MM/yyyy");
            }
        }
    }
//chuyển ngày tháng string để in
    public static String ngaytoString(LocalDate ngay){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return ngay.format(formatter);
    }
//tìm kiếm vị trí theo mã (mã hàng, mã phiếu,...)
    public static int timKiemViTri(IGetMa[] ds, int soLuong, String maCanTim){
        if (ds == null || maCanTim == null || maCanTim.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < soLuong; i++) {
            if (ds[i] != null && ds[i].getMa().equalsIgnoreCase(maCanTim.trim())) {
                return i;
            }
        }
        return -1;
    }
//nhập mã kh bị trùng (mã hàng, mã phiếu,...)
    public static String nhapMa(IGetMa[] ds, int soLuong, String thongbao){
        String ma;
        while(true){
            ma= TienIch.nhapChuoi(thongbao);
            if(ma.trim().isEmpty()){
                System.out.println("Mã không được để trống!");
                continue;
            }
            if((timKiemViTri(ds, soLuong, ma)!=-1)){ //-1 là kh tìm thấy
                System.out.println("Mã trùng!");
                continue;
            }
            return ma;
        }
        
    }
}
