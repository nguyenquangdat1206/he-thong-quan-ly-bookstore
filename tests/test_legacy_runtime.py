#!/usr/bin/env python3
"""Check legacy staff and invalid receipt dates without altering input bytes."""
from pathlib import Path
import os, tempfile, subprocess, shutil
ROOT = Path(__file__).resolve().parent.parent
with tempfile.TemporaryDirectory(prefix='oop_v4_legacy_') as d:
    tmp=Path(d)
    data=tmp/'data'; data.mkdir()
    legacy_staff='\n'.join([
        'dsNhanVien.them(new QuanLy("admin","Quản trị","0900","test-only","Sáng",100));',
        'dsNhanVien.them(new NhanVienBanHang("bh01","Bán hàng","0901","test-only-2","Chiều",80));',
        'dsNhanVien.them(new NhanVienNhapKho("kho01","Kho hàng","0902","test-only-3","Tối",75));',
    ])+'\n'
    (data/'nhanvien_cn.txt').write_text(legacy_staff,encoding='utf-8')
    (data/'chinhanh.txt').write_text('CN01;Chi nhánh 1;Địa chỉ mẫu\n',encoding='utf-8')
    src=ROOT/'tests'/'fixtures'/'phieunhap'/'phieunhap_input.txt'
    pn_text=src.read_text(encoding='utf-8').replace('15/09/2025','15/09.2025')
    (data/'phieunhap_input.txt').write_text(pn_text,encoding='utf-8')
    for name,folder in [('kho_input.txt','kho'),('khachhang.txt','khachhang'),('hoadon.txt','hoadon')]:
        shutil.copyfile(ROOT/'tests'/'fixtures'/folder/name,data/name)
    (data/'mathang.txt').write_text('Sách;SCH0001;Sách mẫu;25000;4;Tác giả;NXB;Thiếu nhi\n',encoding='utf-8')
    original={p.name:p.read_bytes() for p in data.iterdir() if p.name != 'chinhanh.txt'}
    cp=os.environ.get('BOOKSTORE_TEST_CP', str(ROOT/'build-test'))
    run=subprocess.run(['java','-Dfile.encoding=UTF-8','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-cp',cp,'Main'],cwd=tmp,input='1\nadmin\ntest-only\nCN01\n7\n1\n0\n0\n0\n',text=True,capture_output=True,encoding='utf-8',timeout=25)
    assert run.returncode==0,run.stderr[-2000:]
    assert 'Đăng nhập thành công' in run.stdout,run.stdout[-2000:]
    assert 'Bỏ qua lưu nhân viên' in run.stdout, 'Legacy data must not be auto-overwritten'
    assert {p.name:p.read_bytes() for p in data.iterdir() if p.name in original}==original, 'Original main-branch TXT data mutated'
    out=subprocess.run(['java','-Dfile.encoding=UTF-8','-Dstdout.encoding=UTF-8','-Dstderr.encoding=UTF-8','-cp',cp,'Main'],cwd=tmp,input='1\nadmin\ntest-only\nCN01\n2\n8\n.\n0\n0\n0\n',text=True,capture_output=True,encoding='utf-8',timeout=25)
    assert out.returncode==0,out.stderr[-2000:]
    assert 'Đọc phiếu nhập: thêm 8 dòng, bỏ qua 2' in out.stdout, out.stdout[-1500:]
    assert 'Bỏ qua phiếu nhập dòng 1' in out.stdout, out.stdout[-1500:]
    assert {p.name:p.read_bytes() for p in data.iterdir() if p.name in original}==original, 'Legacy PN mutated'
    print('PASS legacy staff login, malformed receipt dates rejected, original files unchanged')
