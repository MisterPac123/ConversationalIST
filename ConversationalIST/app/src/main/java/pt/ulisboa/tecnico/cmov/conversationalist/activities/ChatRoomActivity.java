package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import java.util.List;
import java.util.Random;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatAdapter;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.Message;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ArrayMsgsFromChatResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ReceiveMsgFromChatResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ChatRoomResults;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SendMsgResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatRoomActivity extends AppCompatActivity {

    private UserAccount user;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private ArrayList<Message> messagesArray = new ArrayList<>();
    private ChatRoom chat;
    private RecyclerView recyclerView;
    private ChatAdapter msgsAdapter;

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    private boolean started = false;
    private Handler handler = new Handler();

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
        initBackendConnection();

        getCurrentIntentAndJoinPrivateChat();


        user = (UserAccount) intent.getSerializable("user");
        chat = (ChatRoom) intent.getSerializable("chat");
        Log.i("invite link", chat.getInviteLink());

        //populatemsgArray();
        getMsgFromChat();

        //populatemsgArray();
        configTitle(intent);
        configSendButton();
        configShareButton();
        configInviteLinkButton();


        displayMsgs();
        verifyReadMsgs();

        configTitle(intent);
        configSendButton();
        start();
    }

    private void verifyReadMsgs() {
        Log.i("userRead", "verify read msgs");
        for(int i=0; i < messagesArray.size(); i++){
            Message m = messagesArray.get(i);
            if(m.getUsersRead().contains(user.getUsername())){
                readMsg(m.getId());
            }
        }
    }

    private void readMsg(String id) {
        Log.i("userRead", "read Msg " + id);
        HashMap<String, String> map = new HashMap<>();
        map.put("chatName", chat.getName());
        map.put("msgId", id);
        map.put("chatType", chat.getStringType());

        Call<Void> call = retrofitInterface.executeReadMsg(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Log.i("chatroom read msg", "msg read");

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    public void initBackendConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }


    /*private void populatemsgArray () {
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        timeFormat = new SimpleDateFormat("HH:mm:ss aaa z");
        String date = dateFormat.format(calendar.getTime());
        String time = timeFormat.format(calendar.getTime());
        UserAccount user1 = new UserAccount("user_test", "user_test@test.com","test" );
        Message newMsg = new Message("msg", user1.getUsername(), date, time);
        messagesArray.add(newMsg);
    }*/

    private void getCurrentIntentAndJoinPrivateChat() {
        Uri uri = getIntent().getData();
        if(uri != null){
            List<String> params = uri.getPathSegments();
            String inviteLink = params.get(1);
            Toast.makeText(ChatRoomActivity.this, "id="+inviteLink, Toast.LENGTH_SHORT).show();
            AddUserToPrivateChatRoom(inviteLink);
        }
    }

    private void AddUserToPrivateChatRoom(String inviteLink){
        HashMap<String, String> map = new HashMap<>();

        map.put("user", user.getUsername());
        map.put("inviteLink", inviteLink);

        Call<ChatRoomResults> call = retrofitInterface.executeAddUserToPrivateChatRoom(map);

        call.enqueue(new Callback<ChatRoomResults>() {
            @Override
            public void onResponse(Call<ChatRoomResults> call, Response<ChatRoomResults> response) {

                if (response.code() == 200) {
                    String name = response.body().getName();
                    String type = response.body().getType();
                    String description = response.body().getDescription();
                    ChatRoom chatRoom = new ChatRoom(name, type, description, inviteLink);

                    Log.i("add user to chatroom", chatRoom.getName());
                    Toast.makeText(ChatRoomActivity.this, "Joined Private ChatRoom successfully", Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    Toast.makeText(ChatRoomActivity.this, "Couldn't join ChatRoom", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChatRoomResults> call, Throwable t) {
                Toast.makeText(ChatRoomActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMsgFromChat() {
        HashMap<String, String> map = new HashMap<>();
        map.put("chatName", chat.getName());
        map.put("chatType", chat.getStringType());

        Call<ArrayMsgsFromChatResult> call = retrofitInterface.executeReceiveMsgFromChatRoom(map);

        call.enqueue(new Callback<ArrayMsgsFromChatResult>() {
            @Override
            public void onResponse(Call<ArrayMsgsFromChatResult> call, Response<ArrayMsgsFromChatResult> response) {

                if (response.code() == 200) {
                    ArrayMsgsFromChatResult msgsResult = response.body();
                    if(msgsResult != null) {
                        ArrayList<ReceiveMsgFromChatResult> msgs = msgsResult.getMsgs();

                        for (int i = 0; i < msgs.size(); i++) {

                            ReceiveMsgFromChatResult msg = msgs.get(i);
                            ArrayList<String> users = msg.getUsersRead();
                            Log.i("userRead", "dispList");
                            for(int j=0; j< users.size(); j++)
                                Log.i("userRead", users.get(j));
                            addMsgToList(msg);

                        }
                    }
                    else
                        Log.i("chatroom", "no msgs in server");
                } else if(response.code() == 404){
                    //Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayMsgsFromChatResult> call, Throwable t) {
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

    public void configInviteLinkButton() {
        Button sendButton = this.findViewById(R.id.clipboard);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Invite Link", chat.getInviteLink());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(ChatRoomActivity.this, "Copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void configShareButton(){
        ImageButton sendButton = this.findViewById(R.id.share_menu_btn);
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
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    myIntent.putExtra(Intent.EXTRA_TEXT, msg);
                    startActivity(Intent.createChooser(myIntent, "Share using"));

                }
            }
        });
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
                    sendMsgToServer(msg,editTxtMsg);
                }
            }
        });
    }

    public void sendMsgToServer(String msg, EditText editTxtMsg) {
        HashMap<String, String> map = new HashMap<>();

        map.put("user", user.getUsername());
        map.put("msg", msg);
        map.put("chatName", chat.getName());
        map.put("chatType", chat.getStringType());

        Call<SendMsgResult> call = retrofitInterface.executeSendMsgToChatRoom(map);


        call.enqueue(new Callback<SendMsgResult>() {
            @Override
            public void onResponse( Call<SendMsgResult> call, Response<SendMsgResult> response) {
                if (response.code() == 200) {
                    Log.i("chatroom send msg", response.body().getDate());
                    String date = response.body().getDate();
                    //addMsgToList(date, msg, user.getUsername());
                    editTxtMsg.getText().clear();
                }
            }

            @Override
            public void onFailure(Call<SendMsgResult> call, Throwable t) {

            }
        });
    }

    public void addMsgToList (ReceiveMsgFromChatResult msg) {
        Message newMsg = new Message(msg.getId(), msg.getMsg(), msg.getSender(), msg.getDate(), msg.getDate(), msg.getUsersRead());
        //verify if msg is already in array
        if(!messagesArray.contains(newMsg)) {
            messagesArray.add(newMsg);
            chat.addMsg(newMsg);
            msgsAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(messagesArray.size()-1);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(started) {
                getMsgFromChat();
                verifyReadMsgs();
                start();
            }
        }
    };

    public void stop() {
        started = false;
        handler.removeCallbacks(runnable);
    }

    public void start() {
        started = true;
        handler.postDelayed(runnable, 1000);
    }


}