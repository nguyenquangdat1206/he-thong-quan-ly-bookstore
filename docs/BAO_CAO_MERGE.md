# BÁO CÁO GHÉP CODE VÀ KIỂM THỬ — ĐỒ ÁN OOP NHÀ SÁCH

## A. Căn cứ & quyết định merge

Nguồn: **`Phân công công việc (1)(3).docx`**, `QuanLyCuaHangSach(4).rar` của TV5, và `final(1).rar` của TV2–TV4. Hai ảnh GitHub dùng để đối chiếu bối cảnh nhánh `main`, `sub-main`, `feature/nha-cung-cap`; **không tải trực tiếp code từ GitHub** vì dữ liệu người dùng yêu cầu lấy từ hai archive đính kèm.

- TV1: kiến trúc, interface, tiện ích; nhóm trưởng dự kiến thêm phân quyền. Giữ `interfaces/IQuanLy.java`, `IFileIO`, `IGetMa` và `tienich/TienIch.java` từ final, cùng interface thanh toán của final.
- TV2: toàn bộ class `mathang`. Dùng final; không dùng `thucthe/SanPham` và `danhsach/DanhSachSanPham` từ bản nháp của TV5.
- TV3: `khachhang`, `hoadon`, `giohang`, `thanhtoan`. Dùng final; không trộn mô hình hóa đơn/khách hàng/giỏ hàng nháp của TV5.
- TV4: `kho`, `phieunhap`, `nhacungcap`. Dùng final, chỉ sửa lỗi chặn thao tác sai và chia sẻ nhà cung cấp khi tích hợp.
- **TV5:** `Nguoi`, `NhanVien`, `QuanLy`, `NhanVienBanHang`, `NhanVienNhapKho` từ bản nguồn TV5 (đổi package `thucthe` sang `nhanvien` theo final); `DanhSachNhanVien` được hoàn thiện từ bản TV5; `Main` được tái thiết kế để gọi module final và tái sử dụng cách phân vai console mà TV5 đã làm.

**Đối chiếu nguồn final:** 40 file `.java` ban đầu; 29 file được giữ nguyên về nội dung, 11 file được điều chỉnh (gồm 6 file nhân sự/Main thuộc TV5 và các bản sửa lỗi tích hợp ở TV2–TV4), bổ sung một file Java độc lập `tichhop/LuuTruMatHang.java`. Phần lớn nghiệp vụ gốc TV2–TV4 không thay đổi.

## B. Kiểm tra phân quyền

**Đã có ở cả hai nguồn:** kế thừa nhân viên, `getQuyen()` với `ADMIN`, `BAN_HANG`, `NHAP_KHO`, `DanhSachNhanVien.dangNhap()`. **Còn thiếu ở final:** menu Main cũ cho phép vào các phần quản lý mà không xác thực. Bản merge cung cấp luồng login, từ chối mật khẩu sai, menu 3 vai trò, hạn chế chức năng, chỉ Admin thao tác nhân sự, và tránh xóa tài khoản Admin cuối cùng. Không cần tạo bản phân quyền thứ hai gây xung đột với công việc TV1.

## C. Lỗi phát hiện và đã khắc phục

| # | Nơi có lỗi | Triệu chứng trước khi sửa | Thay đổi tối thiểu |
|---|---|---|---|
| 1 | `giohang/GioHang.java` | Xóa mặt hàng cuối không giảm đếm; xóa giữa có thể làm mất hơn một mục | Dồn mảng bằng vòng lặp, trừ biến đếm đúng một lần |
| 2 | `kho/Kho.java` | `them()` bỏ qua đối tượng mà `taoHangTonMoi()` trả về, dẫn đến không thêm được vào kho | Nhận đối tượng trả về và kiểm tra `null` |
| 3 | `kho/Kho.java` | `xoa()` dừng sớm trong vòng lặp, dồn thiếu mảng và báo “không tìm thấy” dù mã tồn tại | Bỏ vòng lặp trả về sớm, dồn mảng một lần |
| 4 | `phieunhap/PhieuNhap.java` | `xoa()` dùng chỉ số cố định khi dồn mảng | Dùng chỉ số biến lặp đúng |
| 5 | `phieunhap/DanhSachPhieuNhap.java` | Menu sửa phiếu dùng một lựa chọn cũ trong vòng lặp; `switch` thiếu `break` khiến trạng thái rơi qua nhánh tiếp theo | Cho phép nhập lại lựa chọn, bổ sung `break` |
| 6 | `kho/Kho.java`, `phieunhap/DanhSachPhieuNhap.java` | Có thể duyệt cùng phiếu nhập nhiều lần, cộng kho trùng | Chỉ xử lý phiếu `CHO_DUYET`, trả trạng thái đúng và không in thành công khi thất bại |
| 7 | `kho/Kho.java` | Ghi file kho lấy `MatHang.soLuong` (lượng trên quầy) thay vì `HangTonKho.soLuong` (lượng trong kho) | Ghi số lượng tồn kho thực vào cột thứ 5 |
| 8 | `phieunhap/DanhSachPhieuNhap.java` & `Main` | Phiếu nhập và nhà cung cấp dùng hai danh sách riêng biệt | Chia sẻ cùng đối tượng `DanhSachNCC` tại Main |
| 9 | `Main.java` từ final | Chưa có phân quyền console và nhân sự TV5 | Ghép đăng nhập/menu theo vai trò; nối đủ Nhà cung cấp, Nhân sự, Giỏ hàng, thanh toán hóa đơn |
| 10 | `nhanvien/DanhSachNhanVien.java` | Thiếu `nhap()` hợp lệ và đọc/ghi TXT; thêm mã trùng, lương âm chưa được kiểm soát | CRUD hoàn chỉnh bằng mảng, nhập sửa, đọc/ghi UTF-8, bỏ dòng file lỗi, kiểm tra mã trùng, tính tổng lương |
| 11 | Mặt hàng và console | Chưa có lối đọc/ghi `mathang.txt` tiện dùng | Thêm adapter `tichhop/LuuTruMatHang.java` và hai mục menu; không thay thế các hàm nghiệp vụ TV2 |

