package lab1.demo.controller;

import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import lab1.demo.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private PasswordService passwordService;

    // Получить всех пользователей с паролями
    @GetMapping
    public List<User> getAllUsersWithPasswords() {
        return userRepository.findAll();
    }

    // Получить одного пользователя с паролями
    @GetMapping("/{id}")
    public User getUserWithPasswords(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Создать пользователя с паролем
    @PostMapping
    public User createUserWithPassword(@RequestParam String username,
                                       @RequestParam int size,
                                       @RequestParam int level) {
        User user = new User();
        user.setUsername(username);

        Password password = new Password();
        password.setPasswordValue(passwordService.generatePassword(size, level));
        password.setLength(size);
        password.setComplexity(getComplexityString(level));
        password.setUser(user);

        user.setPasswords(List.of(password));
        return userRepository.save(user);
    }

    // Добавить новый пароль для уже существующего пользователя
    @PostMapping("/{userId}/passwords")
    public Password addPasswordToUser(@PathVariable Long userId,
                                      @RequestParam int size,
                                      @RequestParam int level) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Password password = new Password();
        password.setPasswordValue(passwordService.generatePassword(size, level));
        password.setLength(size);
        password.setComplexity(getComplexityString(level));
        password.setUser(user);

        return passwordRepository.save(password);
    }

    // Обновить пароль (сгенерировать новый)
    @PutMapping("/{userId}/passwords/{passwordId}")
    public Password updatePassword(@PathVariable Long userId,
                                   @PathVariable Long passwordId,
                                   @RequestParam int size,
                                   @RequestParam int level) {
        Password password = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!password.getUser().getId().equals(userId)) {
            throw new RuntimeException("Password does not belong to user");
        }

        password.setPasswordValue(passwordService.generatePassword(size, level));
        password.setLength(size);
        password.setComplexity(getComplexityString(level));

        return passwordRepository.save(password);
    }

    // Удалить пользователя (и все его пароли)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // Удалить один конкретный пароль
    // Удалить пароль пользователя
    @DeleteMapping("/{userId}/passwords/{passwordId}")
    public void deletePassword(@PathVariable Long userId, @PathVariable Long passwordId) {
        Password password = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!password.getUser().getId().equals(userId)) {
            throw new RuntimeException("Password does not belong to the user");
        }

        passwordRepository.deleteById(passwordId);
    }

    private String getComplexityString(int level) {
        return switch (level) {
            case 1 -> "low";
            case 2 -> "medium";
            case 3 -> "high";
            default -> "unknown";
        };
    }
}
