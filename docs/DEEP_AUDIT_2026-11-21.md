# Rà soát toàn bộ dự án — 21/11/2026

## Phạm vi

Đọc 52 tệp Java trong src, kiểm tra dữ liệu TXT thực tế, cơ chế lưu phiên, các điểm vào console, script chạy/kiểm thử, cấu hình IDE và tài liệu. Không chỉ dựa vào bộ test sẵn có: 19 tình huống lỗi mới đã được tái hiện trên mã trước khi sửa, sau đó bổ sung thêm các ca biên và kiểm thử console/dữ liệu.

Các thử nghiệm ghi dữ liệu chạy trong thư mục tạm. Không thay đổi tài khoản, mật khẩu hoặc nội dung các tệp đầu vào trong data.

Điều chỉnh tổ chức mã sau rà soát: phần chuyển đổi dùng chung đã chuyển từ lớp DuLieuMatHang trong mathang sang [BoChuyenDoiMatHang](../src/tienich/BoChuyenDoiMatHang.java) trong tienich. Kho và bộ lưu phiên gọi tiện ích này; giữ nguyên định dạng dữ liệu và các lớp sản phẩm.

## Các lỗi và thay đổi đã xử lý

| Nhóm | Vấn đề xác minh được | Cách xử lý |
| --- | --- | --- |
| Dữ liệu thật | Cả 50 dòng phiếu nhập dùng dd-MM-yyyy bị bộ đọc chỉ nhận dd/MM/yyyy bỏ qua | Nhận cả hai định dạng với ngày nghiêm ngặt; tệp xuất dùng dd/MM/yyyy |
| Nhập tay | Tiểu thuyết và truyện bị nhập hai lần do kiểm tra cả lớp cha và lớp con | Gọi phương thức đa hình đúng một lần |
| Duyệt phiếu | Tổng int tràn, nhập một phần nhưng vẫn báo duyệt thành công | Tính tổng bằng long, kiểm tra toàn bộ dòng và sức chứa trước khi thay đổi kho |
| Phiếu rỗng | Phiếu không có hàng hoặc có số lượng 0 vẫn được duyệt | Từ chối duyệt, giữ trạng thái chờ; nhập tay yêu cầu số lượng dương |
| Metadata kho | Kho giữ chung đối tượng hàng với phiếu | Kho giữ bản sao riêng |
| Tồn kho | Giảm số lượng âm lại làm tăng tồn | Từ chối giảm âm, kiểm tra giới hạn khi tăng |
| Nhập phiếu | Chấp nhận mã phiếu rỗng, số trang/độ tuổi sai; danh sách sức chứa 0 không tăng được | Kiểm tra trước khi thêm phiếu/NCC; bảo đảm mảng tăng được |
| Lịch sử phiếu | Đọc lại có thể bổ sung hàng chưa nhập kho vào phiếu đã duyệt | Khóa bổ sung dòng cho phiếu đã xử lý có sẵn |
| Thanh toán | Xóa hàng khỏi kệ nhưng đơn vẫn trừ đối tượng cũ và thu tiền | Đối chiếu mã đã chốt với kệ hiện tại trước khi thu tiền |
| Hóa đơn nhập trước hàng | Nhập đơn lịch sử chưa trả rồi bổ sung hàng lên kệ vẫn không trả được | Nối lại mã hàng với tồn thực tế khi thanh toán |
| Lịch sử hóa đơn | Đổi mã/tên hàng trên kệ làm thay đổi đơn cũ, kể cả sau lưu/khôi phục | Chốt mã/tên hàng, đơn giá và thông tin khách; lưu tên người lập; khóa số lượng dòng đã trả |
| Mã hóa đơn | Mã trống có thể chốt giỏ nhưng khiến bản lưu không khôi phục được | Từ chối mã trống ở danh sách hóa đơn |
| Báo cáo | Mảng cố định 1.000 SKU gây lỗi; lượng bán cộng int có thể tràn | Tự tăng mảng báo cáo, cộng số lượng bằng long |
| Giá bán | Math.round bão hòa long ở giá lớn; NaN có thể thành giá 0 | Dùng hàm tính giá chung, giữ giá lớn đúng độ lớn, từ chối giá/tổng không hữu hạn |
| UTF-8 | BOM trong mã nhân viên/chi nhánh làm đăng nhập hoặc tra cứu thất bại | Loại BOM đầu vào; chuẩn hóa mã nhân viên trước kiểm tra trùng |
| Khách hàng | Đọc file lỗi có thể xóa danh sách đang dùng | Đọc UTF-8 nghiêm ngặt vào danh sách tạm, chỉ thay khi đọc xong |
| Nhân sự | Nhập file không tồn tại vẫn đăng xuất; nhập toàn dòng lỗi có thể mất ADMIN | Giữ dữ liệu và phiên khi thất bại; chặn thay danh sách đang có bằng danh sách rỗng/mất ADMIN cuối |
| Lưu tệp | Nhân viên/chi nhánh ghi trực tiếp; lỗi ghi không được báo về luồng thoát | Ghi qua file tạm rồi thay nguyên tử, kiểm tra trường TXT trước khi ghi, trả lỗi về Main |
| Nguồn chi nhánh lỗi | Đọc lỗi rồi tạo mặc định/ghi đè tệp gốc | Giữ nguyên nguồn lỗi, không tự ghi đè |
| Kết thúc console | Hết đầu vào gây exception và mất dữ liệu chưa lưu | Main bắt EOF và thực hiện lưu; nếu lưu lỗi trả mã khác 0 |
| Đường dẫn | Đường dẫn người dùng không hợp lệ làm dừng menu; mã chi nhánh có thể chứa đường dẫn | Kiểm tra đường dẫn và hỏi lại; chặn thành phần đường dẫn trong mã chi nhánh |
| NCC | Menu trỏ tới nhacungcap.txt không tồn tại thay vì dữ liệu hiện có | Dùng nhacungcap_input.txt mặc định |
| Kho độc lập | Bộ đọc kho bị thiếu; menu mặt hàng độc lập không có cách đưa dữ liệu vào kho | Thêm bộ đọc đủ 11 loại; mục 10 của Kho, mục 12 của mặt hàng độc lập |
| Audit | Công cụ chỉ kiểm tra 6 tệp và báo đạt dù không khớp bộ đọc Java | Kiểm tra 10 tệp, số cột từng loại, số hữu hạn, trùng mã, ngày và tham chiếu |
| Chuyển đổi cũ | Preview chấp nhận lương NaN/Infinity hoặc thiếu trường bắt buộc | Từ chối trước khi tạo bản xem trước |
| Script/IDE | Không cố định target Java 17; mapfile không có trong Bash 3.2; cấu hình nguồn IDE chưa rõ | Biên dịch --release 17, dùng vòng đọc tương thích Bash 3.2, tách đầu ra IDE và test |
| Tài liệu | README mô tả nguồn nhân viên, số menu, định dạng ngày, module và lưu phiên đã lỗi thời | Viết lại hướng dẫn theo luồng hiện tại, giữ báo cáo cũ làm lịch sử |

## Kiểm chứng

Kết quả: **243 ca riêng biệt đạt, 0 ca thất bại; 10/10 tệp dữ liệu hợp lệ đạt audit.**

| Bộ kiểm thử | PASS | FAIL |
| --- | ---: | ---: |
| IntegrationTests | 23 | 0 |
| FeatureNCCPhieuNhapTests | 26 | 0 |
| LegacyProductRegressionTests | 6 | 0 |
| DisplayLogicRegressionTests | 15 | 0 |
| FullFunctionalAuditTests | 55 | 0 |
| PersistenceRegressionTests | 11 | 0 |
| DeepAuditRegressionTests | 27 | 0 |
| test_console.py | 9 | 0 |
| test_console_pn.py | 7 | 0 |
| test_legacy_runtime.py | 1 | 0 |
| test_console_unicode.py | 5 | 0 |
| test_full_console_audit.py | 39 | 0 |
| test_data_audit.py | 9 | 0 |
| test_deep_console.py | 10 | 0 |
| **Tổng** | **243** | **0** |

Đợt chạy mới nhất sau khi chuyển lớp tiện ích đã thực thi đủ 14 bộ và audit. Lượt test.bat đầu đạt 242 ca, có một timeout 15 giây ở ca tạo quản lý chi nhánh, nên lệnh tổng trả mã 1. Khi tăng ngưỡng lên 30 giây nhưng vẫn thu đầu ra bằng pipe, nhóm console gặp một timeout ở ca nhập hóa đơn lỗi, dù đầu ra đã chứa hóa đơn hợp lệ và thông báo Tạm biệt. Hai ca này đều chạy riêng thành công nhiều lần, khoảng 8,2–8,4 giây; chưa tái hiện lỗi nghiệp vụ.

Bộ test console sau đó được chuyển sang thu stdout/stderr vào tệp tạm, chờ trực tiếp trạng thái tiến trình, giữ ngưỡng 30 giây và in tiến độ ngay khi từng ca hoàn tất. Toàn bộ nhóm đã chạy lại đạt **39 PASS, 0 FAIL**, mã thoát 0; timeout không tái diễn trong lượt này. Các kiểm tra mã thoát, phân quyền, hóa đơn, tồn kho, tệp lưu và việc giữ nguyên dữ liệu nguồn được giữ đầy đủ. Không sửa mã nghiệp vụ để xử lý timeout của bộ test.

Kết quả cuối cùng 243 phía trên kết hợp các bộ đã đạt trong lượt đầy đủ và kết quả chạy lại bộ console bị ảnh hưởng; đếm theo ca riêng biệt, không cộng lặp những lần chẩn đoán/chạy lại. Không coi lượt test.bat ban đầu là một lượt thành công trọn vẹn.

Biên dịch toàn bộ src và tests bằng javac --release 17 -Xlint:all: không có lỗi hoặc cảnh báo. VS Code Problems không còn lỗi; git diff --check đạt. Dữ liệu đầu vào trong data không có thay đổi so với Git; test console cũng kiểm tra byte của các tệp nguồn trong bản sao tạm.

Môi trường thực thi: Windows, JDK 20.0.2 với target Java 17, Python 3.14.3. Đã kiểm tra cú pháp hai script shell bằng Git Bash; chưa chạy toàn bộ suite trên Linux/macOS hoặc JVM 17.

Các bộ mới:

- [DeepAuditRegressionTests](../tests/DeepAuditRegressionTests.java): tái hiện lỗi nghiệp vụ/API, dữ liệu thật và các ca biên.
- [test_data_audit.py](../tests/test_data_audit.py): kiểm tra bộ audit và công cụ preview.
- [test_deep_console.py](../tests/test_deep_console.py): chạy dữ liệu thật trong thư mục tạm, khởi động lại, EOF, lỗi lưu, bản lưu/chi nhánh hỏng và menu kho độc lập.

Lệnh chuẩn: test.bat trên Windows; scripts/test.sh trên Linux/macOS. Audit dữ liệu là một bước bắt buộc của hai script. Bộ kiểm thử không cần dependency ngoài JDK/Python.

## Giới hạn còn lại

- Đây là ứng dụng học tập chạy một tiến trình. Chưa có khóa chống hai tiến trình cùng ghi hoặc cơ sở dữ liệu giao dịch.
- Thay thế nguyên tử áp dụng cho từng tệp. Chưa có giao dịch nguyên tử chung giữa bản lưu phiên, tệp nhân viên và tệp chi nhánh; chưa thử nghiệm mất điện hoặc dừng tiến trình tại mọi thời điểm.
- Mật khẩu TXT vẫn là văn bản rõ theo mô hình demo hiện tại. Thanh toán thẻ là mô phỏng, không kết nối ngân hàng.
- TXT lịch sử chỉ có mã khách/SKU nên không thể khôi phục những tên cũ chưa từng được ghi. Bản lưu phiên mới giữ tên đã chốt.
- Nếu bản lưu phiên hỏng, chương trình bảo vệ tệp và bỏ qua tự lưu phiên mới; cần xử lý bản lưu đó trước khi tiếp tục dùng cơ chế phục hồi.
- Không có kết quả kiểm thử nào chứng minh phần mềm hoàn hảo với mọi đầu vào. Báo cáo này nêu các lỗi đã tái hiện/sửa và phạm vi kiểm chứng cụ thể.
