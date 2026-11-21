import kho.Kho;
import mathang.DanhSachMatHang;
import mathang.MatHang;

/** Shared setup for tests that need products on the sales shelf. */
final class TestSupport {
    private TestSupport() {
    }

    static void stockShelf(DanhSachMatHang catalog, MatHang product) {
        Kho warehouse = new Kho();
        int quantity = product.getSoLuong();
        if (!warehouse.tangTonKho(product.saoChep(), quantity)) {
            throw new AssertionError("Could not stock product: " + product.getMa());
        }
        catalog.nhaptukho(warehouse, product.getMa(), quantity);
    }
}
