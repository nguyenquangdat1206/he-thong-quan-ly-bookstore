# BÁO CÁO CẬP NHẬT V2 — NHÀ CUNG CẤP / PHIẾU NHẬP

**Bản gốc của vòng cập nhật:** `FINAL_OOP_QUAN_LY_NHA_SACH_TV5(1).zip` do người dùng gửi; các file bổ sung `menuPN.txt`, `taoNCC.txt`, `docfileDSPN.txt` và yêu cầu getter `getDanhSachNCC()`. Những file liên quan đến các thành viên khác chỉ được sửa khi cần tích hợp, xử lý lỗi rõ ràng; không thay thế module của họ bằng bản nháp TV5.

## 1. Đã tích hợp đúng các yêu cầu

| Yêu cầu từ nhóm | Tích hợp vào | Trạng thái và cách xử lý |
|---|---|---|
| `public DanhSachNCC getDanhSachNCC(){ return dsNCC; }` | `src/phieunhap/DanhSachPhieuNhap.java` | Đã có getter để Main truy cập **danh sách NCC dùng chung**, không tạo mảng NCC thứ hai. |
| `menuPN.txt`: thêm mục **10. Xem danh sách nhà cung cấp trong lần nhập này** | `src/Main.java` | Thay menu phiếu nhập cũ; thêm `case 10: dspn.getDanhSachNCC().xuat(); break;`. Có **`break`** mà đoạn gốc chưa có, tránh rơi sang `case 0`. Cả Admin/Nhập kho truy cập được. |
| `taoNCC.txt`: tạo mã NCC và tránh tên trùng | `src/phieunhap/PhieuNhap.java` và `src/nhacungcap/DanhSachNCC.java` | PhieuNhap `taoNCC` trả về NCC hiện có theo tên (so sánh nội dung, không phải `==`) hoặc tạo NCC mới có mã `NCC0`, `NCC1`,...; tăng biến đếm, tăng sức chứa mảng khi cần, không trùng mã hiện hành. Việc thêm mảng giữ đúng encapsulation trong `DanhSachNCC`. |
| `docfileDSPN.txt`: đọc PN từ file và liên kết NCC | `src/phieunhap/DanhSachPhieuNhap.java` | Xây dựng lại phần đọc theo ý đoạn cung cấp, tạo `PhieuNhap`, các loại mặt hàng, NCC, **gán NCC vào `ChiTietPhieuNhap`**, chia sẻ danh sách chung. Bổ sung kiểm tra dữ liệu, chống đọc trùng, ngày linh hoạt và báo lỗi theo dòng. |

**Các chỉnh sửa liên quan:** chức năng thêm PN thủ công trước đây chưa gán mã/ngày/NCC, đã thêm bước nhập thông tin vào `DanhSachPhieuNhap.them()`; file PN xuất theo `dd-MM-yyyy` để đọc lại được, đồng thời vẫn nhận ngày ISO `yyyy-MM-dd` do bản cũ đã xuất. File NCC xuất dạng UTF-8 và đọc file NCC có chống trùng mã/tự tăng mảng. Mục nhập kho bỏ qua phiếu đã xử lý mà không hỏi xác nhận thừa. `PhieuNhap.them()` không chèn phần tử null nếu người dùng chọn sai loại hàng.

## 2. Lỗi và rủi ro phát hiện qua đối chiếu code

