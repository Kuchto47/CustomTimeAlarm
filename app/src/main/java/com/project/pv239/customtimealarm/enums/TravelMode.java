package com.project.pv239.customtimealarm.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.project.pv239.customtimealarm.enums.TravelMode.BICYCLING;
import static com.project.pv239.customtimealarm.enums.TravelMode.DRIVING;
import static com.project.pv239.customtimealarm.enums.TravelMode.WALKING;

@IntDef({DRIVING, WALKING, BICYCLING})
@Retention(RetentionPolicy.SOURCE)
public @interface TravelMode {
    int DRIVING = 0;
    int WALKING = 1;
    int BICYCLING = 2;
}