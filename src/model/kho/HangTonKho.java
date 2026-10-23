package kho;

import interfaces.IGetMa;
import mathang.*;
import tienich.TienIch;

public class HangTonKho implements IGetMa{
    private MatHang matHang;
    private int soLuong;

//hangtonkho chứa mặt hàng và số lượng ví dụ "001","Bút",..."20"

//constructor
    public HangTonKho(){
        matHang=null;
        soLuong=0;
    }

    public HangTonKho(MatHang matHang, int soLuong){
        this.matHang=matHang;
        this.soLuong=(soLuong<0) ? 0:soLuong;
    }

//getter
    public int getSoLuong() {return soLuong;}
    public MatHang getMatHang() {return matHang;}

//lấy mã lát kiếm trong kho cho dễ (TienIch.timKiemViTri)
    @Override
    public String getMa(){
        return matHang==null ? "" : matHang.getMa();
    }

//tăng số lượng hàng tồn khi nhập kho
    public void tangSoLuong(int soLuongTang){
        soLuong+=soLuongTang;
    }

//giảm số lượng hàng tồn khi xuất kho
    public boolean giamSoLuong(int soLuongGiam){
        if(soLuongGiam<=soLuong){
            soLuong-=soLuongGiam;
            return true;
        }
        else{
            System.out.println("Không đủ hàng!");
            return false;
        }
    }


    public HangTonKho taoHangTonMoi(){
        System.out.println("1. Sách  2. SGK  3. Tiểu thuyết  4. Truyện");
        System.out.println("5. Văn phòng phẩm  6. Báo tạp chí  7. Sản phẩm khác");
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
            default: System.out.println("Chọn kh hợp lệ"); return null;
        }
        mh.nhap();
        System.out.println("Đang nhập hàng nên tự đông chỉnh số lượng trong mặt hàng về 0 ");
        mh.setSoLuong(0); //hàng mới chưa lên dsmh sl =0
        int sl = TienIch.nhapSoNguyenDuong("Nhập số lượng nhập vào kho: ");
        htk = new HangTonKho(mh,sl);
        return htk;
    }

    public void xuat(){
        String ten = (matHang==null) ? "":matHang.getTen();
        String ma = (matHang==null) ? "":matHang.getMa();
        double giaNhap = matHang.getGiaNhap();
        System.out.printf("|%-20s|%-20s|%-15s|%-20s|%-20s|%n", ma, ten, giaNhap, soLuong, matHang.tinhGiaBan());
    }
}
