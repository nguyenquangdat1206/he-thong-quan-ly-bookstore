# Hệ thống quản lý nhà sách

Ứng dụng console tiếng Việt bằng Java 17+, không dùng Maven, Gradle, cơ sở dữ liệu hoặc thư viện ngoài JDK. Mã nguồn và dữ liệu TXT dùng UTF-8.

## Chạy và kiểm thử

Cần JDK 17+ với cả java và javac trên PATH. Python 3 chỉ cần cho kiểm thử console và audit dữ liệu; các script Python không cần package ngoài.

Trên Windows, chạy tại thư mục gốc:

~~~powershell
.\run.bat
.\test.bat
py -3 -X utf8 scripts\audit_data.py
~~~

Trên Linux/macOS:

~~~bash
bash scripts/run.sh
bash scripts/test.sh
python3 scripts/audit_data.py
~~~

Các script biên dịch với --release 17 vào thư mục build. Bộ kiểm thử gồm 7 chương trình Java, 7 script Python và bước audit 10 tệp dữ liệu. Nếu thiếu Python launcher py trên Windows, test.bat trả mã 2 và báo kiểm thử chưa đầy đủ. Mã 0 chỉ được trả khi tất cả bước đạt.

Trong IDE, chọn src làm source root, tests làm test source root, đặt working directory là thư mục gốc dự án. [Cấu hình VS Code](.vscode/settings.json) tách đầu ra của IDE vào build/ide để tránh xung đột với lệnh kiểm thử.

Kết quả và phạm vi đợt rà soát mới nhất: [báo cáo rà soát](docs/DEEP_AUDIT_2026-11-21.md). Các báo cáo cũ trong docs ghi lại lịch sử và có thể mô tả menu/định dạng của phiên bản trước.

Đợt rà soát này xác nhận 243 ca kiểm thử riêng biệt đạt, 0 thất bại; audit 10/10 tệp dữ liệu đạt. Xem báo cáo để biết cách tổng hợp và giới hạn môi trường kiểm chứng.

## Dữ liệu lúc khởi động và khi thoát

[Main](src/Main.java) đọc chi nhánh từ [chinhanh.txt](data/chinhanh.txt), nhân viên từ [nhanvien_cn.txt](data/nhanvien_cn.txt), rồi khôi phục toàn bộ dữ liệu nghiệp vụ từ data/phien.properties nếu có bản lưu hợp lệ.

- Nhân viên hiện dùng 8 cột; chương trình còn đọc được định dạng 7 cột và mô tả khởi tạo Java cũ.
- [nhanvien.txt](data/nhanvien.txt) được giữ để tương thích lịch sử; Main không dùng tệp này làm nguồn tài khoản mặc định.
- Khi chưa có bản lưu phiên, kho, kệ, khách hàng, NCC, phiếu và hóa đơn bắt đầu rỗng. Dùng các menu để nạp dữ liệu.
- Khi thoát bình thường hoặc đầu vào console kết thúc, chương trình lưu kho, kệ từng chi nhánh, khách hàng, NCC, phiếu nhập và hóa đơn vào data/phien.properties.
- Bản lưu phiên và file nhân viên/chi nhánh được ghi qua file tạm rồi thay thế nguyên tử từng tệp. Nếu lưu thất bại, chương trình báo lỗi; khi còn đầu vào, người dùng có thể sửa và thử lại.
- Nếu bản lưu phiên lỗi, chương trình giữ nguyên tệp đó và báo không tự ghi đè. Dữ liệu mới của lần chạy này không được lưu vào bản phiên lỗi.
- File nhân viên hoặc chi nhánh đầu vào có dòng lỗi được bảo vệ khỏi tự ghi đè. Các tệp nhập kho, phiếu, khách hàng và NCC không bị sửa bởi lưu phiên.

TXT xuất bằng menu là bản xuất độc lập; dữ liệu phục hồi tự động nằm trong bản lưu phiên. Không xóa bản lưu phiên nếu vẫn cần dữ liệu nghiệp vụ của phiên trước.

Nếu chưa có file nhân viên mặc định, Main tạo bốn tài khoản demo:

| Mã | Mật khẩu demo | Vai trò |
| --- | --- | --- |
| ADMIN | admin123 | Admin toàn hệ thống |
| QL01 | ql123 | Quản lý CN01 |
| BH01 | bh123 | Bán hàng CN01 |
| KHO01 | kho123 | Nhập kho chung |

