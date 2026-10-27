package warehouse.service;

import org.springframework.stereotype.Service;
import warehouse.dao.CategoryDAO;
import warehouse.dao.ProductDAO;
import warehouse.model.Category;
import warehouse.model.Product;
import warehouse.util.DatabaseException;

import java.util.List;

@Service
public class CatalogService {
    private final ProductDAO products;
    private final CategoryDAO categories;
    public CatalogService(ProductDAO products, CategoryDAO categories) { this.products = products; this.categories = categories; }
    public List<Product> products() { return products.findAll(); }
    public List<Category> categories() { return categories.findAll(); }
    public Product product(int id) { return products.findById(id).orElseThrow(() -> new DatabaseException("Product not found.")); }
    public Category category(int id) { return categories.findById(id).orElseThrow(() -> new DatabaseException("Category not found.")); }
    public Product createProduct(Product product) { return products.save(product); }
    public void updateProduct(Product product) { if (!products.update(product)) throw new DatabaseException("Product not found."); }
    public void deleteProduct(int id) { if (!products.delete(id)) throw new DatabaseException("Product not found."); }
    public Category createCategory(Category category) { return categories.save(category); }
    public void updateCategory(Category category) { if (!categories.update(category)) throw new DatabaseException("Category not found."); }
    public void deleteCategory(int id) { if (!categories.delete(id)) throw new DatabaseException("Category not found."); }
}
