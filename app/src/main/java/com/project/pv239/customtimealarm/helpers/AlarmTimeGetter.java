package com.project.pv239.customtimealarm.helpers;

import com.project.pv239.customtimealarm.database.entity.Alarm;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmTimeGetter {
    public static long getAlarmTimeInMilliSeconds(Alarm alarm) {
        int travelTimeInSeconds = TravelTimeGetter.getEstimatedTravelTimeForAlarm(alarm);
        if(travelTimeInSeconds == -1) {
            return alarm.getTimeOfDefaultAlarmInSeconds()*1000;
        }
        long morningRoutineInMS = (long)alarm.getMorningRoutine()*60*1000;
        long travelTimeInMS = (long)travelTimeInSeconds*1000;
        long arrivalTimeInMS = alarm.getTimeOfArrivalInSeconds()*1000;
        return arrivalTimeInMS-travelTimeInMS-morningRoutineInMS;
    }
}
