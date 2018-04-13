package com.project.pv239.customtimealarm.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.project.pv239.customtimealarm.enums.TrafficModel.BEST_GUESS;
import static com.project.pv239.customtimealarm.enums.TrafficModel.OPTIMISTIC;
import static com.project.pv239.customtimealarm.enums.TrafficModel.PESSIMISTIC;

@IntDef({BEST_GUESS, OPTIMISTIC, PESSIMISTIC})
@Retention(RetentionPolicy.SOURCE)
public @interface TrafficModel {
    int BEST_GUESS = 0;
    int OPTIMISTIC = 1;
    int PESSIMISTIC = 2;
}