## D. Kết quả terminal thực tế

- JDK kiểm tra: **OpenJDK 21**. Lệnh: `javac -encoding UTF-8 -d build $(find src tests -name '*.java')` (Linux); Java process `Main` chạy thành công.
- **16 PASS / 0 FAIL** đối với nghiệp vụ Java: CRUD, lương, quyền, trùng mã, đọc/ghi UTF-8, kiểm tra số kho, chuyển hàng, 7 loại sản phẩm, thanh toán thành công/thất bại, xóa giỏ, ngăn trừ kho hai lần, đọc/ghi hóa đơn và NCC.
- **7 PASS / 0 FAIL** đối với các phiên console thực: khách xem hàng, đăng nhập sai, Admin xem nhân viên/lương, phân menu bán hàng, phân menu kho, nhập menu sai, bán hàng từ kho tới hóa đơn thanh toán.
- Lệnh chạy lại: `test.bat` trên Windows hoặc `bash scripts/test.sh` trên Linux/macOS; file log cùng thư mục báo cáo.
- Các ca kiểm thử chạy trên phiên bản đã sửa, không phải một chứng nhận rằng tất cả dữ liệu bên ngoài hay toàn bộ thao tác thủ công đều sẽ không có lỗi.

## E. Những điểm cần lưu ý / chưa khẳng định hoàn thiện tuyệt đối

1. **Yêu cầu “đủ 9 hàm” TV2** theo tài liệu phân công: final có `them`, `sua`, `xoa`, `timkiem`, `timkiem_nangcao`, `nhapTuKho` và đa hình, nhưng chưa có đầy đủ các hàm tên/ý nghĩa tách biệt như `nhapDanhSach(int n)`, `thongKe()`, `nghiepVuKhac()` mà tài liệu nêu. Để giữ nguyên phạm vi các thành viên khác, bản merge **không viết lại** nghiệp vụ này thay TV2. Đề nghị TV2 rà soát trước ngày nộp.
2. Bộ file `.txt` của TV2–TV4 vận hành theo thao tác đọc/ghi **thủ công**, không tự phục hồi tất cả dữ liệu khi mở ứng dụng. `nhanvien.txt` là dữ liệu tự tải/tự lưu trong Main. Nếu thoát mà chưa ghi file hóa đơn/kho thì dữ liệu mới của module đó chưa được lưu.
3. **Mật khẩu trong `nhanvien.txt` dạng clear text** vì đây là đồ án console demo, chưa có hash, phân quyền dịch vụ, xử lý đa luồng hoặc bảo mật dữ liệu thực. Không đưa mật khẩu thật vào đây. Tài khoản mẫu chỉ để trình diễn.
4. Dữ liệu mặt hàng có liên hệ giữa mảng kho và mảng quầy; nên thao tác nhập kho/chuyển lên quầy theo đúng quy trình, tránh đọc trùng nhiều lần từ file cũ. Test thường gặp đã được sửa nhưng chưa có cơ chế transaction/rollback cơ sở dữ liệu.
5. Các file dữ liệu malformed phức tạp, nhập liên tục qua nhiều vòng trong tất cả lớp TV2–TV4, đóng chương trình đột ngột và các tình huống đa người dùng chưa được kiểm thử toàn diện. Không tuyên bố chương trình tuyệt đối không còn lỗi.
6. **Nhánh GitHub chưa được push/cherry-pick**: đây là file ZIP độc lập từ các archive đính kèm, không làm thay đổi remote, nhánh hay commit của các thành viên khác.

## F. Kết luận bàn giao

Project biên dịch và chạy tốt trong môi trường kiểm thử; TV5 có đầy đủ cây kế thừa, danh sách nhân viên mảng tĩnh, CRUD, lương cơ bản, đọc/ghi `nhanvien.txt`, console ANSI, phân vai và cầu nối tới các module của TV2–TV4. Các lỗi đã sửa được ghi rõ để nhóm trưởng review. **Đề nghị merge vào GitHub qua pull request sau khi TV2–TV4 duyệt các thay đổi lỗi trong phần của họ.**
