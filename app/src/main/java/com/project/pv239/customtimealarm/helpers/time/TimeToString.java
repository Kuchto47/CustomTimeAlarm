package com.project.pv239.customtimealarm.helpers.time;

import java.util.Locale;

public class TimeToString {
    public static String convert(int minutes){
        int hours = minutes/60;
        int minutesLeft = minutes % 60;
        return TimeToString.convert(hours, minutesLeft);
    }

    public static String convert(int hours, int minutes){
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }
}
