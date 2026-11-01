package phieunhap;

import java.time.LocalDate;
import interfaces.*;
import mathang.*;
import nhacungcap.DanhSachNCC;
import nhacungcap.NhaCungCap;
import tienich.TienIch;


public class PhieuNhap implements IGetMa, IQuanLy{
    private String maPN;
    private MatHang[] dsMH;
    private TrangThaiPN trangThai;
    private ChiTietPhieuNhap chiTiet;
    private int soLuongNhap;    
    
    public enum TrangThaiPN{
    CHO_DUYET,
    DA_DUYET,
    DA_HUY
    }
    
//constructor tạo nhanh
    public PhieuNhap(){
        maPN="trống";
        dsMH = new MatHang[1000];
        trangThai=TrangThaiPN.CHO_DUYET;
        chiTiet=new ChiTietPhieuNhap(LocalDate.now(), new NhaCungCap());
        soLuongNhap=0;
    }
    public PhieuNhap(String ma, MatHang[] mh, TrangThaiPN tt, ChiTietPhieuNhap ct, int sl){
        maPN=ma;
        this.dsMH=mh;
        trangThai=tt;
        chiTiet=ct;
        soLuongNhap=sl;
    }

//getter
    @Override 
    public String getMa(){
        return maPN;
    }

    public TrangThaiPN getTrangThai() {return trangThai;}

    public ChiTietPhieuNhap getChiTiet() {return chiTiet;}

    public int getSoLuongNhap() {return soLuongNhap;}

    public MatHang[] getDSMH(){
        return dsMH;
    }

    public int getTongSlNhap() {
        int sum=0;
        for(int i=0;i<soLuongNhap;i++){
            sum+=dsMH[i].getSoLuong();
        }
        return sum;
    }


//setter
    public void setMaPN(String maPN) {this.maPN=maPN;}

    public void setTrangThai(TrangThaiPN trangThai) {this.trangThai=trangThai;}

    public void setChiTiet(ChiTietPhieuNhap chiTiet) {this.chiTiet=chiTiet;}


    public MatHang nhapMH(){
        System.out.println("1. Sách  2. SGK  3. Tiểu thuyết  4. Truyện");
        System.out.println("5. Văn phòng phẩm  6. Báo tạp chí  7. Sản phẩm khác");
        System.out.println("8. Đồ chơi  9. Dụng cụ học tập  10. Trang trí  11. Vở");
        int loai = TienIch.nhapSoNguyenDuong("Nhập loại mặt hàng ghi vào phiếu nhập: ");
        MatHang mhMoi;
        switch(loai){
            case 1: mhMoi=new Sach(); break;
            case 2: mhMoi = new SachGiaoKhoa(); break;
            case 3: mhMoi = new TieuThuyet(); break;
            case 4: mhMoi = new Truyen(); break;
            case 5: mhMoi = new VanPhongPham(); break;
            case 6: mhMoi = new BaoTapChi(); break;
            case 7: mhMoi = new SPKhac(); break;
            case 8: mhMoi = new DoChoi(); break;
            case 9: mhMoi = new DungCuHocTap(); break;
            case 10: mhMoi = new TrangTri(); break;
            case 11: mhMoi = new Vo(); break;
            default: System.out.println("Chọn kh hợp lệ"); return null;
        }
        mhMoi.nhap();
        return mhMoi;
    }
    @Override 
    public void them(){
        MatHang mhMoi = nhapMH();
        if (mhMoi == null) return;
        if(soLuongNhap==dsMH.length){
            MatHang[] mangmoi = new MatHang[dsMH.length*2];
            for(int i=0;i<soLuongNhap;i++){
            mangmoi[i]=dsMH[i];
            }
        dsMH=mangmoi;
        }
        dsMH[soLuongNhap]=mhMoi;
        soLuongNhap++;
    }

