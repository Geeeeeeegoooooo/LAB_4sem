package lab1.demo.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CacheService {
    private final Map<Long, String> passwordCache = new ConcurrentHashMap<>();


    public String get(Long id) {
        return passwordCache.get(id);
    }

    public void remove(Long id) {
        passwordCache.remove(id);
    }
    public void put(Long id, String password) {
        System.out.println("КЭШ: Сохраняем пароль с id = " + id + ", value = " + password);
        passwordCache.put(id, password);
    }

}
