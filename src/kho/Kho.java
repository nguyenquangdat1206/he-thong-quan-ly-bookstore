package kho;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import phieunhap.*;
import mathang.*;
import interfaces.IQuanLy;
import tienich.*;


public class Kho implements IQuanLy  {
    private static final int sucChuaToiDa = 10000;
    private HangTonKho[] dsTonKho;
    private int soLuongMatHang;
    
//số lượng ở đây là số lượng mặt hàng tồn trong kho, kh tính chi tiết từng mặt hàng có bn cái

    public Kho(){
        dsTonKho=new HangTonKho[1000];
        soLuongMatHang=0;
    }
    
//getter
    public int getSuChuaToiDa() {return sucChuaToiDa;}
    public HangTonKho[] getDanhSachTon(){
        return dsTonKho;
    }

    public int getSoLuongTon() {return soLuongMatHang;}

//lấy tổng số lượng của các mặt hàng vdu: sách toán có 4 quyển, sách văn có 3 quyển, tiểu thuyết x có y quyển -> 3+4+y
    public int getTongSoLuongTon() {
        int sum=0;
        for(int i=0;i<soLuongMatHang;i++)
            sum+=dsTonKho[i].getSoLuong();
        return sum;
    }
//kho còn trống
    public int getConTrong(){
        return sucChuaToiDa-getTongSoLuongTon();
    }
    
    public void kiemTraSucChua(){
        System.out.println("--- THÔNG TIN KHO ---");
        System.out.println("Tổng sức chứa: " + sucChuaToiDa);
        System.out.println("Đang chứa: " + getTongSoLuongTon());
        System.out.println("Không gian còn trống: " + getConTrong());

    }

    public void thongKeTheoDanhMuc(){
        String[] danhMuc = {
            "Sách", "Sách giáo khoa", "Tiểu thuyết", "Truyện",
            "Văn phòng phẩm", "Báo tạp chí", "Khác", "Đồ chơi",
            "Dụng cụ học tập", "Trang trí", "Vở"
        };
        System.out.println("\n--- THỐNG KÊ TỒN KHO THEO DANH MỤC ---");
        for(int i = 0; i < danhMuc.length; i++)
            System.out.println((i + 1) + ". " + danhMuc[i]);
        int chon = TienIch.nhapSoNguyenKhongAm("Chọn danh mục (0 để quay lại): ");
        if(chon == 0) return;
        if(chon > danhMuc.length){
            System.out.println("Danh mục không hợp lệ.");
            return;
        }

        String mucChon = danhMuc[chon - 1];
        int soMatHang = 0;
        int tongSoLuong = 0;
        System.out.println("===== HÀNG TỒN KHO: " + mucChon.toUpperCase() + " =====");
        System.out.printf("|%-20s|%-40s|%-15s|%-20s|%-20s|%n",
                "Mã", "Tên", "Giá Nhập", "Số Lượng", "Giá Bán");
        for(int i = 0; i < soLuongMatHang; i++){
            HangTonKho htk = dsTonKho[i];
            if(htk.getMatHang() != null && mucChon.equals(htk.getMatHang().getDanhMuc())){
                htk.xuat();
                soMatHang++;
                tongSoLuong += htk.getSoLuong();
            }
        }
        if(soMatHang == 0){
            System.out.println("Kho không có mặt hàng thuộc danh mục này.");
            return;
        }
        System.out.println("Số mặt hàng: " + soMatHang);
        System.out.println("Tổng số lượng tồn: " + tongSoLuong);
    }

//tự động tăng kích thước mảng
    public void tangKichThuocMang(){
        HangTonKho[] mangMoi= new HangTonKho[dsTonKho.length *2];
        for(int i=0;i<soLuongMatHang;i++){
            mangMoi[i]=dsTonKho[i];
        }
        dsTonKho=mangMoi;
    }

//tăng tồn kho: 
    public boolean tangTonKho(MatHang mh, int soLuong){
        if(mh==null || soLuong<0) return false;
        if(soLuong>getConTrong()){
            System.out.println("Kho không đủ chỗ!");
            return false;
        }
        int vtMH = TienIch.timKiemViTri(dsTonKho, soLuongMatHang, mh.getMa());
        if(vtMH==-1){
            if(soLuongMatHang==dsTonKho.length) tangKichThuocMang();
            dsTonKho[soLuongMatHang]=new HangTonKho(mh, soLuong);
            soLuongMatHang++;
        }
        else dsTonKho[vtMH].tangSoLuong(soLuong);
        return true;
    }


//nhập hàng từ phiếu: 
    public boolean nhapHangTuPhieu(PhieuNhap pn){
        if(pn == null) return false;
        if (pn.getTrangThai() != PhieuNhap.TrangThaiPN.CHO_DUYET) {
            System.out.println("Phiếu không ở trạng thái chờ duyệt; không nhập kho lần nữa!");
            return false;
        }
        if(pn.getTongSlNhap()>getConTrong()){
            System.out.println("Kho không đủ chỗ trống!");
            return false;
        }
        for(int i=0; i<pn.getSoLuongNhap();i++){
            tangTonKho(pn.getDSMH()[i], pn.getDSMH()[i].getSoLuong());
        }
        pn.setTrangThai(PhieuNhap.TrangThaiPN.DA_DUYET);
        System.out.println("Đã tăng hàng tồn từ phiếu nhập: "+pn.getMa());
        return true;
    }

//tìm hàng trong kho theo mã
    public void timHangTon(){
        String ma = TienIch.nhapChuoi("Nhập mã cần tìm: ");
        int vtMH = TienIch.timKiemViTri(dsTonKho, soLuongMatHang, ma);
        if(vtMH==-1){
            System.out.println("Mã hàng này không tồn tại trong kho!");
            return;
        }
        System.out.printf("|%-20s|%-40s|%-15s|%-20s|%-20s|%n",
                "Mã", "Tên", "Giá Nhập", "Số Lượng", "Giá Bán");
        dsTonKho[vtMH].xuat();
    }

