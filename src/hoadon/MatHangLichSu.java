package hoadon;

import mathang.MatHang;


final class MatHangLichSu extends MatHang {
    MatHangLichSu(String ma, double donGia) {
        super(ma, "Mặt hàng " + ma + " (không còn trong danh mục)", donGia, 0);
    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap();
    }
}
