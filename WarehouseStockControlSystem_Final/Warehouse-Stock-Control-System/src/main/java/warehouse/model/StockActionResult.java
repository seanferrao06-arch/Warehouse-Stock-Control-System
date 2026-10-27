package warehouse.model;

/** Message payload returned by a stock service for the next-page flash alert. */
public record StockActionResult(String successMessage, String lowStockWarning) { }
