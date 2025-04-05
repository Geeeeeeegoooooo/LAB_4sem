package lab1.demo.service;

import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;


    private final Map<String, List<User>> userCache = new ConcurrentHashMap<>();

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUserWithPassword(String username, String passwordValue, int size, String complexity) {
        User user = new User();
        user.setUsername(username);

        Password password = new Password();
        password.setPasswordValue(passwordValue);
        password.setLength(size);
        password.setComplexity(complexity);
        password.setUser(user);

        user.setPasswords(List.of(password));
        return userRepository.save(user);
    }

    public User addPasswordToUser(Long userId, String passwordValue, int size, String complexity) {
        User user = getUserById(userId);

        Password password = new Password();
        password.setPasswordValue(passwordValue);
        password.setLength(size);
        password.setComplexity(complexity);
        password.setUser(user);

        // проверка и инициализация, если null
        if (user.getPasswords() == null) {
            user.setPasswords(new ArrayList<>());
        }

        user.getPasswords().add(password);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getUsersByPasswordComplexity(String complexity) {

        List<User> users = userRepository.findUsersByPasswordComplexity(complexity);


        for (User user : users) {
            List<Password> filtered = user.getPasswords().stream()
                    .filter(p -> p.getComplexity().equalsIgnoreCase(complexity))
                    .toList();
            user.setPasswords(filtered);
        }

        return users;
    }


    public User getOrCreateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setUsername(username);
                    return userRepository.save(newUser);
                });
    }
}
