package pt.ulisboa.tecnico.cmov.conversationalist.classes;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String username;
    private String email;
    private String name;

    public UserAccount(String _userID, String _email, String _name) {
        this.username = _userID;
        this.email = _email;
        this.name = _name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userID) {
        this.username = userID;
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

}
