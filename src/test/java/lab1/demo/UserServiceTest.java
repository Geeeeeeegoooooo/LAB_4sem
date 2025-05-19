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
    void getAllUsers_whenCalled_thenReturnsAllUsersAndIncrementsCounter() {
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(userRepository.findAll()).thenReturn(List.of(mockUser1, mockUser2));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(requestCounterService).increment();
    }

    @Test
    void getUserById_whenUserInCache_thenReturnsFromCache() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(cacheService.getUser(userId)).thenReturn(mockUser);

        User result = userService.getUserById(userId);

        assertEquals(mockUser, result);
        verify(userRepository, never()).findById(any());
        verify(requestCounterService).increment();
    }

    @Test
    void getOrCreateUser_whenUserNotInCache_thenCreatesNewUser() {
        String username = "newuser";
        when(cacheService.getUserByUsername(username)).thenReturn(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        User mockUser = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.getOrCreateUser(username);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(cacheService).putUser(anyLong(), any(User.class));
        verify(requestCounterService).increment();
    }

    @Test
    void addPasswordToUser_whenUserExists_thenAddsPassword() {
        Long userId = 1L;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userService.addPasswordToUser(userId, "newPwd", 12, "high");

        assertNotNull(result);
        verify(passwordRepository).save(any(Password.class));
        verify(cacheService).put(anyLong(), eq("newPwd"));
        verify(requestCounterService).increment();
    }

    @Test
    void deleteUser_whenUserHasPasswords_thenClearsAllCacheEntries() {
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

        verify(userRepository).delete(mockUser);
        verify(cacheService).removeUser(userId);
        verify(cacheService).removeUserByUsername(username);
        verify(cacheService).remove(2L);
        verify(requestCounterService).increment();
    }

    @Test
    void createUserWithPassword_whenInvalidPasswordLength_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUserWithPassword("test", "pwd", 0, "low"));
        verify(requestCounterService).increment();
    }

    @Test
    void bulkCreateUsersWithPasswords_whenSingleRequest_thenCreatesOneUser() {
        BulkUserRequest request = new BulkUserRequest();
        request.setUsername("testuser");
        request.setSize(10);
        request.setLevel("high");

        User mockUser = mock(User.class);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(passwordService.generatePassword(anyInt(), anyInt())).thenReturn("generatedPwd");

        List<User> result = userService.bulkCreateUsersWithPasswords(List.of(request));

        assertEquals(1, result.size());
        verify(passwordService).generatePassword(10, 3);
        verify(cacheService).putUser(anyLong(), any(User.class));
        verify(requestCounterService).increment();
    }

    @Test
    void bulkCreateUsersWithPasswords_whenNullRequest_thenThrowsException() {
        assertThrows(NullPointerException.class,
                () -> userService.bulkCreateUsersWithPasswords(null));
        verify(requestCounterService).increment();
    }

    @Test
    void getUsersByPasswordComplexity_whenNoUsersFound_thenReturnsEmptyList() {
        when(userRepository.findUsersByPasswordComplexity("high")).thenReturn(Collections.emptyList());

        List<User> result = userService.getUsersByPasswordComplexity("high");

        assertTrue(result.isEmpty());
        verify(requestCounterService).increment();
    }
}