package com.project.pv239.customtimealarm.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM alarm")
    List<Alarm> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addAlarm(Alarm alarm);

    @Update
    void updateAlarm(Alarm alarm);

    @Delete
    void deleteAlarm(Alarm alarm);
}
