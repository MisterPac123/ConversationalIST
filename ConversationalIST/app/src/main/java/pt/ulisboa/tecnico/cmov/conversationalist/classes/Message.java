package pt.ulisboa.tecnico.cmov.conversationalist.classes;

public class Message {

    private String text;
    private UserAccount sender;
    private String date;
    private String time;

    public Message (String _msg, UserAccount user, String _date, String _time) {
        text = _msg;
        sender = user;
        date = _date;
        time = _time;
    }


    public void setSender(UserAccount sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() { return date; }

    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }

    public void setTime(String time) { this.time = time; }

    public UserAccount getSender() { return sender; }
}
