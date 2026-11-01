# BÁO CÁO V5 — PHỤC HỒI CHỨC NĂNG TỪ CODE GỐC

## Lý do V5

V4 có lỗi nghiêm trọng: 5 file từ Git `src/model` bị thay bằng class/interface chỉ một dòng. Dù 57 bài test V4 qua, hành vi bốn loại sản phẩm bị mất. V5 phục hồi chính xác 5 file từ `OOP_SRC_MODEL_GOC.zip` và bổ sung những kết nối còn thiếu.

## Thay đổi

1. Giữ **nguyên vẹn byte** của `IThaoTacThucThe.java`, `DoChoi.java`, `DungCuHocTap.java`, `TrangTri.java`, `Vo.java` từ bản gốc do TV5 cung cấp (không viết lại các class đó).
2. Bổ sung bốn loại sản phẩm gốc vào menu kho, nhập phiếu, thêm sản phẩm và bộ lọc theo nhà sản xuất.
3. Đọc/ghi bốn loại trong `LuuTruMatHang`, `Kho`, `DanhSachPhieuNhap` với đúng thứ tự trường tương ứng từng `toString()` gốc.
4. Thêm 6 bài test `LegacyProductRegressionTests.java`: interface API, giá bán + thuộc tính, TXT roundtrip, kho, phiếu nhập + NCC, luồng nhập từ menu.
5. Không sửa thư mục `data/` trong bản V5 hay trong script cập nhật. Giữ riêng bản tham chiếu 42 file gốc để nhóm trưởng tra cứu.

## Kiểm thử

Trên OpenJDK 21 + Python 3: 17 integration + 23 supplier/purchase + 6 legacy restoration + 9 console + 7 new console + 1 actual-data-legacy = **63/63 PASS**. `scripts/test.sh` trả mã thoát 0. Việc chạy `test.bat` trên Windows của bạn vẫn cần xác nhận sau khi cài patch.

## Những điểm cần TV1–TV4 review

- Có 37 class ở `src/model` trùng tên/package với `src/<package>`. V5 chọn một bản từ nhánh tích hợp, không khẳng định các class này hoàn toàn tương đương nguồn Git cũ. Bảng đối chiếu: `DOI_CHIEU_42_CLASS_V5.md`.
- Dữ liệu phiếu nhập của nhóm có những phiếu chứa nhiều NCC; phiên bản hiện tại chỉ giữ NCC chính của dòng đầu trong thuộc tính phiếu và các NCC còn lại ở danh sách chung. Đây là giới hạn mô hình dữ liệu chưa được thiết kế lại.
- Mật khẩu demo lưu dạng văn bản trong dữ liệu mẫu: không sử dụng tài khoản demo cho dữ liệu thật và không commit mật khẩu thật.
- Kiểm thử không thay thế review thủ công tất cả tương tác của 11 loại sản phẩm với mọi menu.

## Công bố

`APPLY_V5.ps1` chỉ áp dụng thay đổi cục bộ, sao lưu và chạy test; **KHÔNG push**. `PUBLISH_V5.ps1` là bước riêng, hỏi xác nhận rõ ràng trước khi commit và push nhánh TV5. Không tự merge vào `main`.
