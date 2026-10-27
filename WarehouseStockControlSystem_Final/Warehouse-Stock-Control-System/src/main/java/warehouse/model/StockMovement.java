package warehouse.model;

import java.time.LocalDateTime;

public class StockMovement {
    private int movementId;
    private int productId;
    private String productName;
    private int locationId;
    private String locationName;
    private Integer supplierId;
    private String supplierName;
    private int userId;
    private String username;
    private MovementType type;
    private int quantity;
    private LocalDateTime timestamp;

    public StockMovement(int productId, int locationId, Integer supplierId, int userId, MovementType type, int quantity) {
        this.productId = productId; this.locationId = locationId; this.supplierId = supplierId;
        this.userId = userId; this.type = type; this.quantity = quantity;
    }
    public int getMovementId() { return movementId; }
    public void setMovementId(int movementId) { this.movementId = movementId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getLocationId() { return locationId; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public Integer getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public MovementType getType() { return type; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
