package pt.ulisboa.tecnico.cmov.conversationalist.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.UserAccount;
import pt.ulisboa.tecnico.cmov.conversationalist.adapters.ChatRoomListAdapter;
import pt.ulisboa.tecnico.cmov.conversationalist.chatroom.ChatRoom;

public class MainActivity extends AppCompatActivity {

    UserAccount user;

    private AlertDialog dialog;
    private AlertDialog.Builder dialogBuilder;
    private Spinner newChatTypesSpinner;
    private ArrayList<ChatRoom> availableChats = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUserInfo();

        configNewChatButton();
        displayChatList();
    }

    public void getUserInfo() {
        TextView helloUser = findViewById(R.id.helloUser);
        if(getIntent().getExtras() != null) {
            user = (UserAccount) getIntent().getSerializableExtra("user");
            helloUser.setText("Ahoy " + user.getName());
        }
        else
            helloUser.setText("Ahoy user");
    }

    public void configNewChatButton(){
        Button newChat_Button = (Button) findViewById(R.id.newChat);
        newChat_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChatDialog();
            }
        });
    }

    public void displayChatList() {
        ListView chatsListView = findViewById(R.id.chatRoom_List);
        TextView noChatsMsg =  findViewById(R.id.EmptyChatListMsg);
        ChatRoomListAdapter chatListAdapter = new ChatRoomListAdapter(this, R.layout.chatlist_row_item, availableChats);
        chatsListView.setAdapter(chatListAdapter);
        chatsListView.setEmptyView(noChatsMsg);
    }

//  ##########################
//  ### new chatroom PopUp ###
//  ##########################

    public void createNewChatDialog(){
        EditText input_name;
        EditText description;
        final String[] chatroom_type = new String[1];

        dialogBuilder = new AlertDialog.Builder(this);
        View newChatPopupView = getLayoutInflater().inflate(R.layout.popup_newchat, null);
        dialogBuilder.setTitle("New ChatRoom");

        input_name = newChatPopupView.findViewById(R.id.editTxtNewChatroomTitle);
        description = newChatPopupView.findViewById(R.id.editTxtNewChatroomDescr);

        //Spinner
        newChatTypesSpinner = newChatPopupView.findViewById(R.id.chatTypesSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.chatroom_types, android.R.layout.simple_spinner_item);
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
                if(name.matches("")) {
                    input_name.setError("Chat name is required");
                    Log.i("error", input_name.getText().toString());
                    return;
                }

                else if (!newChatTypesSpinner.getSelectedItem().toString().equalsIgnoreCase("Chat type…")) {
                        ChatRoom new_chatRoom = new ChatRoom(name, chatroom_type[0], description.getText().toString());
                        availableChats.add(new_chatRoom);
                        dialogInterface.dismiss();
                        displayChatList();
                }
            }
        });

        dialogBuilder.setView(newChatPopupView);
        dialog = dialogBuilder.create();
        dialog.show();
    }

}
