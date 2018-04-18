package com.project.pv239.customtimealarm.helpers.converters;

import com.project.pv239.customtimealarm.enums.TravelMode;

public class TravelModeToString {
    public static String get(int travelMode){
        switch (travelMode){
            case TravelMode.WALKING:
                return "walking";
            case TravelMode.BICYCLING:
                return "bicycling";
            default:
                return "driving";
        }
    }
}
