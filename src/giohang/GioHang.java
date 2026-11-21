package giohang;

import java.time.LocalDate;
import hoadon.ChiTietHoaDon;
import hoadon.DanhSachHoaDon;
import hoadon.HoaDon;
import khachhang.KhachHang;
import mathang.MatHang;
import nhanvien.NhanVienBanHang;

public class GioHang {
    private KhachHang khachHang;
    private ChiTietGioHang[] ds;
    private int soDong;

    public GioHang(KhachHang khachHang) {
        this.khachHang = khachHang;
        ds = new ChiTietGioHang[5];
        soDong = 0;
    }

    private void tangKichThuoc() {
        ChiTietGioHang[] temp = new ChiTietGioHang[ds.length * 2];
        for (int i = 0; i < soDong; i++) {
            temp[i] = ds[i];
        }
        ds = temp;
    }

    // tìm index dòng của mặt hàng cần tìm trong danh sách
    private int timViTri(String ma) {
        for (int i = 0; i < soDong; i++) {
            if (ds[i].getMatHang().getMa().equalsIgnoreCase(ma))
                return i;
        }
        return -1; // chỉ số không hợp lệ, không có mặt hàng đó trong danh sách
    }

    // Thêm vào giỏ (đã có thì cộng dồn), không vượt quá số lượng trên kệ
    public void themVaoGio(MatHang mh, int sl) {
        if (mh == null || sl <= 0) {
            System.out.println("Mặt hàng hoặc số lượng không hợp lệ!");
            return;
        }
        int vt = timViTri(mh.getMa());
        int daCo = (vt == -1) ? 0 : ds[vt].getSoLuongMon(); // số lượng mặt hàng có sẵn trong giỏ
        MatHang hangTrenKe = vt == -1 ? mh : ds[vt].getMatHang();
        if ((long) daCo + sl > hangTrenKe.getSoLuong()) {
            System.out.println("Không đủ hàng! Trên kệ còn " + mh.getSoLuong() + ", trong giỏ đã có " + daCo);
            return;
        }
        if (vt != -1) {
            ds[vt].setSoLuongMon(daCo + sl);
        } else {
            if (soDong == ds.length) tangKichThuoc();
            ds[soDong++] = new ChiTietGioHang(mh, sl);
        }
        System.out.println("Đã thêm vào giỏ hàng!");
    }

    //Xóa 1 mặt hàng khỏi giỏ
    public void xoaKhoiGio(String ma) {
        int vt = timViTri(ma);
        if (vt == -1) {
            System.out.println("Không tìm thấy mặt hàng này trong giỏ!");
            return;
        }
        for (int i = vt; i < soDong - 1; i++) {
            ds[i] = ds[i + 1];
        }
        ds[--soDong] = null;
        System.out.println("Đã xóa mặt hàng khỏi giỏ!");
    }

    //Số lượng chốt/ số lượng cuối cùng khách hàng quyết định mua
    public void capNhatSoLuong(String ma, int slmoi) {
        if (slmoi <= 0) {
            xoaKhoiGio(ma);
            return;
        }
        int vt = timViTri(ma);
        if (vt == -1) {
            System.out.println("Không tìm thấy mặt hàng này trong giỏ!");
        } else if (slmoi > ds[vt].getMatHang().getSoLuong()) {
            System.out.println("Trên kệ chỉ còn " + ds[vt].getMatHang().getSoLuong() + " sản phẩm!");
        } else {
            ds[vt].setSoLuongMon(slmoi);
            System.out.println("Đã cập nhật số lượng!");
        }
    }

    public double tongTien() {
        double tong = 0;
        for (int i = 0; i < soDong; i++) {
            tong += ds[i].thanhTien();
        }
        return tong;
    }

    public boolean isRong() {
        return soDong == 0;
    }

    public void lamTrong() {
        ds = new ChiTietGioHang[5];
        soDong = 0;
    }

    public void xemGioHang() {
        if (isRong()) {
            System.out.println("Giỏ hàng đang trống!");
            return;
        }
        System.out.println("=== GIỎ HÀNG CỦA " + khachHang.getTenKH().toUpperCase() + " ===");
        for (int i = 0; i < soDong; i++) {
            // Đơn giá đã gồm thuế: tách ra giá chưa thuế + tiền thuế của 1 sản phẩm
            double thue = ds[i].getMatHang().getThue();
            double giaChuaThue = ds[i].getDonGia() / (1 + thue / 100);
            double tienThue = ds[i].getDonGia() - giaChuaThue;
            System.out.printf("%d. %s (%s) | SL: %d%n", i + 1,
                    ds[i].getMatHang().getTen(), ds[i].getMatHang().getMa(), ds[i].getSoLuongMon());
            System.out.printf("   Giá bán: %.0f + Thuế (%.0f%%): %.0f = Đơn giá: %.0f%n",
                    giaChuaThue, thue, tienThue, ds[i].getDonGia());
            System.out.printf("   Thành tiền: %.0f x %d = %.0f%n",
                    ds[i].getDonGia(), ds[i].getSoLuongMon(), ds[i].thanhTien());
        }
        System.out.printf(">>> TỔNG TẠM TÍNH: %.0f%n", tongTien());
    }

    // Khách xác nhận mua: tạo hóa đơn từ giỏ, lưu vào danh sách hóa đơn rồi làm trống giỏ.
    // Trả về null (giỏ giữ nguyên) nếu giỏ rỗng, mã trùng hoặc kệ không đủ hàng.
    public HoaDon xacNhanMua(DanhSachHoaDon dshd, String maHD, NhanVienBanHang nv) {
        if (isRong()) {
            System.out.println("Giỏ hàng trống, không thể mua!");
            return null;
        }
        if (dshd.timKiem(maHD) != null) {
            System.out.println("Mã hóa đơn đã tồn tại!");
            return null;
        }
        for (int i = 0; i < soDong; i++) {
            MatHang mh = ds[i].getMatHang();
            if (ds[i].getSoLuongMon() > mh.getSoLuong()) {
                System.out.println("'" + mh.getTen() + "' chỉ còn " + mh.getSoLuong() + " trên kệ, hãy chỉnh lại giỏ!");
                return null;
            }
        }
        HoaDon hd = new HoaDon(maHD, LocalDate.now(), khachHang, nv);
        for (int i = 0; i < soDong; i++) {
            ChiTietGioHang ct = ds[i];
            if (!hd.themChiTiet(new ChiTietHoaDon(ct.getSoLuongMon(), ct.getMatHang(), ct.getDonGia())))
                return null;
        }
        if (!dshd.them(hd)) return null;
        lamTrong();
        return hd;
    }

    public KhachHang getKhachHang() { return khachHang; }
    public int getSoDong() { return soDong; }

}
