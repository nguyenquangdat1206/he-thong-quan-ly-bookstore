package tienich;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import interfaces.IGetMa;

public class TienIch {
    public static Scanner sc = new Scanner(System.in);
//nhập số lượng: số nguyên dương
    public static int nhapSoNguyenDuong(String thongbao){
        int num=0;
        while(true){
            try{
                System.out.println(thongbao);
                num=Integer.parseInt(sc.nextLine());
                if(num>=0)
                    break;
            } 
            catch(NumberFormatException e){
                System.out.println("LỖI: Vui lòng nhập lại số nguyên dương!");
           }
        }
        return num;
    }
//nhập giá: số thực, >0
    public static double nhapGiaTien(String thongbao){
        double num=0;
        while(true){
            try{
                System.out.println(thongbao);
                num=Double.parseDouble(sc.nextLine());
                if(num>0)
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
        String input;
        while(true){
            input=nhapChuoi(thongbao);
            LocalDate ngay=null;
            if(input.isEmpty()) return ngay;
            try{
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                ngay=LocalDate.parse(input,formatter);
                return ngay;
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