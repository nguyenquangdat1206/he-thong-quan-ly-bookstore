# QUẢN LÝ NHÀ SÁCH — BẢN MERGE OOP (TV1–TV5)

**Trạng thái kiểm tra bản cập nhật V2 (NCC/Phiếu nhập):** biên dịch Java thành công trên OpenJDK 21; **38/38 bài kiểm thử Java**, **14/14 phiên menu console** qua thử nghiệm tự động. Project sử dụng **mảng Java thuần**, không dùng `ArrayList`/`Collection` cho các danh sách nghiệp vụ. Các bài kiểm thử này không có nghĩa mọi đầu vào khả dĩ đã được kiểm chứng.

## 1. Mở và chạy nhanh trên Windows

1. Giải nén toàn bộ thư mục `QuanLyNhaSach_MERGED` vào vị trí tùy ý.
2. Cài **JDK 17 trở lên** và kiểm tra `javac -version` trong Command Prompt/PowerShell.
3. **Chạy bằng cách nhấp đúp `run.bat`**, hoặc trong PowerShell: `./run.bat`.
4. Để tự biên dịch và chạy bộ kiểm thử: `./test.bat`.

**IntelliJ IDEA:** `File → Open → QuanLyNhaSach_MERGED`. Nếu IDE yêu cầu chọn SDK, dùng JDK 17+; chạy lớp **`src/Main.java`** (class `Main`, không có package). Trong Run Configuration, chọn **Working directory = thư mục gốc `QuanLyNhaSach_MERGED`** để các file `.txt` được đọc đúng đường dẫn. IntelliJ sẽ nhận `src` là Sources Root nhờ cấu hình `.iml` kèm theo.

Linux/macOS: `bash scripts/run.sh` và `bash scripts/test.sh` (bộ test menu yêu cầu Python 3; chương trình Java thì không cần Python).

## 2. Tài khoản **demo** (không phải mật khẩu thật)

| Vai trò | Mã đăng nhập | Mật khẩu mẫu |
|---|---|---|
| Admin | `ADMIN` | `admin123` |
| Bán hàng | `BH01` | `bh123` |
| Nhập kho | `KHO01` | `kho123` |

Thông tin được nạp từ `nhanvien.txt`; tài khoản demo chỉ dùng để thử đồ án. **File này hiện ghi mật khẩu ở dạng văn bản để dễ đọc và chấm bài; không dùng với dữ liệu thực hoặc triển khai ngoài môi trường học tập.** Khi cần đổi tài khoản, vào phần quản lý nhân sự bằng Admin.

## 3. Phạm vi sau merge

| Phần | Nguồn ưu tiên | Trạng thái |
|---|---|---|
| `mathang` | TV2 — `final(1).rar` | Giữ nghiệp vụ lớp/mảng; thêm điểm nối TXT từ TV5 |
| `khachhang`, `hoadon`, `giohang`, `thanhtoan` | TV3 — `final(1).rar` | Giữ logic gốc; sửa lỗi xóa giỏ; kết nối menu lập/thanh toán hóa đơn |
| `kho`, `phieunhap`, `nhacungcap` | TV4 — `final(1).rar` | Giữ nghiệp vụ; vá các lỗi xử lý mảng/nhập kho xác nhận được |
| `nhanvien`, `Main` | **TV5 — từ `QuanLyCuaHangSach(4).rar`** | Dùng lớp kế thừa/phân quyền của bạn; hoàn thiện CRUD, file TXT, menu console |
| `interfaces`, `tienich` | Nền tảng chung / TV1 | Giữ nguyên từ `final`; không tạo một hệ interface xung đột |
| `tichhop/LuuTruMatHang.java` | TV5 (mới) | Cầu nối đọc/ghi `mathang.txt`, không thay nghiệp vụ TV2 |

**Không trộn hai phiên bản lớp khách hàng, hóa đơn, mặt hàng hoặc giỏ hàng.** Phiên bản bạn viết trước đây thuộc những module người khác phụ trách sẽ **không** thay thế cho phiên bản của họ. Vì `KhachHang` bản final không có mật khẩu, không chuyển phần đăng nhập khách hàng của bản nháp sang để tránh sửa thiết kế TV3; khách vãng lai có quyền xem hàng, nghiệp vụ thanh toán đi qua nhân viên bán hàng.

## 4. Quyền truy cập

- **ADMIN:** Kho, Phiếu nhập, Mặt hàng, Khách hàng, Hóa đơn, Nhà cung cấp, Nhân sự, Giỏ hàng.
- **BAN_HANG:** Xem Mặt hàng, Khách hàng, Hóa đơn, Giỏ hàng — **không** có menu chỉnh nhân sự/kho.
- **NHAP_KHO:** Kho, Phiếu nhập, Nhà cung cấp, xem Mặt hàng — **không** có menu nhân sự/hóa đơn.
- **Chưa đăng nhập:** chỉ xem mặt hàng. Sai tài khoản/mật khẩu: không mở menu có quyền.