Nếu file nhân viên đã tồn tại, dùng tài khoản có trong tệp đó. Bảng demo không thay thế thông tin đăng nhập hiện có. Không ghi đè hoặc xóa tệp nhân viên để lấy mật khẩu demo.

## Phân quyền và chi nhánh

Mọi chi nhánh dùng chung kho, khách hàng, nhà cung cấp, phiếu nhập và danh sách nhân viên. Mỗi chi nhánh có kệ hàng và hóa đơn riêng; cùng mã SKU hoặc mã hóa đơn ở hai chi nhánh vẫn là hai dữ liệu bán hàng riêng.

| Vai trò | Quyền |
| --- | --- |
| ADMIN | Quản lý mọi module; chọn/đổi chi nhánh; quản lý nhân sự và chi nhánh |
| QUAN_LY | Mặt hàng, khách hàng, hóa đơn, giỏ tại chi nhánh được gán; xem kho chung và nhân viên chi nhánh |
| BAN_HANG | Xem mặt hàng, khách hàng, hóa đơn, giỏ tại chi nhánh được gán |
| NHAP_KHO | Kho, phiếu nhập, NCC; chọn chi nhánh để xem kệ |
| Khách vãng lai | Chọn chi nhánh và xem mặt hàng |

Nhân viên bán hàng/quản lý không được đăng nhập nếu mã chi nhánh được gán không tồn tại. Để lập hóa đơn, chi nhánh cần ít nhất một nhân viên bán hàng và khách hàng đã có trong danh sách.

## Menu hiện tại

Các số dưới đây là menu của Main; nhập 0 để quay lại hoặc đăng xuất.

| Menu | Chức năng |
| --- | --- |
| Kho | 1 xem; 2 thêm một hàng; 3 thêm nhiều; 4 sửa tồn; 5 xóa; 6 tìm; 7 sức chứa; 8 ghi file; 9 thống kê; 10 đọc kho |
| Phiếu nhập | 1 xem; 2 thêm một phiếu; 3 thêm nhiều; 4 sửa; 5 xóa; 6 duyệt nhập kho; 7 xem kho; 8 đọc file; 9 ghi file; 10 xem NCC; 11 tự nhập tất cả phiếu chờ và xuất phiếu đã nhập |
| Mặt hàng | 1 đếm mã trên kệ; 2 chuyển một mã từ kho; 3 chuyển hàng loạt; 4 trả về kho; 5 xem; 6 xóa; 7 sửa; 8 tìm mã; 9 lọc; 10 ghi file; 11 hàng trong kho chưa lên kệ |
| Khách hàng | 1 xem; 2 thêm; 3 thêm nhiều; 4 sửa; 5 xóa; 6 đọc file; 7 ghi file; 8 tìm; 9 top chi tiêu tại chi nhánh đang chọn |
| Hóa đơn | 1 xem; 2 lập qua giỏ; 3 sửa đơn chưa trả; 4 xóa đơn chưa trả; 5 đọc file; 6 ghi file; 7 thanh toán; 8 tra cứu/lọc; 9 doanh thu và hàng bán chạy |
| NCC | 1 xem; 2 thêm; 3 sửa; 4 xóa; 5 cập nhật liên hệ từ file; 6 ghi file |
| Nhân sự | 1 xem; 2 thêm; 3 sửa; 4 xóa; 5 tìm; 6 tổng lương; 7 nhập file; 8 xuất file; 9 xem theo chi nhánh |
| Chi nhánh | 1 xem; 2 thêm; 3 xóa chi nhánh trống; 4 đổi nơi làm việc; 5 nhập file; 6 xuất; 7 sửa địa chỉ; 8 xem nhân viên |
| Giỏ hàng | 1 thêm; 2 xem; 3 đổi số lượng; 4 xóa; 5 chốt hóa đơn |

Nhập Enter hoặc dấu chấm tại câu hỏi đường dẫn để dùng tệp mặc định hiển thị trên màn hình.

## Quy trình sử dụng

### Nạp kho và đưa hàng lên kệ

1. Đăng nhập Admin, chọn CN01.
2. Vào Kho → 10 để đọc [kho_input.txt](data/kho_input.txt), hoặc Phiếu nhập → 8 để đọc [phieunhap_input.txt](data/phieunhap_input.txt), rồi Phiếu nhập → 6/11 để duyệt.
3. Vào Mặt hàng → 2, nhập mã và số lượng chuyển lên kệ. Ví dụ SCH0001 từ tệp kho, hoặc SCH2001 từ tệp phiếu nhập hiện có.
4. Kiểm tra tồn kho và kệ riêng. Chuyển hàng giảm kho và tăng kệ; trả hàng làm ngược lại.

