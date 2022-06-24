package pt.ulisboa.tecnico.cmov.conversationalist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;


import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.chatroom.ChatRoom;

public class ChatRoomListAdp extends RecyclerView.Adapter<ChatRoomListAdp.ViewHolderClass> {

    ArrayList<ChatRoom> availableChatsList;
    private ItemClickListener clickListener;


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
        if(chat.getNumberUnreadMsgs()>0){
            viewHolderClass.subtitleView.setText(chat.getNumberUnreadMsgs() + " new msgs");
        }
        else {
            viewHolderClass.subtitleView.setText(chat.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return availableChatsList.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titleView;
        TextView subtitleView;


        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.chatTitle);
            subtitleView = (TextView) itemView.findViewById(R.id.subtitle);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            Log.i("onclick", "onclick adp");
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }
}
