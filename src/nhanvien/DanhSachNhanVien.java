package nhanvien;

import interfaces.IFileIO;
import interfaces.IQuanLy;
import tienich.TienIch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Module TV5: danh sách bằng mảng thuần, không dùng Collection. */
public class DanhSachNhanVien implements IQuanLy, IFileIO {
    private NhanVien[] danhSach = new NhanVien[2];
    private int soLuong = 0;
    private boolean duLieuDinhDangCu = false;
    public boolean laDuLieuDinhDangCu() { return duLieuDinhDangCu; }

    // Đọc được các dòng text mô tả khởi tạo nhân viên từ bản main cũ.
    // Không bao giờ thực thi code Java có trong file dữ liệu.
    private static final Pattern NHAN_VIEN_CU = Pattern.compile(
        "^\\s*dsNhanVien\\s*\\.\\s*them\\s*\\(\\s*new\\s+(QuanLy|NhanVienBanHang|NhanVienNhapKho)\\s*\\(\\s*"
        + "\"([^\"\\\\]*)\"\\s*,\\s*".repeat(5)
        + "([0-9]+(?:\\.[0-9]+)?)\\s*\\)\\s*\\)\\s*;\\s*$"
    );
    private static String[] tachDongCu(String s) {
        Matcher m = NHAN_VIEN_CU.matcher(s);
        if (!m.matches()) return null;
        String role;
        switch (m.group(1)) {
            case "QuanLy": role = "ADMIN"; break;
            case "NhanVienBanHang": role = "BAN_HANG"; break;
            case "NhanVienNhapKho": role = "NHAP_KHO"; break;
            default: return null;
        }
        return new String[] {m.group(2), m.group(3), m.group(4), m.group(5),
                             m.group(6), m.group(7), role};
    }

