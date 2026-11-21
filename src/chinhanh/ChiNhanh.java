package chinhanh;

import hoadon.DanhSachHoaDon;
import khachhang.DanhSachKhachHang;
import kho.Kho;
import mathang.DanhSachMatHang;
import nhanvien.DanhSachNhanVien;

/**
 * Một chi nhánh: kệ bán (dsmh) và hóa đơn (dshd) riêng.
 * Tất cả chi nhánh dùng chung một Kho, khách hàng và danh sách nhân viên.
 */
public class ChiNhanh {
    private final String ma;
    private String ten;
    private String diaChi;
    private final DanhSachMatHang dsmh;
    private final DanhSachHoaDon dshd;

    public ChiNhanh(String ma, String ten, Kho khoChung, DanhSachKhachHang dskh, DanhSachNhanVien dsnv) {
        this(ma, ten, "", khoChung, dskh, dsnv);
    }

    public ChiNhanh(String ma, String ten, String diaChi, Kho khoChung, DanhSachKhachHang dskh, DanhSachNhanVien dsnv) {
        this.ma = ma;
        this.ten = ten;
        this.diaChi = diaChi == null ? "" : diaChi.trim();
        this.dsmh = new DanhSachMatHang(khoChung);          // cùng trỏ vào 1 kho
        this.dshd = new DanhSachHoaDon(dsmh, dskh, dsnv);   // hóa đơn trừ hàng trên kệ của chính chi nhánh này
    }

    public String getMa() { return ma; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi == null ? "" : diaChi.trim(); }
    public DanhSachMatHang getDsmh() { return dsmh; }
    public DanhSachHoaDon getDshd() { return dshd; }
    public boolean coDuLieu() { return dsmh.getSoMH() > 0 || dshd.getSoLuong() > 0; }
}
