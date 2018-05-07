package com.project.pv239.customtimealarm.helpers.time;

import java.util.Calendar;

public class TimeHelper {
    public static long getTimeInSeconds(int hour, int minute) {
        boolean isTimeTomorrow = TimeHelper.isTimeTomorrow(hour, minute);
        Calendar requestedTime = TimeHelper.getActualTime();
        requestedTime.set(Calendar.HOUR_OF_DAY, hour);
        requestedTime.set(Calendar.MINUTE, minute);
        requestedTime.set(Calendar.SECOND, 0);
        long requestedTimeInSeconds = requestedTime.getTime().getTime()/1000;
        return requestedTimeInSeconds + (isTimeTomorrow ? 24*60*60 : 0);
    }

    private static boolean isTimeTomorrow(int wakeUpHour, int wakeUpMinute) {
        Calendar actualTime = TimeHelper.getActualTime();
        int actualHour = actualTime.get(Calendar.HOUR_OF_DAY);
        int actualMinute = actualTime.get(Calendar.MINUTE);
        return actualHour > wakeUpHour || (actualHour == wakeUpHour && actualMinute > wakeUpMinute);
    }

    private static Calendar getActualTime() {
        return Calendar.getInstance();
    }
}
