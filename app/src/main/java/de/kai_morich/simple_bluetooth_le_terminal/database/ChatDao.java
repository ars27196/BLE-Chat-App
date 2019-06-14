package de.kai_morich.simple_bluetooth_le_terminal.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT * FROM chat ORDER BY id ")
    List<ChatMessage> loadChatHistory();

    @Insert
    void insertTask(ChatMessage chatMessage);

}
