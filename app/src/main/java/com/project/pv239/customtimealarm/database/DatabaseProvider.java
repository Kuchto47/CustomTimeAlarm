package com.project.pv239.customtimealarm.database;

import android.arch.persistence.room.Room;

import com.project.pv239.customtimealarm.activities.MainActivity;

public class DatabaseProvider {
    private static AppDatabase appDatabase = null;

    public static AppDatabase getDatabase(){
        if(appDatabase == null){
            appDatabase = Room.databaseBuilder(MainActivity.context, AppDatabase.class, "alarm-database").build();
        }
        return appDatabase;
    }
}
