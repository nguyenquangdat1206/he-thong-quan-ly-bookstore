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

    // ================= CÁC HÀM CẦU NỐI CHO KHO VÀ HÓA ĐƠN =================

    // 1. Dành cho Kho: Lấy tổng toàn bộ số lượng hàng để kiểm tra sức chứa
    public int getTongSoLuongTonToanHeThong() {
        int tong = 0;
        for (int i = 0; i < soLuong; i++) {
            tong += ds[i].getSoLuongTon(); 
        }
        return tong;
    }

    // 2. Dành cho Kho: Bơm số lượng vào 1 mặt hàng cụ thể khi nhập phiếu
    public boolean tangSoLuongTon(String ma, int slTang) {
        int viTri = TienIch.timKiemViTri(ds, soLuong, ma);
        if (viTri != -1) {
            int slHienTai = ds[viTri].getSoLuongTon();
            ds[viTri].setSoLuongTon(slHienTai + slTang);
            return true;
        }
        return false;
    }

    // 3. Dành cho Hóa Đơn: Kiểm tra và trừ số lượng khi khách mua
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

    // ================= CÁC HÀM QUẢN LÝ CHÍNH =================

    // Hàm tiện ích nội bộ để dồn mảng khi thêm
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
                    System.out.println("Bỏ qua dòng lỗi số lượng: " + dong);
                    continue;
                }

                // Kiểm tra trùng mã: Trùng thì cộng dồn số lượng
                int vtMH = TienIch.timKiemViTri(ds, soLuong, ma);
                if (vtMH != -1) {
                    ds[vtMH].setSoLuongTon(ds[vtMH].getSoLuongTon() + sl); 
                    continue;
                }

                // Tạo đối tượng theo đúng danh mục và Constructor mới nhất
                switch (danhmuc) {
                    case "Sách":
                        Sach s = new Sach(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim(), phan[7].trim());
                        themMatHangVaoMang(s);
                        break;                       
                    case "Tiểu thuyết":
                        TieuThuyet tt = new TieuThuyet(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim(), phan[7].trim());
                        themMatHangVaoMang(tt);
                        break;
                    case "Sách giáo khoa":
                        SachGiaoKhoa sgk = new SachGiaoKhoa(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim());
                        themMatHangVaoMang(sgk);
                        break;
                    case "Truyện":
                        Truyen tr = new Truyen(ma, ten, gianhap, sl, phan[5].trim(), phan[6].trim(), phan[7].trim());
                        themMatHangVaoMang(tr);
                        break;   
                    case "Trang trí":
                        TrangTri trg = new TrangTri(ma, ten, gianhap, sl, phan[5].trim());
                        themMatHangVaoMang(trg);                    
                        break;
                    case "Vở":
                        Vo vo = new Vo(ma, ten, gianhap, sl, Integer.parseInt(phan[5].trim()), phan[6].trim());
                        themMatHangVaoMang(vo);
                        break;
                    case "Đồ chơi":
                        // File text lưu: [5] độ tuổi, [6] nhà SX. Constructor cần: nhaSX, dotuoi
                        DoChoi dc = new DoChoi(ma, ten, gianhap, sl, phan[6].trim(), Integer.parseInt(phan[5].trim()));
                        themMatHangVaoMang(dc);
                        break;
                    case "Dụng cụ học tập":
                        // File text lưu: [5] loại dụng cụ, [6] nhà SX. Constructor cần: nhaSX, loaidungcu
                        DungCuHocTap dcHT = new DungCuHocTap(ma, ten, gianhap, sl, phan[6].trim(), phan[5].trim());
                        themMatHangVaoMang(dcHT);
                        break;
                    default:
                        Khac kh = new Khac(ma, ten, gianhap, sl, phan[5].trim());
                        themMatHangVaoMang(kh);
                        break;
                }
            }
            System.out.println(">>> ĐÃ TẢI DỮ LIỆU HÀNG HÓA THÀNH CÔNG!");
        } catch (Exception e) {
            System.out.println("LỖI đọc file hàng hóa: " + e.getMessage());
        }
    }

    public void ghiFile(String duongDan) {
        System.out.println("Tính năng ghi file Mặt hàng đang hoàn thiện.");
    }

    public void nhap() {
        System.out.println("Tính năng nhập Mặt hàng thủ công đang hoàn thiện.");
    }

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

    public void them() {
        // Đã gộp vào hàm nhap() và themMatHangVaoMang()
    }

    public void sua() {
        System.out.println("Tính năng sửa Mặt hàng đang cập nhật.");
    }

    public void xoa() {
        System.out.println("Tính năng xóa Mặt hàng đang cập nhật.");
    }

    @Override
    public void sua(String ma) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sua'");
    }

    @Override
    public void xoa(String ma) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'xoa'");
    }
}