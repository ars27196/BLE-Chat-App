package de.kai_morich.simple_bluetooth_le_terminal.database;


import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class CustomTypeConverter {

    @TypeConverter
    public static Date toDate(Long timestamp){
        return timestamp==null?null:new Date((timestamp));
    }

    @TypeConverter
    public static Long toTimestamp(Date date){
        return date==null?null:date.getTime();
    }

}