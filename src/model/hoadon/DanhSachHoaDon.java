package model.hoadon;

import model.interfaces.IQuanLy;
import model.interfaces.IFileIO;
import model.khachhang.DanhSachKhachHang;
import model.khachhang.KhachHang;
import model.mathang.DanhSachMatHang;
import model.mathang.MatHang;
import model.utils.TienIch;
import java.io.*;

public class DanhSachHoaDon implements IQuanLy, IFileIO {
    private HoaDon[] ds;
    private int soLuong;

    public DanhSachHoaDon() {
        ds = new HoaDon[1000];
        soLuong = 0;
    }

    @Override
    public void xuat() {
        if (soLuong == 0) {
            System.out.println("Danh sách hóa đơn trống.");
            return;
        }
        System.out.println("\n================ LỊCH SỬ HÓA ĐƠN ================");
        for (int i = 0; i < soLuong; i++) {
            ds[i].xuat();
            System.out.println("-------------------------------------------------");
        }
    }

    // Truyền dsHangHoa vào để kiểm tra lúc mua
    public void them(DanhSachMatHang dsHangHoa) {
        System.out.println("\n--- BẮT ĐẦU LẬP HÓA ĐƠN ---");
        String maHD;
        boolean biTrung;
        do {
            biTrung = false;
            maHD = TienIch.nhapChuoi("Nhập mã hóa đơn mới (VD: HD01): ");
            // Quét qua danh sách hóa đơn hiện có để check trùng
            for (int i = 0; i < soLuong; i++) {
                if (ds[i].getMaHD().equalsIgnoreCase(maHD)) {
                    System.out.println("[LỖI] Mã hóa đơn '" + maHD + "' đã tồn tại trong hệ thống!");
                    System.out.println("Vui lòng nhập một mã khác.");
                    biTrung = true;
                    break;
                }
            }
        } while (biTrung);
        // Tạm tạo Khách vãng lai để test nhanh
        KhachHang khachLe = new KhachHang("KH00", "Khách Vãng Lai", "0000000000");
        
        HoaDon hd = new HoaDon(maHD, java.time.LocalDate.now(), khachLe);
        
        int soMon = TienIch.nhapSoNguyenDuong("Khách muốn mua bao nhiêu loại mặt hàng? ");
        if (soMon == 0) return;

        for (int i = 0; i < soMon; i++) {
            System.out.println("\n> Nhập món thứ " + (i + 1) + ":");
            String maHang = TienIch.nhapChuoi("- Nhập MÃ mặt hàng cần mua: ");
            
            // Tìm món hàng trên Kệ
            MatHang mh = dsHangHoa.timKiemMatHang(maHang);
            if (mh == null) {
                System.out.println("[LỖI] Kệ hàng không có mã sản phẩm này!");
                continue;
            }

            int slMua = TienIch.nhapSoNguyenDuong("- Nhập số lượng mua: ");
            
            // KIỂM TRA XEM CÓ ĐỦ HÀNG ĐỂ BÁN KHÔNG
            if (mh.getSoLuongTon() < slMua) {
                System.out.println("[CẢNH BÁO] Cửa hàng không đủ hàng! Trên kệ chỉ còn: " + mh.getSoLuongTon() + " sản phẩm.");
                System.out.println("=> Đã hủy món hàng này khỏi giỏ hàng.");
                continue;
            }

            // Thêm vào giỏ hàng
            ChiTietHoaDon ct = new ChiTietHoaDon(slMua, mh);
            hd.themChiTiet(ct);
            System.out.println(">>> Đã thêm " + slMua + " " + mh.getTen() + " vào Hóa Đơn.");
        }

        if (hd.getSoLuongChiTiet() == 0) {
            System.out.println("\n>>> Hóa đơn rỗng, tự động hủy!");
            return;
        }

        // BẮT ĐẦU THANH TOÁN
        System.out.println("\n=== THANH TOÁN HÓA ĐƠN " + maHD + " ===");
        System.out.println("Tổng tiền cần thanh toán: " + hd.tinhThanhTien() + " đ");
        
        double tienKhach = TienIch.nhapGiaTien("Nhập số tiền khách đưa (Nhập 0 để hủy thanh toán): ");
        if (tienKhach > 0) {
            ThanhToanTienMat tm = new ThanhToanTienMat(tienKhach);
            hd.setHinhThucThanhToan(tm);
            
            // Nếu thanh toán thành công, lưu hóa đơn vào lịch sử
            if (hd.thanhToan(dsHangHoa)) {
                if (soLuong >= ds.length) {
                    HoaDon[] temp = new HoaDon[ds.length * 2];
                    for(int i = 0; i < soLuong; i++) temp[i] = ds[i];
                    ds = temp;
                }
                ds[soLuong++] = hd;
            }
        } else {
            System.out.println("Khách hàng hủy mua. Hóa đơn đã bị xóa.");
        }
    }

