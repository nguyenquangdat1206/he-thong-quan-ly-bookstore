package model.mathang;

import model.utils.TienIch;

public class DungCuHocTap extends MatHang {
    private String loaidungcu;
    private String nhaSX;

    public DungCuHocTap() {
        super();
        setThue(12);
        setLoinhuan(18);
        this.loaidungcu = "";
        this.nhaSX = "";
    }

    public DungCuHocTap(String ma, String ten, double giaNhap, int soLuongTon, String nhaSX, String loaidungcu) {
        super(ma, ten, giaNhap, soLuongTon, 18, 12); // loinhuan: 18, thue: 12
        this.nhaSX = nhaSX;
        this.loaidungcu = loaidungcu;
    }

    public String getNhaSX() { return nhaSX; }
    public void setNhaSX(String nhaSX) { this.nhaSX = nhaSX; }

    public String getLoaidungcu() { return loaidungcu; }
    public void setLoaidungcu(String loaidungcu) { this.loaidungcu = loaidungcu; }

    @Override
    public void nhap() {
        setMa(TienIch.nhapChuoi("Nhập mã: "));
        setTen(TienIch.nhapChuoi("Nhập tên: "));
        setGiaNhap(TienIch.nhapGiaTien("Giá nhập hàng: "));
        setSoLuongTon(TienIch.nhapSoNguyenDuong("Nhập số lượng: "));
        
        setThue(12);
        setLoinhuan(18);

        loaidungcu = TienIch.nhapChuoi("Loại dụng cụ: ");
        nhaSX = TienIch.nhapChuoi("Nhà sản xuất: ");
    }

    @Override
    public void xuat() {
        super.xuat();
        System.out.println("Loại dụng cụ: " + loaidungcu + " | Nhà sản xuất: " + nhaSX);
    }
}