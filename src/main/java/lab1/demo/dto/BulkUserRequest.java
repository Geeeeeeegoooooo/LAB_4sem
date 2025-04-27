package lab1.demo.dto;

public class BulkUserRequest {

    private String username;
    private int size;
    private String level;

    public BulkUserRequest() {

    }

    public BulkUserRequest(String username, int size, String level) {
        this.username = username;
        this.size = size;
        this.level = level;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
