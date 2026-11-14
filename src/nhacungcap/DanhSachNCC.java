package nhacungcap;

import interfaces.IQuanLy;
import interfaces.IFileIO;
import tienich.TienIch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class DanhSachNCC implements IQuanLy, IFileIO {
    private NhaCungCap[] dsNCC;
    private int soLuongNCC;

    public DanhSachNCC() {
        dsNCC = new NhaCungCap[100];
        soLuongNCC = 0;
    }

    /** Dùng chung một danh sách NCC với module phiếu nhập. Giữ nguyên mảng tĩnh, tự tăng sức chứa. */
    public NhaCungCap timHoacTaoTheoTen(String tenNCC) {
        if (tenNCC == null || tenNCC.trim().isEmpty())
            throw new IllegalArgumentException("Tên nhà cung cấp không được trống");
        String ten = tenNCC.trim();
        for (int i = 0; i < soLuongNCC; i++) {
            if (dsNCC[i].getTenNCC() != null && dsNCC[i].getTenNCC().trim().equalsIgnoreCase(ten))
                return dsNCC[i];
        }
        if (soLuongNCC == dsNCC.length) {
            NhaCungCap[] moi = new NhaCungCap[dsNCC.length * 2];
            System.arraycopy(dsNCC, 0, moi, 0, soLuongNCC);
            dsNCC = moi;
        }
        // Mã không được lặp lại sau khi xóa NCC hoặc trùng mã được nhập thủ công.
        int soMa = soLuongNCC;
        while (timKiemViTri("NCC" + soMa) != -1) soMa++;
        NhaCungCap ncc = new NhaCungCap("NCC" + soMa, ten, "", "", "");
        dsNCC[soLuongNCC++] = ncc;
        return ncc;
    }

    @Override
    public void nhap() {
        int n = TienIch.nhapSoNguyenDuong("Nhập số lượng nhà cung cấp: ");

        for (int i = 0; i < n; i++) {
            them();
        }
    }

    @Override
    public void xuat() {
        if (soLuongNCC == 0) {
            System.out.println("Danh sách nhà cung cấp trống!");
            return;
        }

        System.out.println("\n===== DANH SÁCH NHÀ CUNG CẤP =====");
        System.out.printf("%-10s %-25s %-25s %-15s %-25s\n",
                "Mã NCC", "Tên NCC", "Địa chỉ", "SĐT", "Email");

        for (int i = 0; i < soLuongNCC; i++) {
            NhaCungCap ncc = dsNCC[i];

            System.out.printf("%-10s %-25s %-25s %-15s %-25s\n",
                    ncc.getMaNCC(),
                    ncc.getTenNCC(),
                    ncc.getDiaChi(),
                    ncc.getSoDienThoai(),
                    ncc.getEmail());
        }
    }

    @Override
    public void them() {
        if (soLuongNCC == dsNCC.length) {
            NhaCungCap[] mangMoi = new NhaCungCap[dsNCC.length * 2];

            for (int i = 0; i < dsNCC.length; i++) {
                mangMoi[i] = dsNCC[i];
            }

            dsNCC = mangMoi;
        }

        String maNCC;

        do {
            maNCC = TienIch.nhapChuoi("Nhập mã NCC: ");
        } while (timKiemViTri(maNCC) != -1);

        String tenNCC = TienIch.nhapChuoi("Nhập tên NCC: ");
        String diaChi = TienIch.nhapChuoi("Nhập địa chỉ: ");
        String sdt = TienIch.nhapChuoi("Nhập số điện thoại: ");
        String email = TienIch.nhapChuoi("Nhập email: ");

        dsNCC[soLuongNCC++] =
                new NhaCungCap(maNCC, tenNCC, diaChi, sdt, email);

        System.out.println("Thêm nhà cung cấp thành công!");
    }

        public void them(String tenNCC) {
        if (soLuongNCC == dsNCC.length) {
            NhaCungCap[] mangMoi = new NhaCungCap[dsNCC.length * 2];

            for (int i = 0; i < dsNCC.length; i++) {
                mangMoi[i] = dsNCC[i];
            }

            dsNCC = mangMoi;
        }

        String maNCC;

        do {
            maNCC = TienIch.nhapChuoi("Nhập mã NCC: ");
        } while (timKiemViTri(maNCC) != -1);
        String diaChi = TienIch.nhapChuoi("Nhập địa chỉ: ");
        String sdt = TienIch.nhapChuoi("Nhập số điện thoại: ");
        String email = TienIch.nhapChuoi("Nhập email: ");

        dsNCC[soLuongNCC++] =
                new NhaCungCap(maNCC, tenNCC, diaChi, sdt, email);

        System.out.println("Thêm nhà cung cấp thành công!");
    }

    @Override
    public void sua() {
        if (soLuongNCC == 0) {
            System.out.println("Danh sách nhà cung cấp trống!");
            return;
        }

        String maCanSua =
                TienIch.nhapChuoi("Nhập mã nhà cung cấp cần sửa: ");

        int vt = timKiemViTri(maCanSua);

        if (vt == -1) {
            System.out.println("Không tìm thấy nhà cung cấp!");
            return;
        }

        dsNCC[vt].setTenNCC(
                TienIch.nhapChuoi("Nhập tên mới: ")
        );

        dsNCC[vt].setDiaChi(
                TienIch.nhapChuoi("Nhập địa chỉ mới: ")
        );

        dsNCC[vt].setSoDienThoai(
                TienIch.nhapChuoi("Nhập SĐT mới: ")
        );

        dsNCC[vt].setEmail(
                TienIch.nhapChuoi("Nhập email mới: ")
        );

        System.out.println("Sửa thành công!");
    }

    @Override
    public void xoa() {
        if (soLuongNCC == 0) {
            System.out.println("Danh sách nhà cung cấp trống!");
            return;
        }

        String maCanXoa =
                TienIch.nhapChuoi("Nhập mã NCC cần xóa: ");

        int vt = timKiemViTri(maCanXoa);

        if (vt == -1) {
            System.out.println("Không tìm thấy nhà cung cấp!");
            return;
        }

        for (int i = vt; i < soLuongNCC - 1; i++) {
            dsNCC[i] = dsNCC[i + 1];
        }

        dsNCC[soLuongNCC - 1] = null;
        soLuongNCC--;

        System.out.println("Xóa thành công!");
    }

    @Override
    public void ghiFile(String duongDan) {
        try (PrintWriter pw =
                     new PrintWriter(java.nio.file.Files.newBufferedWriter(
                             java.nio.file.Path.of(duongDan), java.nio.charset.StandardCharsets.UTF_8))) {

            for (int i = 0; i < soLuongNCC; i++) {
                pw.println(dsNCC[i].toString());
            }

            System.out.println("Ghi file thành công!");

        } catch (IOException e) {
            System.out.println("Lỗi ghi file: " + e);
        }
    }

    @Override
    public void docFile(String duongDan) {
        int them = 0, boQua = 0, dongSo = 0;
        try (BufferedReader br = java.nio.file.Files.newBufferedReader(
                java.nio.file.Path.of(duongDan), java.nio.charset.StandardCharsets.UTF_8)) {
            String dong;
            while ((dong = br.readLine()) != null) {
                dongSo++;
                if (dong.isBlank()) continue;
                String[] phan = dong.replace("\uFEFF", "").split(";", -1);
                if (phan.length != 5 || phan[0].trim().isEmpty() || phan[1].trim().isEmpty()) {
                    System.out.println("Bỏ qua NCC dòng " + dongSo + ": sai cấu trúc/mã/tên"); boQua++; continue;
                }
                String ma = phan[0].trim();
                if (timKiemViTri(ma) != -1) { boQua++; continue; }
                if (soLuongNCC == dsNCC.length) {
                    NhaCungCap[] moi = new NhaCungCap[dsNCC.length * 2];
                    System.arraycopy(dsNCC, 0, moi, 0, soLuongNCC);
                    dsNCC = moi;
                }
                dsNCC[soLuongNCC++] = new NhaCungCap(ma, phan[1].trim(),
                    phan[2].trim(), phan[3].trim(), phan[4].trim());
                them++;
            }
            System.out.println("Đọc NCC: thêm " + them + ", bỏ qua " + boQua + " dòng.");
        } catch (IOException e) {
            System.out.println("Lỗi đọc file NCC: " + e.getMessage());
        }
    }

    public int timKiemViTri(String maNCC) {
        for (int i = 0; i < soLuongNCC; i++) {
            if (dsNCC[i].getMaNCC().equalsIgnoreCase(maNCC)) {
                return i;
            }
        }
        return -1;
    }

    public NhaCungCap timNCC(String maNCC) {
        int vt = timKiemViTri(maNCC);

        if (vt == -1) {
            return null;
        }

        return dsNCC[vt];
    }

    public NhaCungCap[] getDsNCC() {
        return dsNCC;
    }

    public int getSoLuongNCC() {
        return soLuongNCC;
    }
}