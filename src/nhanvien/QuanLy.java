package nhanvien;

public class QuanLy extends NhanVien {

    public QuanLy() {
        super();
    }

    public QuanLy(String ma, String ten, String sdt, String mk, String ca, double luong) {
        super(ma, ten, sdt, mk, ca, luong);
    }

    @Override
    public String getQuyen() {
        return "ADMIN"; // Toàn quyền hệ thống, truy cập mọi chi nhánh
    }

    @Override
    public boolean thuocChiNhanh() { return false; }

    @Override
    public void xuatThongTin() {
        super.xuatThongTin();
        System.out.println("Vai trò: Admin (mọi chi nhánh)");
    }
}