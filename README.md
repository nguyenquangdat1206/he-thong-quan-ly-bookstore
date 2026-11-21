# Hệ thống quản lý nhà sách

Ứng dụng quản lý nhà sách bằng **Java 17+**, chạy trong terminal với menu tiếng Việt. Dự án không dùng Maven, Gradle, cơ sở dữ liệu hay thư viện ngoài JDK. Dữ liệu được đọc và ghi qua các tệp TXT mã hóa UTF-8.

## Cập nhật 21/11/2026

- Quản lý nhiều chi nhánh: mỗi chi nhánh có kệ và hóa đơn riêng, dùng chung kho, khách hàng và nhà cung cấp.
- Nhân viên dùng `data/nhanvien_cn.txt` với 8 cột; cột cuối là mã chi nhánh. Danh sách chi nhánh nằm trong `data/chinhanh.txt`.
- Khi thoát bình thường, chương trình lưu dữ liệu nghiệp vụ vào `data/phien.properties` và khôi phục ở lần mở tiếp theo. Tệp này được tạo trên máy chạy và không đưa vào Git.
- Chạy `test.bat` để thực thi đủ 11 bộ kiểm thử: **197 PASS, 0 FAIL** trên bản cập nhật này. Lệnh trả mã khác 0 nếu biên dịch hoặc kiểm thử thất bại.
- Chi tiết lỗi đã sửa và phạm vi xác minh: [báo cáo kiểm thử](docs/FUNCTIONAL_AUDIT_2026-11-21.txt).

Luồng nghiệp vụ chính:

```text
Nhà cung cấp → Phiếu nhập → Kho → Mặt hàng trên kệ → Giỏ hàng → Hóa đơn → Thanh toán
                                     Khách hàng ────────────┘
                                     Nhân viên ─────────────┘
```

> **Phạm vi:** Đây là ứng dụng console phục vụ học tập và kiểm thử OOP. Mật khẩu nhân viên được lưu dạng văn bản rõ trong TXT; không dùng dữ liệu tài khoản thật hoặc xem đây là hệ thống bán hàng production.

## Mục lục

