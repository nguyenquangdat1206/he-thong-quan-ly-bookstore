package giohang;
import mathang.MatHang;
public class ChiTietGioHang {
    private MatHang matHang;
    private int soLuongMon;

    public ChiTietGioHang(MatHang matHang, int soLuongMon){
        this.matHang = matHang;
        this.soLuongMon = soLuongMon;
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
        return matHang.tinhGiaBan();
    }

    public double thanhTien(){
        return soLuongMon * getDonGia();
    }
}
