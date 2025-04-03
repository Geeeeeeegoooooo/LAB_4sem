package lab1.demo.controller;

import lab1.demo.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @GetMapping("/generate-password")
    public String getPassword(@RequestParam int size, @RequestParam int level) {
        return passwordService.generatePassword(size, level);
    }
}
