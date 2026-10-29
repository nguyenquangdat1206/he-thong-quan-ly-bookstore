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
    private ChiTietHoaDon[] ds;
    private int soDong;

    public GioHang(KhachHang khachHang) {
        this.khachHang = khachHang;
        ds = new ChiTietHoaDon[5];
        soDong = 0;
    }

    private void tangKichThuoc() {
        ChiTietHoaDon[] temp = new ChiTietHoaDon[ds.length * 2];
        for (int i = 0; i < soDong; i++) {
            temp[i] = ds[i];
        }
        ds = temp;
    }

    // tìm index dòng của mặt hàng cần tìm trong danh sách
    private int timViTri(String ma) {
        for (int i = 0; i < soDong; i++) {
            if (ds[i].getMathang().getMa().equalsIgnoreCase(ma))
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
        int daCo = (vt == -1) ? 0 : ds[vt].getsoLuong(); // số lượng mặt hàng có sẵn trong giỏ
        if (daCo + sl > mh.getSoLuong()) {
            System.out.println("Không đủ hàng! Trên kệ còn " + mh.getSoLuong() + ", trong giỏ đã có " + daCo);
            return;
        }
        if (vt != -1) {
            ds[vt].setsoLuong(daCo + sl);
        } else {
            if (soDong == ds.length) tangKichThuoc();
            ds[soDong++] = new ChiTietHoaDon(sl, mh);
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
        } else if (slmoi > ds[vt].getMathang().getSoLuong()) {
            System.out.println("Trên kệ chỉ còn " + ds[vt].getMathang().getSoLuong() + " sản phẩm!");
        } else {
            ds[vt].setsoLuong(slmoi);
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
        ds = new ChiTietHoaDon[5];
        soDong = 0;
    }

    public void xemGioHang() {
        if (isRong()) {
            System.out.println("Giỏ hàng đang trống!");
            return;
        }
        System.out.println("=== GIỎ HÀNG CỦA " + khachHang.getTenKH().toUpperCase() + " ===");
        for (int i = 0; i < soDong; i++) {
            System.out.printf("%d. %s (%s) | SL: %d | Đơn giá: %.0f | Thành tiền: %.0f%n", i + 1,
                    ds[i].getMathang().getTen(), ds[i].getMathang().getMa(),
                    ds[i].getsoLuong(), ds[i].getDonGia(), ds[i].thanhTien());
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
            MatHang mh = ds[i].getMathang();
            if (ds[i].getsoLuong() > mh.getSoLuong()) {
                System.out.println("'" + mh.getTen() + "' chỉ còn " + mh.getSoLuong() + " trên kệ, hãy chỉnh lại giỏ!");
                return null;
            }
        }
        HoaDon hd = new HoaDon(maHD, LocalDate.now(), khachHang, nv);
        for (int i = 0; i < soDong; i++) hd.themChiTiet(ds[i]);
        dshd.them(hd);
        lamTrong();
        return hd;
    }

    public KhachHang getKhachHang() { return khachHang; }
    public int getSoDong() { return soDong; }

}
