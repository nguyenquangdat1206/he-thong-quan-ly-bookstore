"""Exercise Main in isolated folders; continue after failures to audit every scenario."""
from pathlib import Path
import os
import re
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parent.parent
CP = os.environ.get('BOOKSTORE_TEST_CP', str(ROOT / "build-test"))
ADMIN = ["1", "ADMIN", "test-only", "CN01"]
IMPORT = ["2", "8", ".", "11", ".", "0"]
CUSTOMERS = ["4", "6", ".", "0"]
SHELF = ["3", "2", "SCH0001", "10", "0"]
CREATE_ORDER = ["5", "2", "KH01", "1", "SCH0001", "2", "5", "HDAUDIT", "0"]
CARD = ["7", "HDAUDIT", "2", "TXAUDIT", "Test Bank"]
EXIT = ["0", "0"]
passed = failed = 0


def seed(folder):
    data = folder / "data"
    data.mkdir()
    (data / "chinhanh.txt").write_text("CN01;Branch One;Address One\nCN02;Branch Two;Address Two\n", encoding="utf-8")
    (data / "nhanvien_cn.txt").write_text(
        "ADMIN;Admin;090;test-only;Morning;100;ADMIN;\n"
        "QL01;Manager One;091;test-only;Morning;100;QUAN_LY;CN01\n"
        "BH01;Seller One;092;test-only;Morning;100;BAN_HANG;CN01\n"
        "BH02;Seller Two;093;test-only;Evening;100;BAN_HANG;CN02\n"
        "KHO01;Warehouse;094;test-only;Morning;100;NHAP_KHO;\n", encoding="utf-8")
    for filename, sub in [("khachhang.txt", "khachhang"), ("phieunhap_input.txt", "phieunhap"), ("hoadon.txt", "hoadon")]:
        shutil.copyfile(ROOT / "tests" / "fixtures" / sub / filename, data / filename)
    (data / "invoice_valid.txt").write_text("HDFILE;2026-11-21;KH01;BH01;Seller One;0\n1\nSCH0001;1;37500\n", encoding="utf-8")
    (data / "invoice_broken.txt").write_text("BROKEN\n" + (data / "invoice_valid.txt").read_text(encoding="utf-8"), encoding="utf-8")
    (data / "branches_import.txt").write_text("CN03;Third;Third Address\nbroken\nCN01;Duplicate;Address\n", encoding="utf-8")


def launch(folder, inputs):
    run = subprocess.run(
        ["java", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8", "-cp", CP, "Main"],
        input="\n".join(inputs) + "\n", text=True, encoding="utf-8", capture_output=True, cwd=folder, timeout=15)
    output = run.stdout + "\n" + run.stderr
    assert run.returncode == 0, f"exit {run.returncode}: {output[-600:]}"
    assert "Exception in thread" not in output, output[-600:]
    return output


def contains(output, *needles):
    for needle in needles:
        assert needle in output, f"missing {needle!r}"


def saved(folder, name):
    return (folder / "data" / name).read_text(encoding="utf-8")


def case(name, inputs, expected=(), forbidden=(), verify=None):
    global passed, failed
    try:
        with tempfile.TemporaryDirectory(prefix="bookstore-full-console-") as directory:
            folder = Path(directory)
            seed(folder)
            originals = {p.name: p.read_bytes() for p in (folder / "data").iterdir()
                         if p.name not in {"chinhanh.txt", "nhanvien_cn.txt"}}
            output = launch(folder, inputs)
            contains(output, *expected)
            for needle in forbidden:
                assert needle not in output, f"unexpected {needle!r}"
            if verify:
                verify(folder, output)
            for name_in, data in originals.items():
                assert (folder / "data" / name_in).read_bytes() == data, f"input mutated: {name_in}"
        passed += 1
        print("PASS FULL CONSOLE", name)
    except (AssertionError, OSError, subprocess.TimeoutExpired) as error:
        failed += 1
        print("FAIL FULL CONSOLE", name, ":", error)


def expect_file(folder, filename, *needles):
    contains(saved(folder, filename), *needles)


case("guest can view selected branch", ["2", "CN02", "1", "0", "0"], ["Branch Two", "mặt hàng"])
case("wrong password cannot enter admin menu", ["1", "ADMIN", "wrong", "0"], ["Sai mã hoặc mật khẩu"], ["=== ADMIN / TOÀN QUYỀN ==="])
case("unknown account rejected", ["1", "UNKNOWN", "test-only", "0"], ["Sai mã hoặc mật khẩu"])
case("invalid branch retries before admin login", ["1", "ADMIN", "test-only", "NO", "CN01"] + EXIT, ["Không tìm thấy chi nhánh", "CN01 - Branch One"])
case("admin visits warehouse receipts products customers invoices suppliers HR branches",
     ADMIN + ["1", "1", "7", "0", "2", "1", "10", "0", "3", "1", "0", "4", "1", "0", "5", "1", "0", "6", "1", "0", "7", "1", "0", "9", "1", "8", "CN01", "0"] + EXIT,
     ["KHO", "PHIẾU NHẬP", "MẶT HÀNG", "KHÁCH HÀNG", "HÓA ĐƠN", "NHÀ CUNG CẤP", "NHÂN SỰ", "Seller One"])
case("manager sees own staff and read-only warehouse", ["1", "QL01", "test-only", "5", "1", "3", "0", "6", "9", "0", "0"], ["QUẢN LÝ CHI NHÁNH: Branch One", "KHO CHUNG (CHỈ XEM)", "Seller One", "Không được phép"], ["Seller Two", "=== ADMIN / TOÀN QUYỀN ==="])
case("seller assigned to CN02 and denied branch management", ["1", "BH02", "test-only", "9", "0", "0"], ["BÁN HÀNG - Branch Two", "Không được phép"], ["--- CHI NHÁNH ---"])
case("warehouse role can view CN02 shelf", ["1", "KHO01", "test-only", "4", "CN02", "0", "9", "0", "0"], ["NHÂN VIÊN NHẬP KHO", "Không được phép"])
case("branch create edit export delete", ADMIN + ["9", "2", "CN03", "Third", "Third Address", "7", "CN03", "New Address", "6", "data/branches_output.txt", "3", "CN03", "0"] + EXIT,
     ["Đã thêm chi nhánh", "Đã cập nhật địa chỉ", "Đã xóa chi nhánh"], verify=lambda f, o: expect_file(f, "branches_output.txt", "CN03;Third;New Address"))
def verify_branch_import(folder, output):
    records = saved(folder, "chinhanh.txt").splitlines()
    assert len(records) == 3, f"expected 3 branches after import, got {records!r}"
    assert "CN01;Branch One;Address One" in records, "duplicate import replaced the original branch"
    assert "CN03;Third;Third Address" in records, "new branch was not saved"


case("branch import skips invalid and duplicate rows", ADMIN + ["9", "5", "data/branches_import.txt", "1", "0"] + EXIT,
     ["Da doc file chi nhanh.", "CN03"], verify=verify_branch_import)
case("branch with employees cannot be deleted", ADMIN + ["9", "3", "CN01", "0"] + EXIT, ["chi nhánh còn hàng trên kệ / hóa đơn / nhân viên"])
case("admin branch switch", ADMIN + ["9", "4", "CN02", "0", "3", "1", "0"] + EXIT, ["Đang làm việc tại: Branch Two", "CN02 - Branch Two"])
case("admin without branch is guarded", ["1", "ADMIN", "test-only", "0", "3", "5", "8", "0", "0"], ["Chưa chọn chi nhánh"])

for role, label in [(1, "BAN_HANG"), (2, "NHAP_KHO"), (3, "QUAN_LY"), (4, "ADMIN")]:
    staff_id = "NEW" + str(role)
    detail = [staff_id, "New Staff", "090999", "test-only-new", "Evening", "1000", str(role)]
    if role in (1, 3):
        detail += ["MISSING", "CN02"]
    case("HR create role " + label, ADMIN + ["7", "2"] + detail + ["5", staff_id, "0"] + EXIT,
         ["Đã thêm nhân viên", "New Staff"], verify=lambda f, o, ident=staff_id, r=label: expect_file(f, "nhanvien_cn.txt", ident, r))
case("HR edit fields and branch", ADMIN + ["7", "3", "BH01", "Updated Seller", "", "Evening", "120", "changed", "CN02", "0"] + EXIT,
     ["Cập nhật thành công"], verify=lambda f, o: expect_file(f, "nhanvien_cn.txt", "BH01;Updated Seller;092;changed;Evening;120.0;BAN_HANG;CN02"))
case("HR delete seller and protect logged-in admin", ADMIN + ["7", "4", "BH02", "4", "ADMIN", "0"] + EXIT, ["Đã xóa", "Không thể xóa tài khoản đang đăng nhập"])
case("HR export and import ends authenticated session", ADMIN + ["7", "8", "data/staff_export.txt", "7", "data/staff_export.txt", "0"], ["Đã đọc file; kết thúc phiên"], verify=lambda f, o: expect_file(f, "staff_export.txt", "ADMIN", "BH02"))

TOY = ["1", "2", "8", "DCH0101", "Toy", "Maker", "6", "100", "5"]
case("warehouse add search statistics export", ADMIN + TOY + ["6", "DCH0101", "9", "8", "7", "8", ".", "0"] + EXIT,
     ["Đã thêm vào kho thành công", "Toy", "Tổng số lượng tồn: 5"], verify=lambda f, o: expect_file(f, "kho_output.txt", "DCH0101;Toy;100.0;5"))
case("warehouse edit capacity and delete missing/existing", ADMIN + TOY + ["4", "DCH0101", "10001", "5", "UNKNOWN", "5", "DCH0101", "1", "0"] + EXIT, ["Kho không đủ chỗ", "Không tìm thấy mã UNKNOWN", "Đã xóa mã DCH0101", "Kho trống"])
case("receipts import pending once and export approved only", ADMIN + IMPORT + ["2", "11", ".", "0"] + EXIT,
     ["nhập thành công 2 phiếu", "nhập thành công 0 phiếu"], verify=lambda f, o: expect_file(f, "phieunhap_danhan.txt", "PN001", "PN003", "DA_DUYET"))
case("receipts manual supplier inherited", ADMIN + ["2", "2", "PNMAN", "21/11/2026", "Test Supplier", "1", "7", "SPK0101", "Item", "100", "3", "9", ".", "0"] + EXIT,
     ["Thêm thành công phiếu nhập PNMAN"], verify=lambda f, o: expect_file(f, "phieunhap_output.txt", "PNMAN;21/11/2026", ";Test Supplier;CHO_DUYET"))
case("receipts delete existing ID", ADMIN + ["2", "8", ".", "5", "PN001", "0", "0", "0"])
case("receipts delete nonexistent ID", ADMIN + ["2", "8", ".", "5", "MISSING", "0", "0", "0"])
case("receipts edit existing ID", ADMIN + ["2", "8", ".", "4", "PN001", "4", "0", "0", "0"])
case("supplier add edit export delete", ADMIN + ["6", "2", "NCCX", "Supplier", "Address", "090", "test@example.vn", "3", "NCCX", "Changed", "New Address", "091", "new@example.vn", "6", ".", "4", "NCCX", "0"] + EXIT,
     ["Thêm nhà cung cấp thành công", "Sửa thành công", "Xóa thành công"], verify=lambda f, o: expect_file(f, "nhacungcap_output.txt", "NCCX;Changed;New Address;091;new@example.vn"))
case("customer add edit search export delete", ADMIN + ["4", "2", "KHAUDIT", "Audit Customer", "090123", "4", "KHAUDIT", "1", "Changed Customer", "0", "8", "2", "Changed", "7", ".", "5", "KHAUDIT", "0"] + EXIT,
     ["Thêm khách hàng thành công", "Đã sửa thông tin khách hàng", "Changed Customer", "Đã xóa khách hàng"], verify=lambda f, o: expect_file(f, "khachhang_output.txt", "KHAUDIT;Changed Customer;090123"))
case("shelf transfer return search filter export", ADMIN + IMPORT + SHELF + ["3", "4", "SCH0001", "2", "8", "SCH0001", "9", "1", "1", "Fujiko F Fujio", "10", ".", "11", "0"] + EXIT,
     ["Đã trả 2 sản phẩm", "Doraemon Tập 1"], verify=lambda f, o: expect_file(f, "CN01_mathang_output.txt", "SCH0001;Doraemon Tập 1;25000.0;8"))
case("shelf bulk transfer and counts", ADMIN + IMPORT + ["3", "3", "1", "0"] + EXIT, ["Số mặt hàng: 5"])
case("shelf zero and negative user quantities retry", ADMIN + IMPORT + ["3", "2", "SCH0001", "0", "-1", "2", "10", ".", "0"] + EXIT,
     ["lớn hơn 0"], verify=lambda f, o: expect_file(f, "CN01_mathang_output.txt", "SCH0001;Doraemon Tập 1;25000.0;2"))
case("invoice card checkout export and repeat-payment guard", ADMIN + IMPORT + CUSTOMERS + SHELF + CREATE_ORDER + CARD + ["7", "HDAUDIT", "6", ".", "9", "8", "2", "0"] + EXIT,
     ["Thanh toán thành công", "Đã thanh toán trước đó", "Doanh thu thực thu"], verify=lambda f, o: expect_file(f, "CN01_hoadon_output.txt", "HDAUDIT", ";1\n", "SCH0001;2;37500.0"))
case("invoice insufficient cash followed by successful payment", ADMIN + IMPORT + CUSTOMERS + SHELF + CREATE_ORDER + ["7", "HDAUDIT", "1", "1", "7", "HDAUDIT", "1", "100000", "0"] + EXIT,
     ["Số tiền khách đưa không đủ", "Thanh toán thành công"])
case("invoice pending quantity edit and deletion", ADMIN + IMPORT + CUSTOMERS + SHELF + CREATE_ORDER + ["3", "HDAUDIT", "1", "1", "3", "0", "4", "HDAUDIT", "1", "0"] + EXIT,
     ["Tổng tiền mới: 112500.0", "Đã xóa hóa đơn HDAUDIT", "Chưa có hóa đơn nào"])
case("invoice valid manual import", ADMIN + CUSTOMERS + ["5", "5", "data/invoice_valid.txt", "1", "8", "4", "BH01", "0"] + EXIT, ["HDFILE", "Seller One"])
case("invoice malformed line must not hide later valid invoice", ADMIN + CUSTOMERS + ["5", "5", "data/invoice_broken.txt", "1", "0"] + EXIT, ["Mã hóa đơn: HDFILE"])
case("same invoice ID and stock isolated between CN01 and CN02", ADMIN + IMPORT + CUSTOMERS + SHELF + CREATE_ORDER + CARD + ["0", "9", "4", "CN02", "0"] + SHELF + CREATE_ORDER + ["6", ".", "0"] + EXIT,
     ["CN02 - Branch Two"], verify=lambda f, o: expect_file(f, "CN02_hoadon_output.txt", "HDAUDIT", ";0\n"))


def restart_verify(folder, output):
    second = launch(folder, ADMIN + ["3", "1", "0", "5", "1", "0"] + EXIT)
    contains(second, "Số mặt hàng: 0", "Chưa có hóa đơn nào")
    assert "Mã hóa đơn: HDAUDIT" not in second, "business data was unexpectedly loaded automatically"
    assert not (folder / "data" / "phien.properties").exists(), "obsolete session snapshot was created"
    expect_file(folder, "CN01_mathang_output.txt", "SCH0001;Doraemon Tập 1;25000.0;8")
    expect_file(folder, "CN01_hoadon_output.txt", "HDAUDIT", ";1\n", "SCH0001;2;37500.0")
    third = launch(folder, ADMIN + CUSTOMERS + ["5", "5", "data/CN01_hoadon_output.txt", "1", "0"] + EXIT)
    contains(third, "Mã hóa đơn: HDAUDIT", "Đã thanh toán")


case("restart retains metadata; business TXT is exported and invoices reloaded explicitly",
     ADMIN + IMPORT + CUSTOMERS + SHELF + CREATE_ORDER + CARD + ["6", ".", "0", "3", "10", ".", "0"] + EXIT,
     verify=restart_verify)
print(f"FULL CONSOLE AUDIT: {passed} PASS, {failed} FAIL")
raise SystemExit(1 if failed else 0)
