package lab1.demo.service;

import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRepository passwordRepository;

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

        user.getPasswords().add(password);
        userRepository.save(user); // сохраняем и пользователя, и связанные пароли
        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
