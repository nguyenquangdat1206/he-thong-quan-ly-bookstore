"""Verify every console entry point prints Vietnamese without JVM encoding flags."""

from pathlib import Path
import shutil
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parent.parent
CASES = (
    ("Main", "QUẢN LÝ NHÀ SÁCH"),
    ("kho.QuanLyKho", "---------- KHO ----------"),
    ("phieunhap.QuanLyPhieuNhap", "QUẢN LÝ PHIẾU NHẬP"),
    ("mathang.QuanLyMatHang", "MẶT HÀNG (CỬA HÀNG)"),
)

with tempfile.TemporaryDirectory(prefix="bookstore_unicode_") as directory:
    work = Path(directory)
    (work / "data").mkdir()
    shutil.copy2(ROOT / "tests" / "fixtures" / "nhanvien_demo.txt", work / "data" / "nhanvien_cn.txt")
    for main_class, heading in CASES:
        run = subprocess.run(
            ["java", "-cp", str(ROOT / "build"), main_class],
            input="0\n",
            text=True,
            capture_output=True,
            encoding="utf-8",
            errors="strict",
            cwd=work,
            timeout=15,
        )
        assert run.returncode == 0, f"{main_class}: {run.stderr}"
        assert heading in run.stdout, f"{main_class}: Vietnamese heading was corrupted"
        assert "\ufffd" not in run.stdout, f"{main_class}: UTF-8 replacement character"
        print("PASS UTF-8 console", main_class)

    shutil.copy2(ROOT / "tests" / "fixtures" / "phieunhap" / "phieunhap_input.txt", work / "data" / "phieunhap_input.txt")
    run = subprocess.run(
        ["java", "-cp", str(ROOT / "build"), "phieunhap.QuanLyPhieuNhap"],
        input="8\n.\n11\n.\n7\n0\n",
        text=True,
        capture_output=True,
        encoding="utf-8",
        errors="strict",
        cwd=work,
        timeout=15,
    )
    assert run.returncode == 0, run.stderr
    assert "Đọc phiếu nhập: thêm 10 dòng" in run.stdout, "standalone receipt import failed"
    assert "nhập thành công 2 phiếu" in run.stdout, "pending receipts were not stocked"
    assert "SCH0001" in run.stdout and "SGK0001" in run.stdout, "warehouse stock was not displayed"
    print("PASS UTF-8 standalone receipt and warehouse menus")

print(f"UNICODE CONSOLE: {len(CASES) + 1} PASS / 0 FAIL")
