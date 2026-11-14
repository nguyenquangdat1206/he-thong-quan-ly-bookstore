# Bàn giao V3 — dọn cấu trúc dữ liệu cho PR #1

## Nguyên nhân

Bản FINAL V2 đặt các file TXT dữ liệu vào các thư mục cấp cao `hoadon/`, `khachhang/`, `kho/`, `phieunhap/`, trong khi nhánh `main` của nhóm đã có `data/` (gồm `hoadon.txt`, `khachhang.txt`, `kho_input.txt`, `mathang.txt`, `nhanvien.txt`, `phieunhap_input.txt`). Đây **không** phải class Java bị nhân đôi; song có thể tạo nhiều bản dữ liệu cùng tên, gây khó xác định tệp nào đang được sử dụng.

## Đã sửa

- **Không sửa nghiệp vụ TV2–TV4**. Các file `src/hoadon/*.java`, `src/khachhang/*.java`, `src/mathang/*.java`, `src/phieunhap/PhieuNhap.java`, `src/phieunhap/DanhSachPhieuNhap.java`, `src/kho/Kho.java` giữ nguyên so với V2.
- Di chuyển 8 file TXT đang nằm ngoài `src` sang `tests/fixtures/`; xóa một log console đã theo dõi trong Git. Các file dữ liệu lỗi/BOM cũng được giữ làm mẫu kiểm thử, không xóa nội dung.
- Menu chính trỏ đến `data/` là vị trí thống nhất sẵn có của nhóm; file xuất sử dụng tên `_output.txt` để không tự ghi đè file input của nhóm.
- 2 menu standalone `QuanLyKho`, `QuanLyPhieuNhap` được cập nhật đường dẫn mặc định cùng quy ước.
- Chương trình quản lý nhân sự dùng `data/nhanvien.txt`. Khi không có file, chương trình tạo tài khoản **demo**; nếu file có nhưng không có quyền ADMIN hợp lệ, chương trình cảnh báo, không tự xóa nội dung cũ (và tự lưu bị khóa nếu phát hiện dòng không tương thích, để tránh làm mất dữ liệu cũ). Cần kiểm tra tệp thực tế trước khi chạy.
- Cập nhật Java tests và console tests dùng bản fixture riêng trong thư mục tạm; thêm kiểm thử đường dẫn mặc định, không ghi đè đầu vào.
- Thêm `python scripts/audit_data.py` để kiểm tra **chỉ đọc** các file data hiện có trên repository.

## Điều kiện cần kiểm tra khi đưa lên nhánh TV5

**Patch V3 không có nội dung của 6 file `data/*.txt` đang ở nhánh `main`, vì chưa nhận được bytes chính xác của các file đó trong cuộc trò chuyện.** Không nên tự động dùng bản dữ liệu mẫu của V2 ghi đè dữ liệu của TV2–TV4. Hãy chạy `python scripts/audit_data.py` tại thư mục gốc sau khi áp dụng patch; gửi cảnh báo để xử lý tương thích định dạng trước khi merge PR.

Để demo độc lập với dữ liệu của nhóm: chạy `test.bat` / `scripts/test.sh`; test chuẩn bị các file sample của V2 trong `tests/fixtures` vào thư mục tạm và **không chỉnh sửa data/ đang có**.

Dữ liệu hóa đơn cần khách hàng và mặt hàng tương ứng trong bộ nhớ khi đọc; không thể chỉ kiểm tra file TXT độc lập mà đảm bảo tái tạo hóa đơn đúng. Bộ nhập phiếu vẫn có giới hạn một NCC cấp phiếu; V3 không sửa thiết kế do TV4 phụ trách.

## Các lệnh PowerShell trên bản clone GitHub đã push

```
cd "$HOME\bookstore-tv5-push"
git switch feature/tv5-final-v2-ncc-pn
git status --short
# Trạng thái phải sạch, và bạn phải có file PATCH_OOP_V3_DATA_CLEANUP.patch
# đặt trong Downloads trước khi chạy.
git apply --check "$HOME\Downloads\PATCH_OOP_V3_DATA_CLEANUP.patch"
git apply "$HOME\Downloads\PATCH_OOP_V3_DATA_CLEANUP.patch"
py -3 scripts\audit_data.py
.\test.bat
git status --short
# Khi các kiểm thử đạt và đã review thay đổi:
git add -A
git commit -m "refactor: unify TXT data paths and isolate test fixtures"
git push origin feature/tv5-final-v2-ncc-pn
```

**Lưu ý:** `git apply --check` chỉ xác minh patch có khớp với phiên bản V2 hiện tại hay không. Nếu có lỗi phải dừng; không dùng `git apply --force`, `git reset --hard`, hoặc xóa dữ liệu cũ. Sau khi push, **Pull Request #1 tự cập nhật**, không cần tạo PR mới. Nhóm trưởng review và merge vào `main` khi chấp thuận.
