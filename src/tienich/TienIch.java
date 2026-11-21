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

    public static void kiemTraTruongTxt(String... fields) {
        for (String value : fields)
            if (value == null || value.indexOf(';') >= 0 || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0)
                throw new IllegalArgumentException("Trường TXT không được chứa dấu ';' hoặc xuống dòng");
    }

    /** Ghi xong file tạm rồi mới thay thế bản cũ. */
    public static void ghiTepNguyenTu(String path, String content) throws java.io.IOException {
        java.nio.file.Path target = java.nio.file.Path.of(path).toAbsolutePath().normalize();
        java.nio.file.Path temp = java.nio.file.Files.createTempFile(target.getParent(), "bookstore-", ".tmp");
        try {
            java.nio.file.Files.writeString(temp, content, StandardCharsets.UTF_8);
            java.nio.file.Files.move(temp, target, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } finally { java.nio.file.Files.deleteIfExists(temp); }
    }

    public static String nhapDuongDan(String macDinh) {
        while (true) {
            System.out.print("Đường dẫn file (Enter hoặc . để dùng '" + macDinh + "'): ");
            String input = sc.nextLine().trim();
            String path = input.isEmpty() || input.equals(".") ? macDinh : input;
            try { java.nio.file.Path.of(path); return path; }
            catch (java.nio.file.InvalidPathException ex) { System.out.println("Đường dẫn không hợp lệ, vui lòng nhập lại."); }
        }
    }

    /** Dùng ở các điểm vào console để Java không thay chữ tiếng Việt bằng '?'. */
    public static void cauHinhUnicodeConsole() {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
        sc = new Scanner(System.in, StandardCharsets.UTF_8);
    }
    public static int nhapSoNguyenDuong(String thongbao) {
        return nhapSoNguyen(thongbao, 1);
    }

    /** Dùng cho lựa chọn menu và những thao tác cho phép giá trị 0. */
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
                System.out.println("LỖI: Giá tiền phải là số hữu hạn lớn hơn 0!");
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
