package phieunhap;

import java.time.LocalDate;


public class ChiTietPhieuNhap {
    private LocalDate ngayNhap;
    private String nhaCungCap;

//constructor
    public ChiTietPhieuNhap(){
        ngayNhap =  LocalDate.now();
        nhaCungCap = "Trống";
    }

    public ChiTietPhieuNhap(LocalDate ngayNhap, String ncc){
        this.ngayNhap = ngayNhap;
        setNhaCC(ncc);
    }
 
//getter
    public LocalDate getNgayNhap(){
        return ngayNhap;
    }
    
    public String getNhaCungCap(){
        return nhaCungCap;
    }
//setter
    public void setNgayNhap(LocalDate ngayNhap){
        this.ngayNhap=ngayNhap;
    }

    public void setNhaCC(String ncc){
        this.nhaCungCap=ncc;
    }
}

