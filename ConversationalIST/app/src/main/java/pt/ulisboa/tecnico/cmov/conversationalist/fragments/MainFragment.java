package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.ChatRoomResults;
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


    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

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


        return view;
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
        if(!availableChats.contains(chatRoom))
            availableChats.add(chatRoom);
    }


    @Override
    public void onClick(View view, int position) {
        ChatRoom chat = availableChats.get(position);
        openChatRoom(chat);
    }
}