#!/usr/bin/env python3
"""Prepare a NON-DESTRUCTIVE preview of team's legacy TXT data for FINAL OOP V3.

Reads data/nhanvien.txt and data/phieunhap_input.txt. NEVER edits input files.
Writes an isolated full data/ copy under OS temp directory, to allow manual review
and integration testing before team approves replacing any working dataset.
"""
from __future__ import annotations

from datetime import datetime
from pathlib import Path
import re
import shutil
import sys
import tempfile

ROOT = Path(__file__).resolve().parent.parent
DATA = ROOT / 'data'

# Exact, conservative recognition of the legacy Java *text* records in the group's
# nhanvien.txt. We do not execute Java or evaluate arbitrary expressions.
Q = r'"([^"\\]*)"'
STAFF = re.compile(
    r'^\s*dsNhanVien\s*\.\s*them\s*\(\s*new\s+'
    r'(QuanLy|NhanVienBanHang|NhanVienNhapKho)\s*\(\s*'
    + r'\s*,\s*'.join([Q] * 5)
    + r'\s*,\s*(\d+(?:\.\d+)?)\s*\)\s*\)\s*;\s*$'
)
PERMISSIONS = {
    'QuanLy': 'ADMIN',
    'NhanVienBanHang': 'BAN_HANG',
    'NhanVienNhapKho': 'NHAP_KHO',
}
DATE_OLD = re.compile(r'^(\d{2})/(\d{2})\.(\d{4})$')
DATE_NEW = re.compile(r'^(\d{2})-(\d{2})-(\d{4})$')


def staff_rows(src: Path) -> tuple[list[str], int]:
    rows, upgraded, keys = [], 0, set()
    for number, line in enumerate(src.read_text(encoding='utf-8-sig').splitlines(), 1):
        if not line.strip():
            continue
        m = STAFF.fullmatch(line)
        if m:
            klass, ma, ten, sdt, password, ca, luong = m.groups()
            role = PERMISSIONS[klass]
            if not ma or not ten or not password or float(luong) < 0:
                raise ValueError(f'nhanvien.txt dòng {number}: dữ liệu không hợp lệ')
            values = [ma, ten, sdt, password, ca, luong, role]
            upgraded += 1
        else:
            values = [s.strip() for s in line.split(';')]
            if len(values) != 7 or values[-1] not in PERMISSIONS.values():
                raise ValueError(f'nhanvien.txt dòng {number}: không nhận dạng được định dạng')
            try:
                if float(values[5]) < 0:
                    raise ValueError()
            except ValueError:
                raise ValueError(f'nhanvien.txt dòng {number}: lương không hợp lệ') from None
        if any(';' in v or '\n' in v or '\r' in v for v in values) or values[0].lower() in keys:
            raise ValueError(f'nhanvien.txt dòng {number}: dữ liệu không an toàn hoặc mã trùng')
        keys.add(values[0].lower())
        rows.append(';'.join(values))
    if not rows or not any(r.endswith(';ADMIN') for r in rows):
        raise ValueError('nhanvien.txt: không có tài khoản quản lý để đăng nhập')
    return rows, upgraded


def invoice_rows(src: Path) -> tuple[list[str], int]:
    rows, upgraded = [], 0
    for number, line in enumerate(src.read_text(encoding='utf-8-sig').splitlines(), 1):
        if not line.strip():
            continue
        parts = line.split(';')
        if len(parts) < 9 or parts[-1].strip() not in {'CHO_DUYET','DA_DUYET','DA_HUY'}:
            raise ValueError(f'phieunhap_input.txt dòng {number}: sai số cột hoặc trạng thái')
        value = parts[1].strip()
        m = DATE_OLD.fullmatch(value)
        if m:
            parsed = datetime.strptime(value, '%d/%m.%Y')
            parts[1] = parsed.strftime('%d-%m-%Y')
            upgraded += 1
        elif DATE_NEW.fullmatch(value):
            datetime.strptime(value, '%d-%m-%Y')
        else:
            try:
                parsed = datetime.strptime(value, '%Y-%m-%d')
            except ValueError:
                raise ValueError(f'phieunhap_input.txt dòng {number}: ngày không nhận dạng được') from None
            parts[1] = parsed.strftime('%d-%m-%Y')
            upgraded += 1
        rows.append(';'.join(parts))
    if not rows:
        raise ValueError('phieunhap_input.txt rỗng')
    return rows, upgraded


def main() -> int:
    staff = DATA / 'nhanvien.txt'
    invoices = DATA / 'phieunhap_input.txt'
    if not staff.is_file() or not invoices.is_file():
        print('Không thấy data/nhanvien.txt hoặc data/phieunhap_input.txt.'); return 1
    try:
        staff_data, staff_fixed = staff_rows(staff)
        invoice_data, invoices_fixed = invoice_rows(invoices)
    except (ValueError, UnicodeError, OSError) as exc:
        print('KHÔNG TẠO PREVIEW:', exc)
        print('Dữ liệu gốc không bị thay đổi.')
        return 1
    temp = Path(tempfile.mkdtemp(prefix='oop_v3_data_preview_')) / 'data'
    temp.mkdir()
    for f in DATA.iterdir():
        if f.is_file() and f.suffix.lower() == '.txt':
            shutil.copy2(f, temp / f.name)
    (temp / 'nhanvien.txt').write_text('\n'.join(staff_data) + '\n', encoding='utf-8')
    (temp / 'phieunhap_input.txt').write_text('\n'.join(invoice_data) + '\n', encoding='utf-8')
    print('ĐÃ TẠO BẢN XEM TRƯỚC AN TOÀN, KHÔNG SỬA GÌ TRONG REPOSITORY.')
    print('Số dòng nhân viên chuyển đổi:', staff_fixed, '/', len(staff_data))
    print('Số dòng ngày phiếu nhập chuyển đổi:', invoices_fixed, '/', len(invoice_data))
    print('Thư mục bản xem trước:', temp.parent)
    print('Bản xem trước có thể chứa mật khẩu DEMO dạng văn bản, KHÔNG upload lên GitHub.')
    print('Chỉ thay data/ thực tế sau khi nhóm trưởng duyệt và đã có bản sao lưu.')
    return 0


if __name__ == '__main__':
    sys.exit(main())
