package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.interfaces.Crud;
import warehouse.model.Product;
import warehouse.util.DatabaseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAO extends JdbcDao implements Crud<Product> {
    private static final String SELECT = "SELECT p.product_id, p.name, p.category_id, c.name category_name, p.reorder_level, p.unit_price FROM product p JOIN category c ON p.category_id=c.category_id";
    public ProductDAO(JdbcTemplate jdbc) { super(jdbc); }
    @Override public Product save(Product product) {
        validate(product);
        try { product.setProductId(insertAndGetId("INSERT INTO product (name, category_id, reorder_level, unit_price) VALUES (?, ?, ?, ?)", product.getName(), product.getCategoryId(), product.getReorderLevel(), product.getUnitPrice())); return product; }
        catch (DataAccessException e) { throw databaseError("Could not save product. Check its category and name.", e); }
    }
    @Override public Optional<Product> findById(int id) { return queryOne(SELECT + " WHERE p.product_id = ?", id); }
    @Override public List<Product> findAll() {
        try { return jdbc.query(SELECT + " ORDER BY p.name", (rs, row) -> map(rs)); }
        catch (DataAccessException e) { throw databaseError("Could not load products.", e); }
    }
    @Override public boolean update(Product product) {
        validate(product);
        try { return jdbc.update("UPDATE product SET name=?, category_id=?, reorder_level=?, unit_price=? WHERE product_id=?", product.getName(), product.getCategoryId(), product.getReorderLevel(), product.getUnitPrice(), product.getProductId()) == 1; }
        catch (DataAccessException e) { throw databaseError("Could not update product. Check its category and name.", e); }
    }
    @Override public boolean delete(int id) {
        try { return jdbc.update("DELETE FROM product WHERE product_id=?", id) == 1; }
        catch (DataAccessException e) { throw databaseError("Cannot delete a product that has stock or movement history.", e); }
    }
    private Optional<Product> queryOne(String sql, int id) {
        try { return jdbc.query(sql, rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), id); }
        catch (DataAccessException e) { throw databaseError("Could not look up the product.", e); }
    }
    private Product map(ResultSet r) throws SQLException { Product p = new Product(r.getInt("product_id"), r.getString("name"), r.getInt("category_id"), r.getInt("reorder_level"), r.getBigDecimal("unit_price")); p.setCategoryName(r.getString("category_name")); return p; }
    private void validate(Product p) { if (p.getName() == null || p.getName().isBlank() || p.getCategoryId() <= 0 || p.getReorderLevel() < 0 || p.getUnitPrice() == null || p.getUnitPrice().signum() < 0) throw new DatabaseException("Product details are invalid. Price and reorder level cannot be negative."); }
}