    private static HangTonKho taoHangTonMoi(){
        System.out.println("1. Sách  2. SGK  3. Tiểu thuyết  4. Truyện");
        System.out.println("5. Văn phòng phẩm  6. Báo tạp chí  7. Sản phẩm khác");
        System.out.println("8. Đồ chơi  9. Dụng cụ học tập  10. Trang trí  11. Vở");
        int loai = TienIch.nhapSoNguyenDuong("Nhập loại mặt hàng muốn nhập vào kho: ");
        HangTonKho htk;
        MatHang mh;
        switch(loai){
            case 1: mh=new Sach(); break;
            case 2: mh = new SachGiaoKhoa(); break;
            case 3: mh = new TieuThuyet(); break;
            case 4: mh = new Truyen(); break;
            case 5: mh = new VanPhongPham(); break;
            case 6: mh = new BaoTapChi(); break;
            case 7: mh = new SPKhac(); break;
            case 8: mh = new DoChoi(); break;
            case 9: mh = new DungCuHocTap(); break;
            case 10: mh = new TrangTri(); break;
            case 11: mh = new Vo(); break;
            default: System.out.println("Chọn kh hợp lệ"); return null;
        }
        mh.nhap();
        int sl = mh.getSoLuong();
        htk = new HangTonKho(mh,sl);
        return htk;
    }

    @Override
    public void them(){ //thêm 1 hàng vào kho
        HangTonKho htMoi = taoHangTonMoi();
        if (htMoi == null) {
            System.out.println("Tạo hàng tồn thất bại!");
            return;
        }
        if(htMoi.getMatHang()==null){
            System.out.println("Tạo hàng tồn thất bại!");
            return;
        }
        boolean checkNhapMH = tangTonKho(htMoi.getMatHang(), htMoi.getSoLuong());
        if(checkNhapMH){
            System.out.println("Đã thêm vào kho thành công! ");
        }
    }
    @Override
    public void nhap(){ //nhập nhiều hàng vào kho
        int slNhap = TienIch.nhapSoNguyenDuong("Nhập số hàng muốn nhập vào kho: ");
        for(int i=0;i<slNhap;i++){
            System.out.println("Hàng thứ "+(i+1));
            them();
        }
    }
    @Override
    public void xoa(){ //xóa 1 hàng khỏi kho
        if(soLuongMatHang==0){
            System.out.println("Kho đang trống!");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã hàng cần xóa khỏi kho: ");
        int vt = TienIch.timKiemViTri(dsTonKho, soLuongMatHang, ma);
        if(vt==-1){
            System.out.println("Không tìm thấy mã " + ma);
            return;
        }
        for(int i=vt;i<soLuongMatHang-1;i++){
            dsTonKho[i]=dsTonKho[i+1];
        }
        dsTonKho[soLuongMatHang-1]=null;
        soLuongMatHang--;
        System.out.println("Đã xóa mã " + ma + " khỏi kho.");
    }




    @Override 
    public void sua(){
        if(soLuongMatHang==0){
            System.out.println("Kho đang trống!");
            return;
        }
        String ma = TienIch.nhapChuoi("Nhập mã hàng cần sửa số lượng tồn: ");
        int vtMH = TienIch.timKiemViTri(dsTonKho, soLuongMatHang, ma);
        if(vtMH==-1){
            System.out.println("Không tìm thấy mã " + ma);
            return;
        }
        int slMoi = TienIch.nhapSoNguyenKhongAm("Nhập số lượng tồn mới: ");
        int chenh = slMoi - dsTonKho[vtMH].getSoLuong();
        if(chenh>0){
            if(chenh>getConTrong()){
                System.out.println("Kho không đủ chỗ!");
                return;
            }
            dsTonKho[vtMH].tangSoLuong(chenh);
        } else if(chenh<0){
            dsTonKho[vtMH].giamSoLuong(-chenh);
        }
        System.out.println("Đã cập nhật số lượng tồn mã " + ma);
    }
    @Override 
    public void xuat(){
        if(soLuongMatHang==0){
            System.out.println("Kho trống!");
            return;
        }
        System.out.println("=====KHO ĐANG CHỨA=====");
        System.out.printf("|%-20s|%-40s|%-15s|%-20s|%-20s|%n","Mã","Tên","Giá Nhập", "Số Lượng", "Giá Bán");
        for(int i=0;i<soLuongMatHang;i++){
            dsTonKho[i].xuat();
        }
    }

    public void ghiFile(String duongDan){
        try(PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Path.of(duongDan), StandardCharsets.UTF_8))){
            for(int i = 0; i < soLuongMatHang; i++){
                // MatHang.getSoLuong() biểu diễn số trên quầy; tồn kho nằm tại HangTonKho.
                // Không ghi nhầm số lượng trên quầy thành số tồn của kho.
                String[] cot = dsTonKho[i].getMatHang().toString().split(";", -1);
                cot[4] = Integer.toString(dsTonKho[i].getSoLuong());
                pw.println(String.join(";", cot));
            }
            System.out.println("Đã ghi " + soLuongMatHang + " dòng tồn kho vào file: " + duongDan);
        } catch(IOException e){
            System.out.println("LỖI ghi file: " + e.getMessage());
        }
    }
}

