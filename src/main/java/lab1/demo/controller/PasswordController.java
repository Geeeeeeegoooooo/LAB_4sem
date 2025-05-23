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

import java.util.List;

@RestController
public class PasswordController {

    @Autowired
    public PasswordService passwordService;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheService cacheService;


    @GetMapping("/generate-password")
    public String getPassword(@RequestParam int size, @RequestParam int level) {
        return passwordService.generatePassword(size, level);
    }


    @PostMapping("/add-user-password")
    public ResponseEntity<String> addUserWithPassword(@RequestBody UserRequest request) {
        String complexityLabel = passwordService.getComplexityLabel(request.getComplexity());
        User user = userService.createUserWithPassword(
                request.getUsername(),
                request.getPassword(),
                request.getLength(),
                complexityLabel
        );


        Password latest = user.getPasswords().get(user.getPasswords().size() - 1);


        cacheService.put(latest.getId(), latest.getPasswordValue());

        return ResponseEntity.ok("Пользователь и пароль сохранены. ID пароля: " + latest.getId());
    }


    @GetMapping("/cache/get")
    public ResponseEntity<String> getPasswordFromCache(@RequestParam Long passwordId) {
        String cached = cacheService.get(passwordId);
        if (cached != null) {
            return ResponseEntity.ok("КЭШ: " + cached);
        } else {
            return ResponseEntity.ok("Пароль не найден в кэше для ID: " + passwordId);
        }
    }


}