Các vai trò đã có trong cả hai bản mã nguồn (`getQuyen()`); phần **kiểm tra quyền tại menu** đã được kết nối và kiểm thử ở bản này. Các quyền được kiểm soát trong ứng dụng console, không phải lớp bảo mật đa người dùng trên máy chủ.

## 5. Thử một quy trình bán hàng hoàn chỉnh

1. Login `ADMIN`.
2. **Kho → 8. Đọc kho từ file**: nhập `kho/kho_input.txt` (có dữ liệu mẫu).
3. **Mặt hàng → 2. Nhập hàng loạt từ kho lên quầy**: hệ thống chuyển hàng từ kho ra quầy.
4. **Khách hàng → 6. Đọc từ file**: nhập `khachhang/khachhang.txt`, có mã `KH01`.
5. **Hóa đơn → 2. Lập hóa đơn mới**: chọn `KH01`, mã hóa đơn mới `HD999`, ngày theo định dạng `dd/MM/yyyy`, mã `S001`, số lượng 2, và kết thúc nhập hàng bằng `k`.
6. **Hóa đơn → 7. Thanh toán hóa đơn**: nhập `HD999`, chọn tiền mặt, nhập số tiền đủ lớn. Mặt hàng sẽ trừ số lượng trên quầy đúng một lần.
7. **Hóa đơn → 6. Ghi ra file** để lưu; có thể sử dụng menu đọc/ghi tương ứng của các module khác.

Dữ liệu TXT là **những file khác nhau** của từng module; các module TV2–TV4 vẫn dùng menu đọc/ghi file thủ công như thiết kế ban đầu. Chạy lại chương trình không tự mở toàn bộ các file giao dịch và kho; nếu cần dữ liệu đã lưu, sử dụng menu đọc file tương ứng. File `nhanvien.txt` là ngoại lệ: được đọc khi khởi động và ghi khi thoát.

## 6. Vị trí quan trọng

- `src/Main.java`: menu tích hợp, login, phân quyền, gọi mọi module.
- `src/nhanvien/`: module nhân sự TV5, đa hình, mảng tĩnh, CRUD, lương và đọc/ghi TXT.
- `src/{mathang,khachhang,hoadon,giohang,kho,phieunhap,nhacungcap,thanhtoan}`: source của TV2–TV4.
- `src/interfaces/`: giao diện OOP của nhóm.
- `tests/IntegrationTests.java`: bộ kiểm thử nghiệp vụ không cần thư viện ngoài.
- `tests/test_console.py`: kịch bản mở ứng dụng thực bằng stdin/stdout, bao gồm một lần bán hàng hoàn chỉnh.
- `docs/BAO_CAO_MERGE.md`: báo cáo lỗi sửa, ranh giới module, lưu ý chưa kiểm chứng.
- `docs/KET_QUA_KIEM_THU.txt`: log chạy cuối cùng.

**Quan trọng:** đọc `docs/BAO_CAO_MERGE.md` trước khi đẩy lên GitHub. Không nên ghi đè repo của nhóm mà không review pull request; bản này là bản merge độc lập, không tác động trực tiếp tới remote GitHub.


## 7. Bản cập nhật V2: NCC / Phiếu nhập (từ các file nhóm gửi)

Tại **Admin → 2. Phiếu nhập** hoặc **Nhập kho → 2. Phiếu nhập**, dùng **8** để nạp dữ liệu từ `phieunhap/phieunhap_input.txt`, rồi dùng **10** để xem các nhà cung cấp đã tự tạo. Tên NCC được chuẩn hóa khi so sánh, mã tự tăng `NCC0`, `NCC1`,... không trùng mã đang tồn tại. Dùng **9** để ghi phiếu ra `phieunhap/phieunhap_output.txt` (sau đó có thể đọc lại bằng mục 8). Đọc lặp không tự thêm dòng trùng. Thêm PN trực tiếp bằng mục 2 cần nhập mã, ngày, tên NCC và mặt hàng.

**Giới hạn rất quan trọng:** Định dạng file PN của nhóm chưa có trường NCC cấp phiếu riêng; nếu nhập tay tên NCC khác với tên nhà xuất bản/nhà sản xuất, lần đọc lại sẽ suy NCC từ sản phẩm chứ không thể khôi phục tên NCC nhập tay. Một mã phiếu có thể chứa nhiều NCC trong file mẫu, nhưng cấu trúc `PhieuNhap` chỉ có **một NCC**. Phiếu giữ NCC của dòng đầu, các NCC khác vẫn thêm vào danh sách chung, kèm cảnh báo; chưa lưu được quan hệ NCC *trên mỗi dòng hàng*. Nếu giảng viên yêu cầu điều đó, TV4 cần thống nhất sửa mô hình trước khi nộp.

Chạy `test.bat` trên Windows (Python 3 cần để chạy đủ kiểm thử console) hoặc `bash scripts/test.sh`; đã có thêm `FeatureNCCPhieuNhapTests.java` và `test_console_pn.py`. Đọc **`docs/BAO_CAO_CAP_NHAT_NCC_PHIEUNHAP_V2.md`** để xem lỗi cũ, cách sửa và giới hạn, trước khi đưa code lên GitHub.
