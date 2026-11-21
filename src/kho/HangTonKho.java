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

    public void xuat(){
        String ten = (matHang==null) ? "":matHang.getTen();
        String ma = (matHang==null) ? "":matHang.getMa();
        double giaNhap = matHang.getGiaNhap();
        System.out.printf("|%-20s|%-40s|%-15s|%-20s|%-20s|%n", ma, ten, giaNhap, soLuong, matHang.tinhGiaBan());
    }
}
