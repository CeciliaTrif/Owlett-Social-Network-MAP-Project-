package map.socialnetwork.views.wrapper;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class UserTableItemWrapper {

    private Long id;
    private String userName;
    private String status;
    private String date;
    private String firstName;
    private String lastName;

    public Long getId() {
        return id;
    }

    public UserTableItemWrapper setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserTableItemWrapper setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserTableItemWrapper setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getDate() {
        return date;
    }

    public UserTableItemWrapper setDate(String date) {
        this.date = date;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public UserTableItemWrapper setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {return lastName;}

    public UserTableItemWrapper setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
