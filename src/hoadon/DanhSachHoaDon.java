package hoadon;
import java.io.*;
import java.time.LocalDate;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;

import mathang.*;
import interfaces.*;
import tienich.*;
import nhanvien.NhanVienBanHang;

import nhanvien.DanhSachNhanVien;
import nhanvien.NhanVien;
import khachhang.DanhSachKhachHang;
import khachhang.KhachHang;
public class DanhSachHoaDon implements IQuanLy, IFileIO{
    //Thuộc tính
    private HoaDon[] ds;
    private int soLuong;
    private DanhSachMatHang dsmh;
    private DanhSachKhachHang dskh;
    private DanhSachNhanVien dsnv;
    //constructor
        public DanhSachHoaDon(DanhSachMatHang dsmh, DanhSachKhachHang dskh){
        this(dsmh, dskh, null);
    }
    public DanhSachHoaDon(DanhSachMatHang dsmh, DanhSachKhachHang dskh, DanhSachNhanVien dsnv){
        ds = new HoaDon[1000];
        soLuong = 0;
        this.dsmh = dsmh;
        this.dskh = dskh;
        this.dsnv = dsnv;
    }
    //tạo thêm danh sách nếu bị đầy
    private void tangKichThuocHoaDon(){
        HoaDon[] temp = new HoaDon[ds.length + 100];
        for(int i=0; i<ds.length; i++){
            temp[i] = ds[i];
        }
        ds = temp;
    }
    //nhap
   @Override
    public void nhap() {
        System.out.println("Hóa đơn không nhập trực tiếp từ danh sách. "
                + "Hãy dùng menu Giỏ hàng để lập hóa đơn.");
    }

    //xuat
    @Override
    public void xuat() {
        if (soLuong == 0) {
            System.out.println("Chưa có hóa đơn nào.");
            return;
        }
        for (int i = 0; i < soLuong; i++) {
            ds[i].xuat();
            System.out.println("------------------------");
        }
    }

    //them
    @Override
    public void them() {
        System.out.println("Hóa đơn cần khách hàng, nhân viên và mặt hàng, "
                + "hãy dùng menu Giỏ hàng để lập hóa đơn.");
    }

    public int timViTri(String maHD){
        for(int i=0; i<soLuong; i++){
            if(ds[i].getMaHD().equalsIgnoreCase(maHD))
                return i;
        }
        return -1;
    }

