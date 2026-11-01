package phieunhap;

import kho.Kho;
import tienich.TienIch;

public class QuanLyPhieuNhap {

    private static final String FILE_MAC_DINH = "data/phieunhap_input.txt";

    private static void hienMenu(){
        System.out.println();
        System.out.println("========== QUẢN LÝ PHIẾU NHẬP ==========");
        System.out.println("1. Xem danh sách phiếu nhập");
        System.out.println("2. Thêm 1 phiếu nhập");
        System.out.println("3. Thêm nhiều phiếu nhập");
        System.out.println("4. Sửa phiếu nhập");
        System.out.println("5. Xóa phiếu nhập");
        System.out.println("6. Nhập hàng của phiếu vào kho");
        System.out.println("7. Xem hàng tồn trong kho");
        System.out.println("8. Đọc phiếu nhập từ file");
        System.out.println("9. Ghi phiếu nhập ra file");
        System.out.println("0. Thoát");
        System.out.println("========================================");
    }

    private static String hoiDuongDan(String macDinh){
        String s = TienIch.nhapChuoi("Nhập đường dẫn file (gõ . để dùng mặc định '" + macDinh + "'): ");
        return s.equals(".") ? macDinh : s;
    }


    public static void main(String[] args){
        DanhSachPhieuNhap ds = new DanhSachPhieuNhap();
        Kho kho = new Kho();
        int chon;
        do{
            hienMenu();
            chon = TienIch.nhapSoNguyenDuong("Chọn: ");
            switch(chon){
                case 1:  ds.xuat(); break;
                case 2:  ds.them(); break;
                case 3:  ds.nhap(); break;
                case 4:  ds.sua(); break;
                case 5:  ds.xoa(); break;
                case 6:  ds.nhapKho(kho); break;
                case 7:  kho.xuat(); break;
                case 8: ds.docFile(hoiDuongDan(FILE_MAC_DINH)); break;
                case 9: ds.ghiFile(hoiDuongDan("data/phieunhap_output.txt")); break;
                case 0:  System.out.println("Tạm biệt!"); break;
                default: System.out.println("Lựa chọn không hợp lệ, nhập lại!");
            }
        } while(chon != 0);
    }
}
