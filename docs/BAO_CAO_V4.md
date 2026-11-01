# Báo cáo tích hợp OOP V4 — 01/11/2026

## Đã xử lý

- Loại class trùng cùng package/tên trong `src/model`, sử dụng các bản hợp nhất ở `src/`. Đã bảo toàn 5 class/interface trước đó chỉ có trong `src/model`: `DoChoi`, `DungCuHocTap`, `TrangTri`, `Vo`, `IThaoTacThucThe`.
- Chuẩn hóa dữ liệu chạy bằng `data/`; bộ dữ liệu lỗi chỉ đặt trong `tests/fixtures/`; không nhân đôi các folder nghiệp vụ chứa TXT ở root.
- Không bắt buộc ghi đè hoặc chuyển đổi `data/nhanvien.txt`/`data/phieunhap_input.txt` cũ: mã nguồn đọc được cả các định dạng legacy. Chặn ghi đè mặc định vào file nhân viên định dạng cũ khi thoát hoặc khi xuất ngay trên menu.
- Sửa script Java Windows `test.bat`, `run.bat` để tránh sai đường dẫn chứa dấu `\`; sửa test console để xử lý UTF-8.
- Giữ các nghiệp vụ chính TV2/TV3/TV4 và cập nhật TV5 (nhân sự, menu, phân quyền, tích hợp); kết nối NCC/phiếu nhập.

## Kiểm thử thực tế trên bản V4 độc lập

- Java `IntegrationTests`: 17 PASS, 0 FAIL.
- Java `FeatureNCCPhieuNhapTests`: 23 PASS, 0 FAIL.
- Console `test_console.py`: 9 PASS, 0 FAIL.
- Console `test_console_pn.py`: 7 PASS, 0 FAIL.
- Console tương thích dữ liệu cũ `test_legacy_runtime.py`: 1 PASS, 0 FAIL.
- **Tổng: 57 PASS / 0 FAIL** (OpenJDK 21, Python 3).
- Audit dữ liệu demo: 6/6 tệp đúng cấu trúc.
- Kiểm tra class trùng: 0 class trùng trong gói độc lập.

## Phạm vi chưa xác minh

- Không thể truy cập trực tiếp repository GitHub mới nhất hoặc thực thi trên laptop người dùng trong phiên này; cần chạy sync + test trên checkout địa phương trước khi push.
- Các file nghiệp vụ cũ có nội dung khác bản hợp nhất cần thành viên TV2–TV4 xác nhận không có chức năng quan trọng bị bỏ qua.
- Chưa có bằng chứng đủ cho khẳng định mọi tình huống đầu vào đều không còn lỗi; kết quả là phạm vi 57 ca kiểm thử đã chạy.
- Quan hệ nhiều NCC cho một phiếu nhập vẫn là giới hạn của mô hình nghiệp vụ hiện tại.

## An toàn dữ liệu

Công cụ `sync_to_repo.py` chỉ đồng bộ source, test và docs; giữ nguyên mọi file hiện có trong `data/` và sao lưu nguồn trước khi thay đổi. Không tự git commit/push/merge.