    // ================= CÁC HÀM ĐỌC GHI FILE =================
    @Override
    public void ghiFile(String duongDan) {
        System.out.println("Tính năng ghi file Hóa đơn đang cập nhật.");
    }

    @Override
    public void docFile(String duongDan) {
        System.out.println("Hóa đơn cần Kệ hàng và Khách hàng để đọc file. Vui lòng dùng hàm docFile nạp chồng!");
    }

    // HÀM ĐỌC FILE CHÍNH ĐỂ MÓC NỐI DỮ LIỆU
    public void docFile(String duongDan, DanhSachKhachHang dskh, DanhSachMatHang dsmh) {
        try (BufferedReader br = new BufferedReader(new FileReader(duongDan))) {
            String line;
            soLuong = 0;
            ds = new HoaDon[1000];

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Dòng 1: Mã HĐ ; Ngày ; Mã KH
                String[] info = line.split(";");
                String maHD = info[0].trim();
                java.time.LocalDate ngayLap = java.time.LocalDate.parse(info[1].trim());
                KhachHang kh = dskh.timkiem_khachhang(info[2].trim());

                if (kh == null) {
                    System.out.println("Bỏ qua hóa đơn " + maHD + " do không tìm thấy khách hàng.");
                    continue;
                }

                HoaDon hd = new HoaDon(maHD, ngayLap, kh); 
                hd.setHinhThucThanhToan(new ThanhToanTienMat(0)); // Giả lập đã thanh toán khi load từ lịch sử

                // Dòng 2: Số lượng chi tiết
                int soLuongCT = Integer.parseInt(br.readLine().trim());

                // Dòng 3...: Mã MH ; SL ; Đơn giá
                for (int j = 0; j < soLuongCT; j++) {
                    String[] ctData = br.readLine().split(";");
                    MatHang mh = dsmh.timKiemMatHang(ctData[0].trim());
                    int slMua = Integer.parseInt(ctData[1].trim());

                    if (mh != null) {
                        ChiTietHoaDon ct = new ChiTietHoaDon(slMua, mh);
                        hd.themChiTiet(ct);
                        // Lưu ý: Đọc từ lịch sử thì không trừ số lượng trên kệ nữa
                    }
                }

                if (hd.getSoLuongChiTiet() == 0) continue;

                if (soLuong >= ds.length) {
                    HoaDon[] temp = new HoaDon[ds.length * 2];
                    for(int i=0; i<soLuong; i++) temp[i] = ds[i];
                    ds = temp;
                }
                ds[soLuong++] = hd;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Chưa tìm thấy file " + duongDan);
        } catch (Exception e) {
            System.out.println("Lỗi đọc file hóa đơn: " + e.getMessage());
        }
    }

    // Các hàm IQuanLy mặc định (không dùng tới vì đã dùng overloading)
    public void nhap() {}
    public void sua() {}
    public void sua(String ma) {}
    public void xoa() {}
    public void xoa(String ma) {}

    @Override
    public void them() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'them'");
    }
}
