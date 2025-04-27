package lab1.demo.service;

import lab1.demo.cache.CacheService;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import lab1.demo.dto.BulkUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    @Lazy
    private PasswordService passwordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private CacheService cacheService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        User cachedUser = cacheService.getUser(id);
        if (cachedUser != null) {
            System.out.println("КЭШ: Пользователь найден в кэше с id = " + id);
            return cachedUser;
        }

        System.out.println("КЭШ: Пользователь не найден в кэше. Загружаем из базы...");
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id = " + id));

        cacheService.putUser(id, user);
        cacheService.putUserByUsername(user.getUsername(), user);
        return user;
    }

    public User getOrCreateUser(String username) {
        User cachedUser = cacheService.getUserByUsername(username);
        if (cachedUser != null) {
            System.out.println("КЭШ: Пользователь найден в кэше с username = " + username);
            return cachedUser;
        }

        System.out.println("КЭШ: Пользователь не найден в кэше. Проверяем в базе...");
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPasswords(new ArrayList<>());
                    return userRepository.save(newUser);
                });

        cacheService.putUser(user.getId(), user);
        cacheService.putUserByUsername(user.getUsername(), user);
        return user;
    }

    public User addPasswordToUser(Long userId, String passwordValue, int length, String complexity) {
        User user = getUserById(userId);

        Password password = new Password();
        password.setPasswordValue(passwordValue);
        password.setLength(length);
        password.setComplexity(complexity);
        password.setUser(user);

        passwordRepository.save(password);

        cacheService.put(password.getId(), passwordValue);
        return user;
    }

    public void deleteUser(Long userId) {
        User user = getUserById(userId);

        for (Password password : user.getPasswords()) {
            cacheService.remove(password.getId());
        }

        cacheService.removeUser(userId);
        cacheService.removeUserByUsername(user.getUsername());

        userRepository.delete(user);
    }

    public List<User> getUsersByPasswordComplexity(String complexity) {
        return userRepository.findUsersByPasswordComplexity(complexity);
    }

    public User createUserWithPassword(String username, String passwordValue, int length, String complexity) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }

        if (length <= 0) {
            throw new IllegalArgumentException("Длина пароля должна быть больше 0");
        }

        if (!(complexity.equals("low") || complexity.equals("medium") || complexity.equals("high"))) {
            throw new IllegalArgumentException("Недопустимый уровень сложности пароля: " + complexity);
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswords(new ArrayList<>());
        User savedUser = userRepository.save(user);

        Password password = new Password();
        password.setPasswordValue(passwordValue);
        password.setLength(length);
        password.setComplexity(complexity);
        password.setUser(savedUser);

        passwordRepository.save(password);

        cacheService.put(password.getId(), passwordValue);
        cacheService.putUser(savedUser.getId(), savedUser);
        cacheService.putUserByUsername(savedUser.getUsername(), savedUser);

        return savedUser;
    }

    @Transactional
    public List<User> bulkCreateUsersWithPasswords(List<BulkUserRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Список запросов не может быть пустым");
        }

        List<User> savedUsers = new ArrayList<>();

        for (BulkUserRequest request : requests) {
            try {
                User user = new User();
                user.setUsername(request.getUsername());
                User savedUser = userRepository.save(user);

                int levelInt;
                switch (request.getLevel().toLowerCase()) {
                    case "low":
                        levelInt = 1;
                        break;
                    case "medium":
                        levelInt = 2;
                        break;
                    case "high":
                        levelInt = 3;
                        break;
                    default:
                        throw new IllegalArgumentException("Недопустимый уровень сложности пароля: " + request.getLevel());
                }

                Password password = new Password();
                password.setPasswordValue(passwordService.generatePassword(request.getSize(), levelInt));
                password.setUser(savedUser);

                passwordRepository.save(password);

                cacheService.putUser(savedUser.getId(), savedUser);
                cacheService.putUserByUsername(savedUser.getUsername(), savedUser);

                savedUsers.add(savedUser);
            } catch (Exception e) {
                log.error("[ERROR] Ошибка в методе: bulkCreateUsersWithPasswords | Сообщение: {}", e.getMessage());
                throw e;
            }
        }

        return savedUsers;
    }
}
