package nhanvien;

public class NhanVienBanHang extends NhanVien {

    public NhanVienBanHang() {
        super();
    }

    public NhanVienBanHang(String ma, String ten, String sdt, String mk, String ca, double luong) {
        super(ma, ten, sdt, mk, ca, luong);
    }

    @Override
    public String getQuyen() {
        return "BAN_HANG"; // Chỉ được xem tồn kho và bán hàng
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Vai trò: Nhân viên Bán hàng");
    }
}