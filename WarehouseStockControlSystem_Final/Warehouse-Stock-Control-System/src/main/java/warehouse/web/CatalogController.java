package warehouse.web;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import warehouse.model.Category;
import warehouse.model.Product;
import warehouse.service.CatalogService;
import warehouse.util.DatabaseException;
import warehouse.web.form.CategoryForm;
import warehouse.web.form.ProductForm;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class CatalogController {
    private final CatalogService catalog;
    public CatalogController(CatalogService catalog) { this.catalog = catalog; }

    @GetMapping("/products")
    public String products(Model model) { populateProducts(model); return "products"; }

    @PostMapping("/products")
    public String createProduct(@Valid @ModelAttribute("productForm") ProductForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) { populateProducts(model); return "products"; }
        try {
            Product product = catalog.createProduct(form.toProduct());
            flash.addFlashAttribute("success", "Product '" + product.getName() + "' was added successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable int id, Model model) {
        Product product = catalog.product(id);
        model.addAttribute("product", product);
        model.addAttribute("productForm", ProductForm.from(product));
        model.addAttribute("categories", catalog.categories());
        return "product-form";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable int id, @Valid @ModelAttribute ProductForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            model.addAttribute("product", catalog.product(id));
            model.addAttribute("categories", catalog.categories());
            return "product-form";
        }
        try {
            Product product = form.toProduct();
            product.setProductId(id);
            catalog.updateProduct(product);
            flash.addFlashAttribute("success", "Product '" + product.getName() + "' was updated successfully.");
            return "redirect:/products";
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/products/" + id + "/edit";
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable int id, RedirectAttributes flash) {
        try {
            catalog.deleteProduct(id);
            flash.addFlashAttribute("success", "Product deleted successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @PostMapping("/categories")
    public String createCategory(@Valid @ModelAttribute("categoryForm") CategoryForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) { populateProducts(model); return "products"; }
        try {
            Category category = catalog.createCategory(form.toCategory());
            flash.addFlashAttribute("success", "Category '" + category.getName() + "' was added successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategory(@PathVariable int id, Model model) {
        Category category = catalog.category(id);
        model.addAttribute("category", category);
        model.addAttribute("categoryForm", CategoryForm.from(category));
        return "category-form";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable int id, @Valid @ModelAttribute CategoryForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            model.addAttribute("category", catalog.category(id));
            return "category-form";
        }
        try {
            Category category = form.toCategory();
            category.setCategoryId(id);
            catalog.updateCategory(category);
            flash.addFlashAttribute("success", "Category updated successfully.");
            return "redirect:/products";
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/" + id + "/edit";
        }
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable int id, RedirectAttributes flash) {
        try {
            catalog.deleteCategory(id);
            flash.addFlashAttribute("success", "Category deleted successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/products";
    }

    private void populateProducts(Model model) {
        model.addAttribute("products", catalog.products());
        model.addAttribute("categories", catalog.categories());
        if (!model.containsAttribute("productForm")) model.addAttribute("productForm", new ProductForm());
        if (!model.containsAttribute("categoryForm")) model.addAttribute("categoryForm", new CategoryForm());
    }
}
