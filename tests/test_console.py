# integration smoke tests: no external dependencies
from pathlib import Path
import tempfile, shutil, subprocess, os, sys
ROOT=Path(__file__).resolve().parent.parent
CP=str(ROOT/'build')

def simulate(inputs, expected, forbidden=(), max_timeout=25):
    with tempfile.TemporaryDirectory(prefix='oop_console_') as f:
        folder=Path(f)
        for sub in ['kho','khachhang','phieunhap','hoadon']:
            shutil.copytree(ROOT/sub, folder/sub)
        shutil.copy2(ROOT/'nhanvien.txt',folder/'nhanvien.txt')
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
        return text,folder

cases = [
  ('guest cannot modify', ['2','0'], ['Tạm biệt!'],['=== ADMIN / TOÀN QUYỀN ===']),
  ('wrong password rejected', ['1','ADMIN','bad','0'],['Sai mã hoặc mật khẩu.'],['=== ADMIN / TOÀN QUYỀN ===']),
  ('admin HR and payroll', ['1','ADMIN','admin123','7','1','6','0','0','0'],['Đăng nhập thành công: Quản trị demo','Tổng lương cơ bản: 32500000','Tạm biệt!'],[]),
  ('sales limited menu', ['1','BH01','bh123','1','0','0'],['=== NHÂN VIÊN BÁN HÀNG ===','Đã đăng xuất.'],['=== ADMIN / TOÀN QUYỀN ===','=== NHÂN VIÊN NHẬP KHO ===']),
  ('stock limited menu', ['1','KHO01','kho123','4','0','0'],['=== NHÂN VIÊN NHẬP KHO ===','Đã đăng xuất.'],['=== ADMIN / TOÀN QUYỀN ===','=== NHÂN VIÊN BÁN HÀNG ===']),
  ('invalid options', ['99','1','ADMIN','admin123','99','0','0'],['Lựa chọn không hợp lệ!','Lựa chọn không hợp lệ.','Tạm biệt!'],[]),
  ('complete sale in admin role', [
      '1','ADMIN','admin123',
      '1','8','kho/kho_input.txt','0',
      '3','2','0',
      '4','6','khachhang/khachhang.txt','0',
      '5','2','KH01','HD999','26/10/2026','S001','2','k',
      '7','HD999','1','999999',
      '1','6','hoadon.txt','0',
      '0','0'],
      ['Đã nhập xong mặt hàng mã S001','Tạo hóa đơn thành công!','Thanh toán thành công','HD999','Tạm biệt!'],[]),
]
passed=0
for name,inputs,expected,forbidden in cases:
    try:
        output,_=simulate(inputs,expected,forbidden)
        if name=='complete sale in admin role':
            Path(ROOT/'tests'/'console_sale_output.log').write_text(output,encoding='utf-8')
        print('PASS',name)
        passed+=1
    except Exception as e:
        print('FAIL',name, str(e))
        sys.exit(1)
print(f'CONSOLE RESULT: {passed} PASS / 0 FAIL')
