package phieunhap;

import kho.Kho;
import tienich.TienIch;

public class QuanLyPhieuNhap {

    static String hoiFile(String macDinh) {
        System.out.print("Đường dẫn file (Enter hoặc . để dùng '" + macDinh + "'): ");
        String s = TienIch.sc.nextLine().trim();
        return (s.isEmpty() || s.equals(".")) ? macDinh : s;
    }

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
        System.out.println("10. Xem danh sách nhà cung cấp trong lần nhập này");
        System.out.println("11. Tự động nhập tất cả phiếu chờ vào kho và ghi danh sách đã nhập");
        System.out.println("0. Thoát");
        System.out.println("========================================");
    }

    public static void main(String[] args){
        DanhSachPhieuNhap ds = new DanhSachPhieuNhap();
        Kho kho = new Kho();
        int chon;
        do{
            hienMenu();
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch(chon){
                case 1:  ds.xuat(); break;
                case 2:  ds.them(); break;
                case 3:  ds.nhap(); break;
                case 4:  ds.sua(); break;
                case 5:  ds.xoa(); break;
                case 6:  ds.nhapKho(kho); break;
                case 7:  kho.xuat(); break;
                case 8: ds.docFile(hoiFile("data/phieunhap_input.txt")); break;
                case 9: ds.ghiFile(hoiFile("data/phieunhap_output.txt")); break;
                case 10: ds.getDanhSachNCC().xuat();break;
                case 11: ds.tuDongNhapTatCaVaoKho(kho, hoiFile("data/phieunhap_danhan.txt")); break;
                case 0:  System.out.println("Tạm biệt!"); break;
                default: System.out.println("Lựa chọn không hợp lệ, nhập lại!");
            }
        } while(chon != 0);
    }
}
