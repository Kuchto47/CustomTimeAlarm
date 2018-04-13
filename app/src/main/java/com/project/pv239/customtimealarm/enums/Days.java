package com.project.pv239.customtimealarm.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.project.pv239.customtimealarm.enums.Days.FRIDAY;
import static com.project.pv239.customtimealarm.enums.Days.MONDAY;
import static com.project.pv239.customtimealarm.enums.Days.SATURDAY;
import static com.project.pv239.customtimealarm.enums.Days.SUNDAY;
import static com.project.pv239.customtimealarm.enums.Days.THURSDAY;
import static com.project.pv239.customtimealarm.enums.Days.TUESDAY;
import static com.project.pv239.customtimealarm.enums.Days.WEDNESDAY;

@IntDef({MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY})
@Retention(RetentionPolicy.SOURCE)
public @interface Days {
    int MONDAY = 0;
    int TUESDAY = 1;
    int WEDNESDAY = 2;
    int THURSDAY = 3;
    int FRIDAY = 4;
    int SATURDAY = 5;
    int SUNDAY = 6;
}
