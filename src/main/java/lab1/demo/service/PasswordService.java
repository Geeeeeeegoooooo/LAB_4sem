package lab1.demo.service;

import lab1.demo.cache.CacheService;
import lab1.demo.model.Password;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PasswordService {

    @Autowired
    private PasswordRepository passwordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheService cacheService;

    public Password updatePassword(Long userId, Long passwordId, int size, int level) {
        Password password = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!password.getUser().getId().equals(userId)) {
            throw new RuntimeException("Password does not belong to the user");
        }

        String newPassword = generatePassword(size, level);
        password.setPasswordValue(newPassword);
        password.setLength(size);
        password.setComplexity(getComplexityLabel(level));

        // Обновляем кэш
        cacheService.put(passwordId, newPassword);

        return passwordRepository.save(password);
    }

    public void deletePassword(Long userId, Long passwordId) {
        Password password = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!password.getUser().getId().equals(userId)) {
            throw new RuntimeException("Password does not belong to the user");
        }

        passwordRepository.delete(password);

        // Удаляем пароль из кэша
        cacheService.remove(passwordId);
    }

    public String generatePassword(int size, int level) {
        String chars = switch (level) {
            case 1 -> "abcdefghijklmnopqrstuvwxyz";
            case 2 -> "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            case 3 -> "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()";
            default -> "abcdefghijklmnopqrstuvwxyz";
        };

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < size; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    public String getComplexityLabel(int level) {
        return switch (level) {
            case 1 -> "low";
            case 2 -> "medium";
            case 3 -> "high";
            default -> "unknown";
        };
    }
}
