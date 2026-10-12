package tienich;

import danhsach.DanhSachSanPham;
import danhsach.DanhSachKhachHang;
import thucthe.BaoTapChi;
import thucthe.Sach;
import thucthe.SanPham;
import thucthe.VanPhongPham;
import thucthe.KhachHang;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class QuanLyFile {

    public static void ghiFileSanPham(DanhSachSanPham ds, String duongDan) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan));
            SanPham[] mang = ds.getDanhSach();

            for (int i = 0; i < ds.getSoLuong(); i++) {
                SanPham sp = mang[i];
                if (sp instanceof Sach) {
                    Sach s = (Sach) sp;
                    // Format: Loại, Mã, Tên, Giá, Số lượng Kho, Số lượng Quầy, ...thuộc tính riêng
                    bw.write("SACH," + s.getMaSP() + "," + s.getTenSP() + "," + s.getDonGia() + ","
                            + s.getSoLuongKho() + "," + s.getSoLuongQuay() + ","
                            + s.getTacGia() + "," + s.getNhaXuatBan() + "," + s.getNamXuatBan());
                } else if (sp instanceof VanPhongPham) {
                    VanPhongPham vpp = (VanPhongPham) sp;
                    bw.write("VPP," + vpp.getMaSP() + "," + vpp.getTenSP() + "," + vpp.getDonGia() + ","
                            + vpp.getSoLuongKho() + "," + vpp.getSoLuongQuay() + ","
                            + vpp.getThuongHieu() + "," + vpp.getMauSac());
                } else if (sp instanceof BaoTapChi) {
                    BaoTapChi b = (BaoTapChi) sp;
                    bw.write("BAO," + b.getMaSP() + "," + b.getTenSP() + "," + b.getDonGia() + ","
                            + b.getSoLuongKho() + "," + b.getSoLuongQuay() + "," + b.getNgayPhatHanh());
                }
                bw.newLine();
            }
            bw.close();
        } catch (Exception e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    public static void docFileSanPham(DanhSachSanPham ds, String duongDan) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(duongDan));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals("SACH")) {
                    // Chú ý: data[4] là Kho, data[5] là Quầy
                    Sach s = new Sach(data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]), data[6], data[7], Integer.parseInt(data[8]));
                    ds.them(s);
                } else if (data[0].equals("VPP")) {
                    VanPhongPham vpp = new VanPhongPham(data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]), data[6], data[7]);
                    ds.them(vpp);
                } else if (data[0].equals("BAO")) {
                    BaoTapChi b = new BaoTapChi(data[1], data[2], Double.parseDouble(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]), data[6]);
                    ds.them(b);
                }
            }
            br.close();
        } catch (Exception e) {
        }
    }

    // 3. Hàm lưu dữ liệu Khách Hàng
    public static void ghiFileKhachHang(DanhSachKhachHang ds, String duongDan) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(duongDan));
            for (int i = 0; i < ds.getSoLuong(); i++) {
                KhachHang kh = ds.getDanhSach()[i];
                // Ghi theo format: Mã, Tên, SĐT, Mật khẩu, Điểm, Hạng
                bw.write(kh.getMaNguoi() + "," + kh.getHoTen() + "," + kh.getSoDienThoai() + ","
                        + kh.getMatKhau() + "," + kh.getDiemTichLuy() + "," + kh.getHangThanhVien());
                bw.newLine();
            }
            bw.close();
            System.out.println("Đã ghi dữ liệu Khách hàng xuống file!");
        } catch (Exception e) {
            System.out.println("Lỗi ghi file KH: " + e.getMessage());
        }
    }

    // 4. Hàm đọc dữ liệu Khách Hàng
    public static void docFileKhachHang(DanhSachKhachHang ds, String duongDan) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(duongDan));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                KhachHang kh = new KhachHang(data[0], data[1], data[2], data[3], Integer.parseInt(data[4]), data[5]);
                ds.them(kh);
            }
            br.close();
            System.out.println("Đã load dữ liệu Khách hàng từ file!");
        } catch (Exception e) {
            // Khởi tạo file mới nếu chưa có
        }
    }
}