package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import pt.ulisboa.tecnico.cmov.conversationalist.R;

public class ChatRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        configTitle();
    }

    public void configTitle() {
        Toolbar toolbar = this.findViewById(R.id.toolbar_gchannel);
        toolbar.setTitle("Title Goes Here");
    }


}