"""Ensure the auditor rejects invalid data before Java imports it."""
from pathlib import Path
import sys
import unittest
import tempfile

sys.path.insert(0, str(Path(__file__).resolve().parent.parent / 'scripts'))
import audit_data as audit
from prepare_legacy_data import staff_rows


class DataAuditTests(unittest.TestCase):
    def test_nonfinite_negative_and_overflow(self):
        for price in ('NaN', 'Infinity', '-1'):
            self.assertTrue(audit.validate('mathang.txt', [f'Khác;SPK0001;Item;{price};2;Maker']))
        for quantity in ('-1', '2147483648'):
            self.assertTrue(audit.validate('kho_input.txt', [f'Khác;SPK0001;Item;100;{quantity};Maker']))

    def test_exact_product_shape(self):
        for row in ('Vở;VO0001;Notebook;100;2;0;Maker', 'Khác;SPK0001;Item;100;2;Maker;extra',
                    'Khác;BAD;Item;100;2;Maker', 'Khác;SPK0001;Item;100;2;'):
            self.assertTrue(audit.validate('mathang.txt', [row]))

    def test_duplicates(self):
        row = 'KH1;Alice;090'
        self.assertTrue(audit.validate('khachhang.txt', [row, row.lower()]))

    def test_dates_match_runtime(self):
        for value in ('21/11/2026', '21-11-2026'):
            self.assertEqual(audit.receipt_date(value).day, 21)
        for value in ('31/02/2026', '2026-11-21', '21/11.2026'):
            with self.assertRaises(ValueError):
                audit.receipt_date(value)

    def test_staff_branch_reference(self):
        row = 'BH1;Seller;090;test;Morning;100;BAN_HANG;CN1'
        admin = 'ADMIN;Admin;090;test;Morning;100;ADMIN;'
        self.assertFalse(audit.validate('nhanvien_cn.txt', [row, admin], branch_ids={'cn1'}))
        self.assertTrue(audit.validate('nhanvien_cn.txt', [row, admin], branch_ids={'cn2'}))

    def test_invoice_content_and_references(self):
        valid = ['HD1;2026-11-21;KH1;;;1', '1', 'SCH0001;2;150']
        self.assertFalse(audit.validate('hoadon.txt', valid, {'kh1'}))
        self.assertTrue(audit.validate('hoadon.txt', valid, {'kh2'}))
        for detail in ('SCH0001;0;150', 'SCH0001;2;NaN', 'SCH0001;2;-1'):
            self.assertTrue(audit.validate('hoadon.txt', valid[:2] + [detail], {'kh1'}))

    def test_receipt_supplier_conflict_and_blank_code(self):
        row = 'PN1;21-11-2026;Khác;SPK0001;Item;100;2;Maker;CHO_DUYET'
        self.assertFalse(audit.validate('phieunhap_input.txt', [row]))
        self.assertTrue(audit.validate('phieunhap_input.txt', [row.replace('PN1;', ';', 1)]))
        self.assertTrue(audit.validate('phieunhap_input.txt', [row, row.replace('SPK0001', 'SPK0002').replace('Maker', 'Other')]))

    def test_repository_data(self):
        customers = {line.split(';')[0].casefold() for line in audit.rows(audit.ROOT / 'data/khachhang.txt')}
        branches = {line.split(';')[0].casefold() for line in audit.rows(audit.ROOT / 'data/chinhanh.txt')}
        for name in audit.EXPECTED:
            with self.subTest(name=name):
                self.assertFalse(audit.validate(name, audit.rows(audit.ROOT / 'data' / name), customers, branches))

    def test_preview_rejects_invalid_staff_salary(self):
        with tempfile.TemporaryDirectory(prefix='preview-validate-') as folder:
            path = Path(folder) / 'staff.txt'
            for salary in ('NaN', 'Infinity', '-1'):
                path.write_text(f'ADMIN;Admin;090;test;Morning;{salary};ADMIN\n', encoding='utf-8')
                with self.assertRaises(ValueError):
                    staff_rows(path)


if __name__ == '__main__':
    unittest.main(verbosity=2)
