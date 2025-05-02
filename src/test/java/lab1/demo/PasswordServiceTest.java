package lab1.demo;

import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import lab1.demo.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private int convertLevel(String level) {
        return switch (level.toLowerCase()) {
            case "low" -> 0;
            case "medium" -> 1;
            case "high" -> 2;
            default -> throw new IllegalArgumentException("Invalid level: " + level);
        };
    }

    @Test
    public void updatePassword_WhenUserAndPasswordExist_ShouldReturnUpdatedPassword() {
        Long userId = 1L;
        Long passwordId = 2L;
        String level = "high";
        int size = 12;

        User user = new User();
        user.setId(userId);

        Password password = new Password();
        password.setId(passwordId);
        password.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(password));
        when(passwordRepository.save(any(Password.class))).thenReturn(password);

        Password updated = passwordService.updatePassword(userId, passwordId, size, convertLevel(level));

        assertNotNull(updated);
        assertEquals(passwordId, updated.getId());
        verify(passwordRepository, times(1)).save(password);
    }

    @Test
    public void updatePassword_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                passwordService.updatePassword(1L, 2L, 10, convertLevel("medium")));

        assertEquals("Пользователь не найден", exception.getMessage());
    }
}
