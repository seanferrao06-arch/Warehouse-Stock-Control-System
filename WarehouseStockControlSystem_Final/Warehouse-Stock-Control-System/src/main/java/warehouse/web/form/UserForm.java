package warehouse.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import warehouse.model.Role;

public class UserForm {
    @NotBlank(message = "Username is required.") @Size(max = 80, message = "Username must be at most 80 characters.") private String username;
    @NotBlank(message = "A password is required.") @Size(min = 6, message = "Password must contain at least 6 characters.") private String password;
    @NotNull(message = "Choose a role.") private Role role;
    public static UserForm from(warehouse.model.User u) { UserForm f=new UserForm(); f.username=u.getUsername(); f.role=u.getRole(); return f; }
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; } public void setRole(Role role) { this.role = role; }
}
