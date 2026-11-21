package chinhanh;

import hoadon.DanhSachHoaDon;
import khachhang.DanhSachKhachHang;
import kho.Kho;
import mathang.DanhSachMatHang;
import nhanvien.DanhSachNhanVien;

// Moi chi nhanh co danh sach mat hang va hoa don rieng.
// Cac chi nhanh dung chung kho, khach hang va nhan vien.
public class ChiNhanh {
    private final String ma;
    private String ten;
    private String diaChi;
    private final DanhSachMatHang dsmh;
    private final DanhSachHoaDon dshd;

    public ChiNhanh(String ma, String ten, String diaChi,
                    Kho kho, DanhSachKhachHang dskh, DanhSachNhanVien dsnv) {
        this.ma = ma;
        this.ten = ten;
        this.diaChi = diaChi;

        // Tao ke hang rieng cho chi nhanh, nhung van su dung kho chung.
        dsmh = new DanhSachMatHang(kho);
        dshd = new DanhSachHoaDon(dsmh, dskh, dsnv);
    }

    public ChiNhanh(String ma, String ten, Kho kho,
                    DanhSachKhachHang dskh, DanhSachNhanVien dsnv) {
        this(ma, ten, "", kho, dskh, dsnv);
    }

    public String getMa() {
        return ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public DanhSachMatHang getDsmh() {
        return dsmh;
    }

    public DanhSachHoaDon getDshd() {
        return dshd;
    }

    public boolean coDuLieu() {
        return dsmh.getSoMH() > 0 || dshd.getSoLuong() > 0;
    }
}
