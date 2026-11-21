"""Exercise real repository input and restart/failure paths in isolated directories."""
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parent.parent
ADMIN = ['1', 'ADMIN', 'test-only', 'CN01']
IMPORT = ['2', '8', '.', '0']
SHELF = ['3', '2', 'SCH2001', '5', '0']
CUSTOMERS = ['4', '6', '.', '0']
ORDER = ['5', '2', 'KH01', '1', 'SCH2001', '1', '5', 'HDDEEP', '0', '0']
EXIT = ['0', '0']


class DeepConsoleTests(unittest.TestCase):
    def setUp(self):
        self.directory = tempfile.TemporaryDirectory(prefix='bookstore-deep-')
        self.folder = Path(self.directory.name)
        self.data = self.folder / 'data'
        shutil.copytree(ROOT / 'data', self.data, ignore=shutil.ignore_patterns('phien*', '*_output.txt'))
        (self.data / 'nhanvien_cn.txt').write_text(
            'ADMIN;Admin;090;test-only;Morning;100.0;ADMIN;\n'
            'BH01;Seller;091;test-only;Morning;100.0;BAN_HANG;CN01\n', encoding='utf-8')
        self.originals = {p.name: p.read_bytes() for p in self.data.glob('*.txt')
                          if p.name not in {'nhanvien_cn.txt', 'chinhanh.txt'}}

    def tearDown(self):
        try:
            for name, content in self.originals.items():
                self.assertEqual(content, (self.data / name).read_bytes(), f'input file changed: {name}')
        finally:
            self.directory.cleanup()

    def launch(self, inputs, expected_code=0):
        run = subprocess.run(
            ['java', '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8',
             '-cp', str(ROOT / 'build'), 'Main'],
            input='\n'.join(inputs) + '\n', text=True, capture_output=True, encoding='utf-8',
            cwd=self.folder, timeout=25)
        output = run.stdout + '\n' + run.stderr
        self.assertEqual(expected_code, run.returncode, output[-1500:])
        self.assertNotIn('Exception in thread', output)
        return output

    def test_real_receipts_and_default_supplier_contacts(self):
        output = self.launch(ADMIN + IMPORT + ['6', '5', '.', '1', '0'] + EXIT)
        self.assertIn('Đọc phiếu nhập: thêm 50 dòng', output)
        self.assertIn('cập nhật 7, bỏ qua 0', output)
        self.assertIn('contact@nxbtre.example', output)

    def test_manual_novel_once_then_restart(self):
        output = self.launch(ADMIN + ['2', '2', 'PNMAN', '21/11/2026', 'Publisher', '1', '3',
                                     'TTH0101', 'Novel', 'Genre', 'Author', '100', '2', '0'] + EXIT)
        self.assertIn('Thêm thành công phiếu nhập PNMAN', output)
        second = self.launch(ADMIN + ['2', '1', '0'] + EXIT)
        self.assertIn('PNMAN', second)
        self.assertIn('Novel', second)

    def test_deleted_shelf_item_is_not_charged(self):
        output = self.launch(ADMIN + IMPORT + ['2', '11', '.', '0'] + SHELF + CUSTOMERS + ORDER
                             + ['3', '6', 'SCH2001', '0', '5', '7', 'HDDEEP', '1', '999999', '0'] + EXIT)
        self.assertIn('không còn trên kệ; chưa thu tiền', output)
        self.assertNotIn('Thanh toán thành công', output)

    def test_eof_saves_pending_invoice_and_stock(self):
        output = self.launch(ADMIN + IMPORT + ['2', '11', '.', '0'] + SHELF + CUSTOMERS + ORDER)
        self.assertIn('Đã kết thúc đầu vào', output)
        second = self.launch(ADMIN + ['3', '1', '0', '5', '1', '0'] + EXIT)
        self.assertIn('Đã khôi phục dữ liệu phiên trước', second)
        self.assertIn('Số mặt hàng: 1', second)
        self.assertIn('Mã hóa đơn: HDDEEP', second)

    def test_bad_branch_source_is_never_overwritten(self):
        path = self.data / 'chinhanh.txt'
        path.write_bytes(b'BROKEN BRANCH INPUT\n')
        output = self.launch(['0'])
        self.assertIn('Bỏ qua lưu chi nhánh', output)
        self.assertEqual(b'BROKEN BRANCH INPUT\n', path.read_bytes())

    def test_corrupted_session_preserved(self):
        path = self.data / 'phien.properties'
        content = b'broken=\\uZZZZ\n'
        path.write_bytes(content)
        output = self.launch(['0'])
        self.assertIn('Không khôi phục được phiên trước', output)
        self.assertEqual(content, path.read_bytes())

    def test_invalid_path_retries(self):
        output = self.launch(ADMIN + ['2', '8', 'bad\x00path', '.', '0'] + EXIT)
        self.assertIn('Đường dẫn không hợp lệ', output)
        self.assertIn('Đọc phiếu nhập: thêm 50 dòng', output)

    def test_failed_session_save_returns_failure_at_eof(self):
        proc = subprocess.Popen(
            ['java', '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8',
             '-cp', str(ROOT / 'build'), 'Main'],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
            text=True, encoding='utf-8', cwd=self.folder)
        try:
            prefix = []
            for line in proc.stdout:
                prefix.append(line)
                if 'QUẢN LÝ NHÀ SÁCH' in line:
                    break
            # Introduce the write failure only after startup has successfully read the directory.
            (self.data / 'phien.properties').mkdir()
            output, errors = proc.communicate('0\n', timeout=25)
            output = ''.join(prefix) + output + errors
            self.assertEqual(1, proc.returncode, output[-1000:])
        finally:
            if proc.poll() is None:
                proc.kill()
                proc.communicate()
        self.assertIn('Chưa lưu được phiên', output)
        self.assertNotIn('Tạm biệt!', output)

    def test_main_warehouse_input_loads_once(self):
        output = self.launch(ADMIN + ['1', '10', '.', '10', '.', '7', '0'] + EXIT)
        self.assertIn('Đã đọc kho: thêm 50 mã', output)
        self.assertIn('Đã đọc kho: thêm 0 mã', output)

    def test_standalone_shelf_can_load_warehouse(self):
        run = subprocess.run(
            ['java', '-Dfile.encoding=UTF-8', '-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8',
             '-cp', str(ROOT / 'build'), 'mathang.QuanLyMatHang'],
            input='12\n.\n2\nSCH0001\n3\n1\n0\n', text=True, capture_output=True,
            encoding='utf-8', cwd=self.folder, timeout=25)
        self.assertEqual(0, run.returncode, run.stderr)
        self.assertIn('Đã đọc kho: thêm 50 mã', run.stdout)
        self.assertIn('Số mặt hàng: 1', run.stdout)


if __name__ == '__main__':
    unittest.main(verbosity=2)
