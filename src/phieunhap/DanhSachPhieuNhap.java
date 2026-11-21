package phieunhap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import interfaces.IFileIO;
import interfaces.IQuanLy;
import kho.Kho;
import mathang.BaoTapChi;
import mathang.DoChoi;
import mathang.DungCuHocTap;
import mathang.MatHang;
import mathang.SPKhac;
import mathang.Sach;
import mathang.SachGiaoKhoa;
import mathang.TieuThuyet;
import mathang.TrangTri;
import mathang.Truyen;
import mathang.VanPhongPham;
import mathang.Vo;
import nhacungcap.DanhSachNCC;
import tienich.TienIch;
import phieunhap.PhieuNhap.TrangThaiPN;

public class DanhSachPhieuNhap implements IFileIO, IQuanLy{

    private PhieuNhap dsPN[];
    private int slPN;
    private DanhSachNCC dsNcc;

    public DanhSachPhieuNhap(){
        this(new DanhSachNCC());
    }

    //tiết kiệm bộ nhớ vs n pn biết trước
    public DanhSachPhieuNhap(int n){
        dsPN = new PhieuNhap [n];
        slPN=0;
        dsNcc = new DanhSachNCC();
    }

    //truyền nhà cung cấp
    public DanhSachPhieuNhap(DanhSachNCC dsncc){
        dsPN = new PhieuNhap[1000];
        slPN=0;
        this.dsNcc=dsncc;
    }

    public DanhSachNCC getDanhSachNCC(){return dsNcc;}
    public int getSLpn(){return slPN;}

    public PhieuNhap timPhieu(String ma){
        int vt = TienIch.timKiemViTri(dsPN, slPN, ma);
        return vt==-1 ? null:dsPN[vt];
    }

    @Override
    public void nhap(){
        int n= TienIch.nhapSoNguyenDuong("Nhập số lượng phiếu nhập cần nhập tay: ");
        for(int i=0;i<n;i++){
            System.out.println("Nhập phiếu thứ "+ i+1);
            them();
        }

    }

    @Override
    public void them(){
        if(slPN==dsPN.length){
            PhieuNhap[] mangMoi= new PhieuNhap[dsPN.length*2];
            for(int i=0; i<dsPN.length;i++){
                mangMoi[i]=dsPN[i];
            }
            dsPN=mangMoi;
            System.out.println("Đã tự động tăng kích thước mảng lên "+ dsPN.length);
        }
        PhieuNhap pn = new PhieuNhap();

        pn.setMaPN(TienIch.nhapMa(dsPN, slPN, "Nhập mã phiếu nhập"));

        LocalDate ngay = TienIch.nhapNgay("Nhập ngày (dd/MM/yyyy) nhập hàng: (bấm Enter để lấy ngày hôm nay)");
        pn.getChiTiet().setNgayNhap(ngay==null? LocalDate.now():ngay);

        String tenNcc = TienIch.nhapChuoi("Nhập tên nhà cung cấp cho phiếu nhập này: ");
        pn.getChiTiet().setNhaCC(dsNcc.timHoacTaoTheoTen(tenNcc));

        pn.nhap();

        dsPN[slPN++]=pn;
        System.out.println("Thêm thành công phiếu nhập "+pn.getMa());
    }

    @Override
    public void xoa(){
        if(slPN==0){
            System.out.println("Chưa có phiếu nhập nào!");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã phiếu nhập cần xóa: ");
        int vt = TienIch.timKiemViTri(dsPN, slPN, ma);
        if(vt==-1){
            System.out.println("Không có mã phiếu nhập tương ứng trong danh sách!");
            return;
        }

        for(int i = vt;i<slPN-1;i++){
            dsPN[i]=dsPN[i+1];
        }
        dsPN[--slPN]=null;
        System.out.println("Đã xóa thành công phiếu "+ma);
    }

    @Override
    public void sua(){
        if(slPN==0){
            System.out.println("Chưa có phiếu nhập nào!");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã phiếu nhập cần sửa: ");
        int vt = TienIch.timKiemViTri(dsPN, slPN, ma);
        if(vt==-1){
            System.out.println("Không có mã phiếu nhập tương ứng trong danh sách!");
            return;
        }
        boolean tieptuc = true;
        while(tieptuc){
            System.out.println("=======MENU SỬA======");
            System.out.println("1. Sửa thông tin mặt hàng nhập ghi trên phiếu");
            System.out.println("2. Sửa thông tin phiếu nhập(mã phiếu nhập, ngày nhập, ncc,...");
            System.out.println("3. Sửa trạng thái phiếu nhập");
            System.out.println("4. Thoát");
            int n = TienIch.nhapSoNguyenDuong("Nhập lựa chọn: ");
            switch(n){
                case 1: dsPN[vt].sua(); break;

                case 2:
                //sửa mã
                while(true){
                    String lc = TienIch.nhapChuoi("Đổi mã phiếu? (Y/N): ");
                    if(lc.equalsIgnoreCase("Y")){
                        String maMoi = TienIch.nhapMa(dsPN, slPN, "Nhập mã mới: ");
                        dsPN[vt].setMaPN(maMoi);
                        break;
                    }
                    if(lc.equalsIgnoreCase("N")) break;
                    System.out.println("Chỉ nhập Y hoặc N!");
                }
                //sửa ngày
                while(true){
                    String lc = TienIch.nhapChuoi("Đổi ngày nhập? (Y/N): ");
                    if(lc.equalsIgnoreCase("Y")){
                        LocalDate ngay = TienIch.nhapNgay("Nhập ngày nhập mới: ");
                        dsPN[vt].getChiTiet().setNgayNhap(ngay==null? LocalDate.now():ngay);
                        break;
                    }
                    if(lc.equalsIgnoreCase("N")) break;
                    System.out.println("Chỉ nhập Y hoặc N!");
                }
                //sửa ncc
                while(true){
                    String lc = TienIch.nhapChuoi("Đổi nhà cung cấp? (Y/N): ");
                    if(lc.equalsIgnoreCase("Y")){
                        String tenNCC= TienIch.nhapChuoi("Nhập tên nhà cung cấp mới:  ");
                        dsPN[vt].getChiTiet().setNhaCC(dsNcc.timHoacTaoTheoTen(tenNCC));
                        break;
                    }
                    if(lc.equalsIgnoreCase("N")) break;
                    System.out.println("Chỉ nhập Y hoặc N!");
                }
                break;

                case 3:
                    System.out.println("Chọn trạng thái mới của phiếu: ");
                    System.out.println("1. Chờ duyệt\n 2. Đã duyệt\n 3.Đã hủy");
                    while(true){
                    int lc = TienIch.nhapSoNguyenDuong("Nhập lựa chọn: ");
                    if(lc == 1){
                        dsPN[vt].setTrangThai(TrangThaiPN.CHO_DUYET);
                        break;
                    }
                    if(lc==2){
                        dsPN[vt].setTrangThai(TrangThaiPN.DA_DUYET);
                        break;
                    }
                    if(lc==3){
                        dsPN[vt].setTrangThai(TrangThaiPN.DA_HUY);
                    }
                    System.out.println("Vui lòng chọn lại!");
                    }

                case 4: System.out.println("Đã thoát"); tieptuc=false; break;

                default: System.out.println("Vui lòng chọn lại!");
            }
        }
    }

    @Override
    public void xuat(){
        if(slPN==0){
            System.out.println("Danh sách đang trống!");
            return;
        }
        System.out.println("\n======DANH SÁCH PHIẾU NHẬP======");
        System.out.printf("%-15s %-15s %-25s %-15s%n", "Mã Phiếu", "Ngày Nhập", "Nhà Cung Cấp", "Trạng thái");
        System.out.println("--------------------------------------------------------");

        for(int i =0; i<slPN;i++){
            PhieuNhap pn = dsPN[i];
            String ngay = TienIch.ngaytoString(pn.getChiTiet().getNgayNhap());
            System.out.printf("%-15s %-15s %-25s %-15s%n", pn.getMa(), ngay,
                    pn.getChiTiet().getNhaCungCap().getTenNCC(), pn.getTrangThai());
            System.out.println("Thông tin hàng nhập: ");
            pn.xuat();
            System.out.println("=======================");
        }
    }

    //thống nhất format ngày nhập cho đọc file và ghi file
    private static final DateTimeFormatter NGAY_FILE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    @Override
    public void ghiFile(String duongDan){
        int dong = 0;
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8))){
            for(int i = 0;i<slPN;i++){
                PhieuNhap pn = dsPN[i];
                for(int j=0;j<pn.getSoLuongNhap();j++){
                    pw.println(pn.getMa() + ";" + pn.getChiTiet().getNgayNhap().format(NGAY_FILE) + ";" + pn.getDSMH()[j].toString() + ";" + pn.getTrangThai());
                    dong++;
                }
            }
            System.out.println("Đã ghi " + slPN + " phiếu / " + dong + " dòng vào file.");
        }catch (IOException e){
            System.out.println("Không ghi được file "+e.getMessage());
        }
    }


    public void ghiFileDaNhapKho(String duongDan){
        int soPhieu = 0, soDong = 0;
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8))){
            for(int i = 0; i < slPN; i++){
                PhieuNhap pn = dsPN[i];
                if(pn.getTrangThai() != TrangThaiPN.DA_DUYET) continue;
                soPhieu++;
                for(int j = 0; j < pn.getSoLuongNhap(); j++){
                    pw.println(pn.getMa() + ";" + pn.getChiTiet().getNgayNhap().format(NGAY_FILE)
                            + ";" + pn.getDSMH()[j].toString() + ";" + pn.getTrangThai());
                    soDong++;
                }
            }
            System.out.println("Đã ghi " + soPhieu + " phiếu đã nhập kho / " + soDong
                    + " dòng vào file " + duongDan);
        }catch(IOException e){
            System.out.println("Không ghi được danh sách phiếu đã nhập kho: " + e.getMessage());
        }
    }


    public void tuDongNhapTatCaVaoKho(Kho kho, String duongDanGhi){
        if(kho == null){
            System.out.println("Kho rỗng");
            return;
        }
        int thanhCong = 0, thatBai = 0;
        for(int i = 0; i < slPN; i++){
            PhieuNhap pn = dsPN[i];
            if(pn.getTrangThai() != TrangThaiPN.CHO_DUYET) continue;
            if(kho.nhapHangTuPhieu(pn)) thanhCong++;
            else thatBai++;
        }
        System.out.println("Hoàn tất: nhập thành công " + thanhCong + " phiếu, thất bại " + thatBai + " phiếu.");
        ghiFileDaNhapKho(duongDanGhi);
    }

    private static MatHang taoMatHangTuDong(String[]p){
        String loai = p[2].trim(), ma = p[3].trim(), ten=p[4].trim();
        if(ma.isEmpty()||ten.isEmpty()) throw new IllegalArgumentException("thiếu mã hoặc tên hàng");
        double gia = Double.parseDouble(p[5].trim());
        int sl = Integer.parseInt(p[6].trim());
        if(!Double.isFinite(gia) || gia<0 || sl<=0) throw new IllegalArgumentException("giá/số lượng kh hợp lệ");
        switch(loai){
            case "Sách":
                if (p.length != 11) throw new IllegalArgumentException("sai số cột của Sách");
                return new Sach(ma, ten, gia, sl, p[7].trim(), p[8].trim(), p[9].trim());
            case "Tiểu thuyết":
                if (p.length != 11) throw new IllegalArgumentException("sai số cột Tiểu thuyết");
                return new TieuThuyet(ma, ten, gia, sl, p[7].trim(), p[8].trim(), p[9].trim());
            case "Sách giáo khoa":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Sách giáo khoa");
                return new SachGiaoKhoa(ma, ten, gia, sl, p[7].trim(), p[8].trim());
            case "Truyện":
                if (p.length != 11) throw new IllegalArgumentException("sai số cột Truyện");
                return new Truyen(ma, ten, gia, sl, p[7].trim(), p[8].trim(), p[9].trim());
            case "Văn phòng phẩm":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Văn phòng phẩm");
                return new VanPhongPham(ma, ten, gia, sl, p[8].trim(), p[7].trim());
            case "Báo tạp chí":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Báo tạp chí");
                return new BaoTapChi(ma, ten, gia, sl, p[8].trim(), p[7].trim());
            case "Khác":
                if (p.length != 9) throw new IllegalArgumentException("sai số cột Khác");
                return new SPKhac(ma, ten, gia, sl, p[7].trim());
            case "Đồ chơi":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Đồ chơi");
                return new DoChoi(ma, ten, gia, sl, p[8].trim(), Integer.parseInt(p[7].trim()));
            case "Dụng cụ học tập":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Dụng cụ học tập");
                return new DungCuHocTap(ma, ten, gia, sl, p[8].trim(), p[7].trim());
            case "Trang trí":
                if (p.length != 9) throw new IllegalArgumentException("sai số cột Trang trí");
                return new TrangTri(ma, ten, gia, sl, p[7].trim());
            case "Vở":
                if (p.length != 10) throw new IllegalArgumentException("sai số cột Vở");
                return new Vo(ma, ten, gia, sl, Integer.parseInt(p[7].trim()), p[8].trim());
            default: throw new IllegalArgumentException("danh mục không hỗ trợ: " + loai);
        }
    }

    private static MatHang matHangTuDong(String[] p) {
        MatHang mh = taoMatHangTuDong(p);
        mh.setMa(mh.getMa()); // mã phải đúng định dạng của loại mặt hàng, sai thì ném IllegalArgumentException
        return mh;
    }

    private static String layTenNCC(MatHang mh) {
        if (mh instanceof Sach) return ((Sach) mh).getNhaXuatBan();
        if (mh instanceof TieuThuyet) return ((TieuThuyet) mh).getNhaXuatBan();
        if (mh instanceof SachGiaoKhoa) return ((SachGiaoKhoa) mh).getNhaXuatBan();
        if (mh instanceof Truyen) return ((Truyen) mh).getNhaXuatBan();
        if (mh instanceof VanPhongPham) return ((VanPhongPham) mh).getnhaSX();
        if (mh instanceof BaoTapChi) return ((BaoTapChi) mh).getNhaXuatBan();
        if (mh instanceof SPKhac) return ((SPKhac) mh).getnhaSX();
        if (mh instanceof DoChoi) return ((DoChoi) mh).getnhaSX();
        if (mh instanceof DungCuHocTap) return ((DungCuHocTap) mh).getnhaSX();
        if (mh instanceof TrangTri) return ((TrangTri) mh).getnhaSX();
        if (mh instanceof Vo) return ((Vo) mh).getnhaSX();
        throw new IllegalArgumentException("không xác định được NCC");
    }

    @Override
    public void docFile(String duongDan){
        int them = 0, boQua = 0, dongSo = 0, khacNCC = 0;
        try(BufferedReader br = Files.newBufferedReader(Path.of(duongDan), StandardCharsets.UTF_8)){
            String dong;
            while((dong = br.readLine())!=null){
                dongSo++;
                if(dong.isBlank()) continue;
                try{
                    String [] p = dong.replace("\uFEFF", "").split(";");

                    if(p.length < 9)
                        throw new IllegalArgumentException("Thiếu cột dữ liệu");

                    String maPn = p[0].trim();
                    LocalDate ngayNhap= LocalDate.parse(p[1].trim(),NGAY_FILE);
                    MatHang mh = matHangTuDong(p);
                    String tenNcc = layTenNCC(mh);
                    TrangThaiPN tt = TrangThaiPN.valueOf(p[p.length-1].trim());

                    PhieuNhap pn = timPhieu(maPn);

                    if(pn!=null){
                        if (!pn.getChiTiet().getNgayNhap().equals(ngayNhap) || pn.getTrangThai() != tt)
                            throw new IllegalArgumentException("trùng mã phiếu nhưng khác ngày/trạng thái");

                        if (!pn.getChiTiet().getNhaCungCap().getTenNCC().equals(tenNcc)){
                            khacNCC++;
                            throw new IllegalArgumentException("trùng mã phiếu nhưng khác NCC chính");
                        }

                        boolean daCoMH=false;
                        for(int i=0;i<pn.getSoLuongNhap();i++){
                            if(pn.getDSMH()[i].getMa().equals(mh.getMa())){
                                daCoMH=true;
                                break;
                            }
                        }
                        if(daCoMH) {boQua++; continue;}
                    }

                    if(pn==null){
                        if(slPN==dsPN.length){
                            PhieuNhap[] moi = new PhieuNhap[dsPN.length*2];
                            for(int i=0;i<slPN;i++)
                                moi[i]=dsPN[i];
                            dsPN=moi;
                        }
                        pn = new PhieuNhap();
                        pn.setMaPN(maPn);
                        pn.setChiTiet(new ChiTietPhieuNhap(ngayNhap, dsNcc.timHoacTaoTheoTen(tenNcc)));
                        pn.setTrangThai(tt);
                        dsPN[slPN++]=pn;
                    }

                    pn.them(mh);
                    them++;

                }catch (IllegalArgumentException | java.time.DateTimeException e) {
                    boQua++;
                    System.out.println("Bỏ qua phiếu nhập dòng " + dongSo + ": " + e.getMessage());
                }
            }
            System.out.println("Đọc phiếu nhập: thêm " + them + " dòng, bỏ qua " + boQua
                    + ", cảnh báo nhiều NCC " + khacNCC + ".");
        } catch (IOException e){
            System.out.println("Lỗi không đọc được file "+ e.getMessage());
        }
    }

    public void nhapKho(Kho kho){
        for(int i=0;i<slPN;i++){
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
