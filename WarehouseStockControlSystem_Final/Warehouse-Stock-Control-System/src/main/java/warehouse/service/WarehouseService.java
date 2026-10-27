package warehouse.service;

import org.springframework.stereotype.Service;
import warehouse.dao.LocationDAO;
import warehouse.dao.ProductDAO;
import warehouse.dao.StockDAO;
import warehouse.dao.SupplierDAO;
import warehouse.model.*;
import warehouse.util.DatabaseException;

import java.util.List;

@Service
public class WarehouseService {
    private final StockDAO stock;
    private final ProductDAO products;
    private final SupplierDAO suppliers;
    private final LocationDAO locations;
    public WarehouseService(StockDAO stock, ProductDAO products, SupplierDAO suppliers, LocationDAO locations) { this.stock = stock; this.products = products; this.suppliers = suppliers; this.locations = locations; }
    public List<StockRecord> stockLevels() { return stock.findAll(); }
    public List<Product> products() { return products.findAll(); }
    public List<Supplier> suppliers() { return suppliers.findAll(); }
    public List<WarehouseLocation> locations() { return locations.findAll(); }
    public StockActionResult stockIn(int productId, int locationId, int supplierId, int userId, int quantity) {
        Product product = requireProduct(productId); WarehouseLocation location = requireLocation(locationId);
        stockInChecked(productId, locationId, supplierId, userId, quantity);
        return result("Stock IN recorded: +" + quantity + " units of " + product.getName() + " at " + location.getName() + ".", product, location);
    }
    public StockActionResult stockOut(int productId, int locationId, int userId, int quantity) {
        Product product = requireProduct(productId); WarehouseLocation location = requireLocation(locationId);
        stock.stockOut(productId, locationId, userId, quantity);
        return result("Stock OUT recorded: -" + quantity + " units of " + product.getName() + " at " + location.getName() + ".", product, location);
    }
    private void stockInChecked(int productId, int locationId, int supplierId, int userId, int quantity) {
        if (suppliers.findById(supplierId).isEmpty()) throw new DatabaseException("Supplier not found.");
        stock.stockIn(productId, locationId, supplierId, userId, quantity);
    }
    private Product requireProduct(int id) { return products.findById(id).orElseThrow(() -> new DatabaseException("Product not found.")); }
    private WarehouseLocation requireLocation(int id) { return locations.findById(id).orElseThrow(() -> new DatabaseException("Warehouse location not found.")); }
    private StockActionResult result(String message, Product product, WarehouseLocation location) {
        int remaining = stock.getQuantity(product.getProductId(), location.getLocationId());
        String warning = remaining < product.getReorderLevel() ? "Low stock: " + product.getName() + " has " + remaining + " units at " + location.getName() + " (reorder at " + product.getReorderLevel() + ")." : null;
        return new StockActionResult(message, warning);
    }
}
