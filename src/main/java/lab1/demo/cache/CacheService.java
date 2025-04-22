package lab1.demo.cache;

import lab1.demo.model.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

@Component
public class CacheService {

    private final Map<Long, String> passwordCache = new ConcurrentHashMap<>();
    private final Map<Long, User> userCache = new ConcurrentHashMap<>();
    private final Map<String, User> userByUsernameCache = new ConcurrentHashMap<>();

    public void put(Long id, String password) {
        passwordCache.put(id, password);
    }

    public String get(Long id) {
        return passwordCache.get(id);
    }

    public void remove(Long id) {
        passwordCache.remove(id);
    }

    public void putUser(Long id, User user) {
        userCache.put(id, user);
    }

    public User getUser(Long id) {
        return userCache.get(id);
    }

    public void removeUser(Long id) {
        userCache.remove(id);
    }

    public Collection<User> getAllUsers() {
        return userCache.values();
    }

    public void clearCache() {
        userCache.clear();
        passwordCache.clear();
        userByUsernameCache.clear();
    }

    public Collection<User> getAllUsersFromCache() {
        return userCache.values();
    }


    public User getUserByUsername(String username) {
        return userByUsernameCache.get(username);
    }

    public void putUserByUsername(String username, User user) {
        userByUsernameCache.put(username, user);
    }

    public void removeUserByUsername(String username) {
        userByUsernameCache.remove(username);
    }
}