    public int getSoLuong() { return soLuong; }
    public NhanVien[] getDanhSach() { return danhSach; }
    public double tongLuongCoBan() {
        double tong = 0;
        for (int i = 0; i < soLuong; i++) tong += danhSach[i].getLuongCoBan();
        return tong;
    }
    public NhanVien timTheoMa(String ma) {
        int vt = timKiemViTri(ma);
        return vt < 0 ? null : danhSach[vt];
    }
    public int timKiemViTri(String ma) {
        if (ma == null) return -1;
        for (int i = 0; i < soLuong; i++)
            if (danhSach[i].getMaNguoi().equalsIgnoreCase(ma.trim())) return i;
        return -1;
    }
    public int soLuongAdmin() {
        int n = 0;
        for (int i = 0; i < soLuong; i++) if ("ADMIN".equals(danhSach[i].getQuyen())) n++;
        return n;
    }
    public void tangKichThuocMang() {
        NhanVien[] moi = new NhanVien[danhSach.length * 2];
        System.arraycopy(danhSach, 0, moi, 0, soLuong);
        danhSach = moi;
    }
    public boolean them(NhanVien nv) {
        if (nv == null || nv.getMaNguoi() == null || nv.getMaNguoi().isBlank()
            || nv.getHoTen() == null || nv.getHoTen().isBlank()
            || nv.getMatKhau() == null || nv.getMatKhau().isBlank()
            || nv.getLuongCoBan() < 0 || !Double.isFinite(nv.getLuongCoBan())
            || timKiemViTri(nv.getMaNguoi()) != -1) return false;
        if (soLuong == danhSach.length) tangKichThuocMang();
        danhSach[soLuong++] = nv;
        return true;
    }
    public NhanVien dangNhap(String ma, String matKhau) {
        NhanVien nv = timTheoMa(ma);
        return nv != null && matKhau != null && nv.getMatKhau().equals(matKhau) ? nv : null;
    }
    public boolean xoa(String ma) {
        int vt = timKiemViTri(ma);
        if (vt < 0) return false;
        if ("ADMIN".equals(danhSach[vt].getQuyen()) && soLuongAdmin() <= 1) return false;
        for (int i = vt; i < soLuong - 1; i++) danhSach[i] = danhSach[i + 1];
        danhSach[--soLuong] = null;
        return true;
    }
    public boolean sua(String ma, String tenMoi, String sdtMoi, String caMoi, Double luongMoi, String matKhauMoi) {
        NhanVien nv = timTheoMa(ma);
        if (nv == null) return false;
        if (luongMoi != null && (luongMoi < 0 || !Double.isFinite(luongMoi))) return false;
        if (tenMoi != null && !tenMoi.isBlank()) nv.setHoTen(tenMoi.trim());
        if (sdtMoi != null && !sdtMoi.isBlank()) nv.setSoDienThoai(sdtMoi.trim());
        if (caMoi != null && !caMoi.isBlank()) nv.setCaLamViec(caMoi.trim());
        if (luongMoi != null) nv.setLuongCoBan(luongMoi);
        if (matKhauMoi != null && !matKhauMoi.isBlank()) nv.setMatKhau(matKhauMoi);
        return true;
    }
    @Override public void nhap() { them(); }
    @Override public void them() {
        String ma = TienIch.nhapChuoi("Mã nhân viên: ");
        if (timTheoMa(ma) != null) { System.out.println("Mã đã tồn tại."); return; }
        String ten = TienIch.nhapChuoi("Họ tên: ");
        String sdt = TienIch.nhapChuoi("SĐT: ");
        String mk = TienIch.nhapChuoi("Mật khẩu: ");
        String ca = TienIch.nhapChuoi("Ca làm việc: ");
        double luong = nhapLuong();
        int quyen = TienIch.nhapSoNguyenDuong("Vai trò (1: bán hàng, 2: nhập kho, 3: quản lý): ");
        NhanVien nv;
        switch (quyen) {
            case 1: nv = new NhanVienBanHang(ma,ten,sdt,mk,ca,luong); break;
            case 2: nv = new NhanVienNhapKho(ma,ten,sdt,mk,ca,luong); break;
            case 3: nv = new QuanLy(ma,ten,sdt,mk,ca,luong); break;
            default: System.out.println("Vai trò không hợp lệ."); return;
        }
        System.out.println(them(nv) ? "Đã thêm nhân viên." : "Thông tin không hợp lệ hoặc mã bị trùng.");
    }
    private static double nhapLuong() {
        while (true) {
            String s = TienIch.nhapChuoi("Lương cơ bản (>= 0): ");
            try {
                double v = Double.parseDouble(s);
                if (Double.isFinite(v) && v >= 0) return v;
            } catch (NumberFormatException ignored) { }
            System.out.println("Giá trị lương không hợp lệ.");
        }
    }
    @Override public void xoa() {
        String ma = TienIch.nhapChuoi("Mã nhân viên cần xóa: ");
        System.out.println(xoa(ma) ? "Đã xóa nhân viên." : "Không tìm thấy hoặc không thể xóa quản lý cuối cùng.");
    }
    @Override public void sua() {
        String ma = TienIch.nhapChuoi("Mã nhân viên cần sửa: ");
        if (timTheoMa(ma) == null) { System.out.println("Không tìm thấy."); return; }
        String ten = nhapTuyChon("Tên mới");
        String sdt = nhapTuyChon("SĐT mới");
        String ca = nhapTuyChon("Ca mới");
        String luong = nhapTuyChon("Lương mới");
        Double luongMoi = null;
        if (!luong.isEmpty()) {
            try { luongMoi = Double.parseDouble(luong); }
            catch (NumberFormatException e) { System.out.println("Lương không hợp lệ, hủy cập nhật."); return; }
        }
        String mk = nhapTuyChon("Mật khẩu mới");
        System.out.println(sua(ma,ten,sdt,ca,luongMoi,mk) ? "Cập nhật thành công." : "Cập nhật thất bại.");
    }
    private static String nhapTuyChon(String nhan) {
        System.out.print(nhan + " (Enter để giữ nguyên): ");
        return TienIch.sc.nextLine().trim();
    }
    @Override public void xuat() {
        System.out.println("=== DANH SÁCH NHÂN VIÊN (TV5) ===");
        if (soLuong == 0) System.out.println("Danh sách rỗng.");
        for (int i = 0; i < soLuong; i++) danhSach[i].xuatThongTin();
        System.out.printf("Tổng lương cơ bản: %.0f%n", tongLuongCoBan());
    }
    public void timKiem(String ma) {
        NhanVien nv = timTheoMa(ma);
        if (nv == null) System.out.println("Không tìm thấy.");
        else nv.xuatThongTin();
    }
    // Format: ma;ten;sdt;matkhau;ca;luong;quyen. Dữ liệu mẫu học tập, không dùng cho production.
    @Override public void ghiFile(String duongDan) {
        try (BufferedWriter bw = Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8)) {
            for (int i = 0; i < soLuong; i++) {
                NhanVien n = danhSach[i];
                bw.write(String.join(";", n.getMaNguoi(),n.getHoTen(),n.getSoDienThoai(),
                    n.getMatKhau(), n.getCaLamViec(), Double.toString(n.getLuongCoBan()), n.getQuyen()));
                bw.newLine();
            }
            System.out.println("Đã ghi " + soLuong + " nhân viên vào " + duongDan);
        } catch (IOException e) { System.out.println("Lỗi ghi nhân viên: " + e.getMessage()); }
    }
    @Override public void docFile(String duongDan) {
        NhanVien[] moi = new NhanVien[2];
        int dem = 0, boQua = 0;
        boolean coDongCu = false;
        try (BufferedReader br = Files.newBufferedReader(Path.of(duongDan), StandardCharsets.UTF_8)) {
            String s;
            while ((s = br.readLine()) != null) {
                if (s.isBlank()) continue;
                String[] a;
                if (s.stripLeading().startsWith("dsNhanVien")) {
                    coDongCu = true;
                    a = tachDongCu(s);
                    if (a == null) { boQua++; continue; }
                } else {
                    a = s.split(";", -1);
                }
                if (a.length != 7) { boQua++; continue; }
                NhanVien nv;
                try {
                    double luong = Double.parseDouble(a[5]);
                    switch (a[6].trim()) {
                        case "ADMIN": nv = new QuanLy(a[0],a[1],a[2],a[3],a[4],luong); break;
                        case "BAN_HANG": nv = new NhanVienBanHang(a[0],a[1],a[2],a[3],a[4],luong); break;
                        case "NHAP_KHO": nv = new NhanVienNhapKho(a[0],a[1],a[2],a[3],a[4],luong); break;
                        default: boQua++; continue;
                    }
                    boolean trung = false;
                    for (int i = 0; i < dem; i++) if (moi[i].getMaNguoi().equalsIgnoreCase(nv.getMaNguoi())) trung = true;
                    if (trung || nv.getMaNguoi().isBlank() || nv.getHoTen().isBlank() || nv.getMatKhau().isBlank() || luong < 0 || !Double.isFinite(luong)) { boQua++; continue; }
                    if (dem == moi.length) {
                        NhanVien[] n = new NhanVien[moi.length * 2];
                        System.arraycopy(moi, 0, n, 0, dem); moi = n;
                    }
                    moi[dem++] = nv;
                } catch (NumberFormatException e) { boQua++; }
            }
            // Chỉ thay trạng thái khi đọc hoàn tất; đọc file lỗi không phá hỏng danh sách cũ.
            danhSach = moi;
            soLuong = dem;
            duLieuDinhDangCu = coDongCu;
            System.out.println("Đã tải " + dem + " nhân viên (bỏ qua " + boQua + " dòng lỗi).");
        } catch (IOException e) { System.out.println("Không thể đọc " + duongDan + ": " + e.getMessage()); }
    }
}
