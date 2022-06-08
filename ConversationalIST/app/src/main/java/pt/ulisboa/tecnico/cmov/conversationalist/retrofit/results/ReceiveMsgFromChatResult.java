package pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results;

public class ReceiveMsgFromChatResult {

    private String msg;
    private String username;
    private String time;
    private String date;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) { this.msg = msg; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }
}
