package de.kai_morich.simple_bluetooth_le_terminal.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private LiveData<List<ChatMessage>> chatEntryLiveData;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        ChatDatabase database=ChatDatabase.getInstance(getApplication());
//        chatEntryLiveData=database.chatDao().loadChatHistory();
    }

    public  LiveData<List<ChatMessage>> getTask(){
        return chatEntryLiveData;
    }
}

