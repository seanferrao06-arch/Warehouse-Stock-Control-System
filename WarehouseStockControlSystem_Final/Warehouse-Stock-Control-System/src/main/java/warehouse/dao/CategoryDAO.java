package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.interfaces.Crud;
import warehouse.model.Category;
import warehouse.util.DatabaseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class CategoryDAO extends JdbcDao implements Crud<Category> {
    public CategoryDAO(JdbcTemplate jdbc) { super(jdbc); }
    @Override public Category save(Category category) {
        try { category.setCategoryId(insertAndGetId("INSERT INTO category (name) VALUES (?)", category.getName())); return category; }
        catch (DataAccessException e) { throw databaseError("Could not save category. A category with that name may already exist.", e); }
    }
    @Override public Optional<Category> findById(int id) {
        try { return jdbc.query("SELECT category_id, name FROM category WHERE category_id = ?", rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), id); }
        catch (DataAccessException e) { throw databaseError("Could not look up the category.", e); }
    }
    @Override public List<Category> findAll() {
        try { return jdbc.query("SELECT category_id, name FROM category ORDER BY name", (rs, row) -> map(rs)); }
        catch (DataAccessException e) { throw databaseError("Could not load categories.", e); }
    }
    @Override public boolean update(Category category) {
        try { return jdbc.update("UPDATE category SET name = ? WHERE category_id = ?", category.getName(), category.getCategoryId()) == 1; }
        catch (DataAccessException e) { throw databaseError("Could not update category. Its name may already exist.", e); }
    }
    @Override public boolean delete(int id) {
        try { return jdbc.update("DELETE FROM category WHERE category_id = ?", id) == 1; }
        catch (DataAccessException e) { throw databaseError("Cannot delete this category while products still use it.", e); }
    }
    private Category map(ResultSet r) throws SQLException { return new Category(r.getInt("category_id"), r.getString("name")); }
}