    public HoaDon timKiem(String maHD){
        int vt = timViTri(maHD);
        return vt == -1 ? null : ds[vt];
    }
    //them (hàm riêng)
    public boolean them(HoaDon hd) {
        if(hd == null || timKiem(hd.getMaHD()) != null/* ktra có tồn tại ma HD hoặc có bị trùng maHD không*/) return false;
        if(soLuong == ds.length) tangKichThuocHoaDon();
        ds[soLuong++] = hd;
        return true;
    }
    //sua
    @Override
    public void sua() {
        String maHD = TienIch.nhapChuoi("Nhập mã hóa đơn cần sửa: ");
        HoaDon hd = timKiem(maHD);
        if (hd == null) {
            System.out.println("Không tồn tại hóa đơn " + maHD);
            return;
        }
        if (hd.isDaThanhToan()) {
            System.out.println("Hóa đơn đã thanh toán, không thể sửa.");
            return;
        }
        int chon;
        do {
            System.out.println("\n--- Sửa hóa đơn " + maHD + " ---");
            hd.inChiTiet();
            System.out.println("1. Sửa số lượng một dòng");
            System.out.println("2. Xóa một dòng");
            System.out.println("0. Thoát");
            chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");
            if (chon != 1 && chon != 2) continue;
            int stt = TienIch.nhapSoNguyenDuong("Dòng: ");
            try {
                if (chon == 1) {
                    hd.suaSoLuongChiTiet(stt - 1, TienIch.nhapSoNguyenDuong("Số lượng: "));
                }
                else {
                    hd.xoaChiTiet(stt - 1);
                }
                System.out.println("Đã cập nhật. Tổng tiền mới: " + hd.tinhThanhTien());
            }
            catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Lỗi: " + e.getMessage());

            }
        }
        while (chon != 0) ;
    }
    //xoa
    @Override 
    public void xoa (){
        String maHD = TienIch.nhapChuoi("Nhập mã hóa đơn cần xóa: ");
        int vt = timViTri(maHD);
        if (vt == -1) {
            System.out.println("Không tìm thấy hóa đơn: " + maHD);
            return;
        }
        if (ds[vt].isDaThanhToan()) {
            System.out.println("Hóa đơn đã thanh toán, không thể xóa!");
            return;
        }
        for (int i = vt; i < soLuong -1; i++) {
            ds[i] = ds[i + 1];
        }
        ds[soLuong - 1] = null;
        soLuong--;
        System.out.println("Đã xóa hóa đơn " + maHD);
    }
    //get & set
    public HoaDon[] getDs(){return ds;}
    public int getSoLuong(){return soLuong;}

    //Thống kê Top khách hàng chi tiêu cao nhất
    public void thongKeTopKhachHang() {
        int soKH = dskh.getSoLuong();
        if (soKH == 0 || soLuong == 0) {
            System.out.println("Chưa có đủ dữ liệu khách hàng hoặc hóa đơn để thống kê.");
            return;
        }
        KhachHang[] listKH = new KhachHang[soKH];
        double[] chiTieu = new double[soKH];
        int[] soDonMua = new int[soKH];

        for (int i = 0; i < soKH; i++) {
            listKH[i] = dskh.getDs()[i];
            String ma = listKH[i].getMaKH();
            for (int j = 0; j < soLuong; j++) {
                if (ds[j].isDaThanhToan() && ds[j].getKhachHang().getMaKH().equalsIgnoreCase(ma)) {
                    chiTieu[i] += ds[j].tinhThanhTien();
                    soDonMua[i]++;
                }
            }
        }

        for (int i = 0; i < soKH - 1; i++) {
            for (int j = i + 1; j < soKH; j++) {
                if (chiTieu[j] > chiTieu[i]) {
                    double tmpTien = chiTieu[i]; chiTieu[i] = chiTieu[j]; chiTieu[j] = tmpTien;
                    int tmpDon = soDonMua[i]; soDonMua[i] = soDonMua[j]; soDonMua[j] = tmpDon;
                    KhachHang tmpKH = listKH[i]; listKH[i] = listKH[j]; listKH[j] = tmpKH;
                }
            }
        }

        System.out.println("\n=== BẢNG XẾP HẠNG KHÁCH HÀNG CHI TIÊU CAO NHẤT ===");
        int dem = 0;
        for (int i = 0; i < soKH && dem < 5; i++) {
            if (chiTieu[i] <= 0) break;
            dem++;
            System.out.printf("Top %d: %-20s (%s) | Số đơn đã mua: %d | Tổng chi: %.0f%n",
                    dem, listKH[i].getTenKH(), listKH[i].getMaKH(), soDonMua[i], chiTieu[i]);
        }
        if (dem == 0) {
            System.out.println("Chưa có khách hàng nào có hóa đơn đã thanh toán.");
        }
    }

    // Tra cứu và lọc hóa đơn nâng cao
    public void traCuuVaLoc() {
        if (soLuong == 0) {
            System.out.println("Danh sách hóa đơn đang trống.");
            return;
        }
        System.out.println("\n--- TRA CỨU & LỌC HÓA ĐƠN ---");
        System.out.println("1. Tìm theo mã hóa đơn");
        System.out.println("2. Lọc hóa đơn ĐÃ thanh toán");
        System.out.println("3. Lọc hóa đơn CHƯA thanh toán");
        System.out.println("4. Lọc theo mã nhân viên lập");
        System.out.println("5. Lọc theo khoảng ngày lập");
        int chon = TienIch.nhapSoNguyenKhongAm("Chọn: ");

        switch (chon) {
            case 1: {
                String ma = TienIch.nhapChuoi("Nhập mã hóa đơn cần tìm: ");
                HoaDon hd = timKiem(ma);
                if (hd == null) System.out.println("Không tìm thấy hóa đơn " + ma);
                else hd.xuat();
                break;
            }
            case 2:
            case 3: {
                boolean canDaTT = (chon == 2);
                int dem = 0;
                for (int i = 0; i < soLuong; i++) {
                    if (ds[i].isDaThanhToan() == canDaTT) {
                        ds[i].xuat();
                        System.out.println("------------------------");
                        dem++;
                    }
                }
                if (dem == 0) System.out.println("Không có hóa đơn nào phù hợp.");
                else System.out.println("=> Tìm thấy " + dem + " hóa đơn.");
                break;
            }
            case 4: {
                String maNV = TienIch.nhapChuoi("Nhập mã nhân viên lập: ");
                int dem = 0;
                for (int i = 0; i < soLuong; i++) {
                    NhanVienBanHang nv = ds[i].getNhanVienLap();
                    if (nv != null && nv.getMaNguoi().equalsIgnoreCase(maNV)) {
                        ds[i].xuat();
                        System.out.println("------------------------");
                        dem++;
                    }
                }
                if (dem == 0) System.out.println("Không có hóa đơn nào do nhân viên '" + maNV + "' lập.");
                else System.out.println("=> Tìm thấy " + dem + " hóa đơn.");
                break;
            }
            case 5: {
                LocalDate tuNgay = TienIch.nhapNgay("Từ ngày: ");
                LocalDate denNgay = TienIch.nhapNgay("Đến ngày: ");
                if (denNgay.isBefore(tuNgay)) {
                    System.out.println("Khoảng ngày không hợp lệ (ngày kết thúc trước ngày bắt đầu).");
                    return;
                }
                int dem = 0;
                for (int i = 0; i < soLuong; i++) {
                    LocalDate ngay = ds[i].getNgayLap();
                    if (!ngay.isBefore(tuNgay) && !ngay.isAfter(denNgay)) {
                        ds[i].xuat();
                        System.out.println("------------------------");
                        dem++;
                    }
                }
                if (dem == 0) System.out.println("Không có hóa đơn nào trong khoảng ngày này.");
                else System.out.println("=> Tìm thấy " + dem + " hóa đơn.");
                break;
            }
            default:
                System.out.println("Lựa chọn không hợp lệ!");
        }
    }

    //Thống kê doanh thu và Top mặt hàng bán chạy
    public void thongKeDoanhThu() {
        if (soLuong == 0) {
            System.out.println("Chưa có dữ liệu hóa đơn để thống kê.");
            return;
        }
        int soDonDaTT = 0, soDonChuaTT = 0;
        double doanhThuThucTe = 0, tienChoThanhToan = 0;

        String[] maMH = new String[1000];
        String[] tenMH = new String[1000];
        int[] slBan = new int[1000];
        double[] thuVe = new double[1000];
        int soLoaiMH = 0;

        for (int i = 0; i < soLuong; i++) {
            HoaDon hd = ds[i];
            double tien = hd.tinhThanhTien();
            if (hd.isDaThanhToan()) {
                soDonDaTT++;
                doanhThuThucTe += tien;
                for (int j = 0; j < hd.getSoLuongChiTiet(); j++) {
                    ChiTietHoaDon ct = hd.getChiTiet()[j];
                    String ma = ct.getMathang().getMa();
                    int vt = -1;
                    for (int k = 0; k < soLoaiMH; k++) {
                        if (maMH[k].equalsIgnoreCase(ma)) { vt = k; break; }
                    }
                    if (vt == -1) {
                        maMH[soLoaiMH] = ma;
                        tenMH[soLoaiMH] = ct.getMathang().getTen();
                        slBan[soLoaiMH] = ct.getsoLuong();
                        thuVe[soLoaiMH] = ct.thanhTien();
                        soLoaiMH++;
                    } else {
                        slBan[vt] += ct.getsoLuong();
                        thuVe[vt] += ct.thanhTien();
                    }
                }
            } else {
                soDonChuaTT++;
                tienChoThanhToan += tien;
            }
        }

        System.out.println("\n=== THỐNG KÊ DOANH THU HÓA ĐƠN ===");
        System.out.println("Tổng số hóa đơn:           " + soLuong);
        System.out.println("- Đã thanh toán:           " + soDonDaTT + " đơn");
        System.out.println("- Chưa thanh toán:         " + soDonChuaTT + " đơn");
        System.out.printf("Doanh thu thực thu:        %.0f%n", doanhThuThucTe);
        System.out.printf("Tổng tiền chờ thanh toán:  %.0f%n", tienChoThanhToan);

        if (soLoaiMH > 0) {
            for (int i = 0; i < soLoaiMH - 1; i++) {
                for (int j = i + 1; j < soLoaiMH; j++) {
                    if (slBan[j] > slBan[i]) {
                        int tmpSL = slBan[i]; slBan[i] = slBan[j]; slBan[j] = tmpSL;
                        double tmpThu = thuVe[i]; thuVe[i] = thuVe[j]; thuVe[j] = tmpThu;
                        String tmpMa = maMH[i]; maMH[i] = maMH[j]; maMH[j] = tmpMa;
                        String tmpTen = tenMH[i]; tenMH[i] = tenMH[j]; tenMH[j] = tmpTen;
                    }
                }
            }
            System.out.println("\n--- TOP MẶT HÀNG BÁN CHẠY NHẤT (ĐÃ THANH TOÁN) ---");
            int limit = Math.min(5, soLoaiMH);
            for (int i = 0; i < limit; i++) {
                System.out.printf("%d. %-25s (%s) | Đã bán: %d | Doanh thu: %.0f%n",
                        i + 1, tenMH[i], maMH[i], slBan[i], thuVe[i]);
            }
        }
    }
    
    // Ghi dữ liệu hóa đơn ra file
    @Override
    public void ghiFile(String duongDan) {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8))) {
            for (int i = 0; i < soLuong; i++) {
                HoaDon hd = ds[i];
                NhanVienBanHang nv = hd.getNhanVienLap();
                String maNV = (nv == null) ? "" : nv.getMaNguoi();
                String tenNV = (nv == null) ? "" : nv.getHoTen();
                pw.println(hd.getMaHD() + ";" + hd.getNgayLap() + ";" + hd.getKhachHang().getMaKH()
                        + ";" + maNV + ";" + tenNV + ";" + (hd.isDaThanhToan() ? 1 : 0));
                pw.println(hd.getSoLuongChiTiet());
                for (int j = 0; j < hd.getSoLuongChiTiet(); j++) {
                    ChiTietHoaDon ct = hd.getChiTiet()[j];
                    pw.println(ct.getMathang().getMa() + ";" + ct.getsoLuong() + ";" + ct.getDonGia());
                }
            }
            System.out.println("Đã ghi dữ liệu hóa đơn ra file " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    // Bắt buộc phải có hàm này để không bị lỗi Interface IFileIO
    @Override
    public void docFile(String duongDan) {
        docFile(duongDan,this.dskh, this.dsmh);
    }

    // Hàm thực sự làm nhiệm vụ đọc file (Nạp chồng phương thức)
    // Vẫn đọc được file cũ chỉ có 3 trường ở dòng đầu (khi đó coi là chưa thanh toán, không có nhân viên)
    public void docFile(String duongDan, DanhSachKhachHang dskh, DanhSachMatHang dsmh) {
        try {
            String[] dong = Files.readAllLines(Path.of(duongDan), StandardCharsets.UTF_8).toArray(new String[0]);
            int viTri = 0;
            while (viTri < dong.length) {
                int dongDau = viTri + 1;
                String header = dong[viTri++].replace("\uFEFF", "").trim();
                if (header.isEmpty()) continue;
                try {
                    String[] info = header.split(";", -1);
                    if ((info.length != 3 && info.length != 6) || info[0].isBlank() || info[2].isBlank())
                        throw new IllegalArgumentException("Đầu hóa đơn không hợp lệ");
                    String maHD = info[0].trim();
                    LocalDate ngayLap = LocalDate.parse(info[1].trim());
                    if (viTri >= dong.length || laDauHoaDon(dong[viTri]))
                        throw new IllegalArgumentException("Thiếu số dòng chi tiết");
                    int soLuongCT = Integer.parseInt(dong[viTri++].trim());
                    if (soLuongCT <= 0 || soLuongCT > 10000)
                        throw new IllegalArgumentException("Số dòng chi tiết không hợp lệ");
                    String[] dongCT = new String[soLuongCT];
                    for (int j = 0; j < soLuongCT; j++) {
                        if (viTri >= dong.length || laDauHoaDon(dong[viTri]))
                            throw new IllegalArgumentException("Hóa đơn bị thiếu dòng chi tiết");
                        dongCT[j] = dong[viTri++];
                    }
                    KhachHang kh = dskh.timkiem_khachhang(info[2].trim());
                    if (kh == null) throw new IllegalArgumentException("Không tìm thấy khách hàng");
                    if (timKiem(maHD) != null) continue;
                    if (info.length == 6 && !info[5].trim().equals("0") && !info[5].trim().equals("1"))
                        throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ");
                    NhanVienBanHang nv = null;
                    if (info.length == 6 && !info[3].isBlank()) {
                        NhanVien tim = dsnv == null ? null : dsnv.timTheoMa(info[3].trim());
                        nv = tim instanceof NhanVienBanHang ? (NhanVienBanHang) tim
                                : new NhanVienBanHang(info[3].trim(), info[4].trim(), "", "", "", 0);
                    }
                    HoaDon hd = new HoaDon(maHD, ngayLap, kh, nv);
                    int hangLichSu = 0;
                    for (String chiTiet : dongCT) {
                        String[] ct = chiTiet.split(";", -1);
                        if ((ct.length != 2 && ct.length != 3) || ct[0].isBlank())
                            throw new IllegalArgumentException("Dòng hàng không hợp lệ");
                        MatHang mh = dsmh.timKiem_hang(ct[0].trim());
                        int slMua = Integer.parseInt(ct[1].trim());
                        if (mh == null && ct.length != 3)
                            throw new IllegalArgumentException("Thiếu mặt hàng và đơn giá lịch sử");
                        double donGia = ct.length == 3 ? Double.parseDouble(ct[2].trim()) : mh.tinhGiaBan();
                        if (mh == null) {
                            mh = new MatHangLichSu(ct[0].trim(), donGia);
                            for (int j = 0; j < hd.getSoLuongChiTiet(); j++) {
                                MatHang daCo = hd.getChiTiet()[j].getMathang();
                                if (daCo.getMa().equalsIgnoreCase(mh.getMa())) { mh = daCo; break; }
                            }
                            hangLichSu++;
                        }
                        if (!hd.themChiTiet(new ChiTietHoaDon(slMua, mh, donGia)))
                            throw new IllegalArgumentException("Số lượng hoặc đơn giá không hợp lệ");
                    }
                    if (info.length == 6 && info[5].trim().equals("1")) hd.danhDauDaThanhToan();
                    them(hd);
                    if (hangLichSu > 0)
                        System.out.println("Hóa đơn " + maHD + " giữ " + hangLichSu
                                + " dòng hàng lịch sử; đơn chưa thanh toán cần có hàng trên kệ trước khi thu tiền.");
                } catch (IllegalArgumentException | java.time.DateTimeException ex) {
                    System.out.println("Bỏ qua hóa đơn dòng " + dongDau + ": " + ex.getMessage());
                }
            }
            System.out.println("Đã tải dữ liệu từ file " + duongDan);
        } catch (NoSuchFileException e) {
            System.out.println("Chưa tìm thấy file " + duongDan);
        } catch (IOException e) {
            System.out.println("Lỗi khi đọc file: " + e.getMessage());
        }
    }

    private static boolean laDauHoaDon(String dong) {
        String[] cot = dong.replace("\uFEFF", "").split(";", -1);
        if (cot.length != 3 && cot.length != 6) return false;
        try {
            LocalDate.parse(cot[1].trim());
            return !cot[0].isBlank() && !cot[2].isBlank();
        } catch (java.time.DateTimeException ex) { return false; }
    }

}



