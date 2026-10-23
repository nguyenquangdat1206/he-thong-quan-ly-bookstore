package mathang;



import kho.*;
import tienich.*;
import interfaces.IQuanLy;

public class DanhSachMatHang implements IQuanLy {
    private MatHang[] ds;
    private int soluong;
    private Kho kho;

    //constructor
    public DanhSachMatHang() {
        
        ds = new MatHang[100]; // mảng ban đầu
        soluong = 0;
    }

    public int getSoMH(){
        return soluong;
    }

        //Nhap hang(nhap tu kho)
    @Override
    public void nhap(){
        System.out.println("Danh sách mặt hàng đang nhập hàng từ kho: ");
        String maCanLay = TienIch.nhapChuoi("Nhập mã hàng cần lấy: ");
        int soLuongLay = TienIch.nhapSoNguyenDuong("Nhập số lượng cần lấy: ");
        nhapTuKho(kho, maCanLay, soLuongLay);
    }
    public void nhapTuKho(Kho kho, String ma, int soLuongLay) {
        System.out.println("Đang xử lý, xin vui lòng chờ ! ...");
        boolean found = false;
            for (int i = 0; i < kho.getSoLuongTon(); i++) {
                HangTonKho htk = kho.getDanhSachTon()[i];
                if (htk.getMatHang().getMa().equalsIgnoreCase(ma)) {
                    found = true;
                    if (soLuongLay > 0 && soLuongLay <= htk.getSoLuong()) {
                        htk.giamSoLuong(soLuongLay);
                        boolean daCoTrongDS = false;
                        for (int j = 0; j < soluong; j++) {
                            if (ds[j].getMa().equalsIgnoreCase(ma)) {
                                ds[j].setSoLuong(ds[j].getSoLuong() + soLuongLay);
                                daCoTrongDS = true;
                                break;
                            }
                        }

                        if (!daCoTrongDS) {
                            MatHang mh = htk.getMatHang();
                            mh.setSoLuong(soLuongLay);
                            if (soluong == ds.length) tangkichthuoc();
                            ds[soluong++] = mh;
                        }

                        System.out.println("Đã nhập " + soLuongLay +
                                        " sản phẩm mã " + ma);
                    } else {
                        System.out.println("Số lượng trong kho không đủ hoặc không hợp lệ!");
                    }
                    break;
                }
            }
            if (!found) {
                System.out.println("Không tìm thấy mặt hàng có mã " + ma + " trong kho.");
            }
    }

    //Nhập hàng loạt từ kho (20% mỗi loại), nào còn dưới 15 cuốn thì sẽ kh được nhập hàng loạt
    public void nhapHangLoat(Kho kho) {
        System.out.println("Nhập hàng loạt từ kho:");
            for (int i = 0; i < kho.getSoLuongTon(); i++) {
            HangTonKho htk = kho.getDanhSachTon()[i];
                if (htk.getSoLuong() < 15) {
                    System.out.println("Mặt hàng mã " + htk.getMatHang().getMa() +
                            " chỉ còn " + htk.getSoLuong() +
                            " sản phẩm, không nhập hàng loạt.");
                    continue;
                }
            int soLuongLay = (int)(htk.getSoLuong() * 0.2);
            if (soLuongLay > 0) {
                htk.giamSoLuong(soLuongLay);
                boolean daCoTrongDS = false;
                for (int j = 0; j < soluong; j++) {
                    if (ds[j].getMa().equalsIgnoreCase(htk.getMatHang().getMa())) {
                        ds[j].setSoLuong(ds[j].getSoLuong() + soLuongLay);
                        daCoTrongDS = true;
                        break;
                    }
                }
                if (!daCoTrongDS) {
                    MatHang mh = htk.getMatHang();
                    mh.setSoLuong(soLuongLay);
                    if (soluong == ds.length) tangkichthuoc();
                    ds[soluong++] = mh;
                }

                System.out.println("Đã nhập xong mặt hàng mã "
                        + htk.getMatHang().getMa()
                        + " với số lượng " + soLuongLay);
            }
        }
    }


    //Xuat hang
    @Override
    public void xuat(){
        for(int i =0; i < soluong;i++){
            System.out.println("\n--------THÔNG TIN MẶT HÀNG THỨ " +(i+1)+ "--------");
            ds[i].xuat();
        }
    }

        private void tangkichthuoc() {
            MatHang[] temp = new MatHang[ds.length *2];
            for (int i = 0; i < ds.length; i++) {
                temp[i] = ds[i];
            }
            ds = temp;
        }


    //Them Hang
    @Override
    public void them() { 
        if (soluong == ds.length) {
            tangkichthuoc();
        }
        int k;
        do {
            k = TienIch.nhapSoNguyenDuong("Nhập số lượng mặt hàng cần thêm (1-100): ");
            if (k <= 0 || k > 100) {
                System.out.println("Giá trị không hợp lệ! Vui lòng thử lại.");
            }
        } while (k <= 0 || k > 100);

        for (int i = 0; i < k; i++) {
            MatHang mh;
            boolean hopLe;
            do {
                System.out.println("\nChọn loại mặt hàng: ");
                System.out.println("1. Sách");
                System.out.println("2. Sách giáo khoa");
                System.out.println("3. Tiểu thuyết");
                System.out.println("4. Truyện");
                System.out.println("5. Văn phòng phẩm");
                System.out.println("6. Báo tạp chí");
                System.out.println("7. Sản phẩm khác");
                int loai = TienIch.nhapSoNguyenDuong("Nhập loại mặt hàng cần thêm: ");
                switch (loai) {
                    case 1 : mh = new Sach(); break;
                    case 2 : mh = new SachGiaoKhoa(); break;
                    case 3 : mh = new TieuThuyet(); break;
                    case 4 : mh = new Truyen(); break;
                    case 5 : mh = new VanPhongPham(); break;
                    case 6 : mh = new BaoTapChi(); break;
                    case 7 : mh = new SPKhac(); break;
                    default : mh = new SPKhac(); break; // mặc định
                };

                mh.nhap();
                hopLe = true;

                // kiểm tra trùng mã
                for (int j = 0; j < soluong; j++) {
                    if (mh.getMa().equalsIgnoreCase(ds[j].getMa())) {
                        System.out.println("Mã sản phẩm đã tồn tại! Vui lòng nhập lại.");
                        hopLe = false;
                        break;
                    }
                }

                // kiểm tra giá
                if (mh.getGiaNhap() <= 0) {
                    System.out.println("Giá sản phẩm phải là một giá trị dương!");
                    hopLe = false;
                }

                // kiểm tra số lượng
                if (mh.getSoLuong() <= 0) {
                    System.out.println("Số lượng sản phẩm phải là một giá trị nguyên dương!");
                    hopLe = false;
                }

            } while (!hopLe);

            ds[soluong++] = mh; // chỉ thêm khi hợp lệ
        }
    }


        //Xoa Hang
    @Override
    public void xoa(){
        String maCanXoa;
        maCanXoa=TienIch.nhapChuoi("Nhập mã cần xóa: ");
        xoa(maCanXoa);
    }
    public void xoa(String ma){
        boolean found = false;
        for(int i =0; i < soluong;i++){
            if(ds[i].getMa().equalsIgnoreCase(ma)) {
                for(int j = i; j< soluong-1;j++){
                    ds[j] = ds[j+1];
                }
                soluong--;
                found = true;
                System.out.println("Đã xóa mặt hàng có mã "+ ma);
                break;
            }
        }
        if(!found)
            System.out.println("Không tồn tại mặt hàng có mã "+ ma);
    }

    //Sua Hang
    @Override
    public void sua(){
        String ma;
        ma = TienIch.nhapChuoi("Nhập mã cần sửa");
        sua(ma);
    }
    public void sua(String ma) {
        boolean found = false;

        for (int i = 0; i < soluong; i++) {
            if (ds[i].getMa().equalsIgnoreCase(ma)) {
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
                            System.out.print("Nhập mã mới: ");
                            String maMoi = TienIch.nhapMa(ds, soluong, ma); //hàm này có ktra kh trùng
                            System.out.println("Đã chuyển đổi mã " + ds[i].getMa() + "thành " + ma);
                            ds[i].setMa(maMoi);
                            break;
                        case 2:
                            ds[i].setTen(TienIch.nhapChuoi("Nhập tên mới: "));
                            break;
                        case 3:
                            double giaMoi;
                            giaMoi = TienIch.nhapGiaTien("Nhập giá nhập mới: ");
                            ds[i].setGiaNhap(giaMoi);
                            break;
                        case 4:
                            int slMoi;
                            slMoi= TienIch.nhapSoNguyenDuong("Nhập số lượng mới: ");
                            ds[i].setSoLuong(slMoi);
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

    //timkiem
    public MatHang timKiem_hang(String ma) {
        for (int i = 0; i < soluong; i++) {
            if (ds[i].getMa().equalsIgnoreCase(ma)) {
                return ds[i]; // Trả về đối tượng mặt hàng nếu tìm thấy
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }
    
    public void timkiem(){
        String timma = TienIch.nhapChuoi("Nhập mã hàng cần tìm:");
        boolean found = false;
        for(int i = 0 ; i< soluong;i++){
            if(timma.equalsIgnoreCase(ds[i].getMa())){
                ds[i].xuat();
                found = true;
            }
        }
        if(!found){
            System.out.println("Không tồn tại mặt hàng nào có mã " + timma + " !" );
        }
    }

    //bo loc tim kiem nang cao
public void timkiem_nangcao() {

    System.out.println("\nChọn loại mặt hàng: ");
    System.out.println("1. Sách");
    System.out.println("2. Sách giáo khoa");
    System.out.println("3. Tiểu thuyết");
    System.out.println("4. Truyện");
    System.out.println("5. Văn phòng phẩm");
    System.out.println("6. Báo tạp chí");
    System.out.println("7. Sản phẩm khác");
    System.out.println("Lựa chọn khác: thoát");

    int choice = TienIch.nhapSoNguyenDuong("Nhập loại hàng muốn tìm kiếm: ");
    switch (choice) {

        case 1:
            System.out.println("Chọn danh mục tìm kiếm: ");
            System.out.println("1/ Tìm kiếm theo tên tác giả");
            System.out.println("2/ Tìm kiếm theo thể loại");
            System.out.println("3/ Tìm kiếm theo nhà xuất bản");
            System.out.println("Lựa chọn khác: thoát");
            int choice1 = TienIch.nhapSoNguyenDuong("Nhập:");
            switch (choice1) {

                case 1:
                    boolean tontai11 = false;
                    String boloc11 = TienIch.nhapChuoi("Nhập tên tác giả muốn tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i].getClass() == Sach.class) {
                            Sach s = (Sach) ds[i];
                            if (boloc11.equalsIgnoreCase(s.getTacGia())) {
                                s.xuat();
                                tontai11 = true;
                            }
                        }
                    }
                    if (!tontai11) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có tên tác giả này !");
                    }
                    break;

                case 2:
                    boolean tontai12 = false;
                    String boloc12 = TienIch.nhapChuoi("Nhập thể loại cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i].getClass() == Sach.class) {
                            Sach s = (Sach) ds[i];
                            if (boloc12.equalsIgnoreCase(s.getTheloai())) {
                                s.xuat();
                                tontai12 = true;
                            }
                        }
                    }
                    if (!tontai12) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có thể loại này !");
                    }
                    break;

                case 3:
                    boolean tontai13 = false;
                    String boloc13 = TienIch.nhapChuoi("Nhập NXB cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i].getClass() == Sach.class) {
                            Sach s = (Sach) ds[i];
                            if (boloc13.equalsIgnoreCase(s.getNhaXuatBan())) {
                                s.xuat();
                                tontai13 = true;
                            }
                        }
                    }
                    if (!tontai13) {
                        System.out.println("Không tồn tại bất kì sách nào thuộc NXB này !");
                    }
                    break;

                default: break;
            }
            break;

        case 2:
            System.out.println("Chọn danh mục tìm kiếm: ");
            System.out.println("1/ Tìm kiếm theo môn học");
            System.out.println("2/ Tìm kiếm theo NXB");
            System.out.println("Lựa chọn khác: thoát");
            int choice2 = TienIch.nhapSoNguyenDuong("Nhập: ");
            switch (choice2) {

                case 1:
                    boolean tontai21 = false;
                    String boloc21 = TienIch.nhapChuoi("Nhập môn học cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof SachGiaoKhoa) {
                            SachGiaoKhoa sgk = (SachGiaoKhoa) ds[i];
                            if (boloc21.equalsIgnoreCase(sgk.getMonhoc())) {
                                sgk.xuat();
                                tontai21 = true;
                            }
                        }
                    }
                    if (!tontai21) {
                        System.out.println("Không tồn tại bất kì SGK nào thuộc môn học này !");
                    }
                    break;

                case 2:
                    boolean tontai22 = false;
                    String boloc22 = TienIch.nhapChuoi("Nhập NXB cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof SachGiaoKhoa) {
                            SachGiaoKhoa sgk = (SachGiaoKhoa) ds[i];
                            if (boloc22.equalsIgnoreCase(sgk.getNhaXuatBan())) {
                                sgk.xuat();
                                tontai22 = true;
                            }
                        }
                    }
                    if (!tontai22) {
                        System.out.println("Không tồn tại bất kì SGK nào thuộc NXB này !");
                    }
                    break;

                default: break;
            }
            break;

        case 3:
            System.out.println("Chọn danh mục tìm kiếm: ");
            System.out.println("1/ Tìm kiếm theo tên tác giả");
            System.out.println("2/ Tìm kiếm theo thể loại");
            System.out.println("3/ Tìm kiếm theo nhà xuất bản");
            System.out.println("Lựa chọn khác: thoát");
            int choice3 = TienIch.nhapSoNguyenDuong("Nhập: ");
            switch (choice3) {

                case 1:
                    boolean tontai31 = false;
                    String boloc31 = TienIch.nhapChuoi("Nhập tên tác giả muốn tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof TieuThuyet) {
                            TieuThuyet tt = (TieuThuyet) ds[i];
                            if (boloc31.equalsIgnoreCase(tt.getTacGia())) {
                                tt.xuat();
                                tontai31 = true;
                            }
                        }
                    }
                    if (!tontai31) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có tên tác giả này !");
                    }
                    break;

                case 2:
                    boolean tontai32 = false;
                    String boloc32 = TienIch.nhapChuoi("Nhập thể loại cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof TieuThuyet) {
                            TieuThuyet tt = (TieuThuyet) ds[i];
                            if (boloc32.equalsIgnoreCase(tt.getTheloai())) {
                                tt.xuat();
                                tontai32 = true;
                            }
                        }
                    }
                    if (!tontai32) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có thể loại này !");
                    }
                    break;

                case 3:
                    boolean tontai33 = false;
                    String boloc33 = TienIch.nhapChuoi("Nhập NXB cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof TieuThuyet) {
                            TieuThuyet tt = (TieuThuyet) ds[i];
                            if (boloc33.equalsIgnoreCase(tt.getNhaXuatBan())) {
                                tt.xuat();
                                tontai33 = true;
                            }
                        }
                    }
                    if (!tontai33) {
                        System.out.println("Không tồn tại bất kì tiểu thuyết nào thuộc NXB này");
                    }
                    break;

                default: break;
            }
            break;

        case 4:
            System.out.println("Chọn danh mục tìm kiếm: ");
            System.out.println("1/ Tìm kiếm theo tên tác giả");
            System.out.println("2/ Tìm kiếm theo thể loại");
            System.out.println("3/ Tìm kiếm theo nhà xuất bản");
            System.out.println("Lựa chọn khác: thoát");
            int choice4 = TienIch.nhapSoNguyenDuong("Nhập: ");
            switch (choice4) {

                case 1:
                    boolean tontai41 = false;
                    String boloc41 = TienIch.nhapChuoi("Nhập tên tác giả cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof Truyen) {
                            Truyen t = (Truyen) ds[i];
                            if (boloc41.equalsIgnoreCase(t.getTacGia())) {
                                t.xuat();
                                tontai41 = true;
                            }
                        }
                    }
                    if (!tontai41) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có tên tác giả này !");
                    }
                    break;

                case 2:
                    boolean tontai42 = false;
                    String boloc42 = TienIch.nhapChuoi("Nhập thể loại cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof Truyen) {
                            Truyen t = (Truyen) ds[i];
                            if (boloc42.equalsIgnoreCase(t.getTheloai())) {
                                t.xuat();
                                tontai42 = true;
                            }
                        }
                    }
                    if (!tontai42) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có thể loại này !");
                    }
                    break;

                case 3:
                    boolean tontai43 = false;
                    String boloc43 = TienIch.nhapChuoi("Nhập NXB cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof Truyen) {
                            Truyen t = (Truyen) ds[i];
                            if (boloc43.equalsIgnoreCase(t.getNhaXuatBan())) {
                                t.xuat();
                                tontai43 = true;
                            }
                        }
                    }
                    if (!tontai43) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào thuộc NXB này !");
                    }
                    break;

                default: break;
            }
            break;

        case 5:
            System.out.println("Chọn danh mục tìm kiếm: ");
            System.out.println("1/ Tìm kiếm theo loại văn phòng phẩm");
            System.out.println("2/ Tìm kiếm theo nhà sản xuất");
            System.out.println("Lựa chọn khác: thoát");
            int choice5 = TienIch.nhapSoNguyenDuong("Nhập: ");
            switch (choice5) {

                case 1:
                    boolean tontai51 = false;
                    String boloc51 = TienIch.nhapChuoi("Nhập loại văn phòng phẩm cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof VanPhongPham) {
                            VanPhongPham vpp = (VanPhongPham) ds[i];
                            if (boloc51.equalsIgnoreCase(vpp.getLoaiVPP())) {
                                vpp.xuat();
                                tontai51 = true;
                            }
                        }
                    }
                    if (!tontai51) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào thuộc loại văn phòng phẩm này !");
                    }
                    break;

                case 2:
                    boolean tontai52 = false;
                    String boloc52 = TienIch.nhapChuoi("Nhập nhà sản xuất cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof VanPhongPham) {
                            VanPhongPham vpp = (VanPhongPham) ds[i];
                            if (boloc52.equalsIgnoreCase(vpp.getnhaSX())) {
                                vpp.xuat();
                                tontai52 = true;
                            }
                        }
                    }
                    if (!tontai52) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào thuộc nhà sản xuất này !");
                    }
                    break;

                default: break;
            }
            break;

        case 6:
            System.out.println("Chọn danh mục tìm kiếm: ");
            System.out.println("1/ Tìm kiếm theo thể loại");
            System.out.println("2/ Tìm kiếm theo nhà xuất bản");
            System.out.println("Lựa chọn khác: thoát");
            int choice6 = TienIch.nhapSoNguyenDuong("Nhập: ");
            switch (choice6) {

                case 1:
                    boolean tontai61 = false;
                    String boloc61 = TienIch.nhapChuoi("Nhập thể loại cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof BaoTapChi) {
                            BaoTapChi btc = (BaoTapChi) ds[i];
                            if (boloc61.equalsIgnoreCase(btc.getTheloai())) {
                                btc.xuat();
                                tontai61 = true;
                            }
                        }
                    }
                    if (!tontai61) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào có thể loại này !");
                    }
                    break;

                case 2:
                    boolean tontai62 = false;
                    String boloc62 = TienIch.nhapChuoi("Nhập NXB cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof BaoTapChi) {
                            BaoTapChi btc = (BaoTapChi) ds[i];
                            if (boloc62.equalsIgnoreCase(btc.getNhaXuatBan())) {
                                btc.xuat();
                                tontai62 = true;
                            }
                        }
                    }
                    if (!tontai62) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào thuộc NXB này !");
                    }
                    break;

                default: break;
            }
            break;

        case 7:
            System.out.println("1/ Tìm kiếm theo nhà sản xuất");
            System.out.println("Lựa chọn khác: thoát");
            int choice7 = TienIch.nhapSoNguyenDuong("Nhập: ");
            switch (choice7) {

                case 1:
                    boolean tontai71 = false;
                    String boloc71 = TienIch.nhapChuoi("Nhập nhà sản xuất cần tìm: ");
                    for (int i = 0; i < soluong; i++) {
                        if (ds[i] instanceof SPKhac) {
                            SPKhac k = (SPKhac) ds[i];
                            if (boloc71.equalsIgnoreCase(k.getnhaSX())) {
                                k.xuat();
                                tontai71 = true;
                            }
                        }
                    }
                    if (!tontai71) {
                        System.out.println("Không tồn tại bất kì mặt hàng nào thuộc nhà sản xuất này !");
                    }
                    break;

                default: break;
            }
            break;

        default:
            break;
    }
}
}