Kho chứa tối đa 10.000 đơn vị. Đọc lại cùng tệp kho bỏ qua mã đã có. Chuyển hàng loạt lấy 20% của từng mã còn ít nhất 15 đơn vị trong kho. Kho, kệ và phiếu giữ bản sao metadata riêng để việc sửa ở một nơi không âm thầm thay đổi nơi khác.

Ba chương trình độc lập vẫn dùng được: [QuanLyKho](src/kho/QuanLyKho.java), [QuanLyPhieuNhap](src/phieunhap/QuanLyPhieuNhap.java) và [QuanLyMatHang](src/mathang/QuanLyMatHang.java). Menu mặt hàng độc lập có mục 12 để đọc kho trước khi chuyển lên kệ. Các chương trình độc lập không dùng cơ chế lưu phiên của Main; xuất TXT trước khi thoát nếu cần giữ dữ liệu.

### Tạo và thanh toán hóa đơn

1. Khách hàng → 6 đọc [khachhang.txt](data/khachhang.txt).
2. Hóa đơn → 2 hoặc Giỏ hàng: chọn khách, thêm hàng có trên kệ, chốt với mã hóa đơn mới.
3. Hóa đơn → 7 để trả tiền mặt hoặc thanh toán thẻ mô phỏng.
4. Chỉ thanh toán thành công mới giảm hàng trên kệ. Đơn chưa trả tiền chưa giữ chỗ; nếu đơn khác đã mua hết hàng, thanh toán bị từ chối.
5. Đơn đã trả không được sửa, xóa hoặc thanh toán lại.

Giỏ giữ đơn giá lúc thêm hàng. Hóa đơn giữ mã/tên hàng và thông tin khách tại thời điểm lập; đổi tên/mã trên kệ không viết lại lịch sử hóa đơn. Khi thu tiền, hóa đơn đối chiếu mã đã chốt với kệ hiện tại của chi nhánh. Hàng bị xóa không thể bán bằng tham chiếu cũ.

### Phiếu nhập và NCC

Phiếu có ba trạng thái: CHO_DUYET, DA_DUYET, DA_HUY. Duyệt nhập kho kiểm tra toàn bộ dòng và tổng sức chứa trước khi thay đổi kho. Thiếu chỗ hoặc số lượng sai giữ phiếu chờ, không nhập dở dang. Phiếu đã duyệt không được sửa/xóa hay bổ sung dòng khi đọc lại.

Mỗi phiếu dùng một NCC chính. Tên NCC được lấy từ NXB/NSX của dòng hàng và các dòng cùng phiếu phải thống nhất. NCC → 5 mặc định đọc [nhacungcap_input.txt](data/nhacungcap_input.txt) để cập nhật liên hệ cho mã NCC đã có; thao tác này không tự tạo mã NCC mới. Nạp phiếu trước để tạo đủ NCC tương ứng dữ liệu mẫu.

## Định dạng dữ liệu

Tệp TXT phân cách bằng dấu chấm phẩy, mỗi bản ghi trên một dòng; không dùng dấu chấm phẩy hoặc xuống dòng trong các trường TXT. Bản lưu phiên Properties lưu được ký tự đặc biệt trong dữ liệu API. Giá phải hữu hạn và không âm; số lượng kho/kệ không âm, số lượng trên phiếu và hóa đơn phải dương.

### Mặt hàng và kho

~~~text
loai;maSKU;ten;giaNhap;soLuong;[thuocTinhRieng]
~~~

| Loại | Mã | Thuộc tính riêng theo thứ tự |
| --- | --- | --- |
| Sách | SCH + 4 chữ số | tác giả; NXB; thể loại |
| Sách giáo khoa | SGK + 4 chữ số | môn học; NXB |
| Tiểu thuyết | TTH + 4 chữ số | tác giả; NXB; thể loại |
| Truyện | TRU + 4 chữ số | tác giả; NXB; thể loại |
| Văn phòng phẩm | VPP + 4 chữ số | loại VPP; NSX |
| Báo tạp chí | BTC + 4 chữ số | thể loại; NXB |
| Khác | SPK + 4 chữ số | NSX |
| Đồ chơi | DCH + 4 chữ số | độ tuổi dương; NSX |
| Dụng cụ học tập | DCT + 4 chữ số | loại dụng cụ; NSX |
| Trang trí | TTR + 4 chữ số | NSX |
| Vở | VO + 4 chữ số | số trang dương; NSX |

