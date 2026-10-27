package warehouse.model;

import java.math.BigDecimal;

public record StockSummary(String productName, String categoryName, int totalQuantity, int locations, BigDecimal inventoryValue) { }
