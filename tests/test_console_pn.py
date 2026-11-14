from pathlib import Path
import tempfile, subprocess, shutil, sys
ROOT=Path(__file__).resolve().parent.parent

scenarios=[
 ('admin imports PN, views auto-suppliers',
  ['1','ADMIN','admin123','2','8','.','10','1','0','0','0'],
  ['Đọc phiếu nhập: thêm 10 dòng','DANH SÁCH NHÀ CUNG CẤP','Kim Đồng','Hồng Hà','PN004'], []),
 ('warehouse role imports PN and supplier listing',
  ['1','KHO01','kho123','2','8','.','10','0','0','0'],
  ['=== NHÂN VIÊN NHẬP KHO ===','NCC0','Giáo Dục','Tạm biệt!'], ['=== ADMIN / TOÀN QUYỀN ===']),
 ('admin double imports without duplication',
  ['1','ADMIN','admin123','2','8','.','8','.','1','0','0','0'],
  ['Đọc phiếu nhập: thêm 10 dòng','Đọc phiếu nhập: thêm 0 dòng','PN004'], []),
 ('admin writes and reloads PN',
  ['1','ADMIN','admin123','2','8','.','9','.','0','0','0'],
  ['Đã ghi 4 phiếu / 10 dòng','Tạm biệt!'], []),
 ('read nonexistent PN file does not crash',
  ['1','ADMIN','admin123','2','8','nonexistent_zz_OOP.txt','0','0','0'],
  ['Lỗi đọc file phiếu nhập:', 'Tạm biệt!'], []),
 ('manually add PN from menu',
  ['1','ADMIN','admin123','2','2','PNCUSTOM','15/10/2026','Nhà Xuất Bản A','1','7','CUSTOM1','Bìa bọc','NSX A','5000','10','10','1','0','0','0'],
  ['Thêm thành công phiếu: PNCUSTOM', 'Nhà Xuất Bản A', 'PNCUSTOM'], []),
 ('sales does not expose warehouse PN menu',
  ['1','BH01','bh123','7','0','0'],
  ['Không được phép truy cập chức năng này.','Tạm biệt!'], ['---------- PHIẾU NHẬP ----------']),
]
count=0
for name,inputs,expected,forbidden in scenarios:
 with tempfile.TemporaryDirectory(prefix='oop_pn_test_') as temp:
  dest=Path(temp)
  (dest/'data').mkdir(parents=True,exist_ok=True)
  for filename,sub in [('kho_input.txt','kho'),('khachhang.txt','khachhang'),('phieunhap_input.txt','phieunhap'),('hoadon.txt','hoadon')]:
   shutil.copy2(ROOT/'tests'/'fixtures'/sub/filename,dest/'data'/filename)
  shutil.copy2(ROOT/'tests'/'fixtures'/'nhanvien_demo.txt',dest/'data'/'nhanvien.txt')
  run=subprocess.run(['java','-Dfile.encoding=UTF-8','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-cp',str(ROOT/'build'),'Main'],
    input='\n'.join(inputs)+'\n',text=True,encoding='utf8',capture_output=True,cwd=dest,timeout=20)
  out=run.stdout+'\n'+run.stderr
  errors=[f'exit {run.returncode}' if run.returncode!=0 else ''] + [f'missing {p}' for p in expected if p not in out] + [f'unexpected {p}' for p in forbidden if p in out]
  errors=[x for x in errors if x]
  if name=='admin writes and reloads PN' and (not (dest/'data/phieunhap_output.txt').exists() or len((dest/'data/phieunhap_output.txt').read_text(encoding='utf8').splitlines()) != 10):
   errors.append('expected 10 lines in output')
  if errors:
   print('FAIL',name,':',errors,'\n',out[-2200:]);sys.exit(1)
  count+=1
  print('PASS',name)
print(f'NEW CONSOLE TESTS: {count} PASS, 0 FAIL')
