package de.kai_morich.simple_bluetooth_le_terminal.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;


@Database(entities = {ChatMessage.class}, version = 1, exportSchema = false)
@TypeConverters(CustomTypeConverter.class)
public abstract class ChatDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "chatHistory";
    private static ChatDatabase sInstance;

    public static ChatDatabase getInstance(Context context) {

        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        ChatDatabase.class, ChatDatabase.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }

        return sInstance;
    }

    public abstract ChatDao chatDao();

}
