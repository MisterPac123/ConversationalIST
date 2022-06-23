package pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results;

import java.util.ArrayList;

public class ChatRoomResults {

    private String name;
    private String type;
    private String description;
    private ArrayList<String> users;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getReadUsers() {return users;}
}
