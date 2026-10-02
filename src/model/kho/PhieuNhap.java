package model.kho;

import java.time.LocalDate;
import model.interfaces.IGetMa;

enum TrangThaiPN{
    CHO_DUYET,
    DA_DUYET,
    DA_HUY
}
public class PhieuNhap implements IGetMa{
    private String maPN;
    private LocalDate ngayNhap;
    private String nhaCungCap;
    private TrangThaiPN trangThai;
    private ChiTietPhieuNhap[] chiTiet;
    private int soLuongCT;
//constructor tạo nhanh
    public PhieuNhap(){
        maPN="";
        ngayNhap=LocalDate.now();
        nhaCungCap="";
        trangThai=TrangThaiPN.CHO_DUYET;
        chiTiet=new ChiTietPhieuNhap[1000];
        soLuongCT=0;
    }
//constructor tạo đủ tham số
    public PhieuNhap(String maPN, LocalDate ngayNhap, String nhaCungCap){
        this.maPN = maPN;
        this.ngayNhap = (ngayNhap!=null) ? ngayNhap:LocalDate.now();
        this.nhaCungCap = nhaCungCap;
        this.trangThai = TrangThaiPN.CHO_DUYET;
        this.chiTiet = new ChiTietPhieuNhap[1000];
        soLuongCT = 0;
    }
//getter
    @Override 
    public String getMa() {return maPN;}
    public LocalDate getNgayNhap() {return ngayNhap;}
    public String getNhaCungCap() {return nhaCungCap;}
    public TrangThaiPN getTrangThai() {return trangThai;}
    public ChiTietPhieuNhap[] getChiTiet() {return chiTiet;}
    public int getSoLuongCT() {return soLuongCT;}
//setter
    public void setMaPN(String maPN) {this.maPN=maPN;}
    public void setNgayNhap(LocalDate ngayNhap) {this.ngayNhap=ngayNhap;}
    public void setNhaCungCap(String nhaCungCap) {this.nhaCungCap=nhaCungCap;}
    public void setTrangThai(TrangThaiPN trangThai) {this.trangThai=trangThai;}
    public void setChiTiet(ChiTietPhieuNhap[] chiTiet) {this.chiTiet=chiTiet;}
//thêm chi tiết
    public void themChiTiet(ChiTietPhieuNhap ct){
        if(ct==null) return;
        if(soLuongCT>=chiTiet.length){
            ChiTietPhieuNhap[] mangMoi = new ChiTietPhieuNhap[chiTiet.length*2];
            for(int i=0;i<chiTiet.length;i++){
                mangMoi[i]=chiTiet[i];
            }
            chiTiet=mangMoi;
            System.out.println("Đã tự động tăng sức chứa mảng lên: "+ chiTiet.length);
        }
        chiTiet[soLuongCT]=ct;
        soLuongCT++;
    }
//tổng tiền nhập
    public double tongTienNhap(){
        double sum=0;
        for(int i=0;i<soLuongCT;i++){
            sum+=(chiTiet[i].giaThanh());
        }
        return sum;
    }
}
