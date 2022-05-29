package pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom;

import java.io.Serializable;
import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.conversationalist.classes.Message;

public class ChatRoom implements Serializable {

    private String name;
    private ChatRoomTypes type;
    private String description;
    private ArrayList<Message> msgList;

    /*public ChatRoom(String _name, String _type) {
        name = _name;
        if(_type.matches("Public"))
            type =  ChatRoomTypes.PUBLIC;
        else if(_type.matches("Private"))
            type =  ChatRoomTypes.PRIVATE;
        else if(_type.matches("Geo-founder"))
            type =  ChatRoomTypes.GEOFOUNDER;
    }*/
    public ChatRoom(String _name, String _type, String _description) {
        name = _name;
        description = _description;
        msgList = new ArrayList<>();
        if(_type.matches("Public"))
            type =  ChatRoomTypes.PUBLIC;
        else if(_type.matches("Private"))
            type =  ChatRoomTypes.PRIVATE;
        else if(_type.matches("Geo-founder"))
            type =  ChatRoomTypes.GEOFOUNDER;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public ChatRoomTypes getType() {
        return type;
    }

    public void setType(ChatRoomTypes type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addMsg(Message msg) {
        this.msgList.add(msg);
    }

}
