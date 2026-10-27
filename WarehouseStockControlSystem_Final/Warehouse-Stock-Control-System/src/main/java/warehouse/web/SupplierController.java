package warehouse.web;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import warehouse.model.Supplier;
import warehouse.service.SupplierService;
import warehouse.util.DatabaseException;
import warehouse.web.form.SupplierForm;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class SupplierController {
    private final SupplierService suppliers;

    public SupplierController(SupplierService suppliers) {
        this.suppliers = suppliers;
    }

    @GetMapping("/suppliers")
    public String suppliers(Model model) {
        populate(model);
        return "suppliers";
    }

    @PostMapping("/suppliers")
    public String create(@Valid @ModelAttribute("supplierForm") SupplierForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            populate(model);
            return "suppliers";
        }
        try {
            Supplier supplier = suppliers.create(form.toSupplier());
            flash.addFlashAttribute("success", "Supplier '" + supplier.getName() + "' was added successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/suppliers";
    }

    @GetMapping("/suppliers/{id}/edit")
    public String edit(@PathVariable int id, Model model) {
        Supplier supplier = suppliers.one(id);
        model.addAttribute("supplier", supplier);
        model.addAttribute("supplierForm", SupplierForm.from(supplier));
        return "supplier-form";
    }

    @PostMapping("/suppliers/{id}")
    public String update(@PathVariable int id, @Valid @ModelAttribute SupplierForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            model.addAttribute("supplier", suppliers.one(id));
            return "supplier-form";
        }
        try {
            Supplier supplier = form.toSupplier();
            supplier.setSupplierId(id);
            suppliers.update(supplier);
            flash.addFlashAttribute("success", "Supplier updated successfully.");
            return "redirect:/suppliers";
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/suppliers/" + id + "/edit";
        }
    }

    @PostMapping("/suppliers/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes flash) {
        try {
            suppliers.delete(id);
            flash.addFlashAttribute("success", "Supplier deleted successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/suppliers";
    }

    private void populate(Model model) {
        model.addAttribute("suppliers", suppliers.all());
        if (!model.containsAttribute("supplierForm")) {
            model.addAttribute("supplierForm", new SupplierForm());
        }
    }
}
