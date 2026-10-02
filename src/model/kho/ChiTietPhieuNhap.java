package model.kho;

public class ChiTietPhieuNhap {
    private model.mathang.MatHang matHang;
    private int soLuong;
    private double giaNhap;

//constructor
    public ChiTietPhieuNhap(){
        this.matHang=null;
        this.soLuong=0;
        this.giaNhap=0;
    }

    public ChiTietPhieuNhap(model.mathang.MatHang matHang, int soluong, double gianhap){
        this.matHang=matHang;
        this.setSoLuong(soluong);
        this.setGiaNhap(gianhap);
    }
    
//getter
    public model.mathang.MatHang getMatHang(){
        return matHang;
    }
    public int getSoLuong(){
        return soLuong;
    }
    public double getGiaNhap(){
        return giaNhap;
    }
    
//setter
    public void setMatHang(model.mathang.MatHang matHang){
        this.matHang=matHang;
    }
    public void setSoLuong(int soLuong){
        this.soLuong = (soLuong<0)? 0:soLuong;
    }
    public void setGiaNhap(double giaNhap){
        this.giaNhap = (giaNhap<0)? 0:giaNhap;
    }

//tổng tiền hàng nhập
    public double giaThanh(){
        return giaNhap*soLuong;
    }

    public PhieuNhap getMatHang1() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMatHang'");
    } 
}
