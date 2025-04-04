package lab1.demo.controller;

import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.service.PasswordService;
import lab1.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    // Получить всех пользователей с паролями
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Получить одного пользователя с паролями
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    // Создать пользователя с паролем
    @PostMapping
    public User createUser(@RequestParam String username,
                           @RequestParam int size,
                           @RequestParam int level) {
        String passwordValue = passwordService.generatePassword(size, level);
        String complexity = passwordService.getComplexityLabel(level);
        return userService.createUserWithPassword(username, passwordValue, size, complexity);
    }

    // Добавить пароль к существующему пользователю
    @PostMapping("/{id}/passwords")
    public User addPassword(@PathVariable Long id,
                            @RequestParam int size,
                            @RequestParam int level) {
        String passwordValue = passwordService.generatePassword(size, level);
        String complexity = passwordService.getComplexityLabel(level);
        return userService.addPasswordToUser(id, passwordValue, size, complexity);
    }

    // Обновить пароль пользователя (рандомно сгенерированный)
    @PutMapping("/{userId}/passwords/{passwordId}")
    public Password updatePassword(@PathVariable Long userId,
                                   @PathVariable Long passwordId,
                                   @RequestParam int size,
                                   @RequestParam int level) {
        return passwordService.updatePassword(userId, passwordId, size, level);
    }

    // Удалить пользователя
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // Удалить пароль
    @DeleteMapping("/{userId}/passwords/{passwordId}")
    public void deletePassword(@PathVariable Long userId,
                               @PathVariable Long passwordId) {
        passwordService.deletePassword(userId, passwordId);
    }
}