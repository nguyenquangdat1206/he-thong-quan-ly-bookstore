package danhsach;

import thucthe.Sach;
import thucthe.SanPham;
import thucthe.VanPhongPham;
import java.util.Scanner;

public class DanhSachSanPham implements IThaoTacThucThe {
    private SanPham[] danhSach;
    private int soLuong;
    private Scanner sc = new Scanner(System.in);

    public DanhSachSanPham() {
        danhSach = new SanPham[2];
        soLuong = 0;
    }

    public void tangKichThuocMang() {
        int kichThuocMoi = danhSach.length * 2;
        SanPham[] mangMoi = new SanPham[kichThuocMoi];

        for (int i = 0; i < soLuong; i++) {
            mangMoi[i] = danhSach[i];
        }
        danhSach = mangMoi;
    }

    public void them(SanPham sp) {
        if (soLuong == danhSach.length) {
            tangKichThuocMang();
        }
        danhSach[soLuong] = sp;
        soLuong++;
    }

    @Override
    public void them() {
        System.out.println("Chức năng thêm từ bàn phím sẽ được hoàn thiện ở phần Giao diện.");
    }

    @Override
    public void xoa(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri == -1) {
            System.out.println("Lỗi: Không tìm thấy sản phẩm có mã " + ma);
            return;
        }
        for (int i = viTri; i < soLuong - 1; i++) {
            danhSach[i] = danhSach[i + 1];
        }
        danhSach[soLuong - 1] = null;
        soLuong--;
        System.out.println("Đã xóa thành công sản phẩm!");
    }

    @Override
    public void sua(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri == -1) {
            System.out.println("Lỗi: Không tìm thấy sản phẩm có mã " + ma);
            return;
        }
        System.out.println("Đang sửa thông tin cho sản phẩm: " + danhSach[viTri].getTenSP());
        System.out.print("Nhập tên mới (Enter để bỏ qua): ");
        String tenMoi = sc.nextLine();
        if (!tenMoi.isEmpty()) {
            danhSach[viTri].setTenSP(tenMoi);
        }
        System.out.println("Đã cập nhật thông tin thành công!");
    }

    @Override
    public void timKiem(String ma) {
        int viTri = timKiemViTri(ma);
        if (viTri != -1) {
            System.out.println("--- THÔNG TIN SẢN PHẨM TÌM THẤY ---");
            // Tạm ẩn xuatThongTin() vì ta dùng hàm xuat() bảng chung
            System.out.println("Mã SP: " + danhSach[viTri].getMaSP() + " | Tên: " + danhSach[viTri].getTenSP());
        } else {
            System.out.println("Không tồn tại mã sản phẩm này!");
        }
    }

    @Override
    public void xuat() {
        if (soLuong == 0) {
            System.out.println("Danh sách sản phẩm trống!");
            return;
        }

        System.out.println("\n============================================ DANH SÁCH SÁCH ============================================");
        System.out.printf("%-8s | %-20s | %-10s | %-6s | %-6s | %-15s | %-15s | %-6s\n",
                "Mã SP", "Tên Sách", "Đơn Giá", "SL Kho", "SL Quầy", "Tác Giả", "Nhà Xuất Bản", "Năm XB");
        System.out.println("--------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] instanceof Sach) {
                Sach s = (Sach) danhSach[i];
                System.out.printf("%-8s | %-20s | %-10.2f | %-6d | %-6d | %-15s | %-15s | %-6d\n",
                        s.getMaSP(), s.getTenSP(), s.getDonGia(), s.getSoLuongKho(), s.getSoLuongQuay(),
                        s.getTacGia(), s.getNhaXuatBan(), s.getNamXuatBan());
            }
        }

        System.out.println("\n====================================== DANH SÁCH VĂN PHÒNG PHẨM ======================================");
        System.out.printf("%-8s | %-20s | %-10s | %-6s | %-6s | %-15s | %-10s\n",
                "Mã SP", "Tên VPP", "Đơn Giá", "SL Kho", "SL Quầy", "Thương Hiệu", "Màu Sắc");
        System.out.println("------------------------------------------------------------------------------------------------------");
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i] instanceof VanPhongPham) {
                VanPhongPham vpp = (VanPhongPham) danhSach[i];
                System.out.printf("%-8s | %-20s | %-10.2f | %-6d | %-6d | %-15s | %-10s\n",
                        vpp.getMaSP(), vpp.getTenSP(), vpp.getDonGia(), vpp.getSoLuongKho(), vpp.getSoLuongQuay(),
                        vpp.getThuongHieu(), vpp.getMauSac());
            }
        }
        System.out.println("========================================================================================================");
    }

    public int timKiemViTri(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMaSP().equals(ma)) {
                return i;
            }
        }
        return -1;
    }

    // ================= TÍNH NĂNG MỚI: TÌM KIẾM VÀ LỌC =================

    // 1. Tìm kiếm theo tên (Có chứa từ khóa, không phân biệt hoa/thường)
    public void timKiemTheoTen(String tuKhoa) {
        boolean timThay = false;
        System.out.println("\n================ KẾT QUẢ TÌM KIẾM CHO: '" + tuKhoa + "' ================");
        System.out.printf("%-8s | %-20s | %-10s | %-6s | %-6s\n", "Mã SP", "Tên Sản Phẩm", "Đơn Giá", "SL Kho", "SL Quầy");
        System.out.println("----------------------------------------------------------------------");

        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getTenSP().toLowerCase().contains(tuKhoa.toLowerCase())) {
                System.out.printf("%-8s | %-20s | %-10.2f | %-6d | %-6d\n",
                        danhSach[i].getMaSP(), danhSach[i].getTenSP(), danhSach[i].getDonGia(),
                        danhSach[i].getSoLuongKho(), danhSach[i].getSoLuongQuay());
                timThay = true;
            }
        }
        if (!timThay) System.out.println(">>> Không tìm thấy sản phẩm nào chứa từ khóa này!");
        System.out.println("======================================================================");
    }

    // 2. Lọc sản phẩm theo danh mục (Dùng instanceof)
    public void locTheoLoai(int loai) {
        boolean coHang = false;
        if (loai == 1) {
            System.out.println("\n====================== CHỈ HIỂN THỊ: SÁCH ======================");
            System.out.printf("%-8s | %-20s | %-10s | %-6s | %-15s\n", "Mã SP", "Tên Sách", "Đơn Giá", "SL Quầy", "Tác Giả");
            for (int i = 0; i < soLuong; i++) {
                if (danhSach[i] instanceof Sach) {
                    Sach s = (Sach) danhSach[i];
                    System.out.printf("%-8s | %-20s | %-10.2f | %-6d | %-15s\n",
                            s.getMaSP(), s.getTenSP(), s.getDonGia(), s.getSoLuongQuay(), s.getTacGia());
                    coHang = true;
                }
            }
        } else if (loai == 2) {
            System.out.println("\n================= CHỈ HIỂN THỊ: VĂN PHÒNG PHẨM =================");
            System.out.printf("%-8s | %-20s | %-10s | %-6s | %-15s\n", "Mã SP", "Tên VPP", "Đơn Giá", "SL Quầy", "Thương Hiệu");
            for (int i = 0; i < soLuong; i++) {
                if (danhSach[i] instanceof VanPhongPham) {
                    VanPhongPham vpp = (VanPhongPham) danhSach[i];
                    System.out.printf("%-8s | %-20s | %-10.2f | %-6d | %-15s\n",
                            vpp.getMaSP(), vpp.getTenSP(), vpp.getDonGia(), vpp.getSoLuongQuay(), vpp.getThuongHieu());
                    coHang = true;
                }
            }
        } else {
            System.out.println("Lựa chọn danh mục không hợp lệ!");
            return;
        }

        if (!coHang) System.out.println(">>> Danh mục này hiện đang trống!");
        System.out.println("================================================================");
    }

    public SanPham[] getDanhSach() { return danhSach; }
    public int getSoLuong() { return soLuong; }
}