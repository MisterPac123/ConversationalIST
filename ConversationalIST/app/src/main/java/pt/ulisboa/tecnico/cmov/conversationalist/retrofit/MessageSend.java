package pt.ulisboa.tecnico.cmov.conversationalist.retrofit;

public class MessageSend {

    private String user;
    private String msg;

    public MessageSend(String _user, String _msg) {
        user = _user;
        msg = _msg;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
