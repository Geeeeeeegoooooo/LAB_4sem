package lab1.demo.cache;

import lab1.demo.model.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheService {

    private final Map<Long, String> passwordCache = new ConcurrentHashMap<>();
    private final Map<Long, User> userCache = new ConcurrentHashMap<>();



    public String get(Long id) {
        return passwordCache.get(id);
    }

    public void put(Long id, String password) {
        System.out.println("КЭШ: Сохраняем пароль с id = " + id + ", value = " + password);
        passwordCache.put(id, password);
    }

    public void remove(Long id) {
        passwordCache.remove(id);
    }



    public User getUser(Long id) {
        return userCache.get(id);
    }

    public void putUser(Long id, User user) {
        System.out.println("КЭШ: Сохраняем пользователя с id = " + id + ", username = " + user.getUsername());
        userCache.put(id, user);
    }

    public void removeUser(Long id) {
        userCache.remove(id);
    }
}
