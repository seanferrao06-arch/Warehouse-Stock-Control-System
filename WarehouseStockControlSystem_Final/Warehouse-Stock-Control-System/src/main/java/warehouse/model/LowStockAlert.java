package warehouse.model;

public record LowStockAlert(int productId, String productName, String locationName, int quantity, int reorderLevel) { }
