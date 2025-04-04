package lab1.demo.service;

import lab1.demo.model.User;
import lab1.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getOrCreateUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(username);
            return userRepository.save(newUser);
        });
    }
}
