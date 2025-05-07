package entity;

import jakarta.persistence.*;

import java.io.IOException;
import java.io.InputStream;

@Entity
@Table(name = "users")
@SecondaryTables({
        @SecondaryTable(
                name = "user_pictures",
                pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
        ),
        @SecondaryTable(
                name = "user_managers",
                pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
        ),
        @SecondaryTable(
                name = "user_passwords",
                pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "id")
        )
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private String username;

    @Column
    private String email;

    @Column(table = "user_passwords")
    private String password;

    @Column
    private String role;

    // Picture from user_pictures table
    @Basic
    @Column(table = "user_pictures", name = "user_picture")
    private byte[] picture;

    // Self-referencing manager_id from user_managers table
    @OneToOne
    @JoinColumn(table = "user_managers", name = "manager_id", referencedColumnName = "id")
    private User manager;

    // Constructors
    public User() {}

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public byte[] getPicture() {
        if (picture != null) {
            return picture;
        } else {
            try {
                // Try loading the default profile picture from the resources
                InputStream resourceStream = getClass().getResourceAsStream("/profile_default.png");
                return resourceStream.readAllBytes();
            } catch (IOException e) {
                return new byte[0];  // Return an empty byte array on error
            }
        }
    }


    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return name + " (" + username + ")";
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        User other = (User) obj;

        // Use id as the primary key identifier for equality
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
