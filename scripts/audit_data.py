#!/usr/bin/env python3
"""Read-only check for data/*.txt before merging V3 into the team's repository.
Does not print customer/staff details, passwords, or change any file.
"""
from pathlib import Path
from datetime import datetime
import argparse
import sys
from prepare_legacy_data import STAFF

ROOT = Path(__file__).resolve().parent.parent
parser = argparse.ArgumentParser(description='Kiểm tra cấu trúc file dữ liệu; không chỉnh sửa dữ liệu')
parser.add_argument('--data', type=Path, default=ROOT / 'data', help='Thư mục data/ cần kiểm tra')
ARGS = parser.parse_args()
DATA = ARGS.data
expected = {
    'nhanvien.txt': 'Nhân viên / phân quyền (7 cột)',
    'khachhang.txt': 'Khách hàng (3 cột)',
    'hoadon.txt': 'Hóa đơn (đầu phiếu, số dòng, chi tiết)',
    'phieunhap_input.txt': 'Phiếu nhập (ngày, loại hàng, mã, ... trạng thái)',
    'kho_input.txt': 'Kho (loại hàng, mã, tên, giá, số lượng, ...)',
    'mathang.txt': 'Mặt hàng (loại, mã, tên, giá, số lượng, ...)',
}

def rows(path):
    return [line.strip() for line in path.read_text(encoding='utf-8-sig').splitlines() if line.strip()]

def is_num(v):
    try: float(v); return True
    except ValueError: return False

def validate(filename, lines):
    issues = []
    if not lines: return ['Tệp rỗng']
    for ix, line in enumerate(lines, 1):
        parts = [v.strip() for v in line.split(';')]
        if filename == 'nhanvien.txt':
            if not STAFF.fullmatch(line) and (len(parts) != 7 or not is_num(parts[5]) or parts[6] not in {'ADMIN','BAN_HANG','NHAP_KHO'}):
                issues.append(f'dòng {ix}: sai 7 trường/quyền/lương')
        elif filename == 'khachhang.txt':
            if len(parts) != 3 or not all(parts):
                issues.append(f'dòng {ix}: không đúng 3 trường')
        elif filename == 'kho_input.txt':
            if len(parts) < 6 or not is_num(parts[3]) or not parts[4].lstrip('-').isdigit():
                issues.append(f'dòng {ix}: loại/mã/giá/số lượng không khớp')
        elif filename == 'phieunhap_input.txt':
            valid_date = False
            if len(parts) >= 9:
                for fmt in ('%d-%m-%Y', '%Y-%m-%d', '%d/%m.%Y'):
                    try: datetime.strptime(parts[1], fmt); valid_date=True; break
                    except ValueError: pass
            if len(parts) < 9 or not valid_date or parts[-1] not in {'CHO_DUYET','DA_DUYET','DA_HUY'}:
                issues.append(f'dòng {ix}: số cột/ngày/trạng thái chưa tương thích')
        elif filename == 'mathang.txt':
            if len(parts) < 6 or not is_num(parts[3]) or not parts[4].lstrip('-').isdigit():
                issues.append(f'dòng {ix}: loại/mã/giá/số lượng không khớp')
    if filename == 'hoadon.txt':
        # HD header: 3 or 6 fields, then one count, followed by count rows of 3 fields.
        i=0
        while i < len(lines):
            cols=lines[i].split(';')
            if len(cols) not in (3,6):
                issues.append(f'dòng {i+1}: đầu hóa đơn không đúng 3/6 cột'); break
            if i+1 >= len(lines) or not lines[i+1].strip().isdigit():
                issues.append(f'dòng {i+2}: thiếu số dòng chi tiết'); break
            n=int(lines[i+1].strip())
            if n < 0 or i+2+n > len(lines):
                issues.append(f'dòng {i+2}: số dòng chi tiết vượt phạm vi'); break
            for j in range(i+2,i+2+n):
                if len(lines[j].split(';')) != 3:
                    issues.append(f'dòng {j+1}: dòng hàng không đúng 3 cột')
            i+=n+2
    if filename=='nhanvien.txt' and not any(line.endswith(';ADMIN') or (STAFF.fullmatch(line) and STAFF.fullmatch(line).group(1)=='QuanLy') for line in lines):
        issues.append('không thấy tài khoản ADMIN; cần kiểm tra trước khi chạy')
    return issues

print('=== KIỂM TRA DỮ LIỆU REPOSITORY (KHÔNG THAY ĐỔI FILE) ===')
if not DATA.is_dir():
    print('LỖI: Không thấy thư mục dữ liệu:', DATA); raise SystemExit(2)
count_problem = 0
for name, desc in expected.items():
    p = DATA/name
    if not p.is_file():
        print(f'WARN {name}: chưa có ({desc})')
        count_problem += 1
        continue
    try:
        ls=rows(p); errors=validate(name,ls)
        state='OK' if not errors else 'CẦN XEM LẠI'
        if errors: count_problem += 1
        print(f'{state}: {name} — {len(ls)} dòng dữ liệu')
        for issue in errors[:5]: print('    '+issue)
        if len(errors)>5: print(f'    ... và {len(errors)-5} cảnh báo khác')
    except (UnicodeError, OSError) as err:
        print(f'CẦN XEM LẠI: {name} — lỗi đọc tệp {type(err).__name__}')
        count_problem += 1
print('LƯU Ý: Đây là kiểm tra cấu trúc, không thay cho chạy thử đầy đủ với dữ liệu thật của nhóm.')
if count_problem:
    print('Chưa tương thích:', count_problem, 'tệp. KHÔNG nên merge PR vào main.')
    print('Có thể chạy: py -3 scripts\\prepare_legacy_data.py để tạo bản chuyển đổi XEM TRƯỚC.')
    sys.exit(2)
print('KIỂM TRA CẤU TRÚC: 6/6 tệp đạt.')
