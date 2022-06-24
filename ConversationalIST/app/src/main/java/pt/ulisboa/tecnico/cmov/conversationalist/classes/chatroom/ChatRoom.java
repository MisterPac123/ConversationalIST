package pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Objects;

import pt.ulisboa.tecnico.cmov.conversationalist.classes.Message;

public class ChatRoom implements Comparable<ChatRoom> , Serializable {

    private String name;
    private ChatRoomTypes type;
    private String description;
    private ArrayList<Message> msgList;
    private String inviteLink;
    private LocalDateTime lastMsg;
    private ArrayList<String> unreadMsgs = new ArrayList<>();


    public ChatRoom(String _name, String _type, String _description) {
        name = _name;
        description = _description;
        msgList = new ArrayList<>();
        inviteLink = "";

        if(_type.matches("Public"))
            type =  ChatRoomTypes.PUBLIC;
        else if(_type.matches("Private"))
            type =  ChatRoomTypes.PRIVATE;
        else if(_type.matches("Geo-fenced"))
            type =  ChatRoomTypes.GEOFENCED;
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public ChatRoomTypes getType() {
        return type;
    }

    public String getStringType() {
        if(type.equals(ChatRoomTypes.PUBLIC))
            return "Public";
        else if(type.equals(ChatRoomTypes.PRIVATE))
            return "Private";
        else if(type.equals(ChatRoomTypes.GEOFENCED))
            return "Geo-fenced";
        else
            return null;
    }

    public void addUnreadMsgs(String msg) {
        unreadMsgs.add(msg);
    }

    public int getNumberUnreadMsgs() {
        return unreadMsgs.size();
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

    public String getInviteLink() {
        return inviteLink;
    }

    public void setLastMsg(String date){
        String newdate = date.substring(0, date.length()-1);
        LocalDateTime dateTime = LocalDateTime.parse(newdate);
        this.lastMsg = dateTime;
    }

    public LocalDateTime getLastMsg(){
        return this.lastMsg;
    }

    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if(getClass() != object.getClass())
            return false;
        ChatRoom o = (ChatRoom) object;
        return Objects.equals(name, o.getName()) &&
                Objects.equals(type, o.getType()) &&
                Objects.equals(description, o.getDescription());
    }

    @Override
    public int compareTo(ChatRoom c) {
        LocalDateTime  compareLastMsg = ((ChatRoom) c).getLastMsg();
        return this.getLastMsg().compareTo(compareLastMsg);
    }
}
