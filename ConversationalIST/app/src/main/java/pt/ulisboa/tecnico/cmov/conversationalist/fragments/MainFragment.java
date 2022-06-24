package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;


import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.ChatRoomActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.CreateChatRoomActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdp;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ArrayMsgsFromChatResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ChatRoomResults;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ReceiveMsgFromChatResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment implements ChatRoomListAdp.ItemClickListener {

    private View view;

    private UserAccount user;

    private ChatRoomListAdp chatListAdp;
    private ArrayList<ChatRoom> availableChats = new ArrayList<>();
    private ArrayList<ChatRoomResults> userChatRoomArrayList = new ArrayList<>();
    private ArrayList<ChatRoomResults> searchChatRoomArrayList = new ArrayList<>();
    private ArrayList<String> notificationsMsgs = new ArrayList<>();


    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    private boolean started = false;
    private Handler handler = new Handler();

    private NotificationCompat.Builder notification_builder;
    private NotificationManager mNotificationManager;

    public MainFragment(){
        // require a empty public constructor
    }
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);

        initBackendConnection();

        if(this.getArguments() != null )
            user = (UserAccount) this.getArguments().getSerializable("user");

        getUserInfo(view, user);
        configNewChatButton(view);
        configureSearchChat(view);
        getUserChatRooms(view);
        initializeNotificationChannel();
        start();


        return view;
    }

    private void initializeNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //CharSequence name = getString(R.string.channel_name);
            //String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "name", importance);
            //channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        getUserChatRooms(view);
    }

    public void initBackendConnection() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public void getUserInfo(View view, UserAccount user) {
        TextView helloUser = view.findViewById(R.id.helloUser);
        if(user != null) {
            helloUser.setText("Ahoy " + user.getName());
        }
        else
            helloUser.setText("Ahoy user");
    }

    public void configNewChatButton(View parentView){
        Button newChat_Button = (Button) parentView.findViewById(R.id.newChat);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateNewChatActivity();
            }
        });
    }

    public void configureSearchChat(View parentView) {
        /*SearchView searchView = parentView.findViewById(R.id.);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });*/



        ImageButton searchChatRoomBt = parentView.findViewById(R.id.searchChatRoomBtn);
        EditText searchChatRoomEt = parentView.findViewById(R.id.searchChatRoomET);
        searchChatRoomBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = searchChatRoomEt.getText().toString();
                if (name.matches("")){
                     Toast toast = Toast.makeText(getContext(), "Please insert the chatroom name in the searchbar", Toast.LENGTH_SHORT);
                     toast.show();
                }
                else{
                    //search in list?
                }
            }
        });
    }


    public void displayChatList(View view, ChatRoomListAdp chatListAdp ) {

        verifyChatListEmpty(view);

        RecyclerView chatsListView = view.findViewById(R.id.chatRoom_List);

        Log.i("chats:", String.valueOf(availableChats.size()));
        chatsListView.setAdapter(chatListAdp);
        chatListAdp.setClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatsListView.setLayoutManager(linearLayoutManager);
    }

    public void openChatRoom(ChatRoom chat) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("chat", chat);
        startActivity(intent);
    }

    public void openCreateNewChatActivity() {
        Intent intent = new Intent(getActivity(), CreateChatRoomActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void verifyChatListEmpty(View view) {
        if(availableChats.size()>0){
            TextView emptyListmsg = view.findViewById(R.id.EmptyChatListMsg);
            emptyListmsg.setVisibility(View.INVISIBLE);
        }
    }

    public void getUserChatRooms(View view) {
        HashMap<String, String> map = new HashMap<>();

        map.put("username", user.getUsername());

        availableChats = new ArrayList<>();

        getUserPublicChatRooms(map);
        //getUserGeoChatRooms(map);
        getUserPrivateChatRooms(map);

    }

    public void getUserPublicChatRooms(HashMap<String, String> map) {
        Call<ArrayList<ChatRoomResults>> call = retrofitInterface.executeGetUserChatRoom(map);

        call.enqueue(new Callback<ArrayList<ChatRoomResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ChatRoomResults>> call, Response<ArrayList<ChatRoomResults>> response) {

                if (response.code() == 200){

                    userChatRoomArrayList = response.body();
                    for ( int i = 0; i < userChatRoomArrayList.size(); i++) {
                        ChatRoomResults data = userChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        chatroom.setLastMsg(data.getLastMsgTime());
                        //Log.i("date", data.getLastMsgTime());
                        addChatToArray(chatroom);
                    }

                    chatListAdp = new ChatRoomListAdp(availableChats);

                    displayChatList(view, chatListAdp);

                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ChatRoomResults>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getUserPrivateChatRooms(HashMap<String, String> map) {
        Call<ArrayList<ChatRoomResults>> call = retrofitInterface.executeGetUserPrivateChatRoom(map);

        call.enqueue(new Callback<ArrayList<ChatRoomResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ChatRoomResults>> call, Response<ArrayList<ChatRoomResults>> response) {

                if (response.code() == 200){

                    userChatRoomArrayList = response.body();
                    for ( int i = 0; i < userChatRoomArrayList.size(); i++) {
                        ChatRoomResults data = userChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        chatroom.setLastMsg(data.getLastMsgTime());
                        //Log.i("date", data.getLastMsgTime());
                        addChatToArray(chatroom);
                    }

                    chatListAdp = new ChatRoomListAdp(availableChats);

                    displayChatList(view, chatListAdp);

                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ChatRoomResults>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }




    public void getUserGeoChatRooms(HashMap<String, String> map) {
        Call<ArrayList<ChatRoomResults>> call = retrofitInterface.executeGetUserGeoChatRoom(map);

        call.enqueue(new Callback<ArrayList<ChatRoomResults>>() {
            @Override
            public void onResponse(Call<ArrayList<ChatRoomResults>> call, Response<ArrayList<ChatRoomResults>> response) {

                if (response.code() == 200){

                    userChatRoomArrayList = response.body();
                    for ( int i = 0; i < userChatRoomArrayList.size(); i++) {
                        ChatRoomResults data = userChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        addChatToArray(chatroom);
                    }

                    chatListAdp = new ChatRoomListAdp(availableChats);

                    displayChatList(view, chatListAdp);

                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<ChatRoomResults>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addChatToArray(ChatRoom chatRoom) {
        if(!availableChats.contains(chatRoom)){
            availableChats.add(chatRoom);
            Log.i("new hat", chatRoom.getLastMsg().toString());
            Collections.sort(availableChats);
            Collections.reverse(availableChats);
        }
    }


    @Override
    public void onClick(View view, int position) {
        ChatRoom chat = availableChats.get(position);
        openChatRoom(chat);
    }


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(started) {
                getMsgFromChats();
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

    private void getMsgFromChats(){
        for(int i=0; i<availableChats.size(); i++){
            getMsgFromChat(availableChats.get(i));
        }
    }

    private void getMsgFromChat(ChatRoom chat) {
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
                            if(!users.contains(user.getUsername()) && !notificationsMsgs.contains(msg.getId())){
                                Log.i("userRead", "create notification");
                                createNotification(msg, chat.getName());
                                chat.addUnreadMsgs(msg.getMsg());
                                chatListAdp.notifyDataSetChanged();
                            }
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

    private void createNotification(ReceiveMsgFromChatResult msg, String chatName) {
        /*notification_builder.setSmallIcon(R.drawable.ic_baseline_message_24);
        notification_builder.setContentTitle("title");
        notification_builder.setContentText("text");

        mNotificationManager.notify(0, notification_builder.build());*/


        notificationsMsgs.add(msg.getId());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),"CHANNEL_ID");
        builder.setContentTitle("ConversationalIST");
        builder.setContentText(chatName + "\n" + msg.getSender() + ":" + msg.getMsg());
        builder.setSmallIcon(R.drawable.ic_baseline_message_24);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat=NotificationManagerCompat.from(getContext());
        managerCompat.notify(1,builder.build());

    }
}