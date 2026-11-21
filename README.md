# Hệ thống quản lý nhà sách

Ứng dụng console bằng **Java 17+** dành cho nhà sách có nhiều chi nhánh. Chương trình dùng lớp Java và mảng để quản lý dữ liệu trong bộ nhớ, đọc/ghi TXT UTF-8; không cần Maven, Gradle, cơ sở dữ liệu hay thư viện ngoài JDK.


## Yêu cầu và cách chạy

- **JDK 17 trở lên**, có cả `java` và `javac` trên `PATH`.
- **Python 3** cần để chạy đầy đủ bộ kiểm thử (gồm các test console) hoặc dùng công cụ kiểm tra dữ liệu; chạy ứng dụng chỉ cần JDK.
- Chạy từ thư mục gốc chứa [src/](./src), [data/](./data) và [run.bat](./run.bat), vì đường dẫn dữ liệu là tương đối.

### Windows

```powershell
.\run.bat
```

[run.bat](./run.bat) cấu hình terminal UTF-8, biên dịch mã nguồn vào thư mục `build`, rồi chạy lớp `Main`.

### Linux/macOS

```bash
bash scripts/run.sh
```

[scripts/run.sh](./scripts/run.sh) cần Bash hỗ trợ `mapfile`, chẳng hạn Bash 4+. Trên macOS cần dùng Bash mới hơn bản 3.2 có sẵn.

### IDE

Mở thư mục repository bằng IntelliJ IDEA hoặc VS Code, chọn JDK 17+, đặt [src/](./src) làm source root và chạy lớp `Main` trong [Main.java](./src/Main.java). Working directory phải là thư mục gốc dự án. Cấu hình IntelliJ và tệp biên dịch đi kèm ZIP được giữ cục bộ; các script luôn biên dịch lại từ mã nguồn.

## Khởi động và đăng nhập

Chương trình tự đọc [data/chinhanh.txt](./data/chinhanh.txt) và [data/nhanvien_cn.txt](./data/nhanvien_cn.txt). Nếu chưa có chi nhánh hợp lệ, chương trình tạo `CN01`. Kho, kệ bán, khách hàng, nhà cung cấp và hóa đơn ban đầu nằm trong bộ nhớ; cần nhập hoặc đọc dữ liệu từ menu tương ứng.

Nếu tệp nhân viên chưa tồn tại, chương trình tạo tài khoản demo:

| Mã | Mật khẩu demo | Quyền | Phạm vi |
| --- | --- | --- | --- |
| `ADMIN` | `admin123` | `ADMIN` | Toàn hệ thống |
| `QL01` | `ql123` | `QUAN_LY` | Chi nhánh `CN01` |
| `BH01` | `bh123` | `BAN_HANG` | Chi nhánh `CN01` |
| `KHO01` | `kho123` | `NHAP_KHO` | Kho chung |

Nếu [data/nhanvien_cn.txt](./data/nhanvien_cn.txt) đã tồn tại, dùng tài khoản trong tệp đó. Bảng demo không thay thế dữ liệu nhân viên hiện có. Mật khẩu được lưu dạng văn bản trong dữ liệu học tập.

| Quyền | Chức năng chính |
| --- | --- |
| `ADMIN` | Kho, phiếu nhập, mặt hàng, khách hàng, hóa đơn, NCC, nhân sự và chi nhánh; chọn/đổi chi nhánh làm việc |
| `QUAN_LY` | Mặt hàng, khách hàng, hóa đơn và giỏ hàng tại chi nhánh; xem kho chung và nhân viên chi nhánh |
| `BAN_HANG` | Xem mặt hàng, quản lý khách hàng, hóa đơn và giỏ hàng tại chi nhánh được gán |
| `NHAP_KHO` | Quản lý kho chung, phiếu nhập và NCC; chọn chi nhánh để xem hàng trên kệ |

Nhân viên bán hàng và quản lý chi nhánh làm việc tại chi nhánh gắn với tài khoản. Mỗi chi nhánh có **kệ bán và danh sách hóa đơn riêng**; kho, khách hàng, NCC và danh sách nhân viên dùng chung. Khách vãng lai có thể chọn chi nhánh để xem hàng trên kệ. Nhập `0` để quay lại, đăng xuất hoặc thoát theo menu.

## Quy trình sử dụng

### 1. Nhập hàng vào kho

1. Đăng nhập bằng quyền `ADMIN` hoặc `NHAP_KHO`.
2. Vào **Phiếu nhập → 8** để đọc [data/phieunhap_input.txt](./data/phieunhap_input.txt). Nhấn Enter hoặc nhập `.` để dùng đường dẫn mặc định.
3. Chọn **6** để nhập một phiếu vào kho; hoặc **11** để tự nhập tất cả phiếu đang chờ và ghi danh sách đã nhập vào [data/phieunhap_danhan.txt](./data/phieunhap_danhan.txt).
4. Vào **Kho → 1** để xem tồn; **7** kiểm tra sức chứa, **8** ghi kho ra TXT, **9** thống kê theo danh mục.

Kho có sức chứa **10.000 đơn vị sản phẩm**. Chỉ phiếu `CHO_DUYET` được nhập kho; nhập thành công chuyển sang `DA_DUYET`. Phiếu đã duyệt hoặc hủy không được nhập lại. Có thể thêm hàng trực tiếp bằng menu Kho; menu này không có chức năng đọc lại tệp kho TXT.

### 2. Chuyển hàng lên kệ chi nhánh

Với quyền admin hoặc quản lý chi nhánh, mở menu **Mặt hàng**:

| Mục | Chức năng |
| --- | --- |
| `1` | Xem số mã hàng đang bày bán |
| `2` | Chọn mã và số lượng chuyển từ kho lên kệ |
| `3` | Chuyển hàng loạt: lấy 20% tồn của từng mã có ít nhất 15 sản phẩm |
| `4` | Trả hàng từ kệ về kho chung |
| `5` | Xem hàng trên kệ |
| `6`–`9` | Xóa, sửa, tìm mã, lọc nâng cao |
| `10` | Ghi danh sách kệ ra TXT theo chi nhánh |
| `11` | Thống kê hàng trong kho chưa có trên kệ chi nhánh |

Chuyển hàng lên kệ làm giảm tồn kho và tăng lượng trên kệ; trả hàng thực hiện chiều ngược lại. Hàng trên kệ được sao chép từ hàng trong kho.

Hỗ trợ 11 danh mục: sách, sách giáo khoa, tiểu thuyết, truyện, văn phòng phẩm, báo tạp chí, sản phẩm khác, đồ chơi, dụng cụ học tập, trang trí và vở.

### 3. Lập hóa đơn và thanh toán

1. Vào **Khách hàng → 6** để đọc [data/khachhang.txt](./data/khachhang.txt), hoặc thêm khách từ menu.
2. Đảm bảo chi nhánh có hàng trên kệ và ít nhất một nhân viên bán hàng được gán vào chi nhánh đó.
3. Mở **Giỏ hàng** hoặc **Hóa đơn → 2**, nhập mã khách hàng, thêm mã hàng và số lượng.
4. Chọn **Giỏ hàng → 5** để tạo hóa đơn với mã chưa trùng.
5. Vào **Hóa đơn → 7** để thanh toán bằng tiền mặt hoặc thẻ.

Giỏ hàng giữ đơn giá tại thời điểm thêm hàng. Lập hóa đơn chưa đồng nghĩa với thanh toán; thanh toán thành công cập nhật số lượng trên kệ. Menu Hóa đơn có tra cứu/lọc, doanh thu và mặt hàng bán chạy. Menu Khách hàng có thống kê khách chi tiêu cao dựa trên hóa đơn của chi nhánh đang chọn.

### 4. Quản lý chi nhánh và nhân sự

Admin dùng **Chi nhánh** để xem, thêm, xóa, đổi chi nhánh làm việc, đọc/ghi TXT, sửa địa chỉ và xem nhân viên theo chi nhánh. Chỉ xóa được chi nhánh không còn hàng trên kệ, hóa đơn hoặc nhân viên.

Menu **Nhân sự** hỗ trợ thêm/sửa/xóa/tìm nhân viên, tổng lương cơ bản, đọc/ghi TXT và xem nhân viên theo chi nhánh. Không thể xóa tài khoản đang đăng nhập hoặc admin cuối cùng. Sau khi đọc lại danh sách nhân viên, chương trình kết thúc phiên đăng nhập để xác thực lại.

## Dữ liệu và lưu trữ

Các tệp TXT dùng UTF-8 và dấu `;` để tách cột. Khi menu hỏi đường dẫn, Enter hoặc `.` chọn giá trị mặc định.

| Tệp | Nội dung / cách dùng |
| --- | --- |
| [data/chinhanh.txt](./data/chinhanh.txt) | `ma;ten;diaChi`; vẫn đọc được định dạng cũ `ma;ten` |
| [data/nhanvien_cn.txt](./data/nhanvien_cn.txt) | `ma;ten;sdt;matKhau;ca;luong;quyen;maChiNhanh`; vẫn đọc được định dạng 7 cột |
| [data/nhanvien.txt](./data/nhanvien.txt) | Dữ liệu nhân viên cũ, không phải tệp đăng nhập mặc định |
| [data/khachhang.txt](./data/khachhang.txt) | `maKH;tenKH;soDienThoai` |
| [data/nhacungcap_input.txt](./data/nhacungcap_input.txt) | `maNCC;ten;diaChi;soDienThoai;email`; nhập đường dẫn này khi đọc NCC vì mặc định menu là `data/nhacungcap.txt` |
| [data/phieunhap_input.txt](./data/phieunhap_input.txt) | Mỗi dòng là một mặt hàng của phiếu: mã phiếu, ngày, dữ liệu mặt hàng, trạng thái |
| [data/phieunhap_input_loi.txt](./data/phieunhap_input_loi.txt) | Dữ liệu phiếu nhập cố ý chứa lỗi để thử bộ đọc |
| [data/phieunhap_danhan.txt](./data/phieunhap_danhan.txt) | Phiếu đã nhập kho, trạng thái `DA_DUYET`; không tự nạp khi khởi động |
| [data/kho_output.txt](./data/kho_output.txt) | Bản xuất tồn kho; không tự nạp khi khởi động |
| [data/hoadon.txt](./data/hoadon.txt) | Hóa đơn mẫu; cần nhập đường dẫn cụ thể khi đọc vào một chi nhánh |

Tệp xuất mặc định theo chi nhánh là `data/<maCN>_mathang_output.txt` và `data/<maCN>_hoadon_output.txt`. Tệp hóa đơn đầu vào mặc định là `data/<maCN>_hoadon.txt`; có thể chọn tệp khác từ menu. Cần nạp khách hàng trước khi đọc hóa đơn.

Ví dụ một dòng phiếu nhập:

```text
PN001;27/07/2026;Sách;SCH2001;Tuổi trẻ đáng giá bao nhiêu;56000.0;10;Rosie Nguyễn;NXB Trẻ;Kỹ năng sống;CHO_DUYET
```

Các dòng cùng mã phiếu được ghép thành phiếu nhiều mặt hàng. Số cột phụ thuộc danh mục. Ngày trong tệp phiếu nhập dùng **`dd/MM/yyyy`**; trạng thái gồm `CHO_DUYET`, `DA_DUYET`, `DA_HUY`.

Hóa đơn gồm dòng đầu, số dòng chi tiết, rồi các dòng hàng:

```text
maHD;yyyy-MM-dd;maKH;maNV;tenNV;daThanhToan
soDongChiTiet
maSKU;soLuong;donGia
```

`daThanhToan` là `1` hoặc `0`. Bộ đọc cũng hỗ trợ dòng đầu cũ có ba cột: mã hóa đơn, ngày và mã khách hàng.

