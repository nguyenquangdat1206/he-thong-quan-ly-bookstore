package thucthe;

public class LichSuHoatDong {
    private String thoiGian;
    private String tenTaiKhoan; // Ai là người thực hiện?
    private String noiDungHanhDong; // Đã làm gì? (VD: Đăng nhập, Thêm sản phẩm)

    public LichSuHoatDong() {}

    public LichSuHoatDong(String thoiGian, String tenTaiKhoan, String noiDungHanhDong) {
        this.thoiGian = thoiGian;
        this.tenTaiKhoan = tenTaiKhoan;
        this.noiDungHanhDong = noiDungHanhDong;
    }

    public void xuatLog() {
        System.out.printf("[%s] - Tài khoản: %-15s - Hành động: %s\n",
                thoiGian, tenTaiKhoan, noiDungHanhDong);
    }

    // Getters để hỗ trợ ghi file sau này
    public String getThoiGian() { return thoiGian; }
    public String getTenTaiKhoan() { return tenTaiKhoan; }
    public String getNoiDungHanhDong() { return noiDungHanhDong; }
}