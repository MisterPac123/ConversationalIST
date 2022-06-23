package pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results;

import java.util.ArrayList;

public class ReceiveMsgFromChatResult {

    private String msg;
    private String sender;
    private String date;
    private String id;
    private ArrayList<String> usersRead;

    public String getMsg() {
        return msg;
    }

    public String getDate() { return date; }

    public String getSender() {
        return sender;
    }

    public String getId() {return id;}

    public ArrayList<String> getUsersRead() {return usersRead;}
}
