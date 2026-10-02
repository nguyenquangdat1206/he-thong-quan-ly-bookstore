package model.kho;

import model.interfaces.IFileIO;
import model.interfaces.IQuanLy;
import model.mathang.DanhSachMatHang;
import model.utils.TienIch;

public class Kho implements IQuanLy, IFileIO {
    private PhieuNhap[] dsPN;
    private int soLuongPN;
    private static final int SUC_CHUA_TOI_DA = 10000;

    public Kho() {
        dsPN = new PhieuNhap[1000];
        soLuongPN = 0;
    }

    // Lấy tổng sức chứa (Cho Thành viên 5 gọi khi ráp main)
    public int getSucChuaToiDa() {
        return SUC_CHUA_TOI_DA;
    }

    // Giao tiếp với mảng DanhSachMatHang để kiểm tra và cộng dồn số lượng
    public boolean nhapHangTuPhieu(PhieuNhap pn, DanhSachMatHang dsMatHang) {
        if (pn == null || dsMatHang == null) return false;
        
        // 1. Kiểm tra sức chứa (Tổng hàng hiện có + Hàng chuẩn bị nhập)
        int tongHienTai = dsMatHang.getTongSoLuongTonToanHeThong();
        int soLuongCanNhap = 0;
        for (int i = 0; i < pn.getSoLuongCT(); i++) {
            soLuongCanNhap += pn.getChiTiet()[i].getSoLuong();
        }
        
        if (tongHienTai + soLuongCanNhap > SUC_CHUA_TOI_DA) {
            System.out.println("LỖI: Kho không đủ chỗ trống để nhập thêm " + soLuongCanNhap + " sản phẩm!");
            return false;
        }

        // 2. Chọc sang mảng Mặt Hàng để cộng dồn số lượng
        for (int i = 0; i < pn.getSoLuongCT(); i++) {
            ChiTietPhieuNhap ct = pn.getChiTiet()[i];
            // Gọi hàm tangSoLuongTon (Thành viên 2 phải viết hàm này trong DanhSachMatHang)
            dsMatHang.tangSoLuongTon(ct.getMatHang().getMa(), ct.getSoLuong());
        }
        System.out.println("Đã bơm hàng vào kho thành công từ phiếu: " + pn.getMa());
        return true;
    }

    public void nhap() {
        int n = TienIch.nhapSoNguyenDuong("Nhập số lượng phiếu nhập thêm: ");
        for (int i = 0; i < n; i++) {
            System.out.println("====== Nhập phiếu thứ " + (i + 1) + " ======");
            them();
        }
    }

    public void xuat() {
        if (soLuongPN == 0) {
            System.out.println("Danh sách phiếu nhập đang trống!");
            return;
        }
        System.out.println("\n====== DANH SÁCH LỊCH SỬ PHIẾU NHẬP ======");
        for (int i = 0; i < soLuongPN; i++) {
            PhieuNhap pn = dsPN[i];
            System.out.printf("Mã Phiếu: %-10s | Ngày: %-15s | Nhà cung cấp: %-20s\n", 
                              pn.getMa(), pn.getNgayNhap().toString(), pn.getNhaCungCap());
        }
    }

    public void them() {
        if (soLuongPN >= dsPN.length) {
            PhieuNhap[] mangMoi = new PhieuNhap[dsPN.length * 2];
            for (int i = 0; i < dsPN.length; i++) {
                mangMoi[i] = dsPN[i];
            }
            dsPN = mangMoi;
        }
        // Logic tạo PhieuNhap mới do bạn tự triển khai
        PhieuNhap pn = new PhieuNhap();
        // Cần nhập thông tin phiếu và chi tiết phiếu ở đây...
        
        dsPN[soLuongPN] = pn;
        soLuongPN++;
        System.out.println("Thêm phiếu nhập thành công!");
    }

    public void sua() {
        System.out.println("Chức năng sửa Phiếu Nhập đang cập nhật.");
    }

    public void xoa() {
        String maCanXoa = TienIch.nhapChuoi("Nhập mã phiếu cần xóa: ");
        int viTri = TienIch.timKiemViTri(dsPN, soLuongPN, maCanXoa);
        if (viTri == -1) {
            System.out.println("Không tìm thấy phiếu mã: " + maCanXoa);
            return;
        }
        for (int i = viTri; i < soLuongPN - 1; i++) {
            dsPN[i] = dsPN[i + 1];
        }
        dsPN[soLuongPN - 1] = null;
        soLuongPN--;
        System.out.println("Đã xóa phiếu nhập!");
    }

    public void ghiFile(String duongDan) {
        System.out.println("Ghi danh sách Phiếu Nhập ra file.");
    }

    public void docFile(String duongDan) {
        System.out.println("Đọc danh sách Phiếu Nhập từ file.");
    }

    @Override
    public void sua(String ma) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sua'");
    }

    @Override
    public void xoa(String ma) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'xoa'");
    }
}