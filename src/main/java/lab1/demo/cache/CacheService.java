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


    public void put(Long id, String password) {
        passwordCache.put(id, password);
    }


    public String get(Long id) {
        return passwordCache.get(id);
    }


    public void remove(Long id) {
        passwordCache.remove(id);
    }

    //ДЛЯ USERA РАБОТА С КЭШЕМ РЕАЛИЗОВАНА ВОТ (МЕТОДЫ)
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
    }
}

