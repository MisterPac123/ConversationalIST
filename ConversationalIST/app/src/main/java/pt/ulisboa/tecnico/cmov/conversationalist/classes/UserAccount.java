package pt.ulisboa.tecnico.cmov.conversationalist.classes;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String userID;
    private String email;
    private String name;
    private String password;

    public UserAccount(String _userID, String _email, String _password, String _name) {
        this.userID = _userID;
        this.email = _email;
        this.password = _password;
        this.name = _name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
