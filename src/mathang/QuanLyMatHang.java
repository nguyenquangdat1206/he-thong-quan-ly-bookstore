package mathang;
import kho.*;
import tienich.TienIch;

public class QuanLyMatHang {
    static Kho kho = new Kho();
    static DanhSachMatHang dsmh = new DanhSachMatHang(kho);

    static String hoiFile(String macDinh) {
        return TienIch.nhapDuongDan(macDinh);
    }
    public static void main(String[] args) {
        TienIch.cauHinhUnicodeConsole();
        int chon;
        do {
            System.out.println("\n---------- MẶT HÀNG (CỬA HÀNG) ----------");
            System.out.println("1. Xem số lượng mặt hàng đang bày bán");
            System.out.println("2. Nhập 1 mặt hàng cụ thể từ kho lên");
            System.out.println("3. Nhập hàng loạt từ kho lên");
            System.out.println("4. Trả hàng từ kệ về kho");
            System.out.println("5. Xuất danh sách mặt hàng");
            System.out.println("6. Xóa mặt hàng");
            System.out.println("7. Sửa mặt hàng");
            System.out.println("8. Tìm kiếm theo mã");
            System.out.println("9. Bộ lọc nâng cao");
            System.out.println("10. Ghi mặt hàng ra file");
            System.out.println("11. Thống kê hàng trong kho chưa lên kệ bán");
            System.out.println("12. Đọc kho từ file trước khi đưa hàng lên kệ");
            System.out.println("0. Quay lại");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            switch (chon) {
                case 1: System.out.println("Số mặt hàng: " + dsmh.getSoMH()); break;
                case 2: dsmh.nhap(); break;
                case 3: {
                    dsmh.nhapHangLoat(kho);
                    break;
                }
                case 4: dsmh.traHangVeKho(kho); break;
                case 5: dsmh.xuat(); break;
                case 6: dsmh.xoa(); break;
                case 7: dsmh.sua(); break;
                case 8: dsmh.timKiem(); break;
                case 9: dsmh.timkiem_nangcao(); break;
                case 10: dsmh.ghiFile(hoiFile("data/mathang_output.txt")); break;
                case 11: dsmh.thongKeHangChuaLenKe(kho); break;
                case 12: kho.docFile(hoiFile("data/kho_input.txt")); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (chon != 0);
    }
}