1. **Lỗi dùng `==` so sánh chuỗi tên NCC** ở code cũ: hai String cùng nội dung có thể khác địa chỉ bộ nhớ -> tạo NCC lặp. **Đã sửa** bằng `equalsIgnoreCase` và `trim()`.
2. **Không tăng `soLuongNCC` và không tạo mã NCC** ở code cũ, làm `xuat()` không thể nhìn thấy NCC được thêm. **Đã sửa**. Bản snippet tự sinh ID dùng trực tiếp biến đếm; có thể trùng ID hiện hành sau khi xóa hoặc thêm thủ công. **Đã kiểm tra mã hiện hành trước khi cấp**.
3. **Thiếu `setNhaCC(ncc)`** trong hàm đọc PN bản gốc: PN vẫn giữ NCC rỗng dù đã tạo NCC trong danh sách. **Đã sửa** theo đoạn nhóm gửi.
4. **Đọc một file PN hai lần** trước đây thêm phiếu và các mặt hàng trùng. **Đã sửa**: một mã PN trong bộ nhớ chỉ có một dòng hàng với một mã SKU; nhập lại cùng file không nhân đôi kho.
5. **Không bắt được lỗi dữ liệu nhập**: thiếu cột, ngày sai, trạng thái không nằm trong enum, giá NaN/âm, số lượng không hợp lệ có thể ném exception sau khi đã tăng biến đếm PN. **Đã sửa**: kiểm tra trước khi thêm, bỏ dòng lỗi và tiếp tục đọc dòng sau.
6. **Đọc file xuất PN thất bại:** hàm ghi cũ in ngày ISO, hàm đọc chỉ hiểu `dd-MM-yyyy`. **Đã sửa** tương thích cả hai và ghi về `dd-MM-yyyy`.
7. **Đoạn menuPN.txt thiếu `break` sau case 10**: đã thêm để không vô tình chạy tiếp lệnh trong switch.
8. **Thêm PN từ console bị lưu mã `trống` và NCC rỗng**: đã thêm bước nhập mã PN duy nhất, ngày, tên NCC trước nhập sản phẩm; không lưu phiếu trống.

## 3. Giới hạn thiết kế cần TV4 chốt trước khi nộp

**Một phiếu có thể chứa hàng từ nhiều nhà cung cấp trong dữ liệu mẫu.** Ví dụ `PN001` có Kim Đồng và Hồng Hà. Nhưng `PhieuNhap` hiện chứa duy nhất `ChiTietPhieuNhap.nhaCungCap`. Để không âm thầm ghi đè, trình đọc **giữ NCC của dòng đầu tiên** cho trường `chiTiet.nhaCungCap`, đồng thời tạo/giữ **tất cả các tên NCC còn lại** trong danh sách NCC chung và in cảnh báo mỗi lần gặp NCC khác. Vì thế **không được xem trường `nhaCungCap` duy nhất trên phiếu là quan hệ nhà cung cấp đầy đủ cho từng mặt hàng**.

Nếu giảng viên yêu cầu một phiếu có nhiều NCC thì phải thống nhất với TV4 một thay đổi mô hình (`NCC` trên từng dòng hoặc tách PN theo NCC). Bản V2 **chưa tự thay đổi mô hình dữ liệu và format TXT** nhằm giữ cấu trúc nhóm đã viết. Hơn nữa, tự sinh NCC từ *nhà sản xuất/nhà xuất bản* trong file là giả định đã có ở snippet nhóm gửi; nhà xuất bản chưa chắc là bên cung cấp thực tế.

- Các file giao dịch/kho vẫn cần gọi **đọc/ghi file thủ công** qua menu (nhân sự được tự động tải/lưu theo bản cũ).
- Chỉ có quyền thao tác ở menu console, chưa phải bảo mật ứng dụng thực tế; mật khẩu demo ở `nhanvien.txt` vẫn dạng văn bản.
- **Giới hạn lưu NCC khi thêm PN thủ công:** định dạng `phieunhap.txt` hiện chỉ lưu các cột mặt hàng và không có cột tên/mã NCC cấp phiếu riêng. Khi đọc lại, NCC được suy ra từ nhà xuất bản/nhà sản xuất trên dòng hàng, **không khôi phục được một tên NCC nhập tay khác với các tên này**. Muốn lưu chính xác NCC cấp phiếu, nhóm TV4 phải thống nhất định dạng TXT mới và cách đọc tương thích.
- Bản hiện tại bỏ qua SKU đã có trong PN khi đọc lại: **không tự đồng bộ sửa giá/số lượng**. Nếu cần cập nhật mặt hàng trên phiếu, sử dụng chức năng sửa theo module TV4.
- Một số nghiệp vụ khác trong TV2–TV4 vẫn cần các bạn sở hữu đối chiếu đề chấm (đặc biệt yêu cầu 9 chức năng TV2) và thử thêm thủ công ngoài bộ test tự động.

