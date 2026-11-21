package kho;

import interfaces.IGetMa;
import mathang.*;

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
        if (soLuongTang < 0 || (long) soLuong + soLuongTang > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Số lượng tăng không hợp lệ");
        soLuong+=soLuongTang;
    }

//giảm số lượng hàng tồn khi xuất kho
    public boolean giamSoLuong(int soLuongGiam){
        if(soLuongGiam >= 0 && soLuongGiam<=soLuong){
            soLuong-=soLuongGiam;
            return true;
        }
        else{
            System.out.println("Không đủ hàng!");
            return false;
        }
    }

    public void xuat(){
        if (matHang == null) { System.out.println("Hàng tồn chưa có mặt hàng."); return; }
        String ten = (matHang==null) ? "":matHang.getTen();
        String ma = (matHang==null) ? "":matHang.getMa();
        double giaNhap = matHang.getGiaNhap();
        System.out.printf("|%-20s|%-40s|%-15s|%-20s|%-20s|%n", ma, ten, giaNhap, soLuong, matHang.tinhGiaBan());
    }
}
