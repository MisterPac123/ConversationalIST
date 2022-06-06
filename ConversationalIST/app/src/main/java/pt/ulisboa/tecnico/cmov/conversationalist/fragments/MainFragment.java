package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.ChatRoomActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.LoginActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdp;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.LoginResult;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SearchChatRoomResults;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.UserChatRoomsResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainFragment extends Fragment implements ChatRoomListAdp.ItemClickListener {


    UserAccount user;

    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Spinner newChatTypesSpinner;
    private ChatRoomListAdp chatListAdp;
    private ArrayList<ChatRoom> availableChats = new ArrayList<>();
    private ArrayList<SearchChatRoomResults> userChatRoomArrayList = new ArrayList<>();

    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    private String BASE_URL = "http://10.0.2.2:3000";

    public MainFragment(){
        // require a empty public constructor
    }
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initBackendConnection();

        if(this.getArguments() != null )
            user = (UserAccount) this.getArguments().getSerializable("user");

        getUserInfo(view, user);
        configNewChatButton(view);
        getUserChatRooms(view);

        return view;
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
                createNewChatDialog(view, parentView);
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

        //chatsListView.setOnClickListener();
        /*chatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ChatRoom item = availableChats.get(position);
                openChatRoom(item);

            }
        });*/
        //MainActivity main = (MainActivity) getActivity();
        //ChatRoomListAdapter chatListAdapter = new ChatRoomListAdapter(main, R.layout.chatlist_row_item, availableChats);
        //chatsListView.setAdapter(chatListAdapter);
        //chatsListView.setEmptyView(noChatsMsg);
    }

    public void openChatRoom(ChatRoom chat) {
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("chat", chat);
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

        Call<ArrayList<SearchChatRoomResults>> call = retrofitInterface.executeGetUserChatRoom(map);

        call.enqueue(new Callback<ArrayList<SearchChatRoomResults>>() {
            @Override
            public void onResponse(Call<ArrayList<SearchChatRoomResults>> call, Response<ArrayList<SearchChatRoomResults>> response) {

                if (response.code() == 200){

                    availableChats = new ArrayList<>();
                    userChatRoomArrayList = response.body();
                    for ( int i = 0; i < userChatRoomArrayList.size(); i++) {
                        SearchChatRoomResults data = userChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        Log.i("chat found", data.getName());
                        availableChats.add(chatroom);
                    }

                    chatListAdp = new ChatRoomListAdp(availableChats);

                    displayChatList(view, chatListAdp);


                    /*String name = response.body().getName();
                    String description = response.body().getDescription();
                    String type = response.body().getType();

                    ChatRoom chatRoom = new ChatRoom(name, type, description);

                    availableChats.add(chatRoom);*/
                    Toast.makeText(getActivity(), "right", Toast.LENGTH_LONG).show();


                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<ArrayList<SearchChatRoomResults>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }












//  ########################################################################
//  ########################## new chatroom PopUp ##########################
//  ########################################################################

    public void createNewChatDialog(View view, View parentView){
        EditText input_name;
        EditText input_description;
        Button newChatButton;
        final String[] chatroom_type = new String[1];

        dialogBuilder = new AlertDialog.Builder(getActivity());
        View newChatPopupView = getLayoutInflater().inflate(R.layout.popup_newchat, null);
        dialogBuilder.setTitle("New ChatRoom");

        input_name = newChatPopupView.findViewById(R.id.editTxtNewChatroomTitle);
        input_description = newChatPopupView.findViewById(R.id.editTxtNewChatroomDescr);
        newChatButton = newChatPopupView.findViewById(R.id.createNewChatroom);


        //Spinner
        newChatTypesSpinner = newChatPopupView.findViewById(R.id.chatTypesSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.chatroom_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newChatTypesSpinner.setAdapter(adapter);
        newChatTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chatroom_type[0] = adapterView.getItemAtPosition(i).toString();
                Log.i("chatroom type", chatroom_type[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialogBuilder.setView(newChatPopupView);
        dialog = dialogBuilder.create();

        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input_name.getText().toString();
                String description = input_description.getText().toString();
                Log.i("create chat", "estou no criar chat (early) " + availableChats.size());
                if(name.matches("")) {
                    input_name.setError("Chat name is required");
                    return;
                }

                else if (!newChatTypesSpinner.getSelectedItem().toString().equalsIgnoreCase("Chat type…")) {
                    createNewChat(name, description, chatroom_type);
                    dialog.dismiss();

                }
                verifyChatListEmpty(parentView);
            }
        });

        dialog.show();
    }

    private void createNewChat(String name, String description , String[] chatroom_type) {
        HashMap<String, String> map = new HashMap<>();

        map.put("name", name);
        map.put("type", chatroom_type[0]);
        map.put("description", description);
        map.put("admin", user.getUsername());

        Call<Void> call = retrofitInterface.executeCreateNewChat(map);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.code() == 200) {
                    Toast.makeText(getContext(), "ChatRoom added successfully", Toast.LENGTH_LONG).show();
                } else if (response.code() == 400) {
                    Toast.makeText(getContext(), "Name already exists", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



        ChatRoom new_chatRoom = new ChatRoom(name, chatroom_type[0], description);
        availableChats.add(new_chatRoom);

        Log.i("create chat", "estou no criar chat " + availableChats.size());
        chatListAdp.notifyItemInserted(availableChats.size()-1);
    }


    @Override
    public void onClick(View view, int position) {
        Toast t = Toast.makeText(getContext(), "hello", Toast.LENGTH_LONG);
        ChatRoom chat = availableChats.get(position);
        openChatRoom(chat);
    }
}