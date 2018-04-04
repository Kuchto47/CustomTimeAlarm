package com.project.pv239.customtimealarm.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.project.pv239.customtimealarm.database.DAO.AlarmDao;
import com.project.pv239.customtimealarm.database.Entity.Alarm;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
}
