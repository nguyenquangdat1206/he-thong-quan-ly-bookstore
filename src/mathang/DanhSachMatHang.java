package mathang;



import interfaces.IQuanLyMatHang;
import kho.*;
import tienich.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import interfaces.IQuanLy;
import interfaces.IThaoTacThucThe;

public class DanhSachMatHang implements IQuanLyMatHang {
    MatHang[] ds;
    int soluong;
    private Kho kho;
    public DanhSachMatHang(){
        ds = new MatHang[100];
        soluong =0;
    }
    public int getSoMH(){return soluong;}
    public MatHang[] getMangMatHang() { return ds; }
    public DanhSachMatHang(Kho kho){
        this();
        this.kho = kho;
    }

    public void setKho(Kho kho) { this.kho = kho; }

    //Ham tang kich thuoc
    private void tangkichthuoc(){
        MatHang[] temp = new MatHang[ds.length*2];
        for(int i =0; i < ds.length;i++){
            temp[i] = ds[i];
        }
        ds = temp;
    }

    //Nhap hang(nhap tu kho)
    public void themvaods(String macanlay, int sl, HangTonKho htk){
        for(int i =0 ;i < soluong; i++){
            if(ds[i].getMa().equalsIgnoreCase(macanlay)) {
                ds[i].setSoLuong(ds[i].getSoLuong()+sl);
                System.out.println("(ĐÃ TỒN TẠI) Đã tăng " + sl +" mặt hàng "+ macanlay + " lên bày bán !");
                return;
            }
        }
        MatHang mh = htk.getMatHang().saoChep();
        mh.setSoLuong(sl);
        if(soluong == ds.length) tangkichthuoc();
        ds[soluong++] = mh;
        System.out.println("(TẠO MỚI) Đã đưa mặt hàng "+ macanlay + " với số lượng " + sl + " lên bày bán !");
    }

    public void nhaptukho(Kho kho, String macanlay, int sl){
        if (kho == null || macanlay == null || macanlay.isBlank() || sl <= 0) {
            System.out.println("Kho, mã hàng hoặc số lượng nhập không hợp lệ.");
            return;
        }
        for(int i = 0; i < kho.getSoLuongTon(); i++){
            HangTonKho htk = kho.getDanhSachTon()[i];
            if(htk.getMa().equalsIgnoreCase(macanlay)){
                if(sl <= htk.getSoLuong()){
                    htk.giamSoLuong(sl);
                    themvaods(macanlay, sl, htk);
                    return;
                }
                else{System.out.println("Hiện tại mặt hàng này chỉ còn " + htk.getSoLuong()+ " nên không thể nhập !"); return;}
            }
        }
        System.out.println("Không tìm thấy mặt hàng có mã như trên trong kho!");
    }

    public void nhap(){
        if(kho == null) {System.out.println("Lỗi kết nối với kho"); return;}
        System.out.println("Danh sách mặt hàng bày bán đang được nhập lên từ kho...");
        String macanlay = TienIch.nhapChuoi("Nhập mã mh cần lấy: ");
        int slcanlay = TienIch.nhapSoNguyenDuong("Nhập số lượng mh cần lấy: ");
        nhaptukho(kho, macanlay, slcanlay);
    }


    public boolean traHangVeKho(Kho kho, String ma, int soLuongTra) {
        if (kho == null || ma == null || ma.isBlank() || soLuongTra <= 0) {
            System.out.println("Kho, mã hàng hoặc số lượng trả không hợp lệ.");
            return false;
        }
        MatHang mh = timKiem_hang(ma);
        if (mh == null) {
            System.out.println("Không tìm thấy mặt hàng trên kệ: " + ma);
            return false;
        }
        if (soLuongTra > mh.getSoLuong()) {
            System.out.println("(!)Không thể trả " + soLuongTra + " sản phẩm, trên kệ chỉ còn "
                    + mh.getSoLuong() + ".");
            return false;
        }

        // Kho giữ một đối tượng riêng, tránh dùng chung MatHang với danh sách bán.
        if (!kho.tangTonKho(mh.saoChep(), soLuongTra)) return false;

        mh.setSoLuong(mh.getSoLuong() - soLuongTra);
        System.out.println("Đã trả " + soLuongTra + " sản phẩm " + mh.getMa() + " về kho.");
        return true;
    }

    public void traHangVeKho(Kho kho) {
        if (kho == null) {
            System.out.println("Lỗi kết nối với kho");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã mặt hàng cần trả về kho: ");
        int soLuongTra = TienIch.nhapSoNguyenDuong("Nhập số lượng cần trả: ");
        traHangVeKho(kho, ma, soLuongTra);
    }

    //Nhập hàng loạt từ kho (20% mỗi loại), nào còn dưới 15 cuốn thì sẽ kh được nhập hàng loạt
    public void nhapHangLoat(Kho kho){
        System.out.println("Đang thực hiện nhập từ kho, quá trình có thể mất nhiều thời gian...");
        for(int i = 0; i < kho.getSoLuongTon(); i++){
            HangTonKho htk = kho.getDanhSachTon()[i];
            if(htk.getSoLuong() <15){
                System.out.println("Mặt hàng này chỉ còn " + htk.getSoLuong() +" nên không thể nhập hàng loạt !"); continue;
            }
            int sl = (int)(htk.getSoLuong()*0.2);
            themvaods(htk.getMa(), sl , htk);
            htk.giamSoLuong(sl);
        }
    }


    public void thongKeHangChuaLenKe(Kho kho){
        if(kho == null){
            System.out.println("Kho trống!");
            return;
        }
        int soMatHang = 0;
        int tongSoLuong = 0;
        System.out.println("===== HÀNG TRONG KHO CHƯA LÊN KỆ BÁN =====");
        System.out.printf("|%-20s|%-40s|%-15s|%-20s|%-20s|%n",
                "Mã", "Tên", "Giá Nhập", "Số Lượng", "Giá Bán");
        for(int i = 0; i < kho.getSoLuongTon(); i++){
            HangTonKho htk = kho.getDanhSachTon()[i];
            if(htk != null && htk.getMatHang() != null
                    && htk.getSoLuong() > 0 && timKiem_hang(htk.getMa()) == null){
                htk.xuat();
                soMatHang++;
                tongSoLuong += htk.getSoLuong();
            }
        }
        if(soMatHang == 0){
            System.out.println("Không có mặt hàng nào trong kho đang chờ lên kệ.");
            return;
        }
        System.out.println("Số mặt hàng chưa lên kệ: " + soMatHang);
        System.out.println("Tổng số lượng đang chờ lên kệ: " + tongSoLuong);
    }

    //Xuat tt hang

    protected void xuattatca(){
        for(int i =0; i < soluong;i++){
            System.out.println("\n--------THÔNG TIN MẶT HÀNG THỨ " +(i+1)+ "--------");
            ds[i].xuat();
        }
        System.out.println("Đang có tổng cộng " + soluong + " mặt hàng khác nhau đang được bày bán");
    }

    protected void xuattheoloai() {
        System.out.println("\n=== Chọn loại mặt hàng muốn xem ===");
        System.out.println("1. Sách");
        System.out.println("2. Sách giáo khoa");
        System.out.println("3. Tiểu thuyết");
        System.out.println("4. Truyện");
        System.out.println("5. Văn phòng phẩm");
        System.out.println("6. Báo tạp chí");
        System.out.println("7. Sản phẩm khác");
        System.out.println("8. Đồ chơi");
        System.out.println("9. Dụng cụ học tập");
        System.out.println("10. Trang trí");
        System.out.println("11. Vở");
        System.out.println("0. Quay lại");
        String loai;
        switch (TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ")) {
            case 1:  loai = "Sách"; break;
            case 2:  loai = "Sách giáo khoa"; break;
            case 3:  loai = "Tiểu thuyết"; break;
            case 4:  loai = "Truyện"; break;
            case 5:  loai = "Văn phòng phẩm"; break;
            case 6:  loai = "Báo tạp chí"; break;
            case 7:  loai = "Khác"; break;
            case 8:  loai = "Đồ chơi"; break;
            case 9:  loai = "Dụng cụ học tập"; break;
            case 10: loai = "Trang trí"; break;
            case 11: loai = "Vở"; break;
            case 0:  return;
            default: System.out.println("Lựa chọn không hợp lệ !"); return;
        }
        int soluonghang =0;
        for (int i = 0; i < soluong; i++) {
            if (ds[i].getDanhMuc().equals(loai)) {
                System.out.println("\n-------- " + loai.toUpperCase() + " --------");
                ds[i].xuat();
                soluonghang++;
            }
        }
        System.out.println("Mặt hàng " + loai + " đang có tổng: "+ soluonghang +" loại hàng");
        if (soluonghang==0) System.out.println("Chưa có mặt hàng nào thuộc loại này!");
    }

    @Override
    public void xuat() {
        int choice;
        do {
            System.out.println("\n=== CHỌN PHƯƠNG THỨC XEM MẶT HÀNG ===");
            System.out.println("1. Xuất toàn bộ thông tin mặt hàng đang được bày bán");
            System.out.println("2. Xuất và xem mặt hàng theo loại");
            System.out.println("0. Thoát");
            choice = TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn của bạn: ");
            switch (choice) {
                case 0: System.out.println("Đã thoát chức năng xem hàng."); break;
                case 1: xuattatca(); break;
                case 2: xuattheoloai(); break;
                default: System.out.println("Lựa chọn không hợp lệ !");
            }
        } while (choice != 0);
    }


    public MatHang timKiem_hang(String ma) {
        for (int i = 0; i < soluong; i++) {
            if (ds[i].getMa().equalsIgnoreCase(ma)) {
                return ds[i]; // Trả về đối tượng mặt hàng nếu tìm thấy
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    // Tim kiem hang
    @Override
    public void timKiem(String ma) {
        MatHang mh = timKiem_hang(ma);
        if(mh == null){ System.out.println("Không tìm thấy mặt hàng có mã này !"); return;}
        mh.xuat();
    }

    public void timKiem(){
        String macantim = TienIch.nhapChuoi("Nhập mã mặt hàng cần tìm: ");
        timKiem(macantim);
    }

    //Xoa hang
    @Override
    public void xoa(String ma) {
        MatHang mh = timKiem_hang(ma);
        if(mh == null){System.out.println("Không tìm thấy mặt hàng có mã này !"); return;}
        for(int i = 0; i < soluong; i++){
            if(mh.getMa().equalsIgnoreCase(ds[i].getMa())){
                for(int j = i; j < soluong - 1; j++) ds[j] = ds[j + 1];
                break;
            }
        }
        ds[--soluong] = null;
        System.out.println("Đã xóa thành công mặt hàng có mã " + ma);
    }

    public void xoa(){
        xoa(TienIch.nhapChuoi("Nhập mã hàng cần xóa: "));
    }

    public void suathuoctinh(MatHang mh) {
        int c;
        do {
            System.out.println("\nXác nhận lựa chọn 2: Sửa một thuộc tính cụ thể.");
            System.out.println("Các thuộc tính có thể chỉnh sửa: ");
            System.out.println("1. Sửa lại mã");
            System.out.println("2. Sửa lại tên");
            System.out.println("3. Sửa lại giá nhập");
            System.out.println("4. Sửa lại số lượng");
            System.out.println("5. Sửa các thuộc tính riêng của mặt hàng đó");
            System.out.println("0. Quay lại");
            c = TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ");
            switch (c) {
                case 1:
                    while (true) {
                        String maMoi = TienIch.nhapMa(ds, soluong, "Nhập mã mới: ");
                        try {
                            String maCu = mh.getMa();
                            mh.setMa(maMoi);
                            System.out.println("Đã chuyển đổi mã " + maCu + " thành " + maMoi);
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 2: mh.setTen(TienIch.nhapChuoi("Nhập tên mới: ")); break;
                case 3: mh.setGiaNhap(TienIch.nhapGiaTien("Nhập giá nhập mới: ")); break;
                case 4: mh.setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng mới: ")); break;
                case 5: mh.suathuoctinhrieng(); break;
                case 0: break;
                default: System.out.println("Lựa chọn không hợp lệ.");
            }
        } while (c != 0);
    }

    @Override
    public void sua(String ma){
        MatHang mh = timKiem_hang(ma);
        if(mh == null){System.out.println("Không tìm thấy mặt hàng có mã này !"); return;}
        System.out.println("\n === Các phương thức sửa hàng === ");
        System.out.println("1. Sửa toàn bộ (nhập mới lại thông tin mặt hàng)");
        System.out.println("2. Sửa một thuộc tính cụ thể của hàng");
        System.out.println("0. Thoát chương trình");
        int choice = TienIch.nhapSoNguyenKhongAm("Lựa chọn phương thức sửa: ");
        switch(choice){
            case 0: System.out.println("Đã thoát chương trình"); return;
            case 1:
                while (true) {
                    mh.nhap();
                    boolean trung = false;
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] != mh && ds[i].getMa().equalsIgnoreCase(mh.getMa())) {
                            trung = true; break;
                        }
                    }
                    if (!trung) break;
                    System.out.println("Mã sản phẩm đã tồn tại! Vui lòng nhập lại.");
                }
                System.out.println("Đã sửa toàn bộ thông tin mặt hàng!");
                break;
            case 2: suathuoctinh(mh); break;
            default: System.out.println("Lựa chọn không hợp lệ !");
        }
    }

    public void sua(){
        sua(TienIch.nhapChuoi("Nhập mã mặt hàng cần điều chỉnh: "));
    }

    //Boloc tim kiem
    private boolean coHang;
    // Hàm này chỉ in ra các mặt hàng đúng loại và có thuộc tính khớp
    private void in(MatHang mh, String giaTriCuaMH, String boLoc) {
        if (boLoc.equalsIgnoreCase(giaTriCuaMH)) {
            mh.xuat();
            coHang = true;
        }
    }

    public void timkiem_nangcao() {
        System.out.println("\nChọn loại mặt hàng: ");
        System.out.println("1. Sách");
        System.out.println("2. Sách giáo khoa");
        System.out.println("3. Tiểu thuyết");
        System.out.println("4. Truyện");
        System.out.println("5. Văn phòng phẩm");
        System.out.println("6. Báo tạp chí");
        System.out.println("7. Sản phẩm khác");
        System.out.println("8. Đồ chơi");
        System.out.println("9. Dụng cụ học tập");
        System.out.println("10. Trang trí");
        System.out.println("11. Vở");
        System.out.println("0. Thoát");

        int loai = TienIch.nhapSoNguyenKhongAm("Nhập loại hàng muốn tìm kiếm: ");
        if (loai == 0) return;

        // Hỏi thuộc tính theo từng loại
        int k = 1;
        switch (loai) {
            case 1: case 3: case 4:   // Sách, Tiểu thuyết, Truyện
                System.out.println("1. Tác giả\n2. Thể loại\n3. Nhà xuất bản");
                k = TienIch.nhapSoNguyenKhongAm("Chọn thuộc tính: ");
                break;
            case 2:
                System.out.println("1. Môn học\n2. Nhà xuất bản");
                k = TienIch.nhapSoNguyenKhongAm("Chọn thuộc tính: ");
                break;
            case 5:
                System.out.println("1. Loại văn phòng phẩm\n2. Nhà sản xuất");
                k = TienIch.nhapSoNguyenKhongAm("Chọn thuộc tính: ");
                break;
            case 6:
                System.out.println("1. Thể loại\n2. Nhà xuất bản");
                k = TienIch.nhapSoNguyenKhongAm("Chọn thuộc tính: ");
                break;
            default:   // 7, 8, 9, 10, 11: chỉ tìm theo nhà sản xuất
                System.out.println("Tìm theo nhà sản xuất");
                break;
        }
        if (k == 0) return;

        String boLoc = TienIch.nhapChuoi("Nhập giá trị cần tìm: ");
        coHang = false;

        for (int i = 0; i < soluong; i++) {
            MatHang mh = ds[i];

            if (loai == 1 && mh.getClass() == Sach.class) {
                Sach s = (Sach) mh;
                if (k == 1) in(s, s.getTacGia(), boLoc);
                if (k == 2) in(s, s.getTheloai(), boLoc);
                if (k == 3) in(s, s.getNhaXuatBan(), boLoc);
            }
            else if (loai == 2 && mh instanceof SachGiaoKhoa) {
                SachGiaoKhoa s = (SachGiaoKhoa) mh;
                if (k == 1) in(s, s.getMonhoc(), boLoc);
                if (k == 2) in(s, s.getNhaXuatBan(), boLoc);
            }
            else if (loai == 3 && mh instanceof TieuThuyet) {
                TieuThuyet s = (TieuThuyet) mh;
                if (k == 1) in(s, s.getTacGia(), boLoc);
                if (k == 2) in(s, s.getTheloai(), boLoc);
                if (k == 3) in(s, s.getNhaXuatBan(), boLoc);
            }
            else if (loai == 4 && mh instanceof Truyen) {
                Truyen s = (Truyen) mh;
                if (k == 1) in(s, s.getTacGia(), boLoc);
                if (k == 2) in(s, s.getTheloai(), boLoc);
                if (k == 3) in(s, s.getNhaXuatBan(), boLoc);
            }
            else if (loai == 5 && mh instanceof VanPhongPham) {
                VanPhongPham s = (VanPhongPham) mh;
                if (k == 1) in(s, s.getLoaiVPP(), boLoc);
                if (k == 2) in(s, s.getnhaSX(), boLoc);
            }
            else if (loai == 6 && mh instanceof BaoTapChi) {
                BaoTapChi s = (BaoTapChi) mh;
                if (k == 1) in(s, s.getTheloai(), boLoc);
                if (k == 2) in(s, s.getNhaXuatBan(), boLoc);
            }
            else if (loai == 7 && mh instanceof SPKhac)
                in(mh, ((SPKhac) mh).getnhaSX(), boLoc);
            else if (loai == 8 && mh instanceof DoChoi)
                in(mh, ((DoChoi) mh).getnhaSX(), boLoc);
            else if (loai == 9 && mh instanceof DungCuHocTap)
                in(mh, ((DungCuHocTap) mh).getnhaSX(), boLoc);
            else if (loai == 10 && mh instanceof TrangTri)
                in(mh, ((TrangTri) mh).getnhaSX(), boLoc);
            else if (loai == 11 && mh instanceof Vo)
                in(mh, ((Vo) mh).getnhaSX(), boLoc);
        }

        if (!coHang) System.out.println("Không tìm thấy mặt hàng phù hợp!");
    }


    public void ghiFile(String duongDan) {
        int dong=0;
        try(BufferedWriter w = Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8)) {
            for(int i = 0; i < soluong; i++){
                w.write(ds[i].toString());
                w.newLine();
                dong++;
            }
        System.out.println("Đã ghi thành công "+dong +" vào file "+duongDan);
        }catch(IOException ex){
            System.out.println("Lỗi ghi file " +ex.getMessage());
        }
    }

}
