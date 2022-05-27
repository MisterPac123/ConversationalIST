package pt.ulisboa.tecnico.cmov.conversationalist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.chatroom.ChatRoom;

public class ChatRoomListAdp extends Adapter<ChatRoomListAdp.ViewHolderClass> {

    ArrayList<ChatRoom> availableChatsList;

    public ChatRoomListAdp(ArrayList<ChatRoom> availableChats) {
        this.availableChatsList = availableChats;
    }

    @NonNull
    @Override
    public ViewHolderClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatlist_row_item, parent, false);
        ViewHolderClass viewHolderClass = new  ViewHolderClass(view);

        return viewHolderClass;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderClass holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;
        ChatRoom chat = availableChatsList.get(position);
        viewHolderClass.titleView.setText(chat.getName());
        viewHolderClass.subtitleView.setText(chat.getDescription());
    }

    @Override
    public int getItemCount() {
        return availableChatsList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView titleView;
        TextView subtitleView;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.chatTitle);
            subtitleView = (TextView) itemView.findViewById(R.id.subtitle);
        }
    }
}
