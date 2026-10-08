package model.kho;

import model.interfaces.IFileIO;
import model.interfaces.IQuanLy;
import model.mathang.DanhSachMatHang;
import model.mathang.MatHang;
import model.utils.TienIch;

public class Kho implements IQuanLy, IFileIO {
    private PhieuNhap[] dsPN;
    private int soLuongPN;
    private static final int SUC_CHUA_TOI_DA = 10000;

    public Kho() {
        dsPN = new PhieuNhap[1000];
        soLuongPN = 0;
    }

    public int getSucChuaToiDa() {
        return SUC_CHUA_TOI_DA;
    }

    @Override
    public void nhap() {
        System.out.println("Sử dụng tính năng Lập phiếu nhập ở Menu để thao tác.");
    }

    @Override
    public void xuat() {
        if (soLuongPN == 0) {
            System.out.println("Danh sách phiếu nhập đang trống!");
            return;
        }
        System.out.println("\n================ LỊCH SỬ PHIẾU NHẬP ================");
        for (int i = 0; i < soLuongPN; i++) {
            if (dsPN[i] != null) {
                dsPN[i].xuat(); // Gọi hàm xuat của PhieuNhap
            }
        }
    }

    @Override
    public void them() {
        // Bỏ trống do sử dụng Overloading bên dưới
    }

    // ================= HÀM LẬP PHIẾU NHẬP CHÍNH =================
    public void them(DanhSachMatHang dsHangHoa) {
        System.out.println("\n--- BẮT ĐẦU LẬP PHIẾU NHẬP KHO ---");
        
        PhieuNhap pn = new PhieuNhap();
        pn.nhap();

        int soMon = TienIch.nhapSoNguyenDuong("Phiếu này nhập bao nhiêu loại mặt hàng? ");
        if (soMon == 0) {
            System.out.println("Đã hủy lập phiếu nhập!");
            return;
        }

        for (int i = 0; i < soMon; i++) {
            System.out.println("\n> Nhập mặt hàng thứ " + (i + 1) + ":");
            String maHang = TienIch.nhapChuoi("- Nhập MÃ mặt hàng cần nhập: ");
            
            // Lấy nguyên đối tượng Mặt hàng từ Kệ Hàng để truyền cho Phiếu Nhập
            MatHang matHangGoc = dsHangHoa.timKiemMatHang(maHang); 
            
            if (matHangGoc == null) {
                System.out.println("[LỖI] Không tìm thấy mã '" + maHang + "'. Yêu cầu qua Menu 1 tạo danh mục trước!");
                continue; 
            }

            int slNhap = TienIch.nhapSoNguyenDuong("- Nhập số lượng: ");
            
            int dangChua = dsHangHoa.getTongSoLuongTonToanHeThong();
            if (dangChua + slNhap > SUC_CHUA_TOI_DA) {
                System.out.println("[LỖI] Kho bị quá tải! Chỉ còn trống: " + (SUC_CHUA_TOI_DA - dangChua));
                continue; 
            }
            
            ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap(matHangGoc, slNhap, matHangGoc.getGiaNhap());
            pn.themChiTiet(ctpn);

            dsHangHoa.tangSoLuongTon(maHang, slNhap);
            System.out.println(">>> Đã thêm " + slNhap + " sản phẩm '" + matHangGoc.getTen() + "' vào giỏ hàng nhập và Kệ Hàng!");
        }
        
        if (pn.getSoLuongCT() > 0) {
            if (soLuongPN < dsPN.length) {
                dsPN[soLuongPN] = pn;
                soLuongPN++;
                System.out.println("\n>>> HOÀN TẤT LẬP PHIẾU VÀ ĐÃ LƯU VÀO LỊCH SỬ KHO!");
            }
        } else {
            System.out.println("\n>>> Phiếu nhập rỗng (Không có mặt hàng nào hợp lệ), đã tự động hủy phiếu.");
        }
    }

    // ================= CÁC HÀM SỬA/XÓA =================
    public void sua() {
        String maSua = TienIch.nhapChuoi("Nhập mã phiếu cần sửa: ");
        sua(maSua);
    }

    @Override
    public void sua(String ma) {
        System.out.println("Tính năng sửa phiếu nhập đang cập nhật.");
    }

    public void xoa() {
        if (soLuongPN == 0) {
            System.out.println("Không có phiếu nhập nào để xóa.");
            return;
        }
        String maXoa = TienIch.nhapChuoi("Nhập mã phiếu cần xóa: ");
        xoa(maXoa);
    }

    @Override
    public void xoa(String ma) {
        int viTri = TienIch.timKiemViTri(dsPN, soLuongPN, ma);
        if (viTri == -1) {
            System.out.println("Không tìm thấy phiếu mã: " + ma);
            return;
        }
        for (int i = viTri; i < soLuongPN - 1; i++) {
            dsPN[i] = dsPN[i + 1];
        }
        dsPN[soLuongPN - 1] = null;
        soLuongPN--;
        System.out.println(">>> Đã xóa phiếu nhập mã: " + ma);
    }

    @Override
    public void ghiFile(String duongDan) {
        System.out.println("Ghi danh sách Phiếu Nhập ra file.");
    }

    @Override
    public void docFile(String duongDan) {
        System.out.println("Đọc danh sách Phiếu Nhập từ file.");
    }
}