package pt.ulisboa.tecnico.cmov.conversationalist.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.RetrofitInterface;
import pt.ulisboa.tecnico.cmov.conversationalist.retrofit.results.SearchChatRoomResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragmentDialog extends DialogFragment {

    private ArrayList<String> chatRoomNames = new ArrayList<>();
    private String username;
    private RetrofitInterface retrofitInterface;


    public SearchFragmentDialog(ArrayList<ChatRoom> chatRoomsList, RetrofitInterface _retrofitInterface, String _username) {
        for(int i=0; i< chatRoomsList.size(); i++) {
            chatRoomNames.add(chatRoomsList.get(i).getName());
            retrofitInterface = _retrofitInterface;
            username = _username;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chat Options");
        builder.setItems(chatRoomNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addUserToChatRoom(chatRoomNames.get(i));
            }
        });
        return builder.create();
    }

    public void addUserToChatRoom(String chatroom) {
        HashMap<String, String> map = new HashMap<>();

        map.put("chatName", chatroom);
        map.put("username", username);

        Call<SearchChatRoomResults> call = retrofitInterface.executeAddUserToRoom(map);

        call.enqueue(new Callback<SearchChatRoomResults>() {
            @Override
            public void onResponse(Call<SearchChatRoomResults> call, Response<SearchChatRoomResults> response) {

                if (response.code() == 200){
                    String name = response.body().getName();
                    String type = response.body().getType();
                    String description = response.body().getDescription();
                    ChatRoom chatRoom = new ChatRoom(name, type, description);

                    Log.i("add user to chatroom", chatRoom.getName());

                    //Intent intent = new Intent().putExtra("chat", chatRoom);

                    //getTargetFragment().onActivityResult(
                            //getTargetRequestCode(), 1, intent);


                    //send chatRoom to fragment to add to chatRoom Arraylist

                    /*ArrayList <ChatRoom> searchChatsResult = new ArrayList<>();
                    searchChatRoomArrayList = response.body();
                    for ( int i = 0; i < searchChatRoomArrayList.size(); i++) {
                        SearchChatRoomResults data = searchChatRoomArrayList.get(i);
                        ChatRoom chatroom = new ChatRoom(data.getName(), data.getType(), data.getDescription());
                        Log.i("chat found", data.getName());
                        searchChatsResult.add(chatroom);

                    }

                    handleSearchChatRoom(searchChatsResult);*/



                    //chatListAdp = new ChatRoomListAdp(availableChats);

                    //displayChatList(view, chatListAdp);

                } else if(response.code() == 404){
                    Toast.makeText(getActivity(), "No chats error", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<SearchChatRoomResults> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
