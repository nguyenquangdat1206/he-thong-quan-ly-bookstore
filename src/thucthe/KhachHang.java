package thucthe;

public class KhachHang extends Nguoi {
    private int diemTichLuy;
    private String hangThanhVien;

    // Constructor rỗng (khi khách hàng mới đăng ký, mặc định hạng Đồng)
    public KhachHang() {
        super();
        this.diemTichLuy = 0;
        this.hangThanhVien = "Đồng"; // Cấp bậc khởi điểm
    }

    // Constructor đầy đủ
    public KhachHang(String maNguoi, String hoTen, String soDienThoai, String matKhau, int diemTichLuy, String hangThanhVien) {
        super(maNguoi, hoTen, soDienThoai, matKhau); // Truyền dữ liệu chung lên cho lớp Nguoi
        this.diemTichLuy = diemTichLuy;
        this.hangThanhVien = hangThanhVien;
    }

    public int getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(int diemTichLuy) { this.diemTichLuy = diemTichLuy; }

    public String getHangThanhVien() { return hangThanhVien; }
    public void setHangThanhVien(String hangThanhVien) { this.hangThanhVien = hangThanhVien; }

    // Hàm cập nhật hạng thành viên (Sẽ gọi khi khách mua hàng xong)
    public void capNhatHangThanhVien() {
        if (diemTichLuy >= 1000) {
            this.hangThanhVien = "Kim Cương";
        } else if (diemTichLuy >= 500) {
            this.hangThanhVien = "Vàng";
        } else if (diemTichLuy >= 200) {
            this.hangThanhVien = "Bạc";
        } else {
            this.hangThanhVien = "Đồng";
        }
    }

    // Ghi đè hàm in thông tin
    @Override
    public void xuatThongTin() {
        super.xuatThongTin(); // In Mã, Tên, SĐT từ lớp Nguoi
        System.out.printf("Điểm: %-6d | Hạng: %s\n", diemTichLuy, hangThanhVien);
    }
}