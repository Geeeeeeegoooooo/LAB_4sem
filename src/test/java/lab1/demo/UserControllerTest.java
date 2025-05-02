package lab1.demo;

import lab1.demo.controller.UserController;
import lab1.demo.dto.BulkUserRequest;
import lab1.demo.model.User;
import lab1.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void bulkCreateUsers_WhenValidRequest_ShouldReturnCreatedUsers() {
        BulkUserRequest req1 = new BulkUserRequest("test1", 8, "low");
        BulkUserRequest req2 = new BulkUserRequest("test2", 10, "medium");

        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("test1");

        User u2 = new User();
        u2.setId(2L);
        u2.setUsername("test2");

        when(userService.bulkCreateUsersWithPasswords(List.of(req1, req2)))
                .thenReturn(List.of(u1, u2));

        ResponseEntity<List<User>> response = userController.bulkCreate(List.of(req1, req2));

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).bulkCreateUsersWithPasswords(anyList());
    }
}
