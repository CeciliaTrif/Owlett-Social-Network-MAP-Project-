package map.socialnetwork.domain.model;

import java.util.Objects;

public class User extends Entity<Long> {

    private String username;

    private String password;

    private String firstName;

    private String LastName;

    public User(String username, String password, String firstName, String LastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.LastName = LastName;

    }

    public String getFirstName() {
        return firstName;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return LastName;
    }

    public User setLastName(String lastName) {
        LastName = lastName;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "User" +
                "ID=" + getId() + " " +
                "Username='" + username + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return username.equals(user.username) && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, getId());
    }


}
