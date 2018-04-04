package com.project.pv239.customtimealarm.database;

import android.arch.persistence.room.Room;

import com.project.pv239.customtimealarm.activities.MainActivity;

public class DatabaseProvider {
    private static AppDatabase appDatabase = null;
    private static String DB_NAME = "alarm.db";

    public static AppDatabase getDatabase(){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(MainActivity.context, AppDatabase.class, DB_NAME).build();
        }
        return appDatabase;
    }
}
