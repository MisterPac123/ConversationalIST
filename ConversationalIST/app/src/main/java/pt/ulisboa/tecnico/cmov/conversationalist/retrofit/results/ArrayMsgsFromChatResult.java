package pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results;

import java.util.ArrayList;

public class ArrayMsgsFromChatResult {
    private ArrayList<ReceiveMsgFromChatResult> msgs;


    public ArrayList<ReceiveMsgFromChatResult> getMsgs() {
        return msgs;
    }

    public void setMsgs(ArrayList<ReceiveMsgFromChatResult> msgs) {
        this.msgs = msgs;
    }
}
