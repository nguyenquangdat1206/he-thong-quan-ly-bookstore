package phieunhap;

import interfaces.IQuanLy;
import mathang.*;
import nhacungcap.*;
import phieunhap.PhieuNhap.TrangThaiPN;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import interfaces.IFileIO;
import tienich.*;
import kho.Kho;

public class DanhSachPhieuNhap implements IQuanLy, IFileIO{
    private PhieuNhap[] dsPN;
    private int soLuongPN;
    private DanhSachNCC dsNCC;

    public DanhSachPhieuNhap() { this(new DanhSachNCC()); }
    // Cho Main truyền danh sách NCC dùng chung thay vì tạo một danh sách thứ hai.
    public DanhSachPhieuNhap(DanhSachNCC ncc) {
        dsPN = new PhieuNhap[1000];
        soLuongPN = 0;
        dsNCC = ncc;
    }

    public DanhSachNCC getDanhSachNCC() { return dsNCC; }

    public int getSoLuongPN() { return soLuongPN; }
    public PhieuNhap timPhieu(String ma) {
        int vt = TienIch.timKiemViTri(dsPN, soLuongPN, ma);
        return vt < 0 ? null : dsPN[vt];
    }

    @Override 
    public void nhap(){
        int n=TienIch.nhapSoNguyenDuong("Nhập số lượng phiếu nhập: ");
        for(int i=0;i<n;i++){
            System.out.println("======Nhập phiếu nhập thứ"+(i+1)+"======");
            them();
        }
    }
    @Override 
    public void xuat(){
        if(soLuongPN==0){ 
            System.out.println("Danh sách phiếu nhập đang trống!");
            return;
        }    
        System.out.println("\n======DANH SÁCH PHIẾU NHẬP======");
        System.out.printf("%-15s %-15s %-15s\n", "Mã Phiếu", "Ngày Nhập", "Nhà Cung Cấp");
        System.out.println("--------------------------------------------------------");
        for(int i=0;i<soLuongPN;i++){
            PhieuNhap pn = dsPN[i];
            String ngay = TienIch.ngaytoString(pn.getChiTiet().getNgayNhap());
            System.out.printf("%-15s %-15s %-25s\n", pn.getMa(), ngay, pn.getChiTiet().getNhaCungCap());
            System.out.println("Thông tin hàng nhập: ");
            pn.xuat();
        }
    }
    @Override 
    public void them(){
        if(soLuongPN==dsPN.length){
            PhieuNhap[] mangMoi = new PhieuNhap[dsPN.length*2];
            for(int i=0;i<dsPN.length;i++){
                mangMoi[i]=dsPN[i];
            }
            dsPN=mangMoi;
            System.out.println("Đã tự động tăng sức chứa mảng lên: "+ dsPN.length);
        }

        PhieuNhap pn = new PhieuNhap();
        pn.setMaPN(TienIch.nhapMa(dsPN, soLuongPN, "Nhập mã phiếu nhập mới: "));
        LocalDate ngay = TienIch.nhapNgay("Ngày nhập (dd/MM/yyyy; Enter để lấy hôm nay): ");
        pn.getChiTiet().setNgayNhap(ngay == null ? LocalDate.now() : ngay);
        String tenNCC = TienIch.nhapChuoi("Tên nhà cung cấp: ");
        if (tenNCC.trim().isEmpty()) {
            System.out.println("Không tạo phiếu: thiếu tên NCC."); return;
        }
        pn.getChiTiet().setNhaCC(pn.taoNCC(tenNCC, dsNCC));
        pn.nhap();
        if (pn.getSoLuongNhap() == 0) {
            System.out.println("Không lưu phiếu trống."); return;
        }
        dsPN[soLuongPN++]=pn;
        System.out.println("Thêm thành công phiếu: "+pn.getMa());
    }
    @Override 
    public void sua(){
        if(soLuongPN==0){
            System.out.println("Danh sách phiếu nhập đang trống!");
            return;
        }
        String maCanSua=TienIch.nhapChuoi("Nhập mã phiếu cần sửa: ");
        int vitri=TienIch.timKiemViTri(dsPN, soLuongPN, maCanSua);
        if(vitri==-1){
            System.out.println("Không tìm thấy phiếu có mã: "+ maCanSua);
            return;
        }
        System.out.println("=======MENU SỬA======");
        System.out.println("1. Sửa thông tin mặt hàng nhập ghi trên phiếu");
        System.out.println("2. Sửa thông tin phiếu nhập(mã phiếu nhập, ngày nhập, ncc,...");
        System.out.println("3. Sửa trạng thái phiếu nhập");
        System.out.println("4. Thoát");
        boolean tieptuc=true;
        while(tieptuc){
            int n = TienIch.nhapSoNguyenDuong("Nhập lựa chọn của bạn: ");
            switch(n){
                case 1:
                    dsPN[vitri].sua();
                    break;
                case 2:
                    dsPN[vitri].setMaPN(TienIch.nhapMa(dsPN, soLuongPN, "Nhập mã phiếu nhập mới: "));
                    dsPN[vitri].getChiTiet().setNgayNhap(TienIch.nhapNgay("Nhập ngày nhập mới: "));
                    dsPN[vitri].suaNCC(dsNCC);
                    break;
                case 3:
                    System.out.println("Chọn trạng thái mới của phiếu: ");
                    System.out.println("1. Chờ duyệt\n 2. Đã duyệt\n 3.Đã hủy");
                    int chon=TienIch.nhapSoNguyenDuong("Nhập chọn: ");
                    switch(chon){
                        case 1: dsPN[vitri].setTrangThai(TrangThaiPN.CHO_DUYET); break;
                        case 2: dsPN[vitri].setTrangThai(TrangThaiPN.DA_DUYET); break;
                        case 3: dsPN[vitri].setTrangThai(TrangThaiPN.DA_HUY);
                        break;
                    }
                    break;
                case 4:
                    tieptuc=false;
                    break;
                default: System.out.println("Lựa chọn không hợp lệ!");

            }
        }
    }
    @Override 
    public void xoa(){
        if(soLuongPN==0){
            System.out.println("Danh sách phiếu nhập đang trống!");
            return;
        }
        String maCanXoa=TienIch.nhapChuoi("Nhập mã phiếu cần xóa: ");
        int vitri=TienIch.timKiemViTri(dsPN, soLuongPN, maCanXoa);
        if(vitri==-1){
            System.out.println("Không tìm thấy phiếu có mã: "+ maCanXoa);
            return;
        }
        for(int i=vitri;i<soLuongPN-1;i++){
            dsPN[i]=dsPN[i+1];
        }
        dsPN[soLuongPN-1]=null;
        soLuongPN--;
        System.out.println("Xóa thành công phiếu mã "+maCanXoa);
    }

    private static final DateTimeFormatter NGAY_FILE =
            DateTimeFormatter.ofPattern("dd-MM-uuuu").withResolverStyle(ResolverStyle.STRICT);

    @Override
    public void ghiFile(String duongDan) {
        int dong = 0;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8))) {
            for (int i = 0; i < soLuongPN; i++) {
                PhieuNhap pn = dsPN[i];
                for (int j = 0; j < pn.getSoLuongNhap(); j++) {
                    pw.println(pn.getMa() + ";" + pn.getChiTiet().getNgayNhap().format(NGAY_FILE)
                            + ";" + pn.getDSMH()[j].toString() + ";" + pn.getTrangThai());
                    dong++;
                }
            }
            System.out.println("Đã ghi " + soLuongPN + " phiếu / " + dong + " dòng vào file.");
        } catch (IOException e) {
            System.out.println("Không ghi được file phiếu nhập: " + e.getMessage());
        }
    }

    private static LocalDate phanTichNgay(String value) {
        String s = value.trim();
        try { return LocalDate.parse(s, NGAY_FILE); }
        catch (DateTimeParseException e) { return LocalDate.parse(s); } // Tương thích bản ZIP cũ (yyyy-MM-dd)
    }

    /** Kiểm tra cấu trúc trước khi thay đổi mảng để một dòng lỗi không tạo phiếu/NCC rác. */
    private static MatHang matHangTuDong(String[] p) {
        String loai = p[2].trim(), ma = p[3].trim(), ten = p[4].trim();
        if (ma.isEmpty() || ten.isEmpty()) throw new IllegalArgumentException("thiếu mã hoặc tên mặt hàng");
        double gia = Double.parseDouble(p[5].trim());
        int so = Integer.parseInt(p[6].trim());
        if (!Double.isFinite(gia) || gia < 0 || so <= 0) throw new IllegalArgumentException("giá/số lượng không hợp lệ");
        switch (loai) {
            case "Sách":
                if (p.length != 11) throw new IllegalArgumentException("sai số cột của Sách");
                return new Sach(ma, ten, gia, so, p[7].trim(), p[8].trim(), p[9].trim());
            case "Tiểu thuyết":
                if (p.length != 11) throw new IllegalArgumentException("sai số cột Tiểu thuyết");
                return new TieuThuyet(ma, ten, gia, so, p[7].trim(), p[8].trim(), p[9].trim());
            case "Sách giáo khoa":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Sách giáo khoa");
                return new SachGiaoKhoa(ma, ten, gia, so, p[7].trim(), p[8].trim());
            case "Truyện":
                if (p.length != 11) throw new IllegalArgumentException("sai số cột Truyện");
                return new Truyen(ma, ten, gia, so, p[7].trim(), p[8].trim(), p[9].trim());
            case "Văn phòng phẩm":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Văn phòng phẩm");
                return new VanPhongPham(ma, ten, gia, so, p[8].trim(), p[7].trim());
            case "Báo tạp chí":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Báo tạp chí");
                return new BaoTapChi(ma, ten, gia, so, p[8].trim(), p[7].trim());
            case "Khác":
                if (p.length != 9) throw new IllegalArgumentException("sai số cột Khác");
                return new SPKhac(ma, ten, gia, so, p[7].trim());
            default: throw new IllegalArgumentException("danh mục không hỗ trợ: " + loai);
        }
    }

    private static String layTenNCC(MatHang mh) {
        if (mh instanceof Sach) return ((Sach) mh).getNhaXuatBan();
        if (mh instanceof TieuThuyet) return ((TieuThuyet) mh).getNhaXuatBan();
        if (mh instanceof SachGiaoKhoa) return ((SachGiaoKhoa) mh).getNhaXuatBan();
        if (mh instanceof Truyen) return ((Truyen) mh).getNhaXuatBan();
        if (mh instanceof VanPhongPham) return ((VanPhongPham) mh).getnhaSX();
        if (mh instanceof BaoTapChi) return ((BaoTapChi) mh).getNhaXuatBan();
        if (mh instanceof SPKhac) return ((SPKhac) mh).getnhaSX();
        throw new IllegalArgumentException("không xác định được NCC");
    }

    @Override
    public void docFile(String duongDan) {
        int them = 0, boQua = 0, dongSo = 0, khacNCC = 0;
        try (BufferedReader br = Files.newBufferedReader(Path.of(duongDan), StandardCharsets.UTF_8)) {
            String dong;
            while ((dong = br.readLine()) != null) {
                dongSo++;
                if (dong.isBlank()) continue;
                try {
                    String[] p = dong.replace("\uFEFF", "").split(";", -1);
                    if (p.length < 9) throw new IllegalArgumentException("dòng thiếu cột");
                    String maPN = p[0].trim();
                    if (maPN.isEmpty()) throw new IllegalArgumentException("thiếu mã phiếu");
                    LocalDate ngay = phanTichNgay(p[1]);
                    TrangThaiPN tt = TrangThaiPN.valueOf(p[p.length - 1].trim());
                    MatHang mh = matHangTuDong(p);
                    String tenNCC = layTenNCC(mh);
                    if (tenNCC == null || tenNCC.trim().isEmpty())
                        throw new IllegalArgumentException("thiếu tên nhà cung cấp/nhà xuất bản");

                    int vt = TienIch.timKiemViTri(dsPN, soLuongPN, maPN);
                    PhieuNhap pn = vt < 0 ? null : dsPN[vt];
                    if (pn != null) {
                        if (!pn.getChiTiet().getNgayNhap().equals(ngay) || pn.getTrangThai() != tt)
                            throw new IllegalArgumentException("trùng mã phiếu nhưng khác ngày/trạng thái");
                        boolean daCo = false;
                        for (int i = 0; i < pn.getSoLuongNhap(); i++)
                            if (pn.getDSMH()[i].getMa().equalsIgnoreCase(mh.getMa())) { daCo = true; break; }
                        if (daCo) { boQua++; continue; } // Đọc lại file không nhân đôi dòng hàng
                    }
                    if (pn == null) {
                        if (soLuongPN == dsPN.length) {
                            PhieuNhap[] moi = new PhieuNhap[dsPN.length * 2];
                            System.arraycopy(dsPN, 0, moi, 0, soLuongPN); dsPN = moi;
                        }
                        pn = new PhieuNhap();
                        pn.setMaPN(maPN);
                        pn.setChiTiet(new ChiTietPhieuNhap(ngay, pn.taoNCC(tenNCC, dsNCC)));
                        pn.setTrangThai(tt);
                        dsPN[soLuongPN++] = pn;
                    } else {
                        NhaCungCap ncc = pn.taoNCC(tenNCC, dsNCC);
                        // Mỗi phiếu hiện có một trường NCC. Không ghi đè tên NCC của dòng trước.
                        if (!pn.getChiTiet().getNhaCungCap().getMaNCC().equals(ncc.getMaNCC())) {
                            khacNCC++;
                            System.out.println("Cảnh báo dòng " + dongSo + ": phiếu " + maPN
                                    + " có nhiều NCC; giữ NCC của dòng đầu, NCC bổ sung vẫn có trong danh sách.");
                        }
                    }
                    pn.them(mh);
                    them++;
                } catch (IllegalArgumentException | java.time.DateTimeException | ArrayIndexOutOfBoundsException e) {
                    boQua++;
                    System.out.println("Bỏ qua phiếu nhập dòng " + dongSo + ": " + e.getMessage());
                }
            }
            System.out.println("Đọc phiếu nhập: thêm " + them + " dòng, bỏ qua " + boQua
                    + ", cảnh báo nhiều NCC " + khacNCC + ".");
        } catch (IOException e) {
            System.out.println("Lỗi đọc file phiếu nhập: " + e.getMessage());
        }
    }

    public void nhapKho(Kho kho){
        for(int i=0;i<soLuongPN;i++){
            if (dsPN[i].getTrangThai() != TrangThaiPN.CHO_DUYET) {
                System.out.println("Phiếu " + dsPN[i].getMa() + " đã xử lý trước đó, bỏ qua.");
                continue;
            }
            dsPN[i].xuat();
            String chon = TienIch.nhapChuoi("Xác nhận nhập hàng?(y/n)");
            if(chon.equalsIgnoreCase("y")) {
                if (kho.nhapHangTuPhieu(dsPN[i])) System.out.println("Đã nhập kho thành công!");
                else System.out.println("Không nhập kho được.");
            } else {
                dsPN[i].setTrangThai(TrangThaiPN.DA_HUY);
                System.out.println("Đã hủy phiếu!");
            }
        }
    }
}
