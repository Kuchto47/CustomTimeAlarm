package com.project.pv239.customtimealarm.database.facade;

import com.project.pv239.customtimealarm.database.DatabaseProvider;
import com.project.pv239.customtimealarm.database.dao.AlarmDao;
import com.project.pv239.customtimealarm.database.entity.Alarm;

import java.util.List;

public class AlarmFacade {
    private AlarmDao alarmDao;

    public AlarmFacade() {
        this.alarmDao = DatabaseProvider.getDatabase().alarmDao();
    }

    public List<Alarm> getAllAlarms() {
        return alarmDao.getAll();
    }

    public void addAlarm(Alarm alarm) {
        alarmDao.addAlarm(alarm);
    }

    public void deleteAlarm(Alarm alarm) {
        alarmDao.deleteAlarm(alarm);
    }
}
