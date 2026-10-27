package warehouse.service;

import org.springframework.stereotype.Service;
import warehouse.dao.UserDAO;
import warehouse.model.Role;
import warehouse.model.User;
import warehouse.util.DatabaseException;
import warehouse.util.PasswordUtil;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserDAO users;
    public UserService(UserDAO users) { this.users = users; }
    public List<User> all() { return users.findAll(); }
    public User byId(int id) { return users.findById(id).orElseThrow(() -> new DatabaseException("User not found.")); }
    public Optional<User> findByUsername(String username) { return users.findByUsername(username); }
    public User byUsername(String username) { return findByUsername(username).orElseThrow(() -> new DatabaseException("The signed-in user no longer exists.")); }
    public User create(String username, String rawPassword, Role role) { return users.save(new User(username, PasswordUtil.sha256(rawPassword), role)); }
    public void update(int id, String username, String rawPassword, Role role) {
        User existing = byId(id);
        existing.setUsername(username);
        existing.setRole(role);
        if (rawPassword != null && !rawPassword.isBlank()) existing.setPasswordHash(PasswordUtil.sha256(rawPassword));
        if (!users.update(existing)) throw new DatabaseException("User not found.");
    }
    public void delete(int id) { if (!users.delete(id)) throw new DatabaseException("User not found."); }
}
