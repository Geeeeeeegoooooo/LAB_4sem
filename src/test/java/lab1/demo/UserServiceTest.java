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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordRepository passwordRepository;
    @Mock private PasswordService passwordService;
    @Mock private RequestCounterService requestCounterService;
    @Mock private CacheService cacheService;

    @InjectMocks private UserService userService;

    @Test
    void getAllUsers_whenCalled_thenReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(requestCounterService).increment();
    }

    @Test
    void getUserById_whenInCache_thenReturnsCachedUser() {
        User cachedUser = new User();
        cachedUser.setId(1L);
        when(cacheService.getUser(1L)).thenReturn(cachedUser);

        User result = userService.getUserById(1L);

        assertEquals(cachedUser, result);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getOrCreateUser_whenUserExists_thenReturnsUser() {
        User existingUser = new User();
        existingUser.setUsername("test");
        when(cacheService.getUserByUsername("test")).thenReturn(existingUser);

        User result = userService.getOrCreateUser("test");

        assertEquals(existingUser, result);
        verify(userRepository, never()).findByUsername(any());
    }

    @Test
    void addPasswordToUser_whenValidInput_thenAddsPassword() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.addPasswordToUser(1L, "pwd", 8, "low");

        assertNotNull(result);
        verify(passwordRepository).save(any());
        verify(cacheService).put(anyLong(), eq("pwd"));
    }

    @Test
    void deleteUser_whenValidId_thenDeletesAndClearsCache() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        Password password = new Password();
        password.setId(2L);
        user.setPasswords(List.of(password));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
        verify(cacheService).removeUser(1L);
        verify(cacheService).removeUserByUsername("test");
        verify(cacheService).remove(2L);
    }

    @Test
    void createUserWithPassword_whenInvalidComplexity_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUserWithPassword("test", "pwd", 8, "invalid"));
    }

    @Test
    void bulkCreateUsersWithPasswords_whenValidRequests_thenCreatesUsers() {

        BulkUserRequest request = new BulkUserRequest();
        request.setUsername("test");
        request.setSize(8);
        request.setLevel("medium");



        when(userRepository.save(any())).thenReturn(new User());
        when(passwordService.generatePassword(anyInt(), anyInt())).thenReturn("generatedPwd");

        List<User> result = userService.bulkCreateUsersWithPasswords(List.of(request));

        assertEquals(1, result.size());
        verify(passwordService).generatePassword(8, 2);
        verify(cacheService).putUser(anyLong(), any());
    }

    @Test
    void bulkCreateUsersWithPasswords_whenEmptyList_thenThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.bulkCreateUsersWithPasswords(List.of()));
    }
}