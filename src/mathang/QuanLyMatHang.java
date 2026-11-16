package mathang;
import kho.*;
import tienich.TienIch;

public class QuanLyMatHang {
    private static void HienMenu(){
        System.out.println();
        System.out.println("========== QUẢN LÝ CỬA HÀNG ==========");
        System.out.println("1. Xem số lượng hàng mặt hàng được bày bán ");
        System.out.println("2. Nhập hàng loạt mặt hàng từ kho lên");
        System.out.println("3. Nhập một mặt hàng cụ thể từ kho lên");
        System.out.println("4. Xuất danh sách mặt hàng");
        System.out.println("5. Thêm một mặt hàng cụ thể");
        System.out.println("6. Xóa mặt hàng");
        System.out.println("7. Sửa mặt hàng");
        System.out.println("8. Tìm kiếm mặt hàng");
        System.out.println("9. Bộ lọc nâng cao");
        System.out.println("10. Đọc kho từ data/kho_input.txt");
        System.out.println("0. Thoát");
        System.out.println("=================================");
    }

    public static void main(String[] args){
        TienIch.cauHinhUnicodeConsole();
        Kho kho = new Kho(); // tạo kho riêng, không gọi menu kho
        DanhSachMatHang dsmh = new DanhSachMatHang(kho);

        int choice;
        do {
            HienMenu();
            choice = TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ");

            switch(choice){
                case 1: System.out.println("Số mặt hàng: " + dsmh.getSoMH()); break;
                case 2: dsmh.nhapHangLoat(kho); break;
                case 3: dsmh.nhap(); break;
                case 4: dsmh.xuat(); break;
                case 5: dsmh.them(); break;
                case 6: dsmh.xoa(); break;
                case 7: dsmh.sua(); break;
                case 8: dsmh.timkiem(); break;
                case 9: dsmh.timkiem_nangcao(); break;
                case 10: kho.docFile("data/kho_input.txt"); break;
                case 0: System.out.println("Thoát chương trình."); break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while(choice != 0);

    }
}
