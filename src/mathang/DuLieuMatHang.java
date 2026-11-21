package mathang;

import java.util.Properties;

/** Lưu từng thuộc tính để tên chứa dấu ';', dấu xuống dòng vẫn khôi phục được. */
public final class DuLieuMatHang {
    private DuLieuMatHang() { }

    /** Dòng TXT: danhMuc;ma;ten;giaNhap;soLuong;thuocTinhRieng... */
    public static MatHang docDong(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length < 6 || fields.length > 8) throw new IllegalArgumentException("Sai số cột mặt hàng");
        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
            if (fields[i].isEmpty()) throw new IllegalArgumentException("Thiếu trường mặt hàng");
        }
        Properties p = new Properties();
        String[] names = {"loai", "ma", "ten", "gia", "soLuong"};
        for (int i = 0; i < names.length; i++) p.setProperty("row." + names[i], fields[i]);
        p.setProperty("row.soThuocTinh", Integer.toString(fields.length - 5));
        for (int i = 5; i < fields.length; i++) p.setProperty("row.rieng." + (i - 5), fields[i]);
        return doc(p, "row");
    }

    public static void ghi(Properties duLieu, String khoa, MatHang mh) {
        String[] rieng;
        if (mh instanceof Sach) {
            Sach s = (Sach) mh;
            rieng = new String[] {s.getTacGia(), s.getNhaXuatBan(), s.getTheloai()};
        } else if (mh instanceof SachGiaoKhoa) {
            SachGiaoKhoa s = (SachGiaoKhoa) mh;
            rieng = new String[] {s.getMonhoc(), s.getNhaXuatBan()};
        } else if (mh instanceof BaoTapChi) {
            BaoTapChi s = (BaoTapChi) mh;
            rieng = new String[] {s.getTheloai(), s.getNhaXuatBan()};
        } else if (mh instanceof VanPhongPham) {
            VanPhongPham s = (VanPhongPham) mh;
            rieng = new String[] {s.getLoaiVPP(), s.getnhaSX()};
        } else if (mh instanceof DoChoi) {
            DoChoi s = (DoChoi) mh;
            rieng = new String[] {Integer.toString(s.getdotuoi()), s.getnhaSX()};
        } else if (mh instanceof DungCuHocTap) {
            DungCuHocTap s = (DungCuHocTap) mh;
            rieng = new String[] {s.getLoaidungcu(), s.getnhaSX()};
        } else if (mh instanceof Vo) {
            Vo s = (Vo) mh;
            rieng = new String[] {Integer.toString(s.getSotrang()), s.getnhaSX()};
        } else if (mh instanceof TrangTri) {
            rieng = new String[] {((TrangTri) mh).getnhaSX()};
        } else if (mh instanceof SPKhac) {
            rieng = new String[] {((SPKhac) mh).getnhaSX()};
        } else {
            throw new IllegalArgumentException("Loại mặt hàng không hỗ trợ lưu phiên");
        }
        duLieu.setProperty(khoa + ".loai", mh.getDanhMuc());
        duLieu.setProperty(khoa + ".ma", mh.getMa());
        duLieu.setProperty(khoa + ".ten", mh.getTen());
        duLieu.setProperty(khoa + ".gia", Double.toString(mh.getGiaNhap()));
        duLieu.setProperty(khoa + ".soLuong", Integer.toString(mh.getSoLuong()));
        duLieu.setProperty(khoa + ".soThuocTinh", Integer.toString(rieng.length));
        for (int i = 0; i < rieng.length; i++) duLieu.setProperty(khoa + ".rieng." + i, rieng[i]);
    }

    public static MatHang doc(Properties duLieu, String khoa) {
        String loai = lay(duLieu, khoa + ".loai"), ma = lay(duLieu, khoa + ".ma"), ten = lay(duLieu, khoa + ".ten");
        double gia = Double.parseDouble(lay(duLieu, khoa + ".gia"));
        int sl = Integer.parseInt(lay(duLieu, khoa + ".soLuong"));
        int soThuocTinh = Integer.parseInt(lay(duLieu, khoa + ".soThuocTinh"));
        if (ma.isBlank() || ten.isBlank() || !Double.isFinite(gia) || gia < 0 || sl < 0
                || soThuocTinh < 1 || soThuocTinh > 3)
            throw new IllegalArgumentException("Dữ liệu mặt hàng không hợp lệ: " + khoa);
        String[] r = new String[soThuocTinh];
        for (int i = 0; i < r.length; i++) r[i] = lay(duLieu, khoa + ".rieng." + i);
        MatHang mh;
        switch (loai) {
            case "Sách": dem(r, 3); mh = new Sach(ma, ten, gia, sl, r[0], r[1], r[2]); break;
            case "Tiểu thuyết": dem(r, 3); mh = new TieuThuyet(ma, ten, gia, sl, r[0], r[1], r[2]); break;
            case "Truyện": dem(r, 3); mh = new Truyen(ma, ten, gia, sl, r[0], r[1], r[2]); break;
            case "Sách giáo khoa": dem(r, 2); mh = new SachGiaoKhoa(ma, ten, gia, sl, r[0], r[1]); break;
            case "Báo tạp chí": dem(r, 2); mh = new BaoTapChi(ma, ten, gia, sl, r[1], r[0]); break;
            case "Văn phòng phẩm": dem(r, 2); mh = new VanPhongPham(ma, ten, gia, sl, r[1], r[0]); break;
            case "Đồ chơi": dem(r, 2); mh = new DoChoi(ma, ten, gia, sl, r[1], duong(r[0])); break;
            case "Dụng cụ học tập": dem(r, 2); mh = new DungCuHocTap(ma, ten, gia, sl, r[1], r[0]); break;
            case "Vở": dem(r, 2); mh = new Vo(ma, ten, gia, sl, duong(r[0]), r[1]); break;
            case "Trang trí": dem(r, 1); mh = new TrangTri(ma, ten, gia, sl, r[0]); break;
            case "Khác": dem(r, 1); mh = new SPKhac(ma, ten, gia, sl, r[0]); break;
            default: throw new IllegalArgumentException("Loại mặt hàng không hợp lệ");
        }
        mh.setMa(ma);
        return mh;
    }

    private static String lay(Properties duLieu, String khoa) {
        String value = duLieu.getProperty(khoa);
        if (value == null) throw new IllegalArgumentException("Thiếu trường: " + khoa);
        return value;
    }
    private static void dem(String[] values, int n) {
        if (values.length != n) throw new IllegalArgumentException("Sai số thuộc tính mặt hàng");
    }
    private static int duong(String value) {
        int n = Integer.parseInt(value);
        if (n <= 0) throw new IllegalArgumentException("Thuộc tính phải lớn hơn 0");
        return n;
    }
}
