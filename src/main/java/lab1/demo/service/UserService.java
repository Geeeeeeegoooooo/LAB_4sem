package lab1.demo.service;

import lab1.demo.cache.CacheService;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден с id = " + id));
    }

    public User getUserFromCache(Long id) {
        return cacheService.getUser(id);
    }

    public User getOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
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

    public User createUserWithPassword(String username, String passwordValue, int length, String complexity) {
        User user = new User();
        user.setUsername(username);
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
