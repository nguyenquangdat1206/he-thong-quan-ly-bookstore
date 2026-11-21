package interfaces;

/** Các thao tác công khai trên danh sách hàng của một chi nhánh. */
public interface IQuanLyMatHang {

    void nhap();

    void xuat();

    void xoa(String ma);

    void timKiem(String ma);

    void sua(String ma);
}
