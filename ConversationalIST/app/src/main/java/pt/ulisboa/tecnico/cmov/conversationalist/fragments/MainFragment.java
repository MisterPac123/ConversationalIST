package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.ChatRoomActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdp;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoomTypes;

public class MainFragment extends Fragment implements ChatRoomListAdp.ItemClickListener {


    UserAccount user;

    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Spinner newChatTypesSpinner;
    private ArrayList<ChatRoom> availableChats = new ArrayList<>();

    public MainFragment(){
        // require a empty public constructor
    }
    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ChatRoomListAdp chatListAdp = new ChatRoomListAdp(availableChats);
        if(this.getArguments() != null )
            user = (UserAccount) this.getArguments().getSerializable("user");

        getUserInfo(view, user);
        configNewChatButton(view, chatListAdp);
        displayChatList(view, chatListAdp);


        return view;
    }

    public void getUserInfo(View view, UserAccount user) {
        TextView helloUser = view.findViewById(R.id.helloUser);
        if(user != null) {
            helloUser.setText("Ahoy " + user.getName());
        }
        else
            helloUser.setText("Ahoy user");
    }

    public void configNewChatButton(View parentView, ChatRoomListAdp chatListAdp){
        Button newChat_Button = (Button) parentView.findViewById(R.id.newChat);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChatDialog(view, chatListAdp, parentView);
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


//  ##########################
//  ### new chatroom PopUp ###
//  ##########################

    public void createNewChatDialog(View view, ChatRoomListAdp chatListAdp, View parentView){
        EditText input_name;
        EditText description;
        Button newChatButton;
        final String[] chatroom_type = new String[1];

        dialogBuilder = new AlertDialog.Builder(getActivity());
        View newChatPopupView = getLayoutInflater().inflate(R.layout.popup_newchat, null);
        dialogBuilder.setTitle("New ChatRoom");

        input_name = newChatPopupView.findViewById(R.id.editTxtNewChatroomTitle);
        description = newChatPopupView.findViewById(R.id.editTxtNewChatroomDescr);
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

        //create button (talvez mudar de positiveButton para button normal)
        newChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = input_name.getText().toString();
                Log.i("create chat", "estou no criar chat (early) " + availableChats.size());
                if(name.matches("")) {
                    input_name.setError("Chat name is required");
                    return;
                }

                else if (!newChatTypesSpinner.getSelectedItem().toString().equalsIgnoreCase("Chat type…")) {
                    ChatRoom new_chatRoom = new ChatRoom(name, chatroom_type[0], description.getText().toString());
                    availableChats.add(new_chatRoom);
                    dialog.dismiss();
                    Log.i("create chat", "estou no criar chat " + availableChats.size());
                    chatListAdp.notifyItemInserted(availableChats.size()-1);

                }
                verifyChatListEmpty(parentView);
            }
        });

        dialog.show();
    }


    @Override
    public void onClick(View view, int position) {
        Toast t = Toast.makeText(getContext(), "hello", Toast.LENGTH_LONG);
        ChatRoom chat = availableChats.get(position);
        openChatRoom(chat);
    }
}