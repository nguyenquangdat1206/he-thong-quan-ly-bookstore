package model.kho;

import java.time.LocalDate;
import model.interfaces.IGetMa;
import model.utils.TienIch;

enum TrangThaiPN {
    CHO_DUYET,
    DA_DUYET,
    DA_HUY
}

public class PhieuNhap implements IGetMa {
    private String maPN;
    private LocalDate ngayNhap;
    private String nhaCungCap;
    private TrangThaiPN trangThai;
    private ChiTietPhieuNhap[] chiTiet;
    private int soLuongCT;

    public PhieuNhap() {
        maPN = "";
        ngayNhap = LocalDate.now();
        nhaCungCap = "";
        trangThai = TrangThaiPN.CHO_DUYET;
        chiTiet = new ChiTietPhieuNhap[1000];
        soLuongCT = 0;
    }

    public PhieuNhap(String maPN, LocalDate ngayNhap, String nhaCungCap) {
        this.maPN = maPN;
        this.ngayNhap = (ngayNhap != null) ? ngayNhap : LocalDate.now();
        this.nhaCungCap = nhaCungCap;
        this.trangThai = TrangThaiPN.CHO_DUYET;
        this.chiTiet = new ChiTietPhieuNhap[1000];
        soLuongCT = 0;
    }

    @Override
    public String getMa() { return maPN; }
    public LocalDate getNgayNhap() { return ngayNhap; }
    public String getNhaCungCap() { return nhaCungCap; }
    public TrangThaiPN getTrangThai() { return trangThai; }
    public ChiTietPhieuNhap[] getChiTiet() { return chiTiet; }
    public int getSoLuongCT() { return soLuongCT; }

    public void setMaPN(String maPN) { this.maPN = maPN; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }
    public void setNhaCungCap(String nhaCungCap) { this.nhaCungCap = nhaCungCap; }
    public void setTrangThai(TrangThaiPN trangThai) { this.trangThai = trangThai; }
    public void setChiTiet(ChiTietPhieuNhap[] chiTiet) { this.chiTiet = chiTiet; }

    public void themChiTiet(ChiTietPhieuNhap ct) {
        if (ct == null) return;
        if (soLuongCT >= chiTiet.length) {
            ChiTietPhieuNhap[] mangMoi = new ChiTietPhieuNhap[chiTiet.length * 2];
            for (int i = 0; i < chiTiet.length; i++) {
                mangMoi[i] = chiTiet[i];
            }
            chiTiet = mangMoi;
        }
        chiTiet[soLuongCT] = ct;
        soLuongCT++;
    }

    public double tongTienNhap() {
        double sum = 0;
        for (int i = 0; i < soLuongCT; i++) {
            sum += chiTiet[i].giaThanh();
        }
        return sum;
    }

    // ================= CÁC HÀM NẠP RUỘT =================
    
    public void nhap() {
        this.maPN = TienIch.nhapChuoi("Nhập mã Phiếu Nhập (VD: PN01): ");
        this.nhaCungCap = TienIch.nhapChuoi("Nhập tên Nhà cung cấp: ");
        
        LocalDate ngay = TienIch.nhapNgay("Nhập ngày lập phiếu");
        if (ngay != null) {
            this.ngayNhap = ngay;
        }
        
        System.out.println(">>> Tạo khung phiếu nhập " + this.maPN + " thành công!");
    }

    public void xuat() {
        System.out.println("-------------------------------------------------------");
        System.out.println("MÃ PHIẾU: " + this.maPN + " | NGÀY NHẬP: " + TienIch.chuyenNgaySangChuoi(this.ngayNhap));
        System.out.println("NHÀ CUNG CẤP: " + this.nhaCungCap + " | TRẠNG THÁI: " + this.trangThai);
        System.out.println("CHI TIẾT " + this.soLuongCT + " MẶT HÀNG KÈM THEO:");
        
        for (int i = 0; i < soLuongCT; i++) {
            System.out.printf("  %d. %s (SL: %d, Giá nhập: %.0f đ, Thành tiền: %.0f đ)\n", 
                (i + 1), 
                chiTiet[i].getMatHang().getTen(), 
                chiTiet[i].getSoLuong(), 
                chiTiet[i].getGiaNhap(), 
                chiTiet[i].giaThanh());
        }
        System.out.printf(">>> TỔNG TIỀN PHIẾU NHẬP: %.0f VNĐ\n", tongTienNhap());
        System.out.println("-------------------------------------------------------");
    }
}