package pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results;

public class ReceiveMsgFromChatResult {

    private String msg;
    private String sender;
    private String date;
    private String type;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) { this.msg = msg; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
