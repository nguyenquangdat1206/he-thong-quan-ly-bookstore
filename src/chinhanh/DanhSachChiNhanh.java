package chinhanh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import khachhang.DanhSachKhachHang;
import kho.Kho;
import nhanvien.DanhSachNhanVien;
import nhanvien.NhanVien;

// Quan ly cac chi nhanh bang mang, khong dung ArrayList.
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

    public int getSoLuong() {
        return soLuong;
    }

    public ChiNhanh get(int viTri) {
        if (viTri < 0 || viTri >= soLuong) return null;
        return ds[viTri];
    }

    public ChiNhanh timTheoMa(String ma) {
        if (ma == null) return null;

        for (int i = 0; i < soLuong; i++) {
            if (ds[i].getMa().equalsIgnoreCase(ma.trim())) {
                return ds[i];
            }
        }
        return null;
    }

    public boolean them(String ma, String ten) {
        return them(ma, ten, "");
    }

    public boolean them(String ma, String ten, String diaChi) {
        if (ma == null || ma.trim().isEmpty()) return false;
        if (ten == null || ten.trim().isEmpty()) return false;
        if (diaChi == null) diaChi = "";
        if (ma.contains(";") || ten.contains(";") || diaChi.contains(";")) return false;
        if (timTheoMa(ma) != null) return false;

        // Neu mang day thi tang kich thuoc mang len gap doi.
        if (soLuong == ds.length) {
            ChiNhanh[] mangMoi = new ChiNhanh[ds.length * 2];
            for (int i = 0; i < ds.length; i++) {
                mangMoi[i] = ds[i];
            }
            ds = mangMoi;
        }

        ds[soLuong] = new ChiNhanh(ma.trim(), ten.trim(), diaChi.trim(), kho, dskh, dsnv);
        soLuong++;
        return true;
    }

    public boolean xoa(String ma) {
        ChiNhanh cn = timTheoMa(ma);
        if (cn == null) return false;
        if (cn.coDuLieu()) return false;
        if (dsnv.demTheoChiNhanh(cn.getMa()) > 0) return false;

        int viTri = 0;
        while (ds[viTri] != cn) {
            viTri++;
        }

        for (int i = viTri; i < soLuong - 1; i++) {
            ds[i] = ds[i + 1];
        }

        ds[soLuong - 1] = null;
        soLuong--;
        return true;
    }

    public void xuat() {
        System.out.println("=== DANH SACH CHI NHANH ===");

        if (soLuong == 0) {
            System.out.println("Chua co chi nhanh nao.");
            return;
        }

        for (int i = 0; i < soLuong; i++) {
            ChiNhanh cn = ds[i];
            System.out.println("Ma: " + cn.getMa()
                    + " | Ten: " + cn.getTen()
                    + " | Dia chi: " + cn.getDiaChi()
                    + " | Mat hang: " + cn.getDsmh().getSoMH()
                    + " | Hoa don: " + cn.getDshd().getSoLuong());
        }
    }

    public void xuatNhanVien(ChiNhanh cn) {
        if (cn == null) return;

        System.out.println("=== NHAN VIEN CHI NHANH " + cn.getMa() + " ===");
        int dem = 0;

        for (int i = 0; i < dsnv.getSoLuong(); i++) {
            NhanVien nv = dsnv.getDanhSach()[i];
            if (nv.thuocChiNhanh()
                    && nv.getMaChiNhanh().equalsIgnoreCase(cn.getMa())) {
                System.out.println(nv.getMaNguoi() + " | " + nv.getHoTen()
                        + " | " + nv.getQuyen() + " | Ca: " + nv.getCaLamViec());
                dem++;
            }
        }

        if (dem == 0) {
            System.out.println("Chi nhanh chua co nhan vien.");
        }
    }

    public String moTaChiNhanhCua(NhanVien nv) {
        if (!nv.thuocChiNhanh()) {
            return "Toan he thong (kho chung)";
        }

        ChiNhanh cn = timTheoMa(nv.getMaChiNhanh());
        if (cn == null) return "Chua gan chi nhanh hop le";

        return cn.getMa() + " - " + cn.getTen() + " - " + cn.getDiaChi();
    }

    public void xuatNhanVienTatCaChiNhanh() {
        for (int i = 0; i < soLuong; i++) {
            xuatNhanVien(ds[i]);
            System.out.println();
        }

        System.out.println("=== NHAN VIEN TOAN HE THONG ===");
        int dem = 0;
        for (int i = 0; i < dsnv.getSoLuong(); i++) {
            NhanVien nv = dsnv.getDanhSach()[i];
            if (!nv.thuocChiNhanh()) {
                System.out.println(nv.getMaNguoi() + " | " + nv.getHoTen()
                        + " | " + nv.getQuyen());
                dem++;
            }
        }
        if (dem == 0) System.out.println("Khong co nhan vien.");
    }

    public void ghiFile(String duongDan) {
        try {
            BufferedWriter bw = Files.newBufferedWriter(
                    Path.of(duongDan), StandardCharsets.UTF_8);

            for (int i = 0; i < soLuong; i++) {
                bw.write(ds[i].getMa() + ";" + ds[i].getTen() + ";" + ds[i].getDiaChi());
                bw.newLine();
            }
            bw.close();
            System.out.println("Da ghi danh sach chi nhanh.");
        } catch (IOException e) {
            System.out.println("Loi ghi file chi nhanh: " + e.getMessage());
        }
    }

    public void docFile(String duongDan) {
        try {
            BufferedReader br = Files.newBufferedReader(
                    Path.of(duongDan), StandardCharsets.UTF_8);

            String dong;
            while ((dong = br.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;

                String[] thongTin = dong.split(";", -1);
                if (thongTin.length == 2) {
                    them(thongTin[0], thongTin[1]);
                } else if (thongTin.length == 3) {
                    them(thongTin[0], thongTin[1], thongTin[2]);
                }
            }
            br.close();
            System.out.println("Da doc file chi nhanh.");
        } catch (IOException e) {
            System.out.println("Loi doc file chi nhanh: " + e.getMessage());
        }
    }
}
