package model.mathang;

import java.io.BufferedReader;
import java.io.FileReader;
import model.interfaces.IFileIO;
import model.interfaces.IQuanLy;
import model.utils.TienIch;

public class DanhSachMatHang implements IQuanLy, IFileIO {
    private MatHang[] ds;
    private int soLuong;

    public DanhSachMatHang() {
        ds = new MatHang[1000];
        soLuong = 0;
    }

    // ================= 1. CÁC HÀM CẦU NỐI CHO KHO VÀ HÓA ĐƠN =================

    public int getTongSoLuongTonToanHeThong() {
        int tong = 0;
        for (int i = 0; i < soLuong; i++) {
            tong += ds[i].getSoLuongTon(); 
        }
        return tong;
    }

    public boolean tangSoLuongTon(String ma, int slTang) {
        int viTri = TienIch.timKiemViTri(ds, soLuong, ma);
        if (viTri != -1) {
            int slHienTai = ds[viTri].getSoLuongTon();
            ds[viTri].setSoLuongTon(slHienTai + slTang);
            return true;
        }
        return false;
    }

    public boolean giamSoLuongTon(String ma, int slGiam) {
        int viTri = TienIch.timKiemViTri(ds, soLuong, ma);
        if (viTri != -1) {
            int slHienTai = ds[viTri].getSoLuongTon();
            if (slHienTai >= slGiam) {
                ds[viTri].setSoLuongTon(slHienTai - slGiam);
                return true;
            }
        }
        return false;
    }

    // ================= 2. CÁC HÀM ĐỌC / GHI FILE =================

    public void themMatHangVaoMang(MatHang mh) {
        if (mh == null) return;
        if (soLuong >= ds.length) {
            MatHang[] mangMoi = new MatHang[ds.length * 2];
            for (int i = 0; i < soLuong; i++) {
                mangMoi[i] = ds[i];
            }
            ds = mangMoi;
        }
        ds[soLuong] = mh;
        soLuong++;
    }

