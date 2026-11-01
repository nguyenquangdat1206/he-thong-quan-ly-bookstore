package phieunhap;

import java.time.LocalDate;

import nhacungcap.NhaCungCap;


public class ChiTietPhieuNhap {
    private LocalDate ngayNhap;
    private NhaCungCap nhaCungCap;

//constructor
    public ChiTietPhieuNhap(){
        ngayNhap =  LocalDate.now();
        nhaCungCap = new NhaCungCap();
    }

    public ChiTietPhieuNhap(LocalDate ngayNhap, NhaCungCap ncc){
        this.ngayNhap = ngayNhap;
        setNhaCC(ncc);
    }
 
//getter
    public LocalDate getNgayNhap(){
        return ngayNhap;
    }
    
    public NhaCungCap getNhaCungCap(){
        return nhaCungCap;
    }
//setter
    public void setNgayNhap(LocalDate ngayNhap){
        this.ngayNhap=ngayNhap;
    }

    public void setNhaCC(NhaCungCap ncc){
        this.nhaCungCap=ncc;
    }
}

