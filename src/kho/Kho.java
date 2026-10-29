package kho;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import phieunhap.*;
import mathang.*;
import interfaces.IFileIO;
import interfaces.IQuanLy;
import tienich.*;


public class Kho implements IFileIO, IQuanLy  {
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
            pn.setTrangThai(PhieuNhap.TrangThaiPN.DA_HUY);
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
        String thongtin= dsTonKho[vtMH].toString();
        System.out.println(thongtin);
    }

    @Override
    public void them(){ //thêm 1 hàng vào kho
        HangTonKho htMoi = new HangTonKho().taoHangTonMoi();
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
        int slMoi = TienIch.nhapSoNguyenDuong("Nhập số lượng tồn mới: ");
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

    @Override
    public void ghiFile(String duongDan){
        try(PrintWriter pw = new PrintWriter(new FileWriter(duongDan))){
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
 
    @Override
    public void docFile(String duongDan){
        
        try(BufferedReader br=new BufferedReader(new FileReader(duongDan))){

            String dong;
            while((dong=br.readLine()) != null){
                if(dong.trim().isEmpty()) continue;
                String[] phan= dong.split(";");
                String danhmuc=phan[0].trim();
                String ma = phan[1].trim();
                String ten= phan[2].trim();
                double gianhap= Double.parseDouble(phan[3].trim());

                int soLuong;
                try{
                    soLuong = Integer.parseInt(phan[4].trim());
                } catch(NumberFormatException e){
                    System.out.println("Dòng: " + dong + " lỗi số lượng!");
                    continue;
                }
                
                int vtMH = TienIch.timKiemViTri(dsTonKho, soLuongMatHang, ma);
                if(vtMH!=-1){
                    dsTonKho[vtMH].tangSoLuong(soLuong);
                }
                else{
                    switch(danhmuc){
                        case "Sách":
                            String tacGiaSach=phan[5].trim();
                            String nhaXuatBanSach=phan[6].trim();
                            String theLoaiSach=phan[7].trim();
                            Sach themSach = new Sach(ma, ten, gianhap, soLuong ,tacGiaSach, nhaXuatBanSach, theLoaiSach);
                            if(tangTonKho(themSach, soLuong)){
                                System.out.println("Thêm thành công Sách "+ ten);
                            }     
                            break;                       
                        case "Tiểu thuyết":
                            String tacGiaTieuThuyet=phan[5].trim();
                            String nhaXuatBanTieuThuyet=phan[6].trim();
                            String theLoaiTieuThuyet=phan[7].trim();
                            TieuThuyet themTieuThuyet = new TieuThuyet(ma, ten, gianhap, soLuong ,tacGiaTieuThuyet, nhaXuatBanTieuThuyet, theLoaiTieuThuyet);
                            if(tangTonKho(themTieuThuyet, soLuong)){
                                System.out.println("Thêm thành công Tiểu thuyết "+ ten);
                            }
                            break;
                        case "Sách giáo khoa":
                            String monHoc = phan[5].trim();
                            String nhaXuatBan= phan[6].trim();
                            SachGiaoKhoa themSGK = new SachGiaoKhoa(ma, ten, gianhap, soLuong, monHoc, nhaXuatBan);
                            if(tangTonKho(themSGK, soLuong)){
                                System.out.println("Thêm thành công SGK "+ ten);
                            }
                            break;
                        case "Truyện":
                            String tacGiaTruyen=phan[5].trim();
                            String nhaXuatBanTruyen=phan[6].trim();
                            String theLoaiTruyen=phan[7].trim(); 
                            Truyen themTruyen = new Truyen(ma, ten, gianhap, soLuong ,tacGiaTruyen, nhaXuatBanTruyen, theLoaiTruyen);
                            if(tangTonKho(themTruyen, soLuong)){
                                System.out.println("Thêm thành công Truyện "+ ten);
                            }     
                            break;   
                        case "Văn phòng phẩm":
                            String loaiVPP=phan[5].trim();
                            String nhaSXVPP=phan[6].trim();
                            VanPhongPham themVPP = new VanPhongPham(ma, ten, gianhap, soLuong, nhaSXVPP, loaiVPP);
                            if(tangTonKho(themVPP, soLuong)){
                                System.out.println("Thêm thành công Văn phòng phẩm "+ ten);
                            }
                            break;
                        case "Báo tạp chí":
                            String theLoaiBao=phan[5].trim();
                            String nhaXuatBanBao=phan[6].trim();
                            BaoTapChi themBao = new BaoTapChi(ma, ten, gianhap, soLuong, nhaXuatBanBao, theLoaiBao);
                            if(tangTonKho(themBao, soLuong)){
                                System.out.println("Thêm thành công Báo tạp chí "+ ten);
                            }
                            break;
                        default:
                            String nhaSX = phan[5].trim();
                            SPKhac themKhac = new SPKhac(ma, ten, gianhap, soLuong, nhaSX);
                            if(tangTonKho(themKhac, soLuong)){
                                System.out.println("Thêm thành công Khác "+ten);
                            }
                            break;
                    }
                }

            }
            System.out.println("ĐỌC XONG");
        } catch(IOException e){
            System.out.println("LỖI đọc file: " + e.getMessage());
        }
    }
}

