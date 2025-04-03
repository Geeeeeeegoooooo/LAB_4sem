package lab1.demo.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class PasswordService {
    public String generatePassword(int length, int complexity) {
        String numbers = "0123456789";
        String letters = "QWERTYUIOPASDFGHJKLйцукенгшщзфывапролдячсмить";
        String symbols = "!@#$%^";

        String characters = numbers;
        if (complexity > 1) characters += letters;
        if (complexity > 2) characters += symbols;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }

        return password.toString();
    }
}
