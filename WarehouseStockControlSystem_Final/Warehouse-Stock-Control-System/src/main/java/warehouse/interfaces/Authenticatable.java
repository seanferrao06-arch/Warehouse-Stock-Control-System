package warehouse.interfaces;

import warehouse.model.User;

import java.util.Optional;

public interface Authenticatable {
    Optional<User> login(String username, String password);
    void logout();
}
