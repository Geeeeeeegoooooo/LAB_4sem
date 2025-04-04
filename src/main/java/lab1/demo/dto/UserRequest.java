package lab1.demo.dto;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private int length;
    private int complexity;
}
