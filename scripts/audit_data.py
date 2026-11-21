#!/usr/bin/env python3
"""Read-only validation matching the Java TXT readers; never prints record contents."""
from pathlib import Path
from datetime import datetime
import argparse
import math
import re
from prepare_legacy_data import STAFF

ROOT = Path(__file__).resolve().parent.parent
EXPECTED = ('chinhanh.txt', 'nhanvien_cn.txt', 'nhanvien.txt', 'khachhang.txt',
            'nhacungcap_input.txt', 'phieunhap_input.txt', 'kho_input.txt',
            'danhsach_mathang.txt', 'mathang.txt', 'hoadon.txt')
PRODUCTS = {
    'Sách': ('SCH', 8), 'Sách giáo khoa': ('SGK', 7),
    'Tiểu thuyết': ('TTH', 8), 'Truyện': ('TRU', 8),
    'Văn phòng phẩm': ('VPP', 7), 'Báo tạp chí': ('BTC', 7),
    'Khác': ('SPK', 6), 'Đồ chơi': ('DCH', 7),
    'Dụng cụ học tập': ('DCT', 7), 'Trang trí': ('TTR', 6), 'Vở': ('VO', 7),
}
ROLES = {'ADMIN', 'QUAN_LY', 'BAN_HANG', 'NHAP_KHO'}
STATUSES = {'CHO_DUYET', 'DA_DUYET', 'DA_HUY'}


def rows(path):
    return [line.strip() for line in path.read_text(encoding='utf-8-sig').splitlines() if line.strip()]


def number(value):
    try:
        parsed = float(value)
        return math.isfinite(parsed) and parsed >= 0
    except ValueError:
        return False


def integer(value, minimum=0):
    return re.fullmatch(r'[0-9]+', value) is not None and minimum <= int(value) <= 2147483647


def product_error(parts, minimum=0):
    spec = PRODUCTS.get(parts[0]) if parts else None
    if spec is None or len(parts) != spec[1]:
        return 'sai danh mục hoặc số cột mặt hàng'
    if not re.fullmatch(spec[0] + r'[0-9]{4}', parts[1]) or not parts[2]:
        return 'sai mã SKU hoặc thiếu tên'
    if not number(parts[3]) or not integer(parts[4], minimum):
        return 'giá/số lượng không hữu hạn, âm hoặc vượt giới hạn'
    if not all(parts[5:]):
        return 'thiếu thuộc tính riêng/nhà cung cấp'
    if parts[0] in {'Đồ chơi', 'Vở'} and not integer(parts[5], 1):
        return 'độ tuổi/số trang phải là số nguyên dương'
    return None


def receipt_date(value):
    for pattern, fmt in ((r'[0-9]{2}/[0-9]{2}/[0-9]{4}', '%d/%m/%Y'),
                         (r'[0-9]{2}-[0-9]{2}-[0-9]{4}', '%d-%m-%Y')):
        if re.fullmatch(pattern, value):
            return datetime.strptime(value, fmt).date()
    raise ValueError('Ngày phải dùng dd/MM/yyyy hoặc dd-MM-yyyy')


def validate(filename, lines, customer_ids=None, branch_ids=None):
    issues, seen, notes = [], set(), {}
    admin = False
    if not lines:
        return ['Tệp rỗng']
    if filename == 'hoadon.txt':
        return validate_invoices(lines, customer_ids)
    for ix, line in enumerate(lines, 1):
        p = [v.strip() for v in line.split(';')]
        error, key = None, None
        if filename in {'nhanvien.txt', 'nhanvien_cn.txt'}:
            legacy = STAFF.fullmatch(line)
            if legacy:
                key = legacy.group(2).strip().casefold()
                valid = all(legacy.group(i).strip() for i in (2, 3, 5)) and number(legacy.group(7))
                admin |= valid and legacy.group(1) == 'QuanLy'
                if not valid:
                    error = 'nhân viên cũ thiếu thông tin/lương không hợp lệ'
            elif len(p) not in (7, 8) or not all(p[i] for i in (0, 1, 3)) or not number(p[5]) or p[6] not in ROLES:
                error = 'sai 7/8 trường, quyền, mã, tên, mật khẩu hoặc lương'
            else:
                key = p[0].casefold()
                admin |= p[6] == 'ADMIN'
                if p[6] in {'QUAN_LY', 'BAN_HANG'} and branch_ids is not None:
                    if len(p) == 8 and p[7] and p[7].casefold() not in branch_ids:
                        error = 'tham chiếu chi nhánh không tồn tại'
        elif filename == 'chinhanh.txt':
            if len(p) not in (2, 3) or not p[0] or not p[1]:
                error = 'sai số cột/mã/tên chi nhánh'
            elif any(c in p[0] for c in '/\\:') or p[0] in {'.', '..'}:
                error = 'mã chi nhánh không được chứa đường dẫn'
            else:
                key = p[0].casefold()
        elif filename == 'khachhang.txt':
            if len(p) != 3 or not all(p):
                error = 'sai 3 trường khách hàng'
            else:
                key = p[0].casefold()
        elif filename == 'nhacungcap_input.txt':
            if len(p) != 5 or not p[0] or not p[1]:
                error = 'sai 5 trường/mã/tên nhà cung cấp'
            else:
                key = p[0].casefold()
        elif filename == 'phieunhap_input.txt':
            try:
                if len(p) < 9 or not p[0] or p[-1] not in STATUSES:
                    raise ValueError()
                date = receipt_date(p[1])
                error = product_error(p[2:-1], 1)
                if error is None:
                    supplier = p[8] if p[2] in {'Sách', 'Tiểu thuyết', 'Truyện'} else p[-2]
                    identity = (date, p[-1], supplier)
                    note_id = p[0].casefold()
                    if note_id in notes and notes[note_id] != identity:
                        error = 'cùng mã phiếu nhưng khác ngày/trạng thái/NCC'
                    notes[note_id] = identity
                    key = (note_id, p[3].casefold())
            except ValueError:
                error = 'sai mã/ngày/trạng thái phiếu (dd/MM/yyyy hoặc dd-MM-yyyy)'
        elif filename in {'kho_input.txt', 'mathang.txt', 'danhsach_mathang.txt'}:
            error = product_error(p)
            if error is None:
                key = p[1].casefold()
        if key is not None:
            if key in seen:
                error = 'trùng mã dữ liệu'
            seen.add(key)
        if error:
            issues.append(f'dòng {ix}: {error}')
    if filename in {'nhanvien.txt', 'nhanvien_cn.txt'} and not admin:
        issues.append('không có tài khoản ADMIN hợp lệ')
    return issues


