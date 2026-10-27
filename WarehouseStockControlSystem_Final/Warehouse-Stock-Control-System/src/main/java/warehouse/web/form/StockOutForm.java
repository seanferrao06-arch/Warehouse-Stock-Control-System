package warehouse.web.form;

import jakarta.validation.constraints.Min;

public class StockOutForm {
    @Min(value = 1, message = "Choose a product.") private int productId;
    @Min(value = 1, message = "Choose a warehouse location.") private int locationId;
    @Min(value = 1, message = "Quantity must be at least 1.") private int quantity;
    public int getProductId() { return productId; } public void setProductId(int productId) { this.productId = productId; }
    public int getLocationId() { return locationId; } public void setLocationId(int locationId) { this.locationId = locationId; }
    public int getQuantity() { return quantity; } public void setQuantity(int quantity) { this.quantity = quantity; }
}
