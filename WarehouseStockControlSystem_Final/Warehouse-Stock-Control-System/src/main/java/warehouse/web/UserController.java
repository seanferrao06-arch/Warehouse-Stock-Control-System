package warehouse.web;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import warehouse.model.Role;
import warehouse.model.User;
import warehouse.service.UserService;
import warehouse.util.DatabaseException;
import warehouse.web.form.UserForm;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService users;

    public UserController(UserService users) {
        this.users = users;
    }

    @GetMapping("/users")
    public String users(Model model) {
        populate(model);
        return "users";
    }

    @PostMapping("/users")
    public String create(@Valid @ModelAttribute("userForm") UserForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            populate(model);
            return "users";
        }
        try {
            User user = users.create(form.getUsername(), form.getPassword(), form.getRole());
            flash.addFlashAttribute("success", "User '" + user.getUsername() + "' was created successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/{id}/edit")
    public String edit(@PathVariable int id, Model model) {
        User user = users.byId(id);
        model.addAttribute("account", user);
        model.addAttribute("userForm", UserForm.from(user));
        model.addAttribute("roles", Role.values());
        return "user-form";
    }

    @PostMapping("/users/{id}")
    public String update(@PathVariable int id, @Valid @ModelAttribute UserForm form, BindingResult errors, Model model, RedirectAttributes flash) {
        if (errors.hasErrors()) {
            model.addAttribute("account", users.byId(id));
            model.addAttribute("roles", Role.values());
            return "user-form";
        }
        try {
            users.update(id, form.getUsername(), form.getPassword(), form.getRole());
            flash.addFlashAttribute("success", "User account updated successfully.");
            return "redirect:/users";
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/" + id + "/edit";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes flash) {
        try {
            users.delete(id);
            flash.addFlashAttribute("success", "User deleted successfully.");
        } catch (DatabaseException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users";
    }

    private void populate(Model model) {
        model.addAttribute("users", users.all());
        model.addAttribute("roles", Role.values());
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserForm());
        }
    }
}
