package com.project.pv239.customtimealarm.helpers.converters;

import com.project.pv239.customtimealarm.enums.TrafficModel;

public class TrafficModelToString {
    public static String get(int trafficModel){
        switch (trafficModel){
            case TrafficModel.OPTIMISTIC:
                return "optimistic";
            case TrafficModel.PESSIMISTIC:
                return "pessimistic";
            default:
                return "best_guess";
            //TODO What should be returned if for some reason we get bad number? For now i chose best_guess because that's default value in google maps api (I think).
        }
    }
}
