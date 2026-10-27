package warehouse.model;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private Role role;
    public User() { }
    public User(int userId, String username, String passwordHash, Role role) { this.userId = userId; this.username = username; this.passwordHash = passwordHash; this.role = role; }
    public User(String username, String passwordHash, Role role) { this(0, username, passwordHash, role); }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