Số lượng ghi trong tệp kho là tồn kho; số lượng ghi trong tệp kệ là hàng trên kệ. [danhsach_mathang.txt](data/danhsach_mathang.txt) và [mathang.txt](data/mathang.txt) là bộ dữ liệu mặt hàng bổ sung, không tự nạp lúc khởi động.

### Phiếu nhập

~~~text
maPN;ngayNhap;loai;maSKU;ten;giaNhap;soLuong;[thuocTinhRieng];trangThai
~~~

Bộ đọc nhận dd/MM/yyyy và dd-MM-yyyy; bộ ghi dùng dd/MM/yyyy. Không nhận ngày không tồn tại, ngày có dấu chấm hoặc ISO cho tệp phiếu nhập. Ngày nhập trên menu dùng dd/MM/yyyy; Enter lấy ngày hiện tại.

### Hóa đơn

~~~text
maHD;ngayISO;maKH;maNV;tenNV;daThanhToan
soDong
maSKU;soLuong;donGia
~~~

Ngày ISO là yyyy-MM-dd; trạng thái là 0 hoặc 1. Bộ đọc vẫn nhận đầu phiếu cũ 3 cột: maHD;ngayISO;maKH, và dòng hàng 2 cột nếu có mặt hàng trên kệ để lấy giá. Tệp xuất luôn có đơn giá.

Nạp khách hàng trước khi nhập hóa đơn TXT. Với mã hàng đã rời danh mục, đơn giá trong TXT giúp giữ lịch sử. Đơn chưa trả có thể thu tiền sau khi đúng mã hàng đã được đưa lên kệ; việc đọc đơn đã trả không trừ hàng lần nữa. TXT hóa đơn cũ chỉ có mã khách/SKU nên không mang đầy đủ tên lịch sử; bản lưu phiên giữ các tên đã chốt.

### Các thực thể khác

~~~text
Chi nhánh: ma;ten;diaChi
Nhân viên: ma;ten;sdt;matKhau;ca;luong;quyen;maChiNhanh
Khách hàng: ma;ten;sdt
Nhà cung cấp: ma;ten;diaChi;sdt;email
~~~

Mã chi nhánh không được chứa thành phần đường dẫn. Nhập lại khách hàng/nhân viên thay danh sách tương ứng; nhập chi nhánh chỉ bổ sung mã chưa có. Nhập nhân viên thất bại giữ phiên hiện tại; nhập thành công kết thúc đăng nhập để xác thực lại và khóa tự lưu nguồn nhân sự cho tới khi người dùng xuất dữ liệu chủ động.

## Cấu trúc và phạm vi kiểm chứng

- [src](src): 52 tệp Java, gồm nghiệp vụ, menu, interface, chuyển đổi dữ liệu và lưu phiên.
- [tests](tests): các bộ Java/Python; fixture và dữ liệu tạm dùng độc lập với dữ liệu nguồn.
- [scripts](scripts): chạy, kiểm thử, audit và tạo bản xem trước dữ liệu cũ.
- [data](data): 10 tệp dữ liệu hợp lệ cùng 3 tệp cố ý có lỗi cho thử nghiệm.
- [docs](docs): báo cáo hiện tại và lịch sử. Tệp PN999 ở gốc là đầu ra cũ, không được Main tự đọc.
- [prepare_legacy_data.py](scripts/prepare_legacy_data.py) chỉ tạo bản xem trước trong thư mục tạm, không thay dữ liệu nguồn.

Audit kiểm tra định dạng, SKU, số lượng/giá, ngày, trùng mã và tham chiếu khách hàng/chi nhánh. Các tệp có hậu tố _loi.txt được dùng để kiểm tra từ chối dữ liệu lỗi.

Đây là ứng dụng học tập OOP. Mật khẩu TXT là văn bản rõ, thanh toán thẻ là mô phỏng. Lưu nguyên tử áp dụng cho từng tệp, chưa phải giao dịch chung trên nhiều tệp; chưa hỗ trợ nhiều tiến trình cùng ghi hoặc phục hồi sau mất điện. Kết quả kiểm thử xác nhận các kịch bản đã chạy, không phải chứng minh không còn lỗi với mọi đầu vào.
