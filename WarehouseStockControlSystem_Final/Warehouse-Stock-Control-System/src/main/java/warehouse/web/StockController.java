package warehouse.web;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import warehouse.model.StockActionResult;
import warehouse.service.UserService;
import warehouse.service.WarehouseService;
import warehouse.web.form.StockInForm;
import warehouse.web.form.StockOutForm;

@Controller
public class StockController {
    private final WarehouseService warehouse;
    private final UserService users;
    public StockController(WarehouseService warehouse, UserService users) { this.warehouse = warehouse; this.users = users; }
    @GetMapping("/stock") public String stock(Model model) { populate(model); return "stock/index"; }
    @PostMapping("/stock/in")
    public String stockIn(@Valid @ModelAttribute("stockInForm") StockInForm form, BindingResult errors, Model model, @AuthenticationPrincipal UserDetails current, RedirectAttributes flash) {
        if (errors.hasErrors()) { populate(model); return "stock/index"; }
        try {
            StockActionResult result = warehouse.stockIn(form.getProductId(), form.getLocationId(), form.getSupplierId(), users.byUsername(current.getUsername()).getUserId(), form.getQuantity());
            return redirectWithResult(result, flash);
        } catch (warehouse.util.DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/stock";
        }
    }
    @PostMapping("/stock/out")
    public String stockOut(@Valid @ModelAttribute("stockOutForm") StockOutForm form, BindingResult errors, Model model, @AuthenticationPrincipal UserDetails current, RedirectAttributes flash) {
        if (errors.hasErrors()) { populate(model); return "stock/index"; }
        try {
            StockActionResult result = warehouse.stockOut(form.getProductId(), form.getLocationId(), users.byUsername(current.getUsername()).getUserId(), form.getQuantity());
            return redirectWithResult(result, flash);
        } catch (warehouse.util.DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/stock";
        }
    }
    private String redirectWithResult(StockActionResult result, RedirectAttributes flash) {
        flash.addFlashAttribute("success", result.successMessage());
        if (result.lowStockWarning() != null) flash.addFlashAttribute("warning", result.lowStockWarning());
        return "redirect:/stock";
    }
    private void populate(Model model) {
        model.addAttribute("stockLevels", warehouse.stockLevels());
        model.addAttribute("products", warehouse.products());
        model.addAttribute("suppliers", warehouse.suppliers());
        model.addAttribute("locations", warehouse.locations());
        if (!model.containsAttribute("stockInForm")) model.addAttribute("stockInForm", new StockInForm());
        if (!model.containsAttribute("stockOutForm")) model.addAttribute("stockOutForm", new StockOutForm());
    }
}