    @Override
    public void docFile(String duongDan) {
        try (BufferedReader br = new BufferedReader(new FileReader(duongDan))) {
            String dong;
            while ((dong = br.readLine()) != null) {
                if (dong.trim().isEmpty()) continue;
                String[] phan = dong.split(";");
                if (phan.length < 5) continue; 
                
                String danhmuc = phan[0].trim();
                String ma = phan[1].trim();
                String ten = phan[2].trim();
                double gianhap = Double.parseDouble(phan[3].trim());
                int sl;
                try {
                    sl = Integer.parseInt(phan[4].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                int vtMH = TienIch.timKiemViTri(ds, soLuong, ma);
                if (vtMH != -1) {
                    ds[vtMH].setSoLuongTon(ds[vtMH].getSoLuongTon() + sl); 
                    continue;
                }

                switch (danhmuc) {
                    case "Sách": themMatHangVaoMang(new Sach(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim(), phan[7].trim())); break;                       
                    case "Tiểu thuyết": themMatHangVaoMang(new TieuThuyet(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim(), phan[7].trim())); break;
                    case "Sách giáo khoa": themMatHangVaoMang(new SachGiaoKhoa(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim())); break;
                    case "Truyện": themMatHangVaoMang(new Truyen(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim(), phan[7].trim())); break;   
                    case "Trang trí": themMatHangVaoMang(new TrangTri(ma, ten, gianhap, sl, phan[5].trim())); break;
                    case "Vở": themMatHangVaoMang(new Vo(ma, ten, gianhap, sl, Integer.parseInt(phan[5].trim()), phan[6].trim())); break;
                    case "Đồ chơi": themMatHangVaoMang(new DoChoi(ma, ten, gianhap, sl, phan[6].trim(), Integer.parseInt(phan[5].trim()))); break;
                    case "Dụng cụ học tập": themMatHangVaoMang(new DungCuHocTap(ma, ten, gianhap, sl, phan[6].trim(), phan[5].trim())); break;
                    default: themMatHangVaoMang(new Khac(ma, ten, gianhap, sl, phan[5].trim())); break;
                }
            }
            System.out.println(">>> ĐÃ TẢI DỮ LIỆU HÀNG HÓA TỪ FILE THÀNH CÔNG!");
        } catch (Exception e) {
            System.out.println("LỖI đọc file hàng hóa: " + e.getMessage());
        }
    }

    @Override
    public void ghiFile(String duongDan) {
        System.out.println("Tính năng ghi file Mặt hàng đang hoàn thiện.");
    }

    // ================= 3. THỰC THI GIAO DIỆN IQuanLy (NẠP RUỘT THẬT) =================

    @Override
    public void nhap() {
        them(); // Gọi thẳng hàm thêm cho tiện
    }

    @Override
    public void xuat() {
        if (soLuong == 0) {
            System.out.println("Kệ hàng hiện đang trống!");
            return;
        }
        System.out.println("\n================= DANH SÁCH MẶT HÀNG ==================");
        for (int i = 0; i < soLuong; i++) {
            ds[i].xuat();
            System.out.println("-------------------------------------------------------");
        }
    }

    @Override
    public void them() {
        System.out.println("\n--- CHỌN LOẠI MẶT HÀNG CẦN THÊM ---");
        System.out.println("1. Sách | 2. Vở | 3. Đồ chơi | 4. Dụng cụ học tập | 5. Khác");
        int loai = TienIch.nhapSoTuKhoang("Bạn muốn thêm loại nào?", 1, 5);
        
        MatHang mh = null;
        switch (loai) {
            case 1: mh = new Sach(); break;       
            case 2: mh = new Vo(); break;
            case 3: mh = new DoChoi(); break;
            case 4: mh = new DungCuHocTap(); break;
            case 5: mh = new Khac(); break;
        }
        
        if (mh != null) {
            System.out.println("--- Mời nhập thông tin chi tiết ---");
            mh.nhap(); // Sẽ gọi hàm nhap() của class con tương ứng
            themMatHangVaoMang(mh);
            System.out.println(">>> ĐÃ THÊM MẶT HÀNG MỚI VÀO KỆ THÀNH CÔNG!");
        }
    }

    public void sua() {
        if (soLuong == 0) {
            System.out.println("Kệ hàng trống, không có gì để sửa!");
            return;
        }
        String maSua = TienIch.nhapChuoi("Nhập mã mặt hàng cần sửa: ");
        sua(maSua); // Trỏ xuống hàm sửa có tham số bên dưới
    }

    @Override
    public void sua(String ma) {
        int viTri = TienIch.timKiemViTri(ds, soLuong, ma);
        if (viTri == -1) {
            System.out.println("LỖI: Không tìm thấy mặt hàng nào có mã: " + ma);
        } else {
            System.out.println("--- THÔNG TIN HIỆN TẠI ---");
            ds[viTri].xuat();
            System.out.println("--- MỜI NHẬP THÔNG TIN MỚI ---");
            ds[viTri].nhap(); // Gọi hàm nhập để ghi đè dữ liệu mới
            System.out.println(">>> CẬP NHẬT MẶT HÀNG THÀNH CÔNG!");
        }
    }

    public void xoa() {
        if (soLuong == 0) {
            System.out.println("Kệ hàng trống, không có gì để xóa!");
            return;
        }
        String maXoa = TienIch.nhapChuoi("Nhập mã mặt hàng cần xóa: ");
        xoa(maXoa); // Trỏ xuống hàm xóa có tham số bên dưới
    }

    @Override
    public void xoa(String ma) {
        int viTri = TienIch.timKiemViTri(ds, soLuong, ma);
        if (viTri == -1) {
            System.out.println("LỖI: Không tìm thấy mặt hàng nào có mã: " + ma);
        } else {
            // Dồn mảng để xóa phần tử
            for (int i = viTri; i < soLuong - 1; i++) {
                ds[i] = ds[i + 1];
            }
            ds[soLuong - 1] = null; // Hủy ô thừa ở cuối
            soLuong--;
            System.out.println(">>> ĐÃ XÓA HOÀN TOÀN MẶT HÀNG CÓ MÃ: " + ma);
        }
    }
}