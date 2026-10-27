package warehouse.model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String name;
    private int categoryId;
    private String categoryName;
    private int reorderLevel;
    private BigDecimal unitPrice;

    public Product() { }
    public Product(int productId, String name, int categoryId, int reorderLevel, BigDecimal unitPrice) {
        this.productId = productId; this.name = name; this.categoryId = categoryId;
        this.reorderLevel = reorderLevel; this.unitPrice = unitPrice;
    }
    public Product(String name, int categoryId, int reorderLevel, BigDecimal unitPrice) {
        this(0, name, categoryId, reorderLevel, unitPrice);
    }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
