package lab1.demo.controller;

import lab1.demo.dto.UserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import lab1.demo.service.PasswordService;
import lab1.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

    // Генерация пароля
    @GetMapping("/generate-password")
    public String getPassword(@RequestParam int size, @RequestParam int level) {
        return passwordService.generatePassword(size, level);
    }

    // Добавление пользователя с паролем
    @PostMapping("/add-user-password")
    public ResponseEntity<String> addUserWithPassword(@RequestBody UserRequest request) {
        User user = userService.getOrCreateUser(request.getUsername());

        Password password = new Password();
        password.setPasswordValue(request.getPassword());
        password.setLength(request.getLength());
        password.setComplexity(getComplexityLabel(request.getComplexity()));
        password.setUser(user);

        passwordRepository.save(password);

        return ResponseEntity.ok("User and password saved.");
    }

    // Получить всех пользователей и их пароли
    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private String getComplexityLabel(int complexity) {
        return switch (complexity) {
            case 1 -> "low";
            case 2 -> "medium";
            case 3 -> "high";
            default -> "unknown";
        };
    }
}
