package com.project.pv239.customtimealarm.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.project.pv239.customtimealarm.database.dao.AlarmDao;
import com.project.pv239.customtimealarm.database.entity.Alarm;

@Database(entities = {Alarm.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
}
