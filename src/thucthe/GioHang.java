package thucthe;

public class GioHang {
    private KhachHang khachHang;
    private ChiTietHoaDon[] danhSachChiTiet;
    private int soLuongMon;

    public GioHang(KhachHang khachHang) {
        this.khachHang = khachHang;
        this.danhSachChiTiet = new ChiTietHoaDon[5]; // Sức chứa ban đầu
        this.soLuongMon = 0;
    }

    // Thuật toán mảng động (Tương tự DanhSachSanPham)
    private void tangKichThuocMang() {
        ChiTietHoaDon[] mangMoi = new ChiTietHoaDon[danhSachChiTiet.length * 2];
        for (int i = 0; i < soLuongMon; i++) {
            mangMoi[i] = danhSachChiTiet[i];
        }
        danhSachChiTiet = mangMoi;
    }

    public void themVaoGio(SanPham sp, int soLuong) {
        // Kiểm tra xem sản phẩm đã có trong giỏ chưa, nếu có thì cộng dồn số lượng
        for (int i = 0; i < soLuongMon; i++) {
            if (danhSachChiTiet[i].getSanPham().getMaSP().equals(sp.getMaSP())) {
                int slMoi = danhSachChiTiet[i].getSoLuongMua() + soLuong;
                danhSachChiTiet[i].setSoLuongMua(slMoi);
                System.out.println("Đã cập nhật số lượng sản phẩm trong giỏ!");
                return;
            }
        }

        // Nếu chưa có thì thêm mới
        if (soLuongMon == danhSachChiTiet.length) tangKichThuocMang();
        danhSachChiTiet[soLuongMon] = new ChiTietHoaDon(sp, soLuong);
        soLuongMon++;
        System.out.println("Đã thêm vào giỏ hàng thành công!");
    }

    public double tinhTongTien() {
        double tong = 0;
        for (int i = 0; i < soLuongMon; i++) {
            tong += danhSachChiTiet[i].tinhThanhTien();
        }
        return tong;
    }

    public void lamTrongGio() {
        this.danhSachChiTiet = new ChiTietHoaDon[5];
        this.soLuongMon = 0;
    }

    public void xemGioHang() {
        if (soLuongMon == 0) {
            System.out.println("Giỏ hàng của bạn đang trống!");
            return;
        }
        System.out.println("=== GIỎ HÀNG CỦA " + khachHang.getHoTen().toUpperCase() + " ===");
        for (int i = 0; i < soLuongMon; i++) {
            danhSachChiTiet[i].xuatChiTiet();
        }
        System.out.printf(">>> TỔNG TẠM TÍNH: %.2f\n", tinhTongTien());
    }
    // 1. Hàm Xóa món khỏi giỏ hàng
    public void xoaKhoiGio(String maSP) {
        int viTri = -1;
        // Tìm xem sản phẩm nằm ở vị trí nào trong giỏ
        for (int i = 0; i < soLuongMon; i++) {
            if (danhSachChiTiet[i].getSanPham().getMaSP().equalsIgnoreCase(maSP)) {
                viTri = i;
                break;
            }
        }

        if (viTri != -1) {
            // Dời các phần tử phía sau lên để lấp chỗ trống
            for (int i = viTri; i < soLuongMon - 1; i++) {
                danhSachChiTiet[i] = danhSachChiTiet[i + 1];
            }
            danhSachChiTiet[soLuongMon - 1] = null;
            soLuongMon--;
            System.out.println("Đã xóa sản phẩm khỏi giỏ hàng thành công!");
        } else {
            System.out.println("Lỗi: Không tìm thấy sản phẩm này trong giỏ!");
        }
    }

    // 2. Hàm Cập nhật số lượng
    public void capNhatSoLuong(String maSP, int soLuongMoi) {
        if (soLuongMoi <= 0) {
            xoaKhoiGio(maSP); // Nếu khách đổi số lượng về 0 thì tự động xóa luôn món đó
            return;
        }

        for (int i = 0; i < soLuongMon; i++) {
            if (danhSachChiTiet[i].getSanPham().getMaSP().equalsIgnoreCase(maSP)) {
                // Phải kiểm tra xem quầy bán có đủ số lượng khách muốn không
                if (soLuongMoi <= danhSachChiTiet[i].getSanPham().getSoLuongQuay()) {
                    danhSachChiTiet[i].setSoLuongMua(soLuongMoi);
                    System.out.println("Đã cập nhật số lượng thành công!");
                } else {
                    System.out.println("Lỗi: Trên quầy chỉ còn " + danhSachChiTiet[i].getSanPham().getSoLuongQuay() + " sản phẩm!");
                }
                return;
            }
        }
        System.out.println("Lỗi: Không tìm thấy sản phẩm này trong giỏ!");
    }

    public ChiTietHoaDon[] getDanhSachChiTiet() { return danhSachChiTiet; }
    public int getSoLuongMon() { return soLuongMon; }
    public KhachHang getKhachHang() { return khachHang; }
}