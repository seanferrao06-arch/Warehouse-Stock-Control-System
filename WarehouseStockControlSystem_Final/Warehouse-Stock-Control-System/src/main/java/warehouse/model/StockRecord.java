package warehouse.model;

/** A read model for the quantity of one product at one warehouse location. */
public class StockRecord {
    private int productId;
    private String productName;
    private int locationId;
    private String locationName;
    private int quantity;
    private int reorderLevel;
    public StockRecord(int productId, String productName, int locationId, String locationName, int quantity, int reorderLevel) {
        this.productId = productId; this.productName = productName; this.locationId = locationId;
        this.locationName = locationName; this.quantity = quantity; this.reorderLevel = reorderLevel;
    }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public int getQuantity() { return quantity; }
    public int getReorderLevel() { return reorderLevel; }
}
