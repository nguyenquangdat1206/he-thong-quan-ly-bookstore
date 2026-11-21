package mathang;

import tienich.*;

public class Sach extends MatHang {
    private final double thue = 25;
    private final double loinhuan = 20;
    private String tacGia;
    private String nhaXuatBan;
    private String theloai;

    public Sach() {
        super();
        this.tacGia="";
        this.nhaXuatBan ="";
        this.theloai ="";
    }

    public Sach(String ma, String ten, double giaNhap, int soLuong, String tacGia, String nhaXuatBan, String theloai) {
        super(ma, ten, giaNhap, soLuong);
        this.tacGia = tacGia;
        this.nhaXuatBan = nhaXuatBan;
        this.theloai = theloai;
    }

    // Tiền tố mã của từng loại; lớp con của Sach chỉ cần override hàm này
    protected String getTienToMa() { return "SCH"; }

    @Override
    public void setMa(String ma) {
        String tienTo = getTienToMa();
        if (ma == null || !ma.matches(tienTo + "\\d{4}")) {
            throw new IllegalArgumentException(
                "Mã phải có dạng " + tienTo + "xxxx"
            );
        }
        super.setMa(ma);
    }

    public String getTheloai(){return theloai; }
    public void setTheloai(String theloai){this.theloai = theloai;}

    public String getTacGia() { return tacGia; }
    public void setTacGia(String tacGia) { this.tacGia = tacGia; }

    public String getNhaXuatBan() { return nhaXuatBan; }
    public void setNhaXuatBan(String nhaXuatBan) { this.nhaXuatBan = nhaXuatBan; }

    @Override
    public double getThue() { return thue; }
    @Override
    public double getLoiNhuan() { return getLoinhuan(); }
    public double getLoinhuan() { return loinhuan; }

    @Override
    public void nhap() {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (SCHxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        theloai = TienIch.nhapChuoi("Nhập thể loại: ");

        tacGia = TienIch.nhapChuoi("Nhập tác giả: ");

        nhaXuatBan = TienIch.nhapChuoi("Nhà xuất bản: ");

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    // nhập truyền tên nhà cung cấp từ phiếu nhập lên
    public void nhap(String tenNCC) {

        while (true) {
            try {
                setMa(TienIch.nhapChuoi("Nhập mã (SCHxxxx): "));
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        setTen(TienIch.nhapChuoi("Nhập tên: "));

        theloai = TienIch.nhapChuoi("Nhập thể loại: ");

        tacGia = TienIch.nhapChuoi("Nhập tác giả: ");

        nhaXuatBan = tenNCC;

        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));

        setSoLuong(TienIch.nhapSoNguyenKhongAm("Nhập số lượng: "));

    }

    @Override
    public void xuat(){
        System.out.println("Mã: " + getMa());

        System.out.println("Tên: " + getTen());

        System.out.println("Tác giả: " + tacGia);

        System.out.println("Thể loại: " +getTheloai());

        System.out.println("Nhà xuất bản: " + nhaXuatBan);

        System.out.println("Số lượng: " + getSoLuong());

        System.out.println("Giá bán: " + tinhGiaBan());
    }
    @Override
    public double tinhGiaBan() {
        return tinhGiaBan(loinhuan, thue);
    }

    @Override
    public String getDanhMuc(){
        return "Sách";
    }

    @Override
    public void suathuoctinhrieng(){
        System.out.println("\n Lựa chọn thuộc tính riêng");
        System.out.println("1. Tác giả");
        System.out.println("2. Nhà xuất bản");
        System.out.println("3. Thể loại");
        System.out.println("0. Quay lại");
        int choice = TienIch.nhapSoNguyenKhongAm("Nhập lựa chọn: ");
        switch (choice) {
            case 0: System.out.println("Đã thoát"); break;

            case 1: tacGia = TienIch.nhapChuoi("Nhập lại tên tác giả: "); break;

            case 2: nhaXuatBan = TienIch.nhapChuoi("Nhập lại tên NXB: "); break;

            case 3: theloai = TienIch.nhapChuoi("Nhập lại thể loại sách: "); break;

            default: System.out.println("Lựa chọn không hợp lệ.");
        }
    }

    @Override
    public String toString(){
        return super.toString()+";"+getTacGia()+";"+getNhaXuatBan()+";"+getTheloai();
    }
}
