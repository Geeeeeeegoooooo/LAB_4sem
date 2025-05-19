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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    void shouldReturnCreatedUsersForValidBulkRequest() {
        BulkUserRequest mockRequest1 = mock(BulkUserRequest.class);
        when(mockRequest1.getUsername()).thenReturn("test1");
        when(mockRequest1.getSize()).thenReturn(8);
        when(mockRequest1.getLevel()).thenReturn("low");

        BulkUserRequest mockRequest2 = mock(BulkUserRequest.class);
        when(mockRequest2.getUsername()).thenReturn("test2");
        when(mockRequest2.getSize()).thenReturn(10);
        when(mockRequest2.getLevel()).thenReturn("medium");

        User user1 = mock(User.class);
        when(user1.getId()).thenReturn(1L);
        when(user1.getUsername()).thenReturn("test1");

        User user2 = mock(User.class);
        when(user2.getId()).thenReturn(2L);
        when(user2.getUsername()).thenReturn("test2");

        when(userService.bulkCreateUsersWithPasswords(List.of(mockRequest1, mockRequest2)))
                .thenReturn(List.of(user1, user2));

        ResponseEntity<List<User>> response = userController.bulkCreate(List.of(mockRequest1, mockRequest2));

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(userService, times(1)).bulkCreateUsersWithPasswords(List.of(mockRequest1, mockRequest2));
    }
}