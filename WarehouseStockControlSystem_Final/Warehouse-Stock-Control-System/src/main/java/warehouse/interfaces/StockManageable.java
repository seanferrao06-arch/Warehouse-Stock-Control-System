package warehouse.interfaces;

public interface StockManageable {
    void stockIn(int productId, int locationId, int supplierId, int userId, int quantity);
    void stockOut(int productId, int locationId, int userId, int quantity);
}
