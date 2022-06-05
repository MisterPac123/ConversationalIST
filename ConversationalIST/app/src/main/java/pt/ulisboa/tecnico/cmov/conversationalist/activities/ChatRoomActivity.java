package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatAdapter;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.Message;

public class ChatRoomActivity extends AppCompatActivity {

    UserAccount user;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    ArrayList<Message> messagesArray = new ArrayList<>();
    ChatRoom chat;
    RecyclerView recyclerView;
    ChatAdapter msgsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        recyclerView = this.findViewById(R.id.chatRecyclerView);
        if(recyclerView == null) {
            Log.i("chatRoom", "null recycleviw");
        }
        Bundle intent = getIntent().getExtras();
        if(intent != null) {
            //error
        }
        user = (UserAccount) intent.getSerializable("user");

        populatemsgArray();

        displayMsgs();

        configTitle(intent);
        configSendButton();



    }

    private void populatemsgArray () {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss aaa z");
        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());
        UserAccount user1 = new UserAccount("user_test", "user_test@test.com","test", "test" );
        Message newMsg = new Message("msg", user1, date, time);
        messagesArray.add(newMsg);
    }


    private void displayMsgs() {
        msgsAdapter = new ChatAdapter(this, messagesArray, user);
        recyclerView.setAdapter(msgsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    public void configTitle(Bundle intent) {
        TextView title = this.findViewById(R.id.textName);
        chat = (ChatRoom) intent.getSerializable("chat");
        title.setText(chat.getName());
    }

    public void configSendButton() {
        ImageButton sendButton = this.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTxtMsg = findViewById(R.id.editTxtTypemsg);
                String msg = editTxtMsg.getText().toString();

                if(msg.matches("")){
                    //empty msg. dont send nothing
                    CharSequence text = "Empty msg";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    Log.i("no msg", "empty msg");
                }
                else{

                    calendar = Calendar.getInstance();
                    dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    timeFormat = new SimpleDateFormat("HH:mm:ss aaa z");
                    String date = dateFormat.format(calendar.getTime());
                    String time = timeFormat.format(calendar.getTime());
                    Message newMsg = new Message(msg, user, date, time);
                    messagesArray.add(newMsg);
                    chat.addMsg(newMsg);
                    msgsAdapter.notifyDataSetChanged();
                }

            }
        });
    }


}