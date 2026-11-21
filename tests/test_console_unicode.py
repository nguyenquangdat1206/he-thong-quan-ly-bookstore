"""Verify Main's UTF-8 setup and standalone menus with explicit JVM encoding."""

from pathlib import Path
import os
import shutil
import subprocess
import tempfile


ROOT = Path(__file__).resolve().parent.parent
CP = os.environ.get('BOOKSTORE_TEST_CP', str(ROOT / 'build-test'))
CASES = (
    ("Main", "0\n", "QUẢN LÝ NHÀ SÁCH"),
    ("Main", "1\nADMIN\nadmin123\nCN01\n1\n0\n0\n0\n", "---------- KHO ----------"),
    ("phieunhap.QuanLyPhieuNhap", "0\n", "QUẢN LÝ PHIẾU NHẬP"),
    ("mathang.QuanLyMatHang", "0\n", "MẶT HÀNG (CỬA HÀNG)"),
)
UTF8_FLAGS = ["-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8"]

with tempfile.TemporaryDirectory(prefix="bookstore_unicode_") as directory:
    work = Path(directory)
    (work / "data").mkdir()
    shutil.copy2(ROOT / "tests" / "fixtures" / "nhanvien_demo.txt", work / "data" / "nhanvien_cn.txt")
    for main_class, inputs, heading in CASES:
        # Main configures its own UTF-8 streams; standalone modules need JVM flags.
        flags = [] if main_class == "Main" else UTF8_FLAGS
        run = subprocess.run(
            ["java", *flags, "-cp", CP, main_class],
            input=inputs,
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
        ["java", *UTF8_FLAGS, "-cp", CP, "phieunhap.QuanLyPhieuNhap"],
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