- [Yêu cầu và cách chạy](#yêu-cầu-và-cách-chạy)
- [Khởi động và phân quyền](#khởi-động-và-phân-quyền)
- [Hướng dẫn các quy trình chính](#hướng-dẫn-các-quy-trình-chính)
- [Dữ liệu và định dạng tệp](#dữ-liệu-và-định-dạng-tệp)
- [Cấu trúc mã nguồn](#cấu-trúc-mã-nguồn)
- [Kiểm thử và kiểm tra dữ liệu](#kiểm-thử-và-kiểm-tra-dữ-liệu)
- [Lỗi thường gặp và giới hạn hiện tại](#lỗi-thường-gặp-và-giới-hạn-hiện-tại)

## Yêu cầu và cách chạy

### Yêu cầu

- **JDK 17 trở lên**: cần cả `java` và `javac` trên `PATH`.
- **Python 3**: chỉ cần nếu muốn chạy đầy đủ các test console hoặc công cụ kiểm tra dữ liệu. Các test Java không cần Python.
- Chạy từ **thư mục gốc dự án** vì các đường dẫn `data/...` trong chương trình là đường dẫn tương đối.

Kiểm tra JDK bằng `java -version` và `javac -version`. Không cần cài thêm package Java hay Python.

### Windows

Mở PowerShell hoặc Command Prompt tại thư mục gốc:

```powershell
.\run.bat
```

[run.bat](./run.bat) chuyển terminal sang code page UTF-8, biên dịch toàn bộ `src/` vào `build/`, rồi mở [Main.java](./src/Main.java). Thư mục `build/` được tạo tự động và không được đưa vào Git.

Chạy đầy đủ kiểm thử:

```powershell
.\test.bat
py -3 -X utf8 scripts\audit_data.py
```

[test.bat](./test.bat) biên dịch `src/` và `tests/`, chạy ba nhóm test Java, sau đó chạy các test console bằng Python nếu tìm thấy `py`. Nếu máy không có Python, script sẽ **bỏ qua test console** và in thông báo; lúc đó kết quả chưa phải kiểm thử đầy đủ. [audit_data.py](./scripts/audit_data.py) chỉ đọc dữ liệu để kiểm tra cấu trúc, không chỉnh sửa tệp.

### Linux/macOS

Chạy từ thư mục gốc:

```bash
bash scripts/run.sh
bash scripts/test.sh
python3 scripts/audit_data.py
```

Các script ở [scripts/](./scripts) cũng biên dịch vào `build/`. Script test trên Linux/macOS cần cả JDK và Python 3.

### IntelliJ IDEA hoặc IDE Java khác

Mở thư mục dự án như một Java project, chọn `src/` làm source root nếu IDE chưa tự nhận, và chạy lớp `Main` trong [Main.java](./src/Main.java). Đặt **working directory** là thư mục gốc chứa `src/`, `data/` và `tests/`. Mã nguồn, input và output console đều dùng UTF-8; các điểm vào chương trình tự cấu hình output UTF-8. Trên Windows, `run.bat` là cách chạy đơn giản nhất nếu terminal của IDE vẫn hiển thị sai dấu.

Ngoài `Main` còn có ba menu độc lập cho mục đích thử từng module: [QuanLyKho.java](./src/kho/QuanLyKho.java), [QuanLyPhieuNhap.java](./src/phieunhap/QuanLyPhieuNhap.java) và [QuanLyMatHang.java](./src/mathang/QuanLyMatHang.java). Menu mặt hàng độc lập có mục `10` để đọc `data/kho_input.txt` trước khi chuyển hàng từ kho lên kệ.

## Khởi động và phân quyền

Khi mở `Main`, chương trình **chỉ tự đọc `data/nhanvien.txt`**. Kho, phiếu nhập, mặt hàng, khách hàng, NCC và hóa đơn khởi đầu trong bộ nhớ trống; hãy dùng menu tương ứng để nạp dữ liệu cần thiết. Dữ liệu các module này cũng không được tự ghi ra TXT khi thoát: phải chọn mục **Ghi file**.

Nếu `data/nhanvien.txt` chưa tồn tại, chương trình tạo ba tài khoản **demo** và ghi tệp mới:

| Mã demo | Mật khẩu demo | Quyền |
| --- | --- | --- |
| `ADMIN` | `admin123` | `ADMIN` |
| `BH01` | `bh123` | `BAN_HANG` |
| `KHO01` | `kho123` | `NHAP_KHO` |

**Nếu tệp nhân viên đã tồn tại, chương trình dùng tài khoản trong tệp đó.** Các thông tin demo ở bảng trên hoặc trong [nhanvien_demo.txt](./tests/fixtures/nhanvien_demo.txt) không phải mật khẩu của `data/nhanvien.txt` hiện có. Không xóa hoặc ghi đè tệp nhân viên hiện có chỉ để lấy tài khoản demo.

Menu đầu cho phép đăng nhập, xem danh sách hàng trên kệ với tư cách khách vãng lai và thoát. Danh sách khách vãng lai sẽ trống nếu chưa nạp mặt hàng lên kệ trong phiên hiện tại.

| Quyền | Các mục trong menu sau đăng nhập |
| --- | --- |
| `ADMIN` | Kho, phiếu nhập, mặt hàng, khách hàng, hóa đơn, nhà cung cấp, nhân sự, giỏ hàng |
| `BAN_HANG` | Xem mặt hàng, khách hàng, hóa đơn, giỏ hàng |
| `NHAP_KHO` | Kho, phiếu nhập, nhà cung cấp, xem mặt hàng |

Quản lý nhân sự chỉ dành cho `ADMIN`. Hóa đơn được lập từ **giỏ hàng**; mục `2` của menu Hóa đơn mở giỏ hàng. Muốn tạo giỏ cần có khách hàng trong danh sách và ít nhất một nhân viên bán hàng. Trong từng menu, nhập `0` để quay lại hoặc đăng xuất.

### Các chức năng theo menu

| Menu | Chức năng |
| --- | --- |
| **Kho** | `1` xem, `2` thêm một hàng, `3` thêm nhiều hàng, `4` sửa số lượng tồn, `5` xóa, `6` tìm mã, `7` sức chứa, `8` đọc TXT, `9` ghi TXT |
| **Phiếu nhập** | `1` xem, `2` thêm một phiếu, `3` thêm nhiều phiếu, `4` sửa, `5` xóa, `6` duyệt và nhập vào kho, `7` xem kho, `8` đọc TXT, `9` ghi TXT, `10` xem NCC đã ghi nhận |
| **Mặt hàng trên kệ** | `1` đếm loại hàng, `2` chuyển hàng loạt từ kho, `3` chuyển một mã từ kho, `4` xem, `5` thêm trực tiếp, `6` xóa, `7` sửa, `8` tìm mã, `9` lọc nâng cao, `10` đọc TXT, `11` ghi TXT |
| **Khách hàng** | `1` xem, `2` thêm, `3` thêm nhiều, `4` sửa, `5` xóa, `6` đọc TXT, `7` ghi TXT, `8` tìm theo mã/tên/SĐT, `9` xếp hạng khách chi tiêu cao |
| **Hóa đơn** | `1` xem, `2` lập qua giỏ, `3` sửa đơn chưa thanh toán, `4` xóa đơn chưa thanh toán, `5` đọc TXT, `6` ghi TXT, `7` thanh toán, `8` tra cứu/lọc, `9` doanh thu và hàng bán chạy |
| **Nhà cung cấp** | `1` xem, `2` thêm, `3` sửa, `4` xóa, `5` đọc TXT, `6` ghi TXT |
| **Nhân sự** | `1` xem, `2` thêm, `3` sửa, `4` xóa, `5` tìm mã, `6` tổng lương cơ bản, `7` đọc TXT, `8` ghi TXT |
| **Giỏ hàng** | `1` thêm hàng, `2` xem giỏ, `3` đổi số lượng, `4` xóa hàng, `5` tạo hóa đơn từ giỏ |

## Hướng dẫn các quy trình chính

### 1. Nạp kho và đưa hàng lên kệ

1. Đăng nhập bằng tài khoản có quyền `ADMIN` hoặc `NHAP_KHO`.
2. Vào **Kho → 8**, nhấn Enter hoặc gõ `.` để đọc mặc định `data/kho_input.txt`.
3. Vào **Mặt hàng → 3** để chọn mã và số lượng cụ thể cần chuyển từ kho lên kệ. `ADMIN` vào menu này trực tiếp; nhân viên kho có mục xem mặt hàng nhưng không có menu chuyển lên kệ.
4. Với `ADMIN`, có thể dùng **Mặt hàng → 2** để chuyển hàng loạt: chương trình chuyển **20% số lượng kho của từng mã đang có ít nhất 15 sản phẩm**.
5. Dùng **Mặt hàng → 4** để kiểm tra hàng trên kệ. Số lượng trong kho và số lượng trên kệ là hai số riêng: chuyển hàng làm giảm kho và tăng kệ. Mặt hàng trên kệ là bản sao của mặt hàng trong kho, nên sửa mã hoặc giá trên kệ không đổi thông tin trong kho.

Kho có sức chứa tối đa **10.000 đơn vị sản phẩm**. Đọc lại cùng tệp kho sẽ bỏ qua mã đã có, không nhân đôi tồn; dòng sai định dạng cũng được bỏ qua và các dòng hợp lệ phía sau vẫn được xử lý. Tệp kho khi ghi dùng **số lượng còn trong kho**, không dùng số trên kệ.

### 2. Nạp khách hàng, tạo giỏ và thanh toán

1. Vào **Khách hàng → 6** để nạp `data/khachhang.txt`, hoặc thêm khách hàng bằng menu.
2. Đảm bảo mặt hàng muốn bán đã có trên kệ. Ví dụ mã `SCH0001` có trong dữ liệu kho mẫu sau khi nhập kho và chuyển lên kệ.
3. `ADMIN` vào **Hóa đơn → 2** hoặc **Giỏ hàng**; nhân viên `BAN_HANG` vào **Hóa đơn → 2** hoặc **Giỏ hàng**.
4. Nhập mã khách hàng, chọn **Giỏ hàng → 1**, nhập mã mặt hàng và số lượng. Chọn **Giỏ hàng → 5** rồi nhập mã hóa đơn mới để xác nhận mua.
5. Trở về **Hóa đơn → 7**, nhập mã hóa đơn và chọn tiền mặt hoặc thẻ. Đơn chỉ chuyển sang trạng thái đã thanh toán và trừ hàng trên kệ khi thanh toán thành công.
6. Nếu muốn lưu, chọn **Hóa đơn → 6**; mặc định ghi ra `data/hoadon_output.txt`.

Giỏ được làm trống sau khi tạo hóa đơn thành công. Rời menu khi giỏ chưa được chốt sẽ bỏ giỏ của lần thao tác đó. Một mã hóa đơn không được trùng; số lượng gộp của các dòng cùng mã hàng không được vượt số trên kệ. Đơn đã thanh toán không thể thanh toán, sửa hoặc xóa lần nữa qua menu.

Giỏ dùng `ChiTietGioHang` để giữ đơn giá tại thời điểm thêm hàng. Khi chốt giỏ, từng dòng được chuyển thành `ChiTietHoaDon` với đúng đơn giá đã giữ, kể cả khi giá nhập mặt hàng được sửa trong lúc giỏ đang mở.

### 3. Lập hoặc nạp phiếu nhập

1. Vào **Phiếu nhập → 2** để tạo thủ công, hoặc **Phiếu nhập → 8** để đọc `data/phieunhap_input.txt`.
2. Ngày nhập thủ công dùng `dd/MM/yyyy`; nhấn Enter ở bước tạo phiếu để lấy ngày hiện tại. Cung cấp tên NCC chính và ít nhất một dòng mặt hàng.
3. Kiểm tra phiếu bằng **Phiếu nhập → 1** và các NCC đã ghi nhận bằng **Phiếu nhập → 10**.
4. Chọn **Phiếu nhập → 6** và xác nhận `y` cho phiếu chờ duyệt cần nhập vào kho. Nhập thành công đổi trạng thái phiếu sang `DA_DUYET`. Chọn cách khác sẽ hủy phiếu sang `DA_HUY`.
5. Dùng **Phiếu nhập → 9** để ghi ra `data/phieunhap_output.txt` nếu muốn lưu các thay đổi.

Ba trạng thái là `CHO_DUYET`, `DA_DUYET` và `DA_HUY`. Phiếu đã xử lý không được sửa hoặc mở lại bằng menu, tránh nhập kho hai lần. Nếu kho thiếu chỗ, phiếu vẫn ở trạng thái `CHO_DUYET` để có thể thử lại sau khi giải phóng sức chứa. Đọc lại cùng file không nhân đôi dòng hàng trong phiếu.

Mỗi phiếu hiện giữ **một NCC chính**. Tệp cũ có nhiều tên nhà sản xuất/NXB trong cùng phiếu vẫn được đọc: chương trình ghi nhận các NCC đó trong danh sách, giữ NCC của dòng đầu làm NCC chính và in cảnh báo. Khi sửa NCC chính rồi xuất phiếu, tệp mới lưu thêm cột `NCC=<tên>` để đọc lại không mất lựa chọn này.

### 4. Quản lý nhân viên và dữ liệu cũ

Menu **Nhân sự** dành cho `ADMIN`: thêm ba loại vai trò, tìm theo mã, sửa thông tin/lương/mật khẩu, xem tổng lương, đọc và ghi TXT. Không thể xóa quản lý cuối cùng hoặc tài khoản đang đăng nhập qua menu.

Chương trình đọc được dòng nhân viên TXT 7 cột và một số dòng văn bản kiểu cũ `dsNhanVien.them(new ...);`. Dòng kiểu cũ **chỉ được phân tích như dữ liệu**, không thực thi mã Java trong tệp. Nếu tệp nhân viên gốc có định dạng cũ, dòng lỗi hoặc không có ADMIN, chương trình khóa tự ghi đè tệp gốc khi thoát. Sau khi `ADMIN` đọc lại tệp nhân viên bằng menu, phiên đăng nhập kết thúc để xác thực lại. Khi nguồn cũ bị khóa, hãy chọn **đường dẫn xuất khác** nếu cần lưu bản mới.

## Dữ liệu và định dạng tệp

### Đường dẫn mặc định

Khi menu hỏi đường dẫn, **Enter** hoặc `.` dùng đường dẫn mặc định được hiển thị. Các đường dẫn dưới đây tính từ thư mục gốc dự án.

| Module | Tệp đọc mặc định | Tệp ghi mặc định |
| --- | --- | --- |
| Nhân viên | `data/nhanvien.txt` (tự đọc lúc mở) | `data/nhanvien.txt`, có chặn ghi khi nguồn không tương thích |
| Kho | `data/kho_input.txt` | `data/kho_output.txt` |
| Mặt hàng trên kệ | `data/mathang.txt` | `data/mathang_output.txt` |
| Khách hàng | `data/khachhang.txt` | `data/khachhang_output.txt` |
| Phiếu nhập | `data/phieunhap_input.txt` | `data/phieunhap_output.txt` |
| Hóa đơn | `data/hoadon.txt` | `data/hoadon_output.txt` |
| Nhà cung cấp | `data/nhacungcap.txt` (chưa có sẵn trong repo) | `data/nhacungcap_output.txt` |

Các tệp `*_output.txt` chỉ xuất hiện sau khi người dùng chọn **Ghi file**; chúng không tự được nhập lại ở lần chạy sau. Các tệp `*_loi.txt` trong [data/](./data) là dữ liệu thử lỗi, không phải đường dẫn nhập mặc định. `data/danhsach_mathang.txt` là tệp bổ sung trong repo, cũng không phải đường dẫn mặc định của menu mặt hàng.

Mỗi tệp dùng **UTF-8**, các trường phân tách bằng dấu chấm phẩy `;`. Định dạng hiện tại không có cơ chế escape dấu `;` hoặc xuống dòng trong một trường, nên không nhập hai ký tự đó vào tên/thông tin nếu định lưu TXT. Tệp xuất ghi theo đường dẫn được chọn và có thể thay nội dung cũ ở đường dẫn ấy.

### Nhân viên, khách hàng, NCC

```text
nhanvien.txt:  maNV;hoTen;soDienThoai;matKhau;caLamViec;luongCoBan;quyen
khachhang.txt: maKH;tenKH;soDienThoai
nhacungcap.txt: maNCC;tenNCC;diaChi;soDienThoai;email
```

`quyen` của nhân viên nhận `ADMIN`, `BAN_HANG` hoặc `NHAP_KHO`. Lương cơ bản không âm; mã nhân viên không được trùng khi không phân biệt chữ hoa/thường. Tệp khách hàng khi đọc sẽ **thay danh sách khách hàng hiện có trong bộ nhớ**; các dòng thiếu cột, rỗng hoặc trùng mã bị bỏ qua. Tệp NCC khi đọc bổ sung các mã chưa có; mã trùng bị bỏ qua. Khi thêm NCC theo tên từ phiếu nhập, tên được so sánh không phân biệt hoa/thường và mã `NCC0`, `NCC1`... được tạo tự động.

### Mặt hàng và mã SKU

Tệp mặt hàng và tệp kho đều dùng dạng cơ sở:

```text
loai;maSKU;ten;giaNhap;soLuong;[cacTruongRiengCuaLoai]
```

`giaNhap` là số với dấu thập phân `.`; `soLuong` là số nguyên. Trong **tệp kho**, số lượng là tồn kho. Trong **tệp mặt hàng**, số lượng là hàng trên kệ. Mã phải đúng tiền tố của loại và **bốn chữ số**; ví dụ `SCH0001`. Bảng sau ghi chính xác thứ tự các trường sau `soLuong`:

| Loại | Dạng mã | Các trường riêng sau `soLuong` |
| --- | --- | --- |
| Sách | `SCHdddd` | `tacGia;nhaXuatBan;theLoai` |
| Sách giáo khoa | `SGKdddd` | `monHoc;nhaXuatBan` |
| Tiểu thuyết | `TTHdddd` | `tacGia;nhaXuatBan;theLoai` |
| Truyện | `TRUdddd` | `tacGia;nhaXuatBan;theLoai` |
| Văn phòng phẩm | `VPPdddd` | `loaiVPP;nhaSanXuat` |
| Báo tạp chí | `BTCdddd` | `theLoai;nhaXuatBan` |
| Khác | `SPKdddd` | `nhaSanXuat` |
| Đồ chơi | `DCHdddd` | `doTuoi;nhaSanXuat` |
| Dụng cụ học tập | `DCTdddd` | `loaiDungCu;nhaSanXuat` |
| Trang trí | `TTRdddd` | `nhaSanXuat` |
| Vở | `VOdddd` | `soTrang;nhaSanXuat` |

Ở bảng, `dddd` nghĩa là đúng bốn chữ số. Ví dụ một dòng sách:

```text
Sách;SCH3001;Đắc Nhân Tâm;50000;100;Dale Carnegie;NXB Tổng Hợp;Kỹ năng sống
```

Để phù hợp quy tắc mã hiện tại, năm mã cũ trong `data/mathang.txt` đã được đổi như sau; tên, giá và số lượng giữ nguyên:

| Mã cũ | Mã hiện tại |
| --- | --- |
| `S01` | `SCH3001` |
| `V01` | `VO3001` |
| `DC01` | `DCH3001` |
| `DC02` | `DCT3001` |
| `K01` | `SPK3001` |

Nếu có tệp bên ngoài repo tham chiếu các mã cũ này, cần cập nhật tham chiếu trước khi nhập chúng vào ứng dụng.

### Phiếu nhập

Mỗi dòng TXT tương ứng **một mặt hàng trong phiếu**:

```text
maPN;ngayNhap;loai;maSKU;ten;giaNhap;soLuong;[truongRieng];trangThai
maPN;ngayNhap;loai;maSKU;ten;giaNhap;soLuong;[truongRieng];trangThai;NCC=tenNCCChinh
```

Dạng đầu là tệp cũ vẫn đọc được. Dạng thứ hai do chức năng xuất hiện tại ghi để giữ NCC chính. Các dòng có cùng `maPN` tạo thành một phiếu; trong cùng phiếu, mã SKU không được lặp. Tên NCC của từng dòng được suy ra từ nhà xuất bản hoặc nhà sản xuất của mặt hàng. Tệp có thêm `NCC=...` dùng giá trị đó làm NCC chính.

Ngày trong **tệp** nhận `dd-MM-yyyy`, `dd/MM.yyyy` và `yyyy-MM-dd`; ngày nhập trên **menu** dùng `dd/MM/yyyy`. Ví dụ dòng cũ hợp lệ:

```text
PN001;27-07-2026;Sách;SCH2001;Tuổi trẻ đáng giá bao nhiêu;258000.0;10;Rosie Nguyễn;NXB Trẻ;Lãng mạn;CHO_DUYET
```

### Hóa đơn

Hóa đơn là một khối nhiều dòng. Dòng đầu có thể dùng dạng cũ ba cột hoặc dạng mới sáu cột; sau đó là số dòng chi tiết và đúng số dòng hàng:

```text
maHD;ngayLapISO;maKH
soDong
maSKU;soLuong;donGia
```

```text
maHD;ngayLapISO;maKH;maNV;tenNV;daThanhToan
soDong
maSKU;soLuong;donGia
```

`ngayLapISO` dùng `yyyy-MM-dd`. `daThanhToan` là `1` nếu đã thanh toán, `0` nếu chưa. Mã và tên nhân viên có thể rỗng trong file. Tệp xuất luôn ghi đơn giá từng dòng và trạng thái thanh toán. Cần **nạp khách hàng trước khi nạp hóa đơn**, vì hóa đơn không tìm được mã khách hàng sẽ bị bỏ qua.

`data/hoadon.txt` có các mã hàng lịch sử không còn trong danh mục hiện tại. Khi có đơn giá trong tệp, ứng dụng vẫn giữ mã, số lượng, đơn giá và trạng thái hóa đơn, đồng thời hiển thị tên dạng “không còn trong danh mục”. Hàng lịch sử có tồn trên kệ bằng `0`: đơn chưa thanh toán dựa vào hàng này không thể thu tiền trực tiếp. Nếu dòng hàng không có cả sản phẩm trong danh mục lẫn đơn giá lịch sử, hóa đơn đó bị bỏ qua. Đọc hóa đơn đã thanh toán **không trừ kho lần nữa**.

## Cấu trúc mã nguồn

| Đường dẫn | Vai trò |
| --- | --- |
| [src/Main.java](./src/Main.java) | Điểm vào chính, menu và kiểm tra quyền |
| [src/nhanvien/](./src/nhanvien) | Nhân viên, vai trò, đăng nhập, lương và TXT nhân sự |
| [src/kho/](./src/kho) | Tồn kho, sức chứa, nhập phiếu, chuyển hàng và TXT kho |
| [src/mathang/](./src/mathang) | 11 loại mặt hàng, giá bán và danh sách hàng trên kệ |
| [src/phieunhap/](./src/phieunhap) | Phiếu nhập, trạng thái và TXT phiếu |
| [src/nhacungcap/](./src/nhacungcap) | Danh sách nhà cung cấp |
| [src/khachhang/](./src/khachhang) | Khách hàng và tìm kiếm |
| [src/giohang/](./src/giohang) | Giỏ hàng, chi tiết giỏ giữ đơn giá và xác nhận mua |
| [src/hoadon/](./src/hoadon) | Hóa đơn, chi tiết, thống kê, hàng lịch sử và TXT hóa đơn |
| [src/thanhtoan/](./src/thanhtoan) | Thanh toán tiền mặt và thẻ |
| [src/tichhop/](./src/tichhop) | Đọc/ghi TXT mặt hàng |
| [src/tienich/](./src/tienich), [src/interfaces/](./src/interfaces) | Nhập console, UTF-8, ngày tháng và các interface; danh sách mặt hàng triển khai `IThaoTacThucThe` |
| [data/](./data) | Dữ liệu hiện có và dữ liệu thử lỗi |
| [tests/](./tests), [tests/fixtures/](./tests/fixtures) | Test Java/Python và dữ liệu kiểm thử độc lập |
| [scripts/](./scripts) | Script chạy, test, kiểm tra dữ liệu |
| [docs/](./docs) | Báo cáo và ghi chép lịch sử của các lần đồng bộ trước |
| `build/` | File `.class` tạo khi biên dịch; bị Git bỏ qua |

## Kiểm thử và kiểm tra dữ liệu

Lệnh chuẩn là `.\test.bat` trên Windows hoặc `bash scripts/test.sh` trên Linux/macOS. Bộ test hiện gồm:

- [IntegrationTests.java](./tests/IntegrationTests.java): nhân viên, phân quyền, kho, kệ, giỏ, hóa đơn, thanh toán, dữ liệu thật và các lỗi nhập/lưu.
- [FeatureNCCPhieuNhapTests.java](./tests/FeatureNCCPhieuNhapTests.java): NCC, phiếu nhập, ngày, trạng thái, dữ liệu lỗi, lưu và đọc lại.
- [LegacyProductRegressionTests.java](./tests/LegacyProductRegressionTests.java): bốn loại mặt hàng khôi phục, giá bán, TXT và menu nhập tay.
- [DisplayLogicRegressionTests.java](./tests/DisplayLogicRegressionTests.java): 15 ca kiểm tra hiển thị, thuế, số lượng nhập, mã khách hàng trùng, bản sao kho/kệ và cấu trúc OOP.
- [test_console.py](./tests/test_console.py), [test_console_pn.py](./tests/test_console_pn.py), [test_legacy_runtime.py](./tests/test_legacy_runtime.py): thao tác console, phân quyền, bán hàng và tương thích dữ liệu cũ.
- [test_console_unicode.py](./tests/test_console_unicode.py): cả bốn điểm vào Java hiển thị tiếng Việt UTF-8 khi chạy trực tiếp, không cần truyền tham số mã hóa cho JVM.

Lần chạy gần nhất trên Windows đạt **92/92 ca**, không có lỗi biên dịch. Test console chạy trong thư mục tạm với dữ liệu fixture nên không sửa tệp nguồn trong `data/`.

Kiểm tra riêng cấu trúc sáu tệp dữ liệu chính:

```powershell
py -3 -X utf8 scripts\audit_data.py
```

Công cụ kiểm tra số cột, một số kiểu dữ liệu/ngày và dạng mã SKU theo danh mục. Kết quả gần nhất là **6/6 tệp đạt**. Đây là kiểm tra cấu trúc; việc menu và nghiệp vụ chạy đúng vẫn cần bộ test ở trên.

## Lỗi thường gặp và giới hạn hiện tại

| Hiện tượng | Cách kiểm tra/xử lý |
| --- | --- |
| `javac` không tìm thấy | Cài JDK 17+ và kiểm tra `java`, `javac` cùng nằm trên `PATH`. JRE riêng lẻ không đủ để biên dịch. |
| Menu tiếng Việt thành `?` hoặc ký tự lạ | Trên Windows chạy bằng `run.bat`; trong IDE đặt working directory đúng và để console đọc UTF-8. Các điểm vào Java đã ghi output UTF-8. |
| Đăng nhập demo thất bại | Kiểm tra tệp `data/nhanvien.txt` hiện có. Mã/mật khẩu demo chỉ áp dụng khi chương trình tự tạo tệp mới hoặc khi chạy fixture test; chương trình không thay tài khoản đang có bằng demo. |
| Menu khách vãng lai/kệ/kho trống | Chỉ nhân viên được tự nạp lúc mở. Hãy đọc kho, đọc mặt hàng hoặc chuyển hàng lên kệ trong phiên hiện tại. |
| Không tìm được tệp TXT | Chạy từ thư mục gốc và xem đường dẫn mặc định đang hiện trên menu. `Enter` hoặc `.` chọn mặc định; có thể nhập đường dẫn khác. |
| Dòng hàng bị bỏ qua với thông báo dạng mã | Đối chiếu tiền tố danh mục và đúng bốn chữ số trong bảng SKU. Mã và loại phải khớp. |
| Nhập `0` vào số trang, độ tuổi hoặc số lượng nhập mới bị yêu cầu nhập lại | Các trường này phải lớn hơn `0`; lựa chọn menu vẫn cho phép `0` để quay lại. Một số thao tác điều chỉnh tồn hoặc số lượng giỏ cho phép `0` theo ý nghĩa riêng. |
| Nạp lại kho không tăng số lượng | Mã hàng đã có bị bỏ qua có chủ ý để tránh nhân đôi tồn. Dùng chức năng thêm/sửa tồn nếu cần tăng số lượng. |
| Phiếu cảnh báo nhiều NCC | Mô hình hiện chỉ có một NCC chính/phiếu. Các NCC của dòng khác vẫn được ghi nhận trong danh sách. |
| Không thanh toán được hóa đơn lịch sử chưa trả tiền | Mặt hàng lịch sử không có tồn trên kệ. Không đổi mã hoặc trừ kho tùy tiện trên hóa đơn cũ; kiểm tra lại danh mục và lập giao dịch phù hợp. |

Các danh sách chủ yếu sống trong **bộ nhớ của phiên chạy** và dùng mảng Java. TXT không phải cơ sở dữ liệu giao dịch: hãy ghi ra file sau khi thay đổi cần giữ, và xem lại đường dẫn trước khi ghi đè. Tài khoản và mật khẩu trong TXT chỉ phù hợp với môi trường demo/học tập.
