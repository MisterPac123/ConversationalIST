package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatAdapter;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.Message;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ReceiveMsgFromChatResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SendMsgResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;




public class ChatRoomActivity extends AppCompatActivity {

    UserAccount user;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    SimpleDateFormat timeFormat;
    ArrayList<Message> messagesArray = new ArrayList<>();
    ChatRoom chat;
    RecyclerView recyclerView;
    ChatAdapter msgsAdapter;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Uri uri = getIntent().getData();
        if (uri != null) {
            List<String> params = uri.getPathSegments();
            String id = params.get(params.size()-1);
            Toast.makeText(this, "id" + id, Toast.LENGTH_SHORT).show();
        }

        recyclerView = this.findViewById(R.id.chatRecyclerView);
        if(recyclerView == null) {
            Log.i("chatRoom", "null recycleviw");
        }
        Bundle intent = getIntent().getExtras();
        if(intent != null) {
            //error
        }

        Intent intent2 = getIntent();
        String action = intent2.getAction();
        Uri data = intent2.getData();

        user = (UserAccount) intent.getSerializable("user");
        chat = (ChatRoom) intent.getSerializable("chat");

        initBackendConnection();
        populatemsgArray();
        getMsgFromUser();

        displayMsgs();

        configTitle(intent);
        configSendButton();

    }

    public void initBackendConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }


    private void populatemsgArray () {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss aaa z");
        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());
        UserAccount user1 = new UserAccount("user_test", "user_test@test.com","test" );
        Message newMsg = new Message("Go to this website: https://live.conversationalist.com/app", user1.getUsername(), date, time);
        messagesArray.add(newMsg);
    }

    private void getMsgFromUser() {
        HashMap<String, String> map = new HashMap<>();
        map.put("chatName", chat.getName());

        Call<ArrayList<ReceiveMsgFromChatResult>> call = retrofitInterface.executeReceiveMsgFromChatRoom(map);

        Log.i("receive msgs response", "hello");


        call.enqueue(new Callback<ArrayList<ReceiveMsgFromChatResult>>() {
            @Override
            public void onResponse(Call<ArrayList<ReceiveMsgFromChatResult>> call, Response<ArrayList<ReceiveMsgFromChatResult>> response) {
                if (response.code() == 200) {
                    ArrayList<ReceiveMsgFromChatResult> msgs = response.body();
                    for (int i=0; i<msgs.size(); i++){

                        ReceiveMsgFromChatResult msg = msgs.get(i);
                        addMsgToList(msg.getDate(), msg.getMsg(), msg.getUsername());

                        Log.i("receive msgs", msg.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ReceiveMsgFromChatResult>> call, Throwable t) {

            }
        });
    }


    private void displayMsgs() {
        msgsAdapter = new ChatAdapter(this, messagesArray, user);
        recyclerView.setAdapter(msgsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    public void configTitle(Bundle intent) {
        TextView title = this.findViewById(R.id.textName);
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
                    sendMsgToServer(msg);
                }

            }
        });

    }

    public void sendMsgToServer(String msg) {
        HashMap<String, String> map = new HashMap<>();

        map.put("user", user.getName());
        map.put("msg", msg);
        map.put("chatName", chat.getName());

        Call<SendMsgResult> call = retrofitInterface.executeSendMsgToChatRoom(map);

        Log.i("send msg response", "hello");


        call.enqueue(new Callback<SendMsgResult>() {
            @Override
            public void onResponse( Call<SendMsgResult> call, Response<SendMsgResult> response) {
                if (response.code() == 200) {
                    String date = response.body().getDate();
                    Log.i("send msg response", date);
                    addMsgToList(date, msg, user.getName());
                }
            }

            @Override
            public void onFailure(Call<SendMsgResult> call, Throwable t) {

            }
        });
    }

    public void addMsgToList (String date, String msg, String userName) {
        Message newMsg = new Message(msg, userName, date, date);
        messagesArray.add(newMsg);
        chat.addMsg(newMsg);
        msgsAdapter.notifyDataSetChanged();
    }


}