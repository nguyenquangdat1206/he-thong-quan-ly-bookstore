package thucthe;

public class NhanVienNhapKho extends NhanVien {

    public NhanVienNhapKho() {
        super();
    }

    public NhanVienNhapKho(String ma, String ten, String sdt, String mk, String ca, double luong) {
        super(ma, ten, sdt, mk, ca, luong);
    }

    @Override
    public String getQuyen() {
        return "NHAP_KHO"; // Chỉ được thao tác với DanhSachSanPham
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Vai trò: Nhân viên Nhập kho");
    }
}