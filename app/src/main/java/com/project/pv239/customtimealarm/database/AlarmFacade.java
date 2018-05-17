package com.project.pv239.customtimealarm.database;

import java.util.List;

public class AlarmFacade {
    private AlarmDao alarmDao;

    public AlarmFacade() {
        this.alarmDao = DatabaseProvider.getDatabase().alarmDao();
    }

    public List<Alarm> getAllAlarms() {
        return alarmDao.getAll();
    }

    public long addAlarm(Alarm alarm) {
        return alarmDao.addAlarm(alarm);
    }

    public void updateAlarm(Alarm alarm) {
        alarmDao.updateAlarm(alarm);
    }

    public void deleteAlarm(Alarm alarm) {
        alarmDao.deleteAlarm(alarm);
    }
}