def validate_invoices(lines, customer_ids=None):
    issues, seen = [], set()
    i = 0
    while i < len(lines):
        start = i + 1
        p = [v.strip() for v in lines[i].split(';')]
        i += 1
        try:
            if len(p) not in (3, 6) or not p[0] or not p[2]:
                raise ValueError('đầu hóa đơn không hợp lệ')
            datetime.strptime(p[1], '%Y-%m-%d')
            if len(p) == 6 and p[5] not in {'0', '1'}:
                raise ValueError('trạng thái thanh toán không hợp lệ')
            code = p[0].casefold()
            if code in seen:
                raise ValueError('trùng mã hóa đơn')
            seen.add(code)
            if customer_ids is not None and p[2].casefold() not in customer_ids:
                raise ValueError('khách hàng không tồn tại')
            if i >= len(lines) or not integer(lines[i], 1) or int(lines[i]) > 10000:
                raise ValueError('số dòng chi tiết không hợp lệ')
            count = int(lines[i])
            i += 1
            if i + count > len(lines):
                raise ValueError('thiếu dòng chi tiết')
            for row in lines[i:i + count]:
                detail = [v.strip() for v in row.split(';')]
                if len(detail) != 3 or not detail[0] or not integer(detail[1], 1) or not number(detail[2]):
                    raise ValueError('mã/số lượng/đơn giá chi tiết không hợp lệ')
                if not math.isfinite(int(detail[1]) * float(detail[2])):
                    raise ValueError('thành tiền vượt giới hạn')
            i += count
        except (ValueError, OverflowError) as exc:
            issues.append(f'dòng {start}: {exc}')
            break
    return issues


def main(argv=None):
    parser = argparse.ArgumentParser(description='Kiểm tra dữ liệu TXT; không chỉnh sửa dữ liệu')
    parser.add_argument('--data', type=Path, default=ROOT / 'data')
    data = parser.parse_args(argv).data
    if not data.is_dir():
        print('LỖI: Không thấy thư mục dữ liệu:', data)
        return 2
    print('=== KIỂM TRA DỮ LIỆU REPOSITORY (KHÔNG THAY ĐỔI FILE) ===')
    loaded = {}
    for name in EXPECTED:
        try:
            loaded[name] = rows(data / name)
        except (UnicodeError, OSError):
            loaded[name] = None
    def ids(name):
        return {line.split(';')[0].strip().casefold() for line in (loaded[name] or [])}
    failures = 0
    for name in EXPECTED:
        content = loaded[name]
        errors = ['không đọc được tệp/UTF-8'] if content is None else validate(
            name, content, ids('khachhang.txt'), ids('chinhanh.txt'))
        failures += bool(errors)
        print(f'{"CẦN XEM LẠI" if errors else "OK"}: {name} — {len(content or [])} dòng dữ liệu')
        for issue in errors[:5]:
            print('    ' + issue)
        if len(errors) > 5:
            print(f'    ... và {len(errors) - 5} lỗi khác')
    print(f'KIỂM TRA DỮ LIỆU: {len(EXPECTED) - failures}/{len(EXPECTED)} tệp đạt.')
    print('Các tệp *_loi.txt là dữ liệu kiểm thử lỗi, được loại khỏi bộ dữ liệu hợp lệ.')
    return 2 if failures else 0


if __name__ == '__main__':
    raise SystemExit(main())
