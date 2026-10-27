package warehouse.web.form;

import jakarta.validation.constraints.NotBlank;
import warehouse.model.Category;

public class CategoryForm {
    @NotBlank(message = "Category name is required.") private String name;
    public static CategoryForm from(Category c) { CategoryForm f=new CategoryForm(); f.name=c.getName(); return f; }
    public Category toCategory() { return new Category(name); }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
}
