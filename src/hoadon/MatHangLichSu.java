package hoadon;

import mathang.MatHang;

/** Ảnh chụp tối thiểu của mặt hàng đã rời danh mục nhưng còn trong hóa đơn cũ. */
public final class MatHangLichSu extends MatHang {
    public MatHangLichSu(String ma, double donGia) {
        this(ma, "Mặt hàng " + ma + " (không còn trong danh mục)", donGia);
    }
    public MatHangLichSu(String ma, String ten, double donGia) {
        super(ma, ten, donGia, 0);
    }

    @Override
    public double tinhGiaBan() {
        return getGiaNhap();
    }
}
