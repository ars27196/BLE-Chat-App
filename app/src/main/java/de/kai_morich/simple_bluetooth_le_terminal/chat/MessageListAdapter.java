package de.kai_morich.simple_bluetooth_le_terminal.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.kai_morich.simple_bluetooth_le_terminal.Constants;
import de.kai_morich.simple_bluetooth_le_terminal.R;
import de.kai_morich.simple_bluetooth_le_terminal.database.ChatMessage;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;

    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    private Context mContext;

    private List<ChatMessage> mMessageList;


    public MessageListAdapter(Context context, List<ChatMessage> messageList) {

        mContext = context;

        mMessageList = messageList;

    }


    @Override

    public int getItemCount() {

        return mMessageList.size();

    }


    // Determines the appropriate ViewType according to the sender of the message.

    @Override

    public int getItemViewType(int position) {

        ChatMessage message = (ChatMessage) mMessageList.get(position);


        if (message.getSender().equals(Constants.Current_User_Loged_in)) {

            // If the current user is the sender of the message

            return VIEW_TYPE_MESSAGE_SENT;

        } else {

            // If some other user sent the message

            return VIEW_TYPE_MESSAGE_RECEIVED;

        }

    }


    // Inflates the appropriate layout according to the ViewType.

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;


        if (viewType == VIEW_TYPE_MESSAGE_SENT) {

            view = LayoutInflater.from(parent.getContext())

                    .inflate(R.layout.item_message_sent, parent, false);

            return new SentMessageHolder(view);

        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {

            view = LayoutInflater.from(parent.getContext())

                    .inflate(R.layout.item_message_received, parent, false);

            return new ReceivedMessageHolder(view);

        }


        return null;

    }


    // Passes the message object to a ViewHolder so that the contents can be bound to UI.

    @Override

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatMessage message = mMessageList.get(position);


        switch (holder.getItemViewType()) {

            case VIEW_TYPE_MESSAGE_SENT:

                ((SentMessageHolder) holder).bind(message);

                break;

            case VIEW_TYPE_MESSAGE_RECEIVED:

                ((ReceivedMessageHolder) holder).bind(message);

        }


    }
}
