package giohang;
import mathang.MatHang;
public class ChiTietGioHang {
    private MatHang matHang;
    private int soLuongMon;
    private final double donGia;

    public ChiTietGioHang(MatHang matHang, int soLuongMon){
        this.matHang = matHang;
        this.soLuongMon = soLuongMon;
        this.donGia = matHang.tinhGiaBan();
    }

    public MatHang getMatHang() {
        return matHang;
    }

    public int getSoLuongMon() {
        return soLuongMon;
    }

    public void setSoLuongMon(int soLuongMon) {
        this.soLuongMon = soLuongMon;
    }

    public double getDonGia(){
        return donGia;
    }

    public double thanhTien(){
        return soLuongMon * getDonGia();
    }
}
