package com.project.pv239.customtimealarm.database;

import android.arch.persistence.room.Room;

import com.project.pv239.customtimealarm.App;

public class DatabaseProvider {
    private static AppDatabase appDatabase = null;
    private static String DB_NAME = "alarm.db";

    public static AppDatabase getDatabase(){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(App.getInstance(), AppDatabase.class, DB_NAME).build();
        }
        return appDatabase;
    }
}
