package com.project.pv239.customtimealarm.helpers;

import com.project.pv239.customtimealarm.database.entity.Alarm;

import java.util.GregorianCalendar;

public class AlarmTimeGetter {
    public static long getAlarmTimeInMilliSeconds(Alarm alarm) {
        return 10;
//        long travelTimeInMS = (long)TravelTimeGetter.getEstimatedTravelTimeForAlarm(alarm)*1000;
//        long morningRoutineInMS = (long)alarm.getMorningRoutine()*60*1000;
//        long arrivalTimeInMS = alarm.getTimeOfArrivalInSeconds()*1000;
//        return arrivalTimeInMS-travelTimeInMS-morningRoutineInMS;
    }
}