**Lưu trước khi thoát:** bản ZIP tự ghi chi nhánh và nhân viên khi thoát bình thường, đồng thời tránh ghi đè nhân viên nếu dữ liệu đầu vào cũ/lỗi. Các dữ liệu nghiệp vụ khác phải dùng mục **Ghi file** tương ứng; bản này không tự khôi phục toàn bộ phiên làm việc. Menu kho và kệ hiện chỉ hỗ trợ xuất TXT, nên không có quy trình tự đọc lại toàn bộ trạng thái đã xuất.

## Cấu trúc dự án

| Đường dẫn | Vai trò |
| --- | --- |
| [src/Main.java](./src/Main.java) | Khởi tạo, đăng nhập, phân quyền và điều phối menu |
| [src/chinhanh/](./src/chinhanh) | Chi nhánh, kệ/hóa đơn riêng và nhân viên theo chi nhánh |
| [src/nhanvien/](./src/nhanvien) | Các loại nhân viên, đăng nhập và tệp nhân sự |
| [src/kho/](./src/kho) | Kho chung, sức chứa và tồn kho theo danh mục |
| [src/mathang/](./src/mathang) | Các loại hàng, giá bán, chuyển kho/kệ và bộ lọc |
| [src/phieunhap/](./src/phieunhap) | Phiếu nhập, đọc/ghi TXT và duyệt nhập kho |
| [src/nhacungcap/](./src/nhacungcap), [src/khachhang/](./src/khachhang) | Nhà cung cấp và khách hàng |
| [src/giohang/](./src/giohang), [src/hoadon/](./src/hoadon), [src/thanhtoan/](./src/thanhtoan) | Giỏ hàng, hóa đơn và hình thức thanh toán |
| [src/interfaces/](./src/interfaces), [src/tienich/](./src/tienich) | Interface, nhập liệu, ngày và Unicode console |
| [data/](./data) | Dữ liệu TXT |
| [tests/](./tests) | Kiểm thử Java/Python và fixture |
| [scripts/](./scripts) | Script chạy, kiểm thử và kiểm tra dữ liệu |
| [docs/](./docs) | Báo cáo lịch sử; kết quả cũ có thể khác bản ZIP này |

## Kiểm tra mã nguồn

Các script kiểm thử đi kèm là [test.bat](./test.bat) và [scripts/test.sh](./scripts/test.sh):

```powershell
.\test.bat
```

```bash
bash scripts/test.sh
```

Các script biên dịch mã nguồn và test vào thư mục `build-test`, riêng với thư mục `build` dùng để chạy ứng dụng. Bộ kiểm thử được khôi phục từ Git và đồng bộ với API hiện tại, gồm **6 nhóm Java và 5 nhóm Python**: tích hợp nghiệp vụ, NCC/phiếu nhập, các loại mặt hàng, hiển thị và logic, chức năng chi nhánh/phân quyền, đọc/ghi TXT và các luồng console.

Script tiếp tục chạy các nhóm còn lại nếu một nhóm thất bại và trả mã thoát khác `0` nếu có lỗi biên dịch, kiểm thử hoặc thiếu Python. Trên Windows, script ưu tiên Python trong `.venv`, sau đó `py -3` hoặc `python`; trên Linux/macOS ưu tiên `.venv/bin/python`, sau đó `python3`. Có thể chọn trình thông dịch bằng biến môi trường `BOOKSTORE_TEST_PYTHON` (đường dẫn tệp thực thi).

Các test dùng fixture trong [tests/fixtures/](./tests/fixtures) và dữ liệu tạm, không ghi đè dữ liệu nghiệp vụ trong [data/](./data). Nhóm lưu trữ kiểm tra các định dạng TXT đang được hỗ trợ và quy trình đọc lại hóa đơn; không yêu cầu lớp `LuuTruPhien` hoặc tự khôi phục toàn bộ phiên làm việc.

Kết quả xác minh trên Windows ngày **22/11/2026**: **198 ca PASS, 0 FAIL**, đủ **11 nhóm kiểm thử** khi chạy `test.bat`; mã thoát `0`. Đã đối chiếu SHA-256 của các tệp trong [data/](./data) trước và sau khi chạy: dữ liệu gốc không thay đổi.
