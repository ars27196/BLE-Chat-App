package de.kai_morich.simple_bluetooth_le_terminal.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.kai_morich.simple_bluetooth_le_terminal.R;
import de.kai_morich.simple_bluetooth_le_terminal.database.ChatMessage;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    TextView messageText, timeText;

    ImageView profileImage;



    SentMessageHolder(View itemView) {

        super(itemView);

        messageText = (TextView) itemView.findViewById(R.id.text_message_body);

        timeText = (TextView) itemView.findViewById(R.id.text_message_time);


    }

    void bind(ChatMessage message) {

        messageText.setText(message.getMessage());
        timeText.setText("" + message.getSendDate());

    }
}
