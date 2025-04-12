package model;

import javax.swing.*;
import java.util.Objects;

public class User {
    private String id;
    private String username;
    private String role;

    public User(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }

    public ImageIcon getProfilePicture() {

            return new ImageIcon(getClass().getResource("/profile_default.png"));

    }
    public String getRole() {
        if (Objects.equals(id, "admin")) role = "admin";
        if (Objects.equals(id, "staff")) role = "staff";
        return role; }
}
