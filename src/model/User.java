package model;

import java.util.Objects;

public class User {
    private String id = null;
    private String username = null;
    private String role = null;

    public User(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getRole() {
        if (Objects.equals(id, "admin")) role = "admin";
        if (Objects.equals(id, "staff")) role = "staff";
        return role; }
}
