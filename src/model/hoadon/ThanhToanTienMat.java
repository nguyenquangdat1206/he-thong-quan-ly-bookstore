package model.hoadon;

import model.interfaces.IThanhToan;

public class ThanhToanTienMat implements IThanhToan {
    private double tienKhachDua;

    public ThanhToanTienMat(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    public double getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    @Override
    public boolean xuLyThanhToan(double soTien) {
        if (tienKhachDua < soTien) {
            System.out.println("Số tiền khách đưa không đủ! Cần " + soTien + ", đưa " + getTienKhachDua());
            return false;
        }
        double tienThua = tienKhachDua - soTien;
        System.out.println("Thanh toán tiền mặt thành công. Tiền thối lại: " + tienThua);
        return true;
    }
}