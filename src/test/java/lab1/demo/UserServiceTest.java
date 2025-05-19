package lab1.demo;

import lab1.demo.cache.CacheService;
import lab1.demo.dto.BulkUserRequest;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private RequestCounterService requestCounterService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnAllUsersAndIncrementCounter() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(userRepository.findAll()).thenReturn(List.of(mockUser1, mockUser2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldReturnUserFromCacheWhenUserInCache() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(cacheService.getUser(userId)).thenReturn(mockUser);

        User result = userService.getUserById(userId);

        assertEquals(mockUser, result);
        verify(userRepository, never()).findById(any());
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldCreateNewUserWhenNotInCache() {
        String username = "newuser";
        when(cacheService.getUserByUsername(username)).thenReturn(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        User mockUser = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.getOrCreateUser(username);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
        verify(cacheService, times(1)).putUser(anyLong(), any(User.class));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldAddPasswordWhenUserExists() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userService.addPasswordToUser(userId, "newPwd", 12, "high");

        assertNotNull(result);
        verify(passwordRepository, times(1)).save(any(Password.class));
        verify(cacheService, times(1)).put(anyLong(), eq("newPwd"));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldClearAllCacheEntriesWhenDeletingUserWithPasswords() {
        Long userId = 1L;
        String username = "testuser";

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getUsername()).thenReturn(username);

        Password mockPassword = mock(Password.class);
        when(mockPassword.getId()).thenReturn(2L);
        when(mockUser.getPasswords()).thenReturn(List.of(mockPassword));

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(mockUser);
        verify(cacheService, times(1)).removeUser(userId);
        verify(cacheService, times(1)).removeUserByUsername(username);
        verify(cacheService, times(1)).remove(2L);
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithInvalidPasswordLength() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUserWithPassword("test", "pwd", 0, "low"));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldCreateOneUserForSingleBulkRequest() {
        BulkUserRequest mockRequest = mock(BulkUserRequest.class);
        when(mockRequest.getUsername()).thenReturn("testuser");
        when(mockRequest.getSize()).thenReturn(10);
        when(mockRequest.getLevel()).thenReturn("high");

        User mockUser = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordService.generatePassword(anyInt(), anyInt())).thenReturn("generatedPwd");

        List<User> result = userService.bulkCreateUsersWithPasswords(List.of(mockRequest));

        assertEquals(1, result.size());
        verify(passwordService, times(1)).generatePassword(10, 3);
        verify(cacheService, times(1)).putUser(anyLong(), any(User.class));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldThrowExceptionWhenBulkRequestIsNull() {
        assertThrows(NullPointerException.class,
                () -> userService.bulkCreateUsersWithPasswords(null));
        verify(requestCounterService, times(1)).increment();
    }

    @Test
    void shouldReturnEmptyListWhenNoUsersFoundByPasswordComplexity() {
        when(userRepository.findUsersByPasswordComplexity("high")).thenReturn(Collections.emptyList());

        List<User> result = userService.getUsersByPasswordComplexity("high");

        assertTrue(result.isEmpty());
        verify(requestCounterService, times(1)).increment();
    }
}