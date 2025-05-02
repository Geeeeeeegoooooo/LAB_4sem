package lab1.demo;

import lab1.demo.cache.CacheService;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.service.PasswordService;
import lab1.demo.service.RequestCounterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private RequestCounterService requestCounterService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private PasswordService passwordService;

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
        int size = 12;
        int level = convertLevel("high");

        User owner = new User();
        owner.setId(userId);
        Password pwd = new Password();
        pwd.setId(passwordId);
        pwd.setUser(owner);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(pwd));
        lenient().when(passwordRepository.save(any(Password.class))).thenReturn(pwd);
        lenient().doNothing().when(cacheService).put(anyLong(), anyString());

        Password updated = passwordService.updatePassword(userId, passwordId, size, level);

        assertNotNull(updated);
        assertEquals(passwordId, updated.getId());
        verify(passwordRepository, times(1)).save(pwd);
        verify(cacheService, times(1)).put(eq(passwordId), anyString());
    }

    @Test
    public void updatePassword_WhenPasswordNotOwnedByUser_ShouldThrowException() {
        Long userId = 1L;
        Long passwordId = 2L;


        User otherUser = new User();
        otherUser.setId(99L);
        Password pwd = new Password();
        pwd.setId(passwordId);
        pwd.setUser(otherUser);

        when(passwordRepository.findById(passwordId)).thenReturn(Optional.of(pwd));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> passwordService.updatePassword(userId, passwordId, 10, convertLevel("medium"))
        );
        assertEquals("Password does not belong to the user", ex.getMessage());
    }
}
