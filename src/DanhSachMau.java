// ĐÂY LÀ CODE MẪU CHO CÁC BẠN LÀM CLASS DANH SÁCH (COPY VÀ SỬA TÊN CLASS)
public class DanhSachMau implements IQuanLy {
    private Object[] ds; // Đổi Object thành MatHang, NhanVien, HoaDon... tùy người làm
    private int soLuong;

    public DanhSachMau() {
        ds = new Object[1000]; // Khởi tạo mảng to cố định
        soLuong = 0;           // Lúc đầu mảng rỗng
    }

    @Override
    public void them() {
        // TODO: Viết code nhập thông tin đối tượng mới ở đây
        // Giả sử đã tạo được đối tượng mới tên là 'obj'
        Object obj = new Object(); 
        
        if (soLuong < ds.length) {
            ds[soLuong] = obj;
            soLuong++;
            System.out.println("Thêm thành công!");
        } else {
            System.out.println("Danh sách đã đầy!");
        }
    }

    @Override
    public void xoa(String ma) {
        int viTri = -1;
        // 1. Tìm vị trí của phần tử cần xóa
        for (int i = 0; i < soLuong; i++) {
            // TODO: Viết logic so sánh mã ở đây. Giả sử tìm thấy ở vị trí i:
            // if (ds[i].getMa().equals(ma)) { viTri = i; break; }
        }

        // 2. Thuật toán dồn mảng để xóa
        if (viTri != -1) {
            for (int i = viTri; i < soLuong - 1; i++) {
                ds[i] = ds[i + 1]; // Đẩy phần tử phía sau lên trước
            }
            ds[soLuong - 1] = null; // Xóa dữ liệu rác ở cuối
            soLuong--;
            System.out.println("Xóa thành công!");
        } else {
            System.out.println("Không tìm thấy mã cần xóa!");
        }
    }
    
    @Override
    public void nhap() {
        // TODO: Viết logic vòng lặp nhập danh sách (gọi từ TienIch)
    }

    @Override
    public void xuat() {
        // TODO: Viết logic vòng lặp in danh sách ra màn hình
    }

    @Override
    public void sua(String ma) {
        // TODO: Viết logic tìm kiếm vị trí theo mã (giống hàm xóa) rồi set lại dữ liệu
    }
}
