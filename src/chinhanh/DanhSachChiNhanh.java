package chinhanh;

import khachhang.DanhSachKhachHang;
import kho.Kho;
import nhanvien.DanhSachNhanVien;
import nhanvien.NhanVien;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Danh sách chi nhánh bằng mảng thuần. Định dạng file: ma;ten;diachi (file cũ 2 cột ma;ten vẫn đọc được) */
public class DanhSachChiNhanh {
    private ChiNhanh[] ds = new ChiNhanh[4];
    private int soLuong = 0;
    private final Kho kho;
    private final DanhSachKhachHang dskh;
    private final DanhSachNhanVien dsnv;

    public DanhSachChiNhanh(Kho kho, DanhSachKhachHang dskh, DanhSachNhanVien dsnv) {
        this.kho = kho;
        this.dskh = dskh;
        this.dsnv = dsnv;
    }

    public int getSoLuong() { return soLuong; }
    public ChiNhanh get(int i) { return ds[i]; }

    public ChiNhanh timTheoMa(String ma) {
        if (ma == null) return null;
        for (int i = 0; i < soLuong; i++)
            if (ds[i].getMa().equalsIgnoreCase(ma.trim())) return ds[i];
        return null;
    }

    public boolean them(String ma, String ten) { return them(ma, ten, ""); }

    public boolean them(String ma, String ten, String diaChi) {
        if (ma == null || ma.isBlank() || ten == null || ten.isBlank()) return false;
        if (diaChi == null) diaChi = "";
        try { tienich.TienIch.kiemTraTruongTxt(ma, ten, diaChi); }
        catch (IllegalArgumentException ex) { return false; }
        if (ma.contains("/") || ma.contains("\\") || ma.contains(":") || ma.trim().equals(".") || ma.trim().equals("..")
                || timTheoMa(ma) != null) return false;
        if (soLuong == ds.length) ds = java.util.Arrays.copyOf(ds, ds.length * 2);
        ds[soLuong++] = new ChiNhanh(ma.trim(), ten.trim(), diaChi.trim(), kho, dskh, dsnv);
        return true;
    }

    /** Chỉ xóa được chi nhánh trống (không còn hàng trên kệ, hóa đơn, hay nhân viên). */
    public boolean xoa(String ma) {
        ChiNhanh c = timTheoMa(ma);
        if (c == null || c.coDuLieu() || dsnv.demTheoChiNhanh(c.getMa()) > 0) return false;
        int vt = 0;
        while (ds[vt] != c) vt++;
        for (int i = vt; i < soLuong - 1; i++) ds[i] = ds[i + 1];
        ds[--soLuong] = null;
        return true;
    }

    public void xuat() {
        System.out.println("=== DANH SÁCH CHI NHÁNH ===");
        if (soLuong == 0) System.out.println("Chưa có chi nhánh nào.");
        for (int i = 0; i < soLuong; i++)
            System.out.printf("%-8s | %-22s | %-32s | %d mặt hàng trên kệ | %d hóa đơn%n", ds[i].getMa(), ds[i].getTen(),
                    ds[i].getDiaChi().isEmpty() ? "(chưa có địa chỉ)" : ds[i].getDiaChi(),
                    ds[i].getDsmh().getSoMH(), ds[i].getDshd().getSoLuong());
    }

    /** Xem nhân viên đang làm ở một chi nhánh (chỉ đọc danh sách nhân viên). */
    public void xuatNhanVien(ChiNhanh c) {
        System.out.println("=== NHÂN VIÊN - " + c.getMa() + " | " + c.getTen()
                + (c.getDiaChi().isEmpty() ? "" : " | " + c.getDiaChi()) + " ===");
        int n = 0;
        for (int i = 0; i < dsnv.getSoLuong(); i++) {
            NhanVien nv = dsnv.getDanhSach()[i];
            if (nv.thuocChiNhanh() && nv.getMaChiNhanh().equalsIgnoreCase(c.getMa())) {
                System.out.printf("%-8s | %-20s | %-10s | Ca: %s%n",
                        nv.getMaNguoi(), nv.getHoTen(), nv.getQuyen(), nv.getCaLamViec());
                n++;
            }
        }
        if (n == 0) System.out.println("Chi nhánh chưa có nhân viên.");
    }

    /** Mô tả nơi làm việc của một nhân viên: chi nhánh (kèm địa chỉ) hoặc toàn hệ thống (kho chung). */
    public String moTaChiNhanhCua(NhanVien nv) {
        if (!nv.thuocChiNhanh()) return "Toàn hệ thống (kho chung)";
        ChiNhanh c = timTheoMa(nv.getMaChiNhanh());
        if (c == null) return "(chưa gán chi nhánh hợp lệ)";
        return c.getMa() + " - " + c.getTen() + (c.getDiaChi().isEmpty() ? "" : " (" + c.getDiaChi() + ")");
    }

    public void xuatNhanVienTatCaChiNhanh() {
        for (int i = 0; i < soLuong; i++) { xuatNhanVien(ds[i]); System.out.println(); }
        System.out.println("=== NHÂN VIÊN TOÀN HỆ THỐNG (ADMIN / NHẬP KHO, dùng kho chung) ===");
        int n = 0;
        for (int i = 0; i < dsnv.getSoLuong(); i++) {
            NhanVien nv = dsnv.getDanhSach()[i];
            if (!nv.thuocChiNhanh()) {
                System.out.printf("%-8s | %-20s | %s%n", nv.getMaNguoi(), nv.getHoTen(), nv.getQuyen());
                n++;
            }
        }
        if (n == 0) System.out.println("(không có)");
    }

    public void ghiFile(String duongDan) {
        try {
            ghiFileAnToan(duongDan);
            System.out.println("Đã ghi " + soLuong + " chi nhánh vào " + duongDan);
        } catch (IOException | IllegalArgumentException e) { System.out.println("Lỗi ghi chi nhánh: " + e.getMessage()); }
    }

    public void ghiFileAnToan(String duongDan) throws IOException {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < soLuong; i++) {
            tienich.TienIch.kiemTraTruongTxt(ds[i].getMa(), ds[i].getTen(), ds[i].getDiaChi());
            text.append(ds[i].getMa()).append(';').append(ds[i].getTen()).append(';').append(ds[i].getDiaChi()).append('\n');
        }
        tienich.TienIch.ghiTepNguyenTu(duongDan, text.toString());
    }

    /** Chỉ thêm chi nhánh chưa có; không làm mất dữ liệu của chi nhánh đã nạp. */
    public void docFile(String duongDan) {
        int them = 0, boQua = 0;
        try (BufferedReader br = Files.newBufferedReader(Path.of(duongDan), StandardCharsets.UTF_8)) {
            String s;
            while ((s = br.readLine()) != null) {
                if (s.startsWith("\uFEFF")) s = s.substring(1);
                if (s.isBlank()) continue;
                String[] a = s.split(";", -1);
                if ((a.length == 2 && them(a[0], a[1])) || (a.length == 3 && them(a[0], a[1], a[2]))) them++; else boQua++;
            }
            System.out.println("Đã tải " + them + " chi nhánh (bỏ qua " + boQua + " dòng lỗi/trùng).");
        } catch (IOException e) { System.out.println("Không thể đọc " + duongDan + ": " + e.getMessage()); }
    }
}