## 4. Kiểm thử thực tế — kết quả lần cuối

Môi trường: OpenJDK 21; biên dịch với `javac` (source Java tương thích JDK 17), chạy bộ kiểm thử không dùng thư viện ngoài.

| Bộ kiểm thử | Kết quả |
|---|---|
| `tests/IntegrationTests.java`: nhân sự, vai trò, kho, giỏ hàng, mặt hàng, hóa đơn, NCC | **16 PASS / 0 FAIL** |
| `tests/FeatureNCCPhieuNhapTests.java`: getter, mã NCC, trùng tên, sức chứa, 7 loại hàng, file mẫu, đọc/ghi, đọc lặp, sai dữ liệu, NCC nhiều tên, nhập kho lặp, nhập thủ công... | **22 PASS / 0 FAIL** |
| `tests/test_console.py`: luồng console cũ & thanh toán | **7 PASS / 0 FAIL** |
| `tests/test_console_pn.py`: Admin/Nhập kho xem NCC, nhập file 2 lần, ghi file, file vắng, thêm PN tay, giới hạn quyền bán hàng | **7 PASS / 0 FAIL** |
| **Tổng** | **52 bài/kịch bản PASS, 0 FAIL** |

Kiểm thử bao phủ 10 dòng của file mẫu `phieunhap/phieunhap_input.txt` (4 phiếu, 9 NCC duy nhất), đọc file hai lần, chu trình ghi/đọc, ISO date và `dd-MM-yyyy`, mã trùng, đầu vào lỗi, mảng NCC lớn hơn 100, kho không nhập phiếu hai lần. Các test chạy từ thư mục project và không cần mạng/cơ sở dữ liệu.

**Hướng dẫn chạy lại trên Windows:** cài JDK 17+ rồi chạy `test.bat` (cần Python 3 để chạy thêm hai bộ test console); trên Linux/macOS chạy `bash scripts/test.sh`. Để chạy app: `run.bat` hoặc `java -jar QuanLyNhaSach.jar` từ thư mục gốc. Không khẳng định tuyệt đối không tồn tại lỗi ngoài các trường hợp đã kiểm thử.

## 5. Phạm vi nguồn được sửa trong lần cập nhật V2

- `src/Main.java`: mục menu xem NCC từ PN.
- `src/phieunhap/DanhSachPhieuNhap.java`: getter, đọc/ghi PN, thêm PN thủ công, kiểm tra, phòng trùng và kết nối NCC.
- `src/phieunhap/PhieuNhap.java`: tạo/tìm NCC, bỏ thao tác thêm hàng null.
- `src/nhacungcap/DanhSachNCC.java`: cấp mã an toàn, chống trùng, UTF-8, hỗ trợ danh sách lớn.
- `tests/FeatureNCCPhieuNhapTests.java`, `tests/test_console_pn.py`: test mới.
- `test.bat`, `scripts/test.sh`: chạy cả test cũ và mới.
- `README.md`, `docs/BAO_CAO_CAP_NHAT_NCC_PHIEUNHAP_V2.md`, `docs/KET_QUA_KIEM_THU_V2.txt`: tài liệu.
- `QuanLyNhaSach.jar`: build lại từ mã V2.

**Tuyệt đối chưa push lên GitHub.** Nên đưa thay đổi lên một nhánh mới, để TV4 review bốn file nghiệp vụ trước khi lập pull request sang `main`.
