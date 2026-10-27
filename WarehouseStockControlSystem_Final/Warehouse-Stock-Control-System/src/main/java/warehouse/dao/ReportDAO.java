package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.interfaces.Reportable;
import warehouse.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public class ReportDAO extends JdbcDao implements Reportable {
    public ReportDAO(JdbcTemplate jdbc) { super(jdbc); }

    @Override public List<LowStockAlert> getLowStockAlerts() {
        String sql = "SELECT p.product_id,p.name product_name,l.name location_name,cs.quantity,p.reorder_level FROM current_stock cs JOIN product p ON p.product_id=cs.product_id JOIN warehouse_location l ON l.location_id=cs.location_id WHERE cs.quantity < p.reorder_level ORDER BY (p.reorder_level-cs.quantity) DESC,p.name";
        try { return jdbc.query(sql, (rs, row) -> new LowStockAlert(rs.getInt("product_id"), rs.getString("product_name"), rs.getString("location_name"), rs.getInt("quantity"), rs.getInt("reorder_level"))); }
        catch (DataAccessException e) { throw databaseError("Could not load low-stock alerts.", e); }
    }

    @Override public List<StockMovement> getStockMovementReport(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) throw new warehouse.util.DatabaseException("Choose a valid date range.");
        String sql = "SELECT m.movement_id,m.product_id,p.name product_name,m.location_id,l.name location_name,m.supplier_id,s.name supplier_name,m.user_id,u.username,m.type,m.quantity,m.`timestamp` FROM stock_movement m JOIN product p ON p.product_id=m.product_id JOIN warehouse_location l ON l.location_id=m.location_id LEFT JOIN supplier s ON s.supplier_id=m.supplier_id JOIN `user` u ON u.user_id=m.user_id WHERE m.`timestamp` >= ? AND m.`timestamp` < ? ORDER BY m.`timestamp` DESC";
        try { return jdbc.query(sql, (rs, row) -> mapMovement(rs), Timestamp.valueOf(start.atStartOfDay()), Timestamp.valueOf(end.plusDays(1).atStartOfDay())); }
        catch (DataAccessException e) { throw databaseError("Could not load the movement report.", e); }
    }

    @Override public List<StockSummary> getStockSummary() {
        String sql = "SELECT p.name product_name,c.name category_name,COALESCE(SUM(cs.quantity),0) total_quantity,COUNT(cs.location_id) locations,COALESCE(SUM(cs.quantity*p.unit_price),0) inventory_value FROM product p JOIN category c ON c.category_id=p.category_id LEFT JOIN current_stock cs ON cs.product_id=p.product_id GROUP BY p.product_id,p.name,c.name ORDER BY p.name";
        try { return jdbc.query(sql, (rs, row) -> new StockSummary(rs.getString("product_name"), rs.getString("category_name"), rs.getInt("total_quantity"), rs.getInt("locations"), rs.getBigDecimal("inventory_value"))); }
        catch (DataAccessException e) { throw databaseError("Could not load the stock summary.", e); }
    }

    /** Additional browser report: all time IN versus OUT quantities per product. */
    public List<MovementTotals> getMovementTotals() {
        String sql = "SELECT p.name product_name,COALESCE(SUM(CASE WHEN m.type='IN' THEN m.quantity ELSE 0 END),0) units_in,COALESCE(SUM(CASE WHEN m.type='OUT' THEN m.quantity ELSE 0 END),0) units_out FROM product p LEFT JOIN stock_movement m ON m.product_id=p.product_id GROUP BY p.product_id,p.name ORDER BY p.name";
        try { return jdbc.query(sql, (rs, row) -> new MovementTotals(rs.getString("product_name"), rs.getInt("units_in"), rs.getInt("units_out"))); }
        catch (DataAccessException e) { throw databaseError("Could not load movement totals.", e); }
    }

    private StockMovement mapMovement(ResultSet rs) throws SQLException {
        Integer supplierId = (Integer) rs.getObject("supplier_id");
        StockMovement movement = new StockMovement(rs.getInt("product_id"), rs.getInt("location_id"), supplierId, rs.getInt("user_id"), MovementType.valueOf(rs.getString("type")), rs.getInt("quantity"));
        movement.setMovementId(rs.getInt("movement_id"));
        movement.setProductName(rs.getString("product_name"));
        movement.setLocationName(rs.getString("location_name"));
        movement.setSupplierName(rs.getString("supplier_name"));
        movement.setUsername(rs.getString("username"));
        movement.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return movement;
    }
}
