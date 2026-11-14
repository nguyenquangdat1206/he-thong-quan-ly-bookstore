# Kết quả kiểm thử V3 — chuẩn hóa cấu trúc TXT

- Thực thi trong môi trường Linux, OpenJDK 21, Python 3; chạy `bash scripts/test.sh` từ thư mục gốc.
- **16/16** ca `IntegrationTests.java`: PASS.
- **22/22** ca `FeatureNCCPhieuNhapTests.java`: PASS.
- **9/9** kịch bản `tests/test_console.py`: PASS, gồm đăng nhập, phân quyền, bán hàng, đọc dữ liệu mặc định và bảo vệ file nhân viên cũ không tương thích.
- **7/7** kịch bản `tests/test_console_pn.py`: PASS, gồm NCC tự sinh, đọc phiếu nhập lặp, ghi file, file lỗi, hạn chế menu theo vai trò.
- Tổng cộng **54/54 ca/kịch bản qua thử nghiệm, 0 thất bại** trong bộ test có sẵn sau khi bổ sung các kiểm tra đường dẫn/bảo toàn dữ liệu.
- Các test dùng bộ TXT cố định trong `tests/fixtures/` và thư mục tạm; không đọc hay ghi đè sáu file `data/*.txt` của nhánh `main`.

### Giới hạn

- Chưa có bản đầy đủ của `data/*.txt` trên nhánh `main` trong môi trường kiểm thử; cần chạy `py -3 scripts\audit_data.py` và test lại trên máy có bản clone thật trước khi merge PR.
- Không khẳng định đã thử hết tất cả ca đầu vào người dùng có thể tạo.
- Lược đồ phiếu nhập vẫn chỉ có một NCC cấp phiếu; các cảnh báo NCC khác nhau trong một phiếu vẫn được giữ, không thay đổi nghiệp vụ TV4.
- Nếu dùng IntelliJ, đặt Working directory là thư mục gốc project; đường dẫn là tương đối tới `data/`.
