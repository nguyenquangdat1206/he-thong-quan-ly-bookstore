# Đối chiếu 42 class gốc và V5

Nguồn: ZIP `OOP_SRC_MODEL_GOC.zip` do thành viên TV5 xuất bằng `git archive HEAD src/model`.
V5 chỉ biên dịch một bản của mỗi package/class từ `src/<package>`; không tiếp tục giữ thư mục `src/model` trong `src`.

**Đã phục hồi nguyên văn:** `interfaces/IThaoTacThucThe.java`, `mathang/DoChoi.java`, `mathang/DungCuHocTap.java`, `mathang/TrangTri.java`, `mathang/Vo.java`.
Bốn loại hàng đã được kết nối lại với menu, kho, phiếu nhập và bộ lưu trữ mặt hàng.
**Những class thay đổi so với Git HEAD:** chỉ được kiểm thử theo phạm vi test và nên được TV1–TV4 review nghiệp vụ trước khi merge; không thể cam kết mọi nhánh nghiệp vụ bằng kiểm thử tự động.

| Java source relative to `src/model` | Đánh giá | Gốc (bytes) | V5 (bytes) |
|---|---|---:|---:|
| `hoadon/ChiTietHoaDon.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 1159 | 1337 |
| `hoadon/DanhSachHoaDon.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 10025 | 12368 |
| `hoadon/HoaDon.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 6605 | 7004 |
| `interfaces/IFileIO.java` | Trùng byte với file gốc | 126 | 126 |
| `interfaces/IGetMa.java` | Trùng byte với file gốc | 72 | 72 |
| `interfaces/IQuanLy.java` | Trùng byte với file gốc | 140 | 140 |
| `interfaces/IThaoTacThucThe.java` | Trùng byte với file gốc | 258 | 258 |
| `khachhang/DanhSachKhachHang.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 6452 | 7141 |
| `khachhang/KhachHang.java` | Trùng byte với file gốc | 1405 | 1405 |
| `kho/HangTonKho.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 2750 | 2958 |
| `kho/Kho.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 12176 | 13918 |
| `kho/QuanLyKho.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 2190 | 2200 |
| `mathang/BaoTapChi.java` | Trùng byte với file gốc | 2017 | 2017 |
| `mathang/DanhSachMatHang.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 28519 | 30260 |
| `mathang/DoChoi.java` | Trùng byte với file gốc | 1875 | 1875 |
| `mathang/DungCuHocTap.java` | Trùng byte với file gốc | 2041 | 2041 |
| `mathang/MatHang.java` | Trùng byte với file gốc | 2245 | 2245 |
| `mathang/QuanLyMatHang.java` | Trùng byte với file gốc | 2068 | 2068 |
| `mathang/SPKhac.java` | Trùng byte với file gốc | 1556 | 1556 |
| `mathang/Sach.java` | Trùng byte với file gốc | 2387 | 2387 |
| `mathang/SachGiaoKhoa.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 2124 | 2106 |
| `mathang/TieuThuyet.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 2424 | 1790 |
| `mathang/TrangTri.java` | Trùng byte với file gốc | 1592 | 1592 |
| `mathang/Truyen.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 2444 | 1833 |
| `mathang/VanPhongPham.java` | Trùng byte với file gốc | 1977 | 1977 |
| `mathang/Vo.java` | Trùng byte với file gốc | 2015 | 2015 |
| `nhacungcap/DanhSachNCC.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 5885 | 8307 |
| `nhacungcap/NhaCungCap.java` | Trùng byte với file gốc | 1517 | 1517 |
| `nhanvien/DanhSachNhanVien.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 6228 | 10831 |
| `nhanvien/Nguoi.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 1307 | 1270 |
| `nhanvien/NhanVien.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 1064 | 1049 |
| `nhanvien/NhanVienBanHang.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 574 | 548 |
| `nhanvien/NhanVienNhapKho.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 575 | 551 |
| `nhanvien/QuanLy.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 522 | 498 |
| `phieunhap/ChiTietPhieuNhap.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 753 | 809 |
| `phieunhap/DanhSachPhieuNhap.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 11707 | 16488 |
| `phieunhap/PhieuNhap.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 6745 | 7232 |
| `phieunhap/QuanLyPhieuNhap.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 2193 | 2218 |
| `thanhtoan/IThanhToan.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 97 | 97 |
| `thanhtoan/ThanhToanThe.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 856 | 1295 |
| `thanhtoan/ThanhToanTienMat.java` | Giữ phiên bản tích hợp; cần review nghiệp vụ | 955 | 935 |
| `tienich/TienIch.java` | Trùng byte với file gốc | 3784 | 3784 |
