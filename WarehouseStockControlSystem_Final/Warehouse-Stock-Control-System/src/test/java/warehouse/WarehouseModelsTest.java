package warehouse;

import org.junit.jupiter.api.Test;
import warehouse.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseModelsTest {

    @Test
    void testProductModel() {
        Product p = new Product(1, "Test Product", 2, 10, new BigDecimal("49.99"));
        assertEquals(1, p.getProductId());
        assertEquals("Test Product", p.getName());
        assertEquals(2, p.getCategoryId());
        assertEquals(10, p.getReorderLevel());
        assertEquals(new BigDecimal("49.99"), p.getUnitPrice());

        p.setCategoryName("Beverages");
        assertEquals("Beverages", p.getCategoryName());
    }

    @Test
    void testStockMovementModel() {
        StockMovement m = new StockMovement(1, 2, 3, 4, MovementType.IN, 50);
        m.setMovementId(100);
        m.setProductName("Test Product");
        m.setLocationName("Main Warehouse");
        m.setSupplierName("Vendor A");
        m.setUsername("admin");
        m.setTimestamp(LocalDateTime.now());

        assertEquals(100, m.getMovementId());
        assertEquals(MovementType.IN, m.getType());
        assertEquals(50, m.getQuantity());
        assertEquals("Vendor A", m.getSupplierName());
    }

    @Test
    void testStockActionResult() {
        StockActionResult result = new StockActionResult("Recorded successfully", "Low stock warning");
        assertEquals("Recorded successfully", result.successMessage());
        assertEquals("Low stock warning", result.lowStockWarning());
    }

    @Test
    void testUserModel() {
        User u = new User(1, "admin", "hash123", Role.ADMIN);
        assertEquals("admin", u.getUsername());
        assertEquals(Role.ADMIN, u.getRole());
    }
}
