package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdp;
import pt.ulisboa.tecnico.cmov.conversationalist.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.chatroom.ChatRoomTypes;

public class MainFragment extends Fragment {

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

        ChatRoom chat1 = new ChatRoom("name", "Public", "desc");
        availableChats.add(chat1);

        ChatRoomListAdp chatListAdp = new ChatRoomListAdp(availableChats);

        getUserInfo(view);
        displayChatList(view, chatListAdp);
        configNewChatButton(view, chatListAdp);

        return view;
    }

    public void getUserInfo(View view) {
        TextView helloUser = view.findViewById(R.id.helloUser);
        //transfer data to fragment!!
        /*if(getIntent().getExtras() != null) {
            user = (UserAccount) getIntent().getSerializableExtra("user");
            helloUser.setText("Ahoy " + user.getName());
        }
        else*/
            helloUser.setText("Ahoy user");
    }

    public void configNewChatButton(View view, ChatRoomListAdp chatListAdp){
        Button newChat_Button = (Button) view.findViewById(R.id.newChat);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChatDialog(view, chatListAdp);
            }
        });
    }

    public void displayChatList(View view, ChatRoomListAdp chatListAdp ) {

        RecyclerView chatsListView = view.findViewById(R.id.chatRoom_List);
        Log.i("ChatListView", chatsListView.toString());

        Log.i("chats:", String.valueOf(availableChats.size()));
        chatsListView.setAdapter(chatListAdp);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        chatsListView.setLayoutManager(linearLayoutManager);
        //MainActivity main = (MainActivity) getActivity();
        //ChatRoomListAdapter chatListAdapter = new ChatRoomListAdapter(main, R.layout.chatlist_row_item, availableChats);
        //chatsListView.setAdapter(chatListAdapter);
        //chatsListView.setEmptyView(noChatsMsg);
    }


//  ##########################
//  ### new chatroom PopUp ###
//  ##########################

    public void createNewChatDialog(View view, ChatRoomListAdp chatListAdp){
        EditText input_name;
        EditText description;
        final String[] chatroom_type = new String[1];

        dialogBuilder = new AlertDialog.Builder(getActivity());
        View newChatPopupView = getLayoutInflater().inflate(R.layout.popup_newchat, null);
        dialogBuilder.setTitle("New ChatRoom");

        input_name = newChatPopupView.findViewById(R.id.editTxtNewChatroomTitle);
        description = newChatPopupView.findViewById(R.id.editTxtNewChatroomDescr);

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

        //create button (talvez mudar de positiveButton para button normal)
        dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input_name.getText().toString();
                Log.i("create chat", "estou no criar chat (early) " + availableChats.size());
                if(name.matches("")) {
                    input_name.setError("Chat name is required");
                    return;
                }

                else if (!newChatTypesSpinner.getSelectedItem().toString().equalsIgnoreCase("Chat type…")) {
                    ChatRoom new_chatRoom = new ChatRoom(name, chatroom_type[0], description.getText().toString());
                    availableChats.add(new_chatRoom);
                    dialogInterface.dismiss();
                    Log.i("create chat", "estou no criar chat " + availableChats.size());
                    //chatListAdp.notifyItemInserted(availableChats.size()-1);
                    displayChatList(view, chatListAdp);
                }
            }
        });

        dialogBuilder.setView(newChatPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }
}