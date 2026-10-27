package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.interfaces.Authenticatable;
import warehouse.interfaces.Crud;
import warehouse.model.Role;
import warehouse.model.User;
import warehouse.util.PasswordUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO extends JdbcDao implements Crud<User>, Authenticatable {
    private User activeUser;
    public UserDAO(JdbcTemplate jdbc) { super(jdbc); }

    @Override public User save(User user) {
        try { user.setUserId(insertAndGetId("INSERT INTO `user` (username,password_hash,role) VALUES (?,?,?)", user.getUsername(), user.getPasswordHash(), user.getRole().name())); return user; }
        catch (DataAccessException e) { throw databaseError("Could not save user. That username may already be in use.", e); }
    }
    @Override public Optional<User> findById(int id) { return one("SELECT user_id,username,password_hash,role FROM `user` WHERE user_id=?", id); }
    public Optional<User> findByUsername(String username) { return queryUsername(username); }
    @Override public List<User> findAll() {
        try { return jdbc.query("SELECT user_id,username,password_hash,role FROM `user` ORDER BY username", (rs, row) -> map(rs)); }
        catch (DataAccessException e) { throw databaseError("Could not load users.", e); }
    }
    @Override public boolean update(User user) {
        try { return jdbc.update("UPDATE `user` SET username=?,password_hash=?,role=? WHERE user_id=?", user.getUsername(), user.getPasswordHash(), user.getRole().name(), user.getUserId()) == 1; }
        catch (DataAccessException e) { throw databaseError("Could not update user. That username may already be in use.", e); }
    }
    @Override public boolean delete(int id) {
        try { return jdbc.update("DELETE FROM `user` WHERE user_id=?", id) == 1; }
        catch (DataAccessException e) { throw databaseError("Cannot delete a user with stock-movement history.", e); }
    }
    @Override public Optional<User> login(String username, String password) {
        if (username == null || password == null) return Optional.empty();
        Optional<User> found = findByUsername(username);
        if (found.isEmpty() || found.get().getPasswordHash() == null) return Optional.empty();
        if (!found.get().getPasswordHash().trim().equalsIgnoreCase(PasswordUtil.sha256(password))) return Optional.empty();
        activeUser = found.get();
        return Optional.of(activeUser);
    }
    @Override public void logout() { activeUser = null; }
    public Optional<User> getActiveUser() { return Optional.ofNullable(activeUser); }
    private Optional<User> one(String sql, int id) {
        try { return jdbc.query(sql, rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), id); }
        catch (DataAccessException e) { throw databaseError("Could not look up user.", e); }
    }
    private Optional<User> queryUsername(String username) {
        try { return jdbc.query("SELECT user_id,username,password_hash,role FROM `user` WHERE username=?", rs -> rs.next() ? Optional.of(map(rs)) : Optional.empty(), username); }
        catch (DataAccessException e) { throw databaseError("Could not check login credentials.", e); }
    }
    private User map(ResultSet rs) throws SQLException { return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password_hash"), Role.valueOf(rs.getString("role"))); }
}
