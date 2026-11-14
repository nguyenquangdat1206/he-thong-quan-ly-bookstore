# integration smoke tests: no external dependencies
from pathlib import Path
import tempfile, shutil, subprocess, sys
ROOT=Path(__file__).resolve().parent.parent
CP=str(ROOT/'build')

def simulate(inputs, expected, forbidden=(), max_timeout=25):
    with tempfile.TemporaryDirectory(prefix='oop_console_') as f:
        folder=Path(f)
        (folder/'data').mkdir(parents=True,exist_ok=True)
        for name, sub in [('kho_input.txt','kho'),('khachhang.txt','khachhang'),('phieunhap_input.txt','phieunhap'),('hoadon.txt','hoadon')]:
            shutil.copy2(ROOT/'tests'/'fixtures'/sub/name,folder/'data'/name)
        shutil.copy2(ROOT/'tests'/'fixtures'/'nhanvien_demo.txt',folder/'data'/'nhanvien.txt')
        # standalone default user session
        proc=subprocess.run(['java','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-Dfile.encoding=UTF-8','-cp',CP,'Main'],
            input='\n'.join(inputs)+'\n',stdout=subprocess.PIPE,stderr=subprocess.PIPE,
            encoding='utf-8',cwd=folder,timeout=max_timeout)
        text=proc.stdout+'\n'+proc.stderr
        if proc.returncode!=0: raise AssertionError(f'exit code {proc.returncode}\n{text[-3000:]}')
        for pattern in expected:
            if pattern not in text:raise AssertionError(f'missing {pattern!r}\n{text[-3000:]}')
        for pattern in forbidden:
            if pattern in text: raise AssertionError(f'unexpected {pattern!r}\n{text[-3000:]}')
        for input_name in ('kho_input.txt','khachhang.txt','phieunhap_input.txt','hoadon.txt','nhanvien.txt'):
            expected_bytes = (ROOT/'tests'/'fixtures'/'nhanvien_demo.txt').read_bytes() if input_name=='nhanvien.txt' else None
            if expected_bytes is None:
                part = 'hoadon' if input_name=='hoadon.txt' else ('khachhang' if input_name=='khachhang.txt' else ('phieunhap' if input_name.startswith('phieu') else 'kho'))
                expected_bytes = (ROOT/'tests'/'fixtures'/part/input_name).read_bytes()
            if input_name!='nhanvien.txt' and (folder/'data'/input_name).read_bytes()!=expected_bytes:
                raise AssertionError(f'test unexpectedly overwrote sample data/{input_name}')
        return text,folder

cases = [
  ('guest cannot modify', ['2','0'], ['Tạm biệt!'],['=== ADMIN / TOÀN QUYỀN ===']),
  ('wrong password rejected', ['1','ADMIN','bad','0'],['Sai mã hoặc mật khẩu.'],['=== ADMIN / TOÀN QUYỀN ===']),
  ('admin HR and payroll', ['1','ADMIN','admin123','7','1','6','0','0','0'],['Đăng nhập thành công: Quản trị demo','Tổng lương cơ bản: 32500000','Tạm biệt!'],[]),
  ('sales limited menu', ['1','BH01','bh123','1','0','0'],['=== NHÂN VIÊN BÁN HÀNG ===','Đã đăng xuất.'],['=== ADMIN / TOÀN QUYỀN ===','=== NHÂN VIÊN NHẬP KHO ===']),
  ('stock limited menu', ['1','KHO01','kho123','4','0','0'],['=== NHÂN VIÊN NHẬP KHO ===','Đã đăng xuất.'],['=== ADMIN / TOÀN QUYỀN ===','=== NHÂN VIÊN BÁN HÀNG ===']),
  ('invalid options', ['99','1','ADMIN','admin123','99','0','0'],['Lựa chọn không hợp lệ!','Lựa chọn không hợp lệ.','Tạm biệt!'],[]),
  ('default data paths import correctly', ['1','ADMIN','admin123', '1','8','.','0', '4','6','.','0', '2','8','.','1','0', '0','0'], ['Đã tải 5 khách hàng','Đọc phiếu nhập: thêm 10 dòng','PN004','Tạm biệt!'],[]),
  ('complete sale in admin role', [
      '1','ADMIN','admin123',
      '1','8','data/kho_input.txt','0',
      '3','2','0',
      '4','6','data/khachhang.txt','0',
      '5','2','KH01','HD999','26/10/2026','S001','2','k',
      '7','HD999','1','999999',
      '1','6','data/hoadon_output.txt','0',
      '0','0'],
      ['Đã nhập xong mặt hàng mã S001','Tạo hóa đơn thành công!','Thanh toán thành công','HD999','Tạm biệt!'],[]),
]
passed=0
for name,inputs,expected,forbidden in cases:
    try:
        output,_=simulate(inputs,expected,forbidden)
        if name=='complete sale in admin role':
            pass  # Không ghi log console (có thể chứa dữ liệu mẫu/tài khoản) vào Git
        print('PASS',name)
        passed+=1
    except Exception as e:
        print('FAIL',name, str(e))
        sys.exit(1)
# Legacy data/nhanvien.txt must never be silently overwritten when import format differs.
with tempfile.TemporaryDirectory(prefix='oop_legacy_staff_') as tmp:
    staff = Path(tmp)/'data'/'nhanvien.txt'
    staff.parent.mkdir(parents=True,exist_ok=True)
    legacy = b'OLD_LEGACY_FORMAT;ONLY_TWO_FIELDS\n'
    staff.write_bytes(legacy)
    run = subprocess.run(['java','-Dfile.encoding=UTF-8','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-cp',CP,'Main'],
        input='0\n',text=True,encoding='utf-8',capture_output=True,cwd=tmp,timeout=10)
    if run.returncode!=0 or staff.read_bytes()!=legacy or 'Bỏ qua lưu nhân viên' not in run.stdout:
        raise AssertionError('Legacy staff input was overwritten or guard did not trigger')
    print('PASS legacy staff file is preserved on exit')
    passed+=1
print(f'CONSOLE RESULT: {passed} PASS / 0 FAIL')
