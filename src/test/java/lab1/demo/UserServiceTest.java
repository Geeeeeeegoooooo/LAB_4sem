package lab1.demo;

import lab1.demo.cache.CacheService;
import lab1.demo.dto.BulkUserRequest;
import lab1.demo.model.Password;
import lab1.demo.model.User;
import lab1.demo.repository.PasswordRepository;
import lab1.demo.repository.UserRepository;
import lab1.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordRepository passwordRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void bulkCreateUsersWithPasswords_success() {
        BulkUserRequest request1 = new BulkUserRequest("user1", 8, "medium");
        BulkUserRequest request2 = new BulkUserRequest("user2", 10, "high");


        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userRepository.save(any(User.class)))
                .thenReturn(user1)
                .thenReturn(user2);

        List<User> users = userService.bulkCreateUsersWithPasswords(List.of(request1, request2));

        assertEquals(2, users.size());
        verify(userRepository, times(2)).save(any(User.class));
        verify(passwordRepository, times(2)).save(any(Password.class));
        verify(cacheService, times(2)).putUser(anyLong(), any(User.class));
        verify(cacheService, times(2)).putUserByUsername(anyString(), any(User.class));
    }

    @Test
    public void bulkCreateUsersWithPasswords_emptyList() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.bulkCreateUsersWithPasswords(List.of()));

        assertEquals("Список запросов не может быть пустым", exception.getMessage());
    }

}
