package tienich;

import chinhanh.*;
import hoadon.*;
import khachhang.*;
import kho.*;
import mathang.*;
import nhacungcap.*;
import nhanvien.*;
import phieunhap.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Properties;

/** Ảnh chụp toàn bộ dữ liệu nghiệp vụ; chỉ công bố sau khi ghi/đọc hoàn tất. */
public final class LuuTruPhien {
    private LuuTruPhien() { }

    public static final class DuLieu {
        public final Kho kho;
        public final DanhSachKhachHang khachHang;
        public final DanhSachNCC nhaCungCap;
        public final DanhSachPhieuNhap phieuNhap;
        public final DanhSachChiNhanh chiNhanh;

        public DuLieu(Kho kho, DanhSachKhachHang khachHang, DanhSachNCC nhaCungCap,
                DanhSachPhieuNhap phieuNhap, DanhSachChiNhanh chiNhanh) {
            this.kho = kho; this.khachHang = khachHang; this.nhaCungCap = nhaCungCap;
            this.phieuNhap = phieuNhap; this.chiNhanh = chiNhanh;
        }
    }

    public static void ghi(Path file, DuLieu data, DanhSachNhanVien nhanVien) throws IOException {
        Properties p = new Properties();
        put(p, "version", "1");
        put(p, "kho.count", data.kho.getSoLuongTon());
        for (int i = 0; i < data.kho.getSoLuongTon(); i++) {
            HangTonKho row = data.kho.getDanhSachTon()[i];
            MatHang mh = row.getMatHang().saoChep(); mh.setSoLuong(row.getSoLuong());
            DuLieuMatHang.ghi(p, "kho." + i, mh);
        }
        put(p, "kh.count", data.khachHang.getSoLuong());
        for (int i = 0; i < data.khachHang.getSoLuong(); i++) ghiKhach(p, "kh." + i, data.khachHang.getDs()[i]);
        put(p, "ncc.count", data.nhaCungCap.getSoLuongNCC());
        for (int i = 0; i < data.nhaCungCap.getSoLuongNCC(); i++) ghiNcc(p, "ncc." + i, data.nhaCungCap.getDsNCC()[i]);
        put(p, "pn.count", data.phieuNhap.getSLpn());
        for (int i = 0; i < data.phieuNhap.getSLpn(); i++) {
            PhieuNhap pn = data.phieuNhap.getMangPhieuNhap()[i]; String key = "pn." + i;
            put(p, key + ".ma", pn.getMa()); put(p, key + ".ngay", pn.getChiTiet().getNgayNhap());
            put(p, key + ".trangThai", pn.getTrangThai()); ghiNcc(p, key + ".ncc", pn.getChiTiet().getNhaCungCap());
            put(p, key + ".count", pn.getSoLuongNhap());
            for (int j = 0; j < pn.getSoLuongNhap(); j++) DuLieuMatHang.ghi(p, key + ".hang." + j, pn.getDSMH()[j]);
        }
        put(p, "cn.count", data.chiNhanh.getSoLuong());
        for (int i = 0; i < data.chiNhanh.getSoLuong(); i++) {
            ChiNhanh cn = data.chiNhanh.get(i); String key = "cn." + i;
            put(p, key + ".ma", cn.getMa()); put(p, key + ".ten", cn.getTen()); put(p, key + ".diaChi", cn.getDiaChi());
            put(p, key + ".ke.count", cn.getDsmh().getSoMH());
            for (int j = 0; j < cn.getDsmh().getSoMH(); j++)
                DuLieuMatHang.ghi(p, key + ".ke." + j, cn.getDsmh().getMangMatHang()[j]);
            put(p, key + ".hd.count", cn.getDshd().getSoLuong());
            for (int j = 0; j < cn.getDshd().getSoLuong(); j++) ghiHoaDon(p, key + ".hd." + j, cn.getDshd().getDs()[j]);
        }
        // Không thay bản lưu cũ nếu dữ liệu đang có không thể khôi phục đầy đủ.
        try { doc(p, nhanVien); }
        catch (IllegalArgumentException | java.time.DateTimeException ex) {
            throw new IOException("Dữ liệu phiên không hợp lệ", ex);
        }
        Path target = file.toAbsolutePath().normalize();
        Files.createDirectories(target.getParent());
        Path temp = Files.createTempFile(target.getParent(), "phien-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) { p.store(writer, "Bookstore session v1"); }
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally { Files.deleteIfExists(temp); }
    }

    public static DuLieu doc(Path file, DanhSachNhanVien nhanVien) throws IOException {
        if (!Files.exists(file)) return null;
        Properties p = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            p.load(reader);
            return doc(p, nhanVien);
        }
        catch (IllegalArgumentException | java.time.DateTimeException ex) { throw new IOException("Bản lưu phiên bị lỗi", ex); }
    }

    private static DuLieu doc(Properties p, DanhSachNhanVien nhanVien) {
        check("1".equals(get(p, "version")), "Phiên bản lưu không hỗ trợ");
        Kho kho = new Kho(); DanhSachKhachHang kh = new DanhSachKhachHang(); DanhSachNCC ncc = new DanhSachNCC();
        DanhSachPhieuNhap pn = new DanhSachPhieuNhap(ncc); DanhSachChiNhanh cn = new DanhSachChiNhanh(kho, kh, nhanVien);
        for (int i = 0, n = count(p, "kho.count"); i < n; i++) {
            MatHang item = DuLieuMatHang.doc(p, "kho." + i);
            for (int j = 0; j < kho.getSoLuongTon(); j++)
                check(!kho.getDanhSachTon()[j].getMa().equalsIgnoreCase(item.getMa()), "Trùng mã tồn kho");
            check(kho.tangTonKho(item, item.getSoLuong()), "Tồn kho vượt sức chứa");
        }
        for (int i = 0, n = count(p, "kh.count"); i < n; i++) check(kh.them(docKhach(p, "kh." + i)), "Khách hàng không hợp lệ hoặc trùng mã");
        for (int i = 0, n = count(p, "ncc.count"); i < n; i++) check(ncc.them(docNcc(p, "ncc." + i)), "NCC không hợp lệ hoặc trùng mã");
        for (int i = 0, n = count(p, "pn.count"); i < n; i++) {
            String key = "pn." + i; PhieuNhap note = new PhieuNhap(); note.setMaPN(get(p, key + ".ma"));
            NhaCungCap saved = docNcc(p, key + ".ncc"), supplier = ncc.timNCC(saved.getMaNCC());
            if (supplier == null || !supplier.getTenNCC().equals(saved.getTenNCC())) supplier = saved;
            note.setChiTiet(new ChiTietPhieuNhap(LocalDate.parse(get(p, key + ".ngay")), supplier));
            for (int j = 0, m = count(p, key + ".count"); j < m; j++) {
                MatHang item = DuLieuMatHang.doc(p, key + ".hang." + j);
                check(item.getSoLuong() > 0, "Dòng phiếu nhập không có số lượng");
                check(TienIch.timKiemViTri(note.getDSMH(), note.getSoLuongNhap(), item.getMa()) < 0, "Trùng hàng trên phiếu nhập");
                note.them(item);
            }
            note.setTrangThai(PhieuNhap.TrangThaiPN.valueOf(get(p, key + ".trangThai")));
            check(pn.them(note), "Phiếu nhập không hợp lệ hoặc trùng mã");
        }
        for (int i = 0, n = count(p, "cn.count"); i < n; i++) {
            String key = "cn." + i;
            check(cn.them(get(p, key + ".ma"), get(p, key + ".ten"), get(p, key + ".diaChi")), "Chi nhánh không hợp lệ hoặc trùng mã");
            ChiNhanh branch = cn.get(i);
            for (int j = 0, m = count(p, key + ".ke.count"); j < m; j++)
                check(branch.getDsmh().khoiPhuc(DuLieuMatHang.doc(p, key + ".ke." + j)), "Dữ liệu kệ không hợp lệ");
            for (int j = 0, m = count(p, key + ".hd.count"); j < m; j++)
                check(branch.getDshd().them(docHoaDon(p, key + ".hd." + j, branch, kh, nhanVien)), "Trùng hóa đơn");
        }
        return new DuLieu(kho, kh, ncc, pn, cn);
    }

    private static void ghiHoaDon(Properties p, String key, HoaDon hd) {
        put(p, key + ".ma", hd.getMaHD()); put(p, key + ".ngay", hd.getNgayLap());
        put(p, key + ".daThanhToan", hd.isDaThanhToan()); ghiKhach(p, key + ".kh", hd.getKhachHang());
        put(p, key + ".nv.ma", hd.getMaNhanVienLap());
        put(p, key + ".nv.ten", hd.getTenNhanVienLap());
        put(p, key + ".count", hd.getSoLuongChiTiet());
        for (int i = 0; i < hd.getSoLuongChiTiet(); i++) {
            ChiTietHoaDon ct = hd.getChiTiet()[i]; String row = key + ".dong." + i;
            put(p, row + ".ma", ct.getMaMatHang()); put(p, row + ".ten", ct.getTenMatHang());
            put(p, row + ".soLuong", ct.getsoLuong()); put(p, row + ".donGia", ct.getDonGia());
        }
    }

    private static HoaDon docHoaDon(Properties p, String key, ChiNhanh cn, DanhSachKhachHang kh, DanhSachNhanVien nv) {
        KhachHang saved = docKhach(p, key + ".kh"), customer = kh.timkiem_khachhang(saved.getMaKH());
        if (customer == null || !customer.getTenKH().equals(saved.getTenKH())
                || !customer.getSoDienThoai().equals(saved.getSoDienThoai())) customer = saved;
        String maNv = get(p, key + ".nv.ma"); NhanVien employee = nv.timTheoMa(maNv);
        NhanVienBanHang seller = null;
        if (!maNv.isBlank()) {
            seller = employee instanceof NhanVienBanHang && cn.getMa().equalsIgnoreCase(employee.getMaChiNhanh())
                    && get(p, key + ".nv.ten").equals(employee.getHoTen())
                    ? (NhanVienBanHang) employee
                    : new NhanVienBanHang(maNv, get(p, key + ".nv.ten"), "", "", "", 0, cn.getMa());
        }
        String ma = get(p, key + ".ma"); check(!ma.isBlank(), "Thiếu mã hóa đơn");
        HoaDon hd = new HoaDon(ma, LocalDate.parse(get(p, key + ".ngay")), customer, seller);
        int n = count(p, key + ".count"); check(n > 0, "Hóa đơn rỗng");
        for (int i = 0; i < n; i++) {
            String row = key + ".dong." + i, code = get(p, row + ".ma");
            int qty = Integer.parseInt(get(p, row + ".soLuong")); double price = Double.parseDouble(get(p, row + ".donGia"));
            MatHang item = cn.getDsmh().timKiem_hang(code);
            if (item == null) {
                for (int j = 0; j < hd.getSoLuongChiTiet(); j++)
                    if (hd.getChiTiet()[j].getMathang().getMa().equalsIgnoreCase(code)) item = hd.getChiTiet()[j].getMathang();
                if (item == null) item = new MatHangLichSu(code, get(p, row + ".ten"), price);
            }
            check(hd.themChiTiet(new ChiTietHoaDon(qty, item, price, code, get(p, row + ".ten"))), "Chi tiết hóa đơn không hợp lệ");
        }
        String paid = get(p, key + ".daThanhToan");
        check(paid.equals("true") || paid.equals("false"), "Trạng thái hóa đơn không hợp lệ");
        if (paid.equals("true")) hd.danhDauDaThanhToan();
        return hd;
    }

    private static void ghiKhach(Properties p, String key, KhachHang kh) {
        put(p, key + ".ma", kh.getMaKH()); put(p, key + ".ten", kh.getTenKH()); put(p, key + ".sdt", kh.getSoDienThoai());
    }
    private static KhachHang docKhach(Properties p, String key) {
        String ma = get(p, key + ".ma"), ten = get(p, key + ".ten");
        check(!ma.isBlank() && !ten.isBlank(), "Khách hàng không hợp lệ");
        return new KhachHang(ma, ten, get(p, key + ".sdt"));
    }
    private static void ghiNcc(Properties p, String key, NhaCungCap ncc) {
        put(p, key + ".ma", ncc.getMaNCC()); put(p, key + ".ten", ncc.getTenNCC());
        put(p, key + ".diaChi", ncc.getDiaChi()); put(p, key + ".sdt", ncc.getSoDienThoai()); put(p, key + ".email", ncc.getEmail());
    }
    private static NhaCungCap docNcc(Properties p, String key) {
        return new NhaCungCap(get(p, key + ".ma"), get(p, key + ".ten"), get(p, key + ".diaChi"), get(p, key + ".sdt"), get(p, key + ".email"));
    }
    private static String get(Properties p, String key) {
        String value = p.getProperty(key); check(value != null, "Thiếu trường: " + key); return value;
    }
    private static int count(Properties p, String key) {
        int n = Integer.parseInt(get(p, key)); check(n >= 0 && n <= 100000, "Số dòng không hợp lệ: " + key); return n;
    }
    private static void put(Properties p, String key, Object value) { p.setProperty(key, value == null ? "" : value.toString()); }
    private static void check(boolean ok, String message) { if (!ok) throw new IllegalArgumentException(message); }
}