    public void them(MatHang mh){
        if(soLuongNhap==dsMH.length){
            MatHang[] mangmoi = new MatHang[dsMH.length*2];
            for(int i=0;i<soLuongNhap;i++){
            mangmoi[i]=dsMH[i];
            }
        dsMH=mangmoi;
        }
        dsMH[soLuongNhap]=mh;
        soLuongNhap++;
    }

    @Override
    public void nhap(){
        int sl = TienIch.nhapSoNguyenDuong("Nhập số lượng mặt hàng ghi lên phiếu nhập: ");
        for(int i=0;i<sl;i++){
            System.out.println("-----ĐANG NHẬP MẶT HÀNG THỨ "+(i+1));
            them();
        }
    }


    @Override 
    public void xoa(){
        String ma=TienIch.nhapChuoi("Nhập mã mặt hàng xóa khỏi phiếu: ");
        int vtMH=TienIch.timKiemViTri(dsMH, soLuongNhap, ma);
        if(vtMH==-1){
            System.out.println("Không tìm thấy mặt hàng!");
            return;
        }
        for(int i=vtMH;i<soLuongNhap-1;i++){
            dsMH[i]=dsMH[i+1];
        }
        dsMH[soLuongNhap-1]=null;
        soLuongNhap--;
    }

    @Override 
    public void sua(){
        String ma=TienIch.nhapChuoi("Nhập mã mặt hàng cần sửa thông tin: ");
        boolean found = false;

        for (int i = 0; i < soLuongNhap; i++) {
            if (dsMH[i].getMa().equalsIgnoreCase(ma)) {
                found = true;
                int choice;
                do {
                    System.out.println("Lựa chọn thông tin cần sửa của mặt hàng: ");
                    System.out.println("1. Mã mặt hàng");
                    System.out.println("2. Tên mặt hàng");
                    System.out.println("3. Giá nhập mặt hàng");
                    System.out.println("4. Số lượng mặt hàng");
                    System.out.println("0. Thoát chức năng");

                    choice = TienIch.nhapSoNguyenDuong("Nhập: ");

                    switch (choice) {
                        case 1:
                            String maMoi = TienIch.nhapMa(dsMH, soLuongNhap, "Nhập mã mới: "); //hàm này có ktra kh trùng
                            System.out.println("Đã chuyển đổi mã " + dsMH[i].getMa() + "thành " + maMoi);
                            dsMH[i].setMa(maMoi);
                            break;
                        case 2:
                            dsMH[i].setTen(TienIch.nhapChuoi("Nhập tên mới: "));
                            break;
                        case 3:
                            double giaMoi;
                            giaMoi = TienIch.nhapGiaTien("Nhập giá nhập mới: ");
                            dsMH[i].setGiaNhap(giaMoi);
                            break;
                        case 4:
                            int slMoi;
                            slMoi= TienIch.nhapSoNguyenDuong("Nhập số lượng mới: ");
                            dsMH[i].setSoLuong(slMoi);
                            break;
                        case 0:
                            System.out.println("Thoát sửa mặt hàng.");
                            break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ.");
                    }
                } while (choice != 0);

                System.out.println("Đã sửa thông tin mặt hàng thành công!");
                break;
            }
        }

        if (!found) {
            System.out.println("Không tồn tại mặt hàng có mã " + ma);
        }
    }

    @Override 
    public void xuat(){        
        for(int i =0; i < soLuongNhap;i++){
            System.out.println("\n--------THÔNG TIN MẶT HÀNG THỨ " +(i+1)+ "--------");
            dsMH[i].xuat();
        }
    }

    /** Tự sinh mã NCC và tái sử dụng NCC cùng tên; không tạo bản ghi trùng. */
    public NhaCungCap taoNCC(String tenNCC, DanhSachNCC dsncc) {
        return dsncc.timHoacTaoTheoTen(tenNCC);
    }

    public void suaNCC(DanhSachNCC dsncc){
        dsncc.sua();
    }
}
