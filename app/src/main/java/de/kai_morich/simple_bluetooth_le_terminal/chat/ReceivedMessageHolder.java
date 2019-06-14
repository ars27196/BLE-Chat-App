package de.kai_morich.simple_bluetooth_le_terminal.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import de.kai_morich.simple_bluetooth_le_terminal.R;
import de.kai_morich.simple_bluetooth_le_terminal.database.ChatMessage;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {

    TextView messageText, timeText, nameText;


    ReceivedMessageHolder(View itemView) {

        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.text_message_body);

        timeText = (TextView) itemView.findViewById(R.id.text_message_time);

        nameText = (TextView) itemView.findViewById(R.id.text_message_name);


    }


    void bind(ChatMessage message) {

        messageText.setText(message.getMessage());
        timeText.setText("" + message.getSendDate());
        nameText.setText(message.getSender());

    }
}
