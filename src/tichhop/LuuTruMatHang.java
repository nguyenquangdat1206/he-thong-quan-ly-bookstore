package tichhop;

import mathang.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Adapter TV5 cho file mathang.txt của TV2, không sửa thuật toán mặt hàng. */
public final class LuuTruMatHang {
    private LuuTruMatHang() { }
    public static int ghiFile(DanhSachMatHang ds, String tenFile) {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(tenFile), StandardCharsets.UTF_8)) {
            for (int i = 0; i < ds.getSoMH(); i++) {
                w.write(ds.getMangMatHang()[i].toString());
                w.newLine();
            }
            System.out.println("Đã lưu " + ds.getSoMH() + " mặt hàng vào " + tenFile);
            return ds.getSoMH();
        } catch (IOException ex) {
            System.out.println("Lỗi ghi file mặt hàng: " + ex.getMessage());
            return -1;
        }
    }
    public static int docFile(DanhSachMatHang ds, String tenFile) {
        int them = 0, boQua = 0;
        try (BufferedReader r = Files.newBufferedReader(Path.of(tenFile), StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank()) continue;
                try {
                    MatHang mh = parse(line);
                    if (mh != null && ds.themTuFile(mh)) them++;
                    else boQua++;
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) { boQua++; }
            }
            System.out.println("Đã tải " + them + " mặt hàng (bỏ qua " + boQua + " dòng lỗi/trùng mã).");
            return them;
        } catch (IOException ex) {
            System.out.println("Không đọc được file mặt hàng: " + ex.getMessage());
            return -1;
        }
    }
    private static MatHang parse(String line) {
        String[] a = line.split(";", -1);
        if (a.length < 6) return null;
        String ma = a[1].trim(), ten = a[2].trim();
        double gia = Double.parseDouble(a[3].trim());
        int sl = Integer.parseInt(a[4].trim());
        if (ma.isEmpty() || ten.isEmpty() || gia <= 0 || !Double.isFinite(gia) || sl < 0) return null;
        switch (a[0].trim()) {
            case "Sách": return a.length==8 ? new Sach(ma,ten,gia,sl,a[5],a[6],a[7]) : null;
            case "Sách giáo khoa": return a.length==7 ? new SachGiaoKhoa(ma,ten,gia,sl,a[5],a[6]) : null;
            case "Tiểu thuyết": return a.length==8 ? new TieuThuyet(ma,ten,gia,sl,a[5],a[6],a[7]) : null;
            case "Truyện": return a.length==8 ? new Truyen(ma,ten,gia,sl,a[5],a[6],a[7]) : null;
            // Định dạng do chính toString() của TV2 ghi: loại VPP trước, nhà SX sau.
            case "Văn phòng phẩm": return a.length==7 ? new VanPhongPham(ma,ten,gia,sl,a[6],a[5]) : null;
            // Định dạng TV2: thể loại báo trước, nhà xuất bản sau.
            case "Báo tạp chí": return a.length==7 ? new BaoTapChi(ma,ten,gia,sl,a[6],a[5]) : null;
            case "Khác": return a.length==6 ? new SPKhac(ma,ten,gia,sl,a[5]) : null;
            default: return null;
        }
    }
}
