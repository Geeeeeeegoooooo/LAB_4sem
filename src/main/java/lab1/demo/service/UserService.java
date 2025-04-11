package lab1.demo.service;

import lab1.demo.cache.CacheService;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private CacheService cacheService;


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    //ЗДЕСЬ ДОБАВЛЕНИЕ В КЭШ ПРИ GETBYID/ПОЛУЧЕНИЕ ИЗ КЭША
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
        return user;
    }


    public User getOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPasswords(new ArrayList<>());
                    return userRepository.save(newUser);
                });
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

    //УДАЛЕНИЕ ИЗ КЭША
    public void deleteUser(Long userId) {
        User user = getUserById(userId);

        for (Password password : user.getPasswords()) {
            cacheService.remove(password.getId());
        }

        cacheService.removeUser(userId);
        userRepository.delete(user);
    }


    public List<User> getUsersByPasswordComplexity(String complexity) {
        return userRepository.findUsersByPasswordComplexity(complexity);
    }

    // ЗДЕСЬ ДОБАВЛЕНИЕ В КЭШ ПРИ СОЗДАНИИ
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

        return savedUser;
    }
}
