# Bản sửa V3.1 — PowerShell/Windows và dữ liệu legacy

## Lỗi Java `Invalid filename`

`test.bat` và `run.bat` tạo tệp `@argfile` chứa đường dẫn Windows `C:\Users\...`. Trình đọc Java argfile coi dấu `\` là ký tự escape, ví dụ `\t` thành tab, nên `javac` không tìm thấy file. V3.1 ghi đường dẫn `C:/Users/...` vào argfile. Đây là lỗi của script do bản merge tạo, không phải lỗi máy người dùng.

## Tình trạng 6 tệp data/ của nhánh main (kiểm tra 01/11/2026)

- `data/nhanvien.txt`: 3 dòng cú pháp khởi tạo class Java, không phải 7 trường phân cách `;` mà ứng dụng đang đọc. Nếu chạy trực tiếp, không có tài khoản ADMIN; V3 tránh ghi đè dữ liệu này.
- `data/phieunhap_input.txt`: nhiều dòng có ngày dạng `dd/MM.yyyy`, không phải `dd-MM-yyyy` hoặc `yyyy-MM-dd`; trình nhập phiếu V3 bỏ qua các dòng này.
- Bốn tệp còn lại: audit cấu trúc báo đạt (chưa bảo đảm đúng hoàn toàn nghiệp vụ).

## Chuyển đổi không phá hủy dữ liệu của nhóm

1. Chạy `py -3 scripts\prepare_legacy_data.py` trong repository. Script chỉ đọc data/; xuất bản sao có chuyển đổi trong thư mục **TEMP của Windows** (ngoài Git), gồm 6 tệp. Nó từ chối đầu vào không nhận dạng được và **không ghi đè dữ liệu gốc**.
2. Chạy `py -3 scripts\audit_data.py --data "<đường dẫn TEMP\...\data>"` để kiểm tra bản xem trước (thay đường dẫn theo output).
3. Muốn thử dữ liệu xem trước thì mở chương trình ở bản sao thư mục có data/ này, với classpath Java trỏ tới build đã biên dịch.
4. Sau khi TV4 và nhóm trưởng xác nhận các dòng phiếu, mã và tài khoản được chuyển đổi đúng, mới sao lưu dữ liệu gốc và đưa dữ liệu được duyệt trở lại `data/` trên một commit riêng.
5. Không commit thư mục TEMP hoặc mật khẩu thực. Với repository Public, tuyệt đối không dùng mật khẩu tài khoản cá nhân làm dữ liệu demo.

## Kiểm thử

- `test.bat`: kiểm thử Java và console trên **fixtures** độc lập, không kiểm chứng tương thích của dữ liệu `data/` thật.
- `run.bat`: chạy chương trình, sử dụng file `data/nhanvien.txt` đang có. Nếu chưa chuyển đổi, ứng dụng không đăng nhập được vì tệp nhân viên chưa tương thích.
- `py -3 scripts\audit_data.py`: kiểm tra dữ liệu repo. Mã thoát 2 khi còn tệp không tương thích.

Không push vội, không merge PR #1 khi chưa xử lý hai tệp trên.
