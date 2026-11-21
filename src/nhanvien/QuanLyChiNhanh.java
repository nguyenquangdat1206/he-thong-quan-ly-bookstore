package nhanvien;


public class QuanLyChiNhanh extends NhanVien {

    public QuanLyChiNhanh() {
        super();
    }

    public QuanLyChiNhanh(String ma, String ten, String sdt, String mk, String ca, double luong, String maChiNhanh) {
        super(ma, ten, sdt, mk, ca, luong);
        setMaChiNhanh(maChiNhanh);
    }

    @Override
    public String getQuyen() {
        return "QUAN_LY";
    }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Vai trò: Quản lý chi nhánh");
    }
}
