package lab1.demo;
import java.util.ArrayList;
import lab1.demo.cache.CacheService;
import lab1.demo.dto.UserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import lab1.demo.service.PasswordService;
import lab1.demo.service.RequestCounterService;
import lab1.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock private PasswordRepository passwordRepository;
    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @Mock private CacheService cacheService;
    @Mock private RequestCounterService requestCounterService;

    @InjectMocks private PasswordService passwordService;

    @Test
    void updatePassword_whenValidInput_thenUpdatesAndReturnsPassword() {
        Long userId = 1L;
        Long passwordId = 2L;
        User owner = new User();
        owner.setId(userId);
        Password password = new Password();
        password.setId(passwordId);
        password.setUser(owner);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(password));
        when(passwordRepository.save(any())).thenReturn(password);

        Password result = passwordService.updatePassword(userId, passwordId, 12, 2);

        assertNotNull(result);
        assertEquals(passwordId, result.getId());
        verify(cacheService).put(passwordId, result.getPasswordValue());
        verify(requestCounterService).increment();
    }

    @Test
    void updatePassword_whenPasswordNotFound_thenThrowsException() {
        when(passwordRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> passwordService.updatePassword(1L, 1L, 10, 1));
    }

    @Test
    void deletePassword_whenValidInput_thenDeletesAndClearsCache() {
        Long userId = 1L;
        Long passwordId = 2L;
        User user = new User();
        user.setId(userId);
        Password password = new Password();
        password.setId(passwordId);
        password.setUser(user);
        user.getPasswords().add(password);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(password));

        passwordService.deletePassword(userId, passwordId);

        verify(passwordRepository, times(2)).delete(password);
        verify(cacheService).remove(passwordId);
        verify(requestCounterService).increment();
    }

    @Test
    void generatePassword_whenValidLevel_thenReturnsCorrectFormat() {
        String result = passwordService.generatePassword(10, 2);
        assertNotNull(result);
        assertEquals(10, result.length());
        verify(requestCounterService).increment();
    }

    @Test
    void generatePassword_whenInvalidLevel_thenUsesDefaultChars() {
        String result = passwordService.generatePassword(8, 999);
        assertNotNull(result);
        assertTrue(result.matches("[a-z]+"));
    }

    @Test
    void getComplexityLabel_whenValidLevel_thenReturnsCorrectLabel() {
        assertEquals("low", passwordService.getComplexityLabel(1));
        assertEquals("medium", passwordService.getComplexityLabel(2));
        assertEquals("high", passwordService.getComplexityLabel(3));
        assertEquals("unknown", passwordService.getComplexityLabel(999));
    }

    @Test
    void createPasswordForUser_whenValidRequest_thenCreatesPassword() {

        UserRequest request = new UserRequest();
        request.setUsername("test");
        request.setLength(8);
        request.setComplexity(1);

        User user = new User();
        user.setId(1L);
        user.setPasswords(new ArrayList<>());

        when(userService.getOrCreateUser("test")).thenReturn(user);
        when(userService.addPasswordToUser(anyLong(), anyString(), anyInt(), anyString())).thenReturn(user);

        Password result = passwordService.createPasswordForUser(request);

        assertNotNull(result);
        verify(cacheService).put(anyLong(), anyString());
        verify(requestCounterService).increment();
    }
}