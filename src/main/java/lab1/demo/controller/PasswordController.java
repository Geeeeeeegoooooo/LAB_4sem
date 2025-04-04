package lab1.demo.controller;

import lab1.demo.dto.UserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.service.PasswordService;
import lab1.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserService userService;


    @GetMapping("/generate-password")
    public String getPassword(@RequestParam int size, @RequestParam int level) {
        return passwordService.generatePassword(size, level);
    }


    @PostMapping("/add-user-password")
    public ResponseEntity<String> addUserWithPassword(@RequestBody UserRequest request) {
        User user = userService.getOrCreateUser(request.getUsername());

        String complexityLabel = passwordService.getComplexityLabel(request.getComplexity());
        String generatedPassword = request.getPassword();

        Password password = new Password();
        password.setPasswordValue(generatedPassword);
        password.setLength(request.getLength());
        password.setComplexity(complexityLabel);
        password.setUser(user);

        userService.addPasswordToUser(user.getId(), password.getPasswordValue(), password.getLength(), password.getComplexity());

        return ResponseEntity.ok("User and password saved.");
    }
}
