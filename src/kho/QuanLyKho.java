package kho;
 
import tienich.*;
 
public class QuanLyKho {
 
    private static final String FILE_NHAP_MAC_DINH = "data/kho_input.txt";
    private static final String FILE_XUAT_MAC_DINH = "data/kho_output.txt";
 
    private static void hienMenu(){
        System.out.println();
        System.out.println("========== QUẢN LÝ KHO ==========");
        System.out.println("1. Xem hàng tồn trong kho");
        System.out.println("2. Thêm 1 mặt hàng vào kho");
        System.out.println("3. Nhập nhiều mặt hàng vào kho");
        System.out.println("4. Sửa số lượng tồn");
        System.out.println("5. Xóa mặt hàng khỏi kho");
        System.out.println("6. Tra cứu chi tiết theo mã");
        System.out.println("7. Kiểm tra sức chứa");
        System.out.println("8. Đọc kho từ file");
        System.out.println("9. Ghi kho ra file");
        System.out.println("0. Thoát");
        System.out.println("=================================");
    }
 
    private static String hoiDuongDan(String macDinh){
        String s = TienIch.nhapChuoi("Nhập đường dẫn file (gõ . để dùng mặc định '" + macDinh + "'): ").trim();
        return (s.isEmpty() || s.equals(".")) ? macDinh : s;
    }
    public static void main(String[] args){
        Kho kho = new Kho();
        int chon;
        do{
            hienMenu();
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch(chon){
                case 1: kho.xuat(); break;
                case 2: kho.them(); break;
                case 3: kho.nhap(); break;
                case 4: kho.sua(); break;
                case 5: kho.xoa(); break;
                case 6: kho.timHangTon(); break;
                case 7: kho.kiemTraSucChua(); break;
                case 8: kho.docFile(hoiDuongDan(FILE_NHAP_MAC_DINH)); break;
                case 9: kho.ghiFile(hoiDuongDan(FILE_XUAT_MAC_DINH)); break;
                case 0: System.out.println("Tạm biệt!"); break;
                default: System.out.println("Lựa chọn không hợp lệ, nhập lại!");
            }
        } while(chon != 0);
    }
}
