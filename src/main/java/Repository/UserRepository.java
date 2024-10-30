package Repository;

import java.util.Collection;
import java.util.HashMap;
import Entity.*;
import java.util.Map;

public class UserRepository {

    private final Map<String, User> users = new HashMap<>();
    private static UserRepository memoryUserRepository;

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        if (memoryUserRepository == null) {
            memoryUserRepository = new UserRepository();
            return memoryUserRepository;
        }
        return memoryUserRepository;
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public void updateUser(User user) {
        if (users.containsKey(user.getUserId())) {
            users.put(user.getUserId(), user);
        }
    }

}
