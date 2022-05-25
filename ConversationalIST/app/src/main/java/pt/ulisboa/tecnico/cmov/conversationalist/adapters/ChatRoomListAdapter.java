package pt.ulisboa.tecnico.cmov.conversationalist.adapters;

import android.app.Activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.activities.MainActivity;
import pt.ulisboa.tecnico.cmov.conversationalist.chatroom.ChatRoom;
import pt.ulisboa.tecnico.cmov.conversationalist.chatroom.ChatRoomTypes;
import pt.ulisboa.tecnico.cmov.conversationalist.fragments.MainFragment;

public class ChatRoomListAdapter extends ArrayAdapter<ChatRoom> {

    private Context context;
    private int resource;


    public ChatRoomListAdapter(Context context, int chatlist_row_item, ArrayList<ChatRoom> availableChats) {
        super(context, chatlist_row_item, availableChats);
        this.context = context;
        resource = chatlist_row_item;
    }

    public View getView(int position,View convertView,ViewGroup parent) {

        String chatName = getItem(position).getName();
        ChatRoomTypes type = getItem(position).getType();
        String chatDescr = getItem(position).getDescription();

        //ChatRoom chat = new ChatRoom(chatName, type)

        LayoutInflater inflater= LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);


        TextView titleText = (TextView) convertView.findViewById(R.id.title);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) convertView.findViewById(R.id.subtitle);

        Log.i("chat name:", chatName);
        Log.i("chat descr:", chatDescr);


        titleText.setText(chatName);
        subtitleText.setText(chatDescr);

        return convertView;

    };
}  