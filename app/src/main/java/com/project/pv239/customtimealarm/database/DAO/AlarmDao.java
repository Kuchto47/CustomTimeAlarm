package com.project.pv239.customtimealarm.database.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.project.pv239.customtimealarm.database.Entity.Alarm;

import java.util.List;

@Dao
public interface AlarmDao {
    @Query("SELECT * FROM alarm")
    List<Alarm> getAll();

    @Insert
    void addAlarm(Alarm alarm);

    @Delete
    void deleteAlarm(Alarm alarm);
}
