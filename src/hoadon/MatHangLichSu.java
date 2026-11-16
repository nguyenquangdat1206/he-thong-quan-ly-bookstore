package hoadon;

import mathang.MatHang;

/** Ảnh chụp tối thiểu của mặt hàng đã rời danh mục nhưng còn trong hóa đơn cũ. */
final class MatHangLichSu extends MatHang {
    MatHangLichSu(String ma, double donGia) {
        super(ma, "Mặt hàng " + ma + " (không còn trong danh mục)", donGia, 0);
    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap();
    }
}
