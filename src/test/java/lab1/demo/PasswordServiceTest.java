package lab1.demo;

import lab1.demo.cache.CacheService;
import lab1.demo.dto.UserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.service.PasswordService;
import lab1.demo.service.RequestCounterService;
import lab1.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private UserService userService;

    @Mock
    private CacheService cacheService;

    @Mock
    private RequestCounterService requestCounterService;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    void shouldUpdatePasswordWhenInputIsValid() {
        Long userId = 1L;
        Long passwordId = 2L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Password mockPassword = mock(Password.class);
        when(mockPassword.getId()).thenReturn(passwordId);
        when(mockPassword.getUser()).thenReturn(mockUser);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(mockPassword));
        when(passwordRepository.save(any(Password.class))).thenReturn(mockPassword);

        Password result = passwordService.updatePassword(userId, passwordId, 12, 2);

        assertNotNull(result);
        verify(passwordRepository, times(1)).save(mockPassword);
        verify(cacheService, times(1)).put(passwordId, anyString());
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldThrowExceptionWhenPasswordNotFound() {
        when(passwordRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> passwordService.updatePassword(1L, 1L, 10, 1));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldDeletePasswordWhenInputIsValid() {
        Long userId = 1L;
        Long passwordId = 2L;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Password mockPassword = mock(Password.class);
        when(mockPassword.getId()).thenReturn(passwordId);
        when(mockPassword.getUser()).thenReturn(mockUser);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(mockPassword));

        passwordService.deletePassword(userId, passwordId);

        verify(passwordRepository, times(1)).delete(mockPassword);
        verify(cacheService, times(1)).remove(passwordId);
        verify(requestCounterService, times(2)).increment();
    }

    @Test
    void shouldGenerateLowercasePasswordForLevel1() {
        String result = passwordService.generatePassword(10, 1);

        assertNotNull(result);
        assertEquals(10, result.length());
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldUseDefaultCharsForInvalidComplexityLevel() {
        String result = passwordService.generatePassword(8, 999);

        assertNotNull(result);
        assertEquals(8, result.length());
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldThrowExceptionWhenPasswordSizeIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> passwordService.generatePassword(0, 1));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldReturnCorrectComplexityLabelForValidLevel() {
        assertEquals("low", passwordService.getComplexityLabel(1));
        assertEquals("medium", passwordService.getComplexityLabel(2));
        assertEquals("high", passwordService.getComplexityLabel(3));
        verify(requestCounterService, times(3)).increment();
    }

    @Test
    void shouldCreatePasswordForValidUserRequest() {
        UserRequest mockRequest = mock(UserRequest.class);
        when(mockRequest.getUsername()).thenReturn("test");
        when(mockRequest.getLength()).thenReturn(8);
        when(mockRequest.getComplexity()).thenReturn(1);

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getPasswords()).thenReturn(Arrays.asList(mock(Password.class)));

        when(userService.getOrCreateUser("test")).thenReturn(mockUser);
        when(userService.addPasswordToUser(anyLong(), anyString(), anyInt(), anyString()))
                .thenReturn(mockUser);

        Password result = passwordService.createPasswordForUser(mockRequest);

        assertNotNull(result);
        verify(cacheService, times(1)).put(anyLong(), anyString());
        verify(requestCounterService, times(1)).increment();
    }
}