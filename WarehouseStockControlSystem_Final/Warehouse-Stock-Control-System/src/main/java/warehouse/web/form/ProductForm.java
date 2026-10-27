package warehouse.web.form;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import warehouse.model.Product;

import java.math.BigDecimal;

public class ProductForm {
    @NotBlank(message = "Product name is required.") private String name;
    @Min(value = 1, message = "Choose a category.") private int categoryId;
    @Min(value = 0, message = "Reorder level cannot be negative.") private int reorderLevel;
    @NotNull(message = "Unit price is required.") @DecimalMin(value = "0.0", message = "Unit price cannot be negative.") private BigDecimal unitPrice;
    public static ProductForm from(Product p) { ProductForm f = new ProductForm(); f.name=p.getName(); f.categoryId=p.getCategoryId(); f.reorderLevel=p.getReorderLevel(); f.unitPrice=p.getUnitPrice(); return f; }
    public Product toProduct() { return new Product(name, categoryId, reorderLevel, unitPrice); }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; } public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public int getReorderLevel() { return reorderLevel; } public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public BigDecimal getUnitPrice() { return unitPrice; } public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
