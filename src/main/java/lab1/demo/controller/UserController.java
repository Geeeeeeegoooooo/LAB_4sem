package lab1.demo.controller;

import lab1.demo.cache.CacheService;
import lab1.demo.dto.UserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.service.PasswordService;
import lab1.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collection;
import java.util.List;

import lab1.demo.dto.BulkUserRequest;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private CacheService cacheService;

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/cache/getAll")
    public ResponseEntity<?> getAllUsersFromCache() {
        Collection<User> cachedUsers = cacheService.getAllUsers();
        if (cachedUsers != null && !cachedUsers.isEmpty()) {
            return ResponseEntity.ok(cachedUsers);
        } else {
            return ResponseEntity.ok("Нет пользователей в кэше.");
        }
    }

    @DeleteMapping("/cache/clear")
    public ResponseEntity<?> clearCache() {
        cacheService.clearCache(); // Очистка кэша
        return ResponseEntity.ok("Кэш очищен.");
    }


    @GetMapping("/by-password-complexity")
    public List<User> getUsersByPasswordComplexity(@RequestParam String complexity) {
        return userService.getUsersByPasswordComplexity(complexity);
    }

    @PostMapping
    public User createUser(@RequestBody UserRequest request) {
        String passwordValue = passwordService.generatePassword(request.getLength(), request.getComplexity());
        String complexity = passwordService.getComplexityLabel(request.getComplexity());
        return userService.createUserWithPassword(request.getUsername(), passwordValue, request.getLength(), complexity);
    }


    @PostMapping("/create-with-password")
    public ResponseEntity<?> createWithCustomPassword(@RequestBody UserRequest request) {
        String complexityLabel;
        try {
            complexityLabel = passwordService.getComplexityLabel(request.getComplexity());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Некорректный уровень сложности: " + request.getComplexity());
        }

        User user = userService.createUserWithPassword(
                request.getUsername(),
                request.getPassword(),
                request.getLength(),
                complexityLabel
        );
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/passwords")
    public User addPassword(@PathVariable Long id,
                            @RequestParam int size,
                            @RequestParam int level) {
        String passwordValue = passwordService.generatePassword(size, level);
        String complexity = passwordService.getComplexityLabel(level);
        return userService.addPasswordToUser(id, passwordValue, size, complexity);
    }

    @PutMapping("/{userId}/passwords/{passwordId}")
    public Password updatePassword(@PathVariable Long userId,
                                   @PathVariable Long passwordId,
                                   @RequestParam int size,
                                   @RequestParam int level) {
        return passwordService.updatePassword(userId, passwordId, size, level);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/{userId}/passwords/{passwordId}")
    public void deletePassword(@PathVariable Long userId,
                               @PathVariable Long passwordId) {
        passwordService.deletePassword(userId, passwordId);
    }

    @GetMapping("/test-500")
    public String testError() {
        throw new RuntimeException("Это тестовая ошибка 500");
    }

    @GetMapping("/cache/get")
    public ResponseEntity<?> getUserFromCacheById(@RequestParam Long id) {
        User cachedUser = cacheService.getUser(id);
        if (cachedUser != null) {
            return ResponseEntity.ok(cachedUser);
        } else {
            return ResponseEntity.ok("Пользователь не найден в кэше для ID: " + id);
        }
    }

    @PostMapping("/cache/put")
    public ResponseEntity<?> putUserToCache(@RequestParam Long id) {
        User user = userService.getUserById(id);
        cacheService.putUser(id, user);
        return ResponseEntity.ok("Пользователь с ID " + id + " добавлен в кэш.");
    }
    @PostMapping("/bulk-create")
    public ResponseEntity<List<User>> bulkCreate(@RequestBody List<BulkUserRequest> requests) {
        List<User> createdUsers = userService.bulkCreateUsersWithPasswords(requests);
        return ResponseEntity.ok(createdUsers);
    }
}
