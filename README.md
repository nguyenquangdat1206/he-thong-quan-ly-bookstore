# Quản lý nhà sách — OOP TV1–TV5 / Bản đồng bộ V4

Java 17+, giao diện dòng lệnh. Cấu trúc: `src/` (mã nguồn), `data/` (dữ liệu), `tests/` (test), `tests/fixtures/` (bộ dữ liệu thử lỗi), `scripts/` (công cụ).

## Chạy trên Windows

- Mở thư mục này bằng IntelliJ IDEA, chạy `Main.java` từ working directory là thư mục gốc của project, **hoặc** chạy `run.bat`.
- Kiểm thử: `test.bat` (cần JDK 17+ và Python 3 để chạy đầy đủ test console).
- Linux/macOS: `bash scripts/test.sh` hoặc `bash scripts/run.sh`.

## Dữ liệu nhóm cũ

Ứng dụng nhận định dạng TXT nhân viên 7 trường và cả định dạng cũ `dsNhanVien.them(new ...);` nhưng **không thực thi mã Java** từ TXT. Trường hợp định dạng cũ được nhận diện, ứng dụng khóa tự ghi đè file nguồn khi thoát; quản trị viên muốn xuất dữ liệu theo mẫu mới phải chọn file đầu ra **khác**. Phiếu nhập nhận ngày `dd-MM-yyyy`, `dd/MM.yyyy`, `yyyy-MM-dd`.

Trên bản ZIP độc lập, `data/` là dữ liệu **demo** (tài khoản `ADMIN`, mật khẩu `admin123`; chỉ dùng minh họa, không phải môi trường thật). Khi đồng bộ vào nhánh Git của nhóm bằng `sync_to_repo.py`, mọi file `data/` hiện có **được giữ nguyên byte**.

## Trường hợp cần nhóm xác nhận

- Một mã phiếu nhập đang gắn **một NCC chính**. Với nhiều NCC trong cùng phiếu, danh sách NCC vẫn ghi nhận tất cả nhưng phiếu chỉ giữ NCC đầu tiên và in cảnh báo. Thay đổi mô hình một-phiếu-nhiều-NCC cần TV4 xác nhận.
- Có 37 cặp class cũ/mới cùng tên, trong đó nhiều file khác nội dung. Bản V4 dùng một bộ duy nhất trong `src/<package>` và giữ 5 file loại hàng/interface chưa có bản thay thế. Trưởng nhóm/TV2–TV4 vẫn cần review khác biệt nghiệp vụ trước khi merge vào `main`.
- Dữ liệu TXT mật khẩu dạng rõ chỉ phù hợp demo; không dùng tài khoản thật và không công khai mật khẩu thật.

## Đồng bộ với repository nhóm

Giải nén `OOP_FINAL_CLEAN_V4.zip`. Tại PowerShell, xem trước:

```powershell
py -3 "$HOME\Downloads\OOP_FINAL_CLEAN_V4\sync_to_repo.py" --repo "$HOME\bookstore-tv5-push" --check
```

Sau khi kiểm tra:

```powershell
py -3 "$HOME\Downloads\OOP_FINAL_CLEAN_V4\sync_to_repo.py" --repo "$HOME\bookstore-tv5-push" --apply
cd "$HOME\bookstore-tv5-push"
.\test.bat
py -3 scripts\audit_data.py
```

Nếu đạt và file thay đổi đã được review, commit/push lên nhánh `feature/tv5-final-v2-ncc-pn`; PR #1 sẽ tự cập nhật. Công cụ **không** tự push và luôn sao lưu file thay đổi ra thư mục TEMP bên ngoài repo.
