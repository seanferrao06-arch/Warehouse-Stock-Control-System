package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import warehouse.interfaces.Auditable;
import warehouse.interfaces.Crud;
import warehouse.interfaces.StockManageable;
import warehouse.model.MovementType;
import warehouse.model.StockMovement;
import warehouse.model.StockRecord;
import warehouse.util.DatabaseException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Owns the only legal inventory mutations: audited Stock IN and Stock OUT. */
@Repository
public class StockDAO extends JdbcDao implements Crud<StockRecord>, StockManageable, Auditable {
    private static final String STOCK_SELECT = "SELECT cs.product_id,p.name product_name,cs.location_id,l.name location_name,cs.quantity,p.reorder_level FROM current_stock cs JOIN product p ON p.product_id=cs.product_id JOIN warehouse_location l ON l.location_id=cs.location_id";

    public StockDAO(JdbcTemplate jdbc) { super(jdbc); }

    @Override @Transactional
    public void stockIn(int productId, int locationId, int supplierId, int userId, int quantity) {
        validateIdsAndQuantity(productId, locationId, userId, quantity);
        try {
            requireExists("product", "product_id", productId, "product");
            requireExists("warehouse_location", "location_id", locationId, "warehouse location");
            requireExists("supplier", "supplier_id", supplierId, "supplier");
            requireExists("`user`", "user_id", userId, "user");
            int current = lockedQuantity(productId, locationId);
            if (current < 0) jdbc.update("INSERT INTO current_stock (product_id,location_id,quantity) VALUES (?,?,?)", productId, locationId, quantity);
            else jdbc.update("UPDATE current_stock SET quantity=? WHERE product_id=? AND location_id=?", current + quantity, productId, locationId);
            writeMovement(new StockMovement(productId, locationId, supplierId, userId, MovementType.IN, quantity));
        } catch (DataAccessException e) {
            throw databaseError("Could not record stock IN. Please try again.", e);
        }
    }

    @Override @Transactional
    public void stockOut(int productId, int locationId, int userId, int quantity) {
        validateIdsAndQuantity(productId, locationId, userId, quantity);
        try {
            requireExists("product", "product_id", productId, "product");
            requireExists("warehouse_location", "location_id", locationId, "warehouse location");
            requireExists("`user`", "user_id", userId, "user");
            int current = lockedQuantity(productId, locationId);
            if (current < 0 || current < quantity) throw new DatabaseException("Insufficient stock. Available quantity: " + Math.max(current, 0) + ".");
            jdbc.update("UPDATE current_stock SET quantity=? WHERE product_id=? AND location_id=?", current - quantity, productId, locationId);
            writeMovement(new StockMovement(productId, locationId, null, userId, MovementType.OUT, quantity));
        } catch (DataAccessException e) {
            throw databaseError("Could not record stock OUT. Please try again.", e);
        }
    }

    /** Allows an explicit immutable audit event without permitting a direct balance edit. */
    @Override @Transactional
    public void logMovement(StockMovement movement) {
        if (movement == null || movement.getQuantity() <= 0) throw new DatabaseException("An audit movement needs a positive quantity.");
        try {
            requireExists("product", "product_id", movement.getProductId(), "product");
            requireExists("warehouse_location", "location_id", movement.getLocationId(), "warehouse location");
            requireExists("`user`", "user_id", movement.getUserId(), "user");
            if (movement.getType() == MovementType.IN && movement.getSupplierId() == null) throw new DatabaseException("Stock IN audit records require a supplier.");
            if (movement.getSupplierId() != null) requireExists("supplier", "supplier_id", movement.getSupplierId(), "supplier");
            writeMovement(movement);
        } catch (DataAccessException e) {
            throw databaseError("Could not write the audit movement.", e);
        }
    }

    @Override public StockRecord save(StockRecord record) { throw protectedWrite(); }
    @Override public boolean update(StockRecord record) { throw protectedWrite(); }
    @Override public boolean delete(int id) { throw protectedWrite(); }

    @Override public Optional<StockRecord> findById(int productId) {
        try { return jdbc.query(STOCK_SELECT + " WHERE cs.product_id=? ORDER BY l.name LIMIT 1", rs -> rs.next() ? Optional.of(mapStock(rs)) : Optional.empty(), productId); }
        catch (DataAccessException e) { throw databaseError("Could not look up the stock level.", e); }
    }

    @Override public List<StockRecord> findAll() {
        try { return jdbc.query(STOCK_SELECT + " ORDER BY p.name,l.name", (rs, row) -> mapStock(rs)); }
        catch (DataAccessException e) { throw databaseError("Could not load stock levels.", e); }
    }

    public int getQuantity(int productId, int locationId) {
        try { return jdbc.query("SELECT quantity FROM current_stock WHERE product_id=? AND location_id=?", rs -> rs.next() ? rs.getInt(1) : 0, productId, locationId); }
        catch (DataAccessException e) { throw databaseError("Could not look up the remaining stock.", e); }
    }

    private void writeMovement(StockMovement movement) {
        jdbc.update("INSERT INTO stock_movement (product_id,location_id,supplier_id,user_id,type,quantity) VALUES (?,?,?,?,?,?)",
                movement.getProductId(), movement.getLocationId(), movement.getSupplierId(), movement.getUserId(), movement.getType().name(), movement.getQuantity());
    }

    /** FOR UPDATE makes concurrent stock operations at the same location serializable. */
    private int lockedQuantity(int productId, int locationId) {
        List<Integer> quantities = jdbc.query("SELECT quantity FROM current_stock WHERE product_id=? AND location_id=? FOR UPDATE", (rs, row) -> rs.getInt(1), productId, locationId);
        return quantities.isEmpty() ? -1 : quantities.get(0);
    }

    private void requireExists(String table, String column, int id, String label) {
        Integer found = jdbc.query("SELECT 1 FROM " + table + " WHERE " + column + "=?", rs -> rs.next() ? 1 : null, id);
        if (found == null) throw new DatabaseException("No " + label + " exists with ID " + id + ".");
    }

    private StockRecord mapStock(ResultSet rs) throws SQLException {
        return new StockRecord(rs.getInt("product_id"), rs.getString("product_name"), rs.getInt("location_id"), rs.getString("location_name"), rs.getInt("quantity"), rs.getInt("reorder_level"));
    }

    private void validateIdsAndQuantity(int product, int location, int user, int quantity) {
        if (product <= 0 || location <= 0 || user <= 0 || quantity <= 0) throw new DatabaseException("All IDs and quantity must be positive numbers.");
    }

    private DatabaseException protectedWrite() {
        return new DatabaseException("Direct stock edits are blocked. Use Stock IN or Stock OUT so the audit trail remains complete.");
    }
}
