package lab1.demo.service;

import lab1.demo.cache.CacheService;
import lab1.demo.dto.UserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
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
    private UserService userService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private RequestCounterService requestCounterService;

    public Password updatePassword(Long userId, Long passwordId, int size, int level) {
        requestCounterService.increment();
        Password password = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!password.getUser().getId().equals(userId)) {
            throw new RuntimeException("Password does not belong to the user");
        }

        String newPassword = generatePassword(size, level);
        password.setPasswordValue(newPassword);
        password.setLength(size);
        password.setComplexity(getComplexityLabel(level));

        cacheService.put(passwordId, newPassword);

        return passwordRepository.save(password);
    }

    public void deletePassword(Long userId, Long passwordId) {
        requestCounterService.increment();
        Password password = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!password.getUser().getId().equals(userId)) {
            throw new RuntimeException("Password does not belong to the user");
        }

        User user = password.getUser();
        user.getPasswords().remove(password);

        passwordRepository.delete(password);

        cacheService.remove(passwordId);



        passwordRepository.delete(password);
        cacheService.remove(passwordId);
    }

    public String generatePassword(int size, int level) {
        requestCounterService.increment();
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
        requestCounterService.increment();
        return switch (level) {
            case 1 -> "low";
            case 2 -> "medium";
            case 3 -> "high";
            default -> "unknown";
        };
    }

    public Password createPasswordForUser(UserRequest request) {
        requestCounterService.increment();
        User user = userService.getOrCreateUser(request.getUsername());

        String complexityLabel = getComplexityLabel(request.getComplexity());
        String generatedPassword = request.getPassword();

        Password password = new Password();
        password.setPasswordValue(generatedPassword);
        password.setLength(request.getLength());
        password.setComplexity(complexityLabel);
        password.setUser(user);

        userService.addPasswordToUser(user.getId(), password.getPasswordValue(), password.getLength(), password.getComplexity());

        Password latest = user.getPasswords().get(user.getPasswords().size() - 1);
        cacheService.put(latest.getId(), latest.getPasswordValue());

        return latest;
    }
}
