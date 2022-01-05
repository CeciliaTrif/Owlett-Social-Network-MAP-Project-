package map.socialnetwork.views.wrapper;

import java.util.Date;


public class UserTableItemWrapper {

    private String userName;
    private String status;
    private String date;

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
}
