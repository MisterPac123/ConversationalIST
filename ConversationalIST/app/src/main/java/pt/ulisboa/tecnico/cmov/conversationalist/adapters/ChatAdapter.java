package pt.ulisboa.tecnico.cmov.conversationalist.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ulisboa.tecnico.cmov.conversationalist.R;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.Message;
import pt.ulisboa.tecnico.cmov.conversationalist.classes.UserAccount;

public class ChatAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private ArrayList<Message> messageList;
    private UserAccount user;

    public ChatAdapter ( Context _context, ArrayList<Message> _messageList, UserAccount _user) {
        context = _context;
        messageList = _messageList;
        user = _user;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message msg = (Message) messageList.get(position);
        Log.i("Chat Adapter user msg", msg.getSender());
        Log.i("Chat Adapter user", user.getUsername());


        if (msg.getSender().equals(user.getUsername())) {
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_container_sent_msg, parent, false);
            return new SentMessageHolder(view);
        }
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_container_receiver_msg, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.textMessage);
            timeText = (TextView) itemView.findViewById(R.id.textDateTime);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.textMessage);
            timeText = (TextView) itemView.findViewById(R.id.textDateTime);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        }

        void bind(Message message) {
            messageText.setText(message.getText());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

            nameText.setText(message.getSender());
        }
    }


}
