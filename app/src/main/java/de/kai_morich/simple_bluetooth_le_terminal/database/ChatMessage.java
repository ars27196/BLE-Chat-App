package de.kai_morich.simple_bluetooth_le_terminal.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "chat")
public class ChatMessage {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String message;
    private String sendDate;
    private String sender;
    private String receiver;

    @Ignore
    public ChatMessage(String message, String sendDate, String sender, String receiver) {
        this.message = message;
        this.sendDate = sendDate;
        this.sender = sender;
        this.receiver=receiver;
    }

    public ChatMessage(int id, String message, String sendDate, String sender, String receiver) {
        this.id = id;
        this.message = message;
        this.sendDate = sendDate;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
