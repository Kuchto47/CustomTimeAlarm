package com.project.pv239.customtimealarm.helpers.transport;

import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;

public class TransportDetailProvider {
    public static TrafficModel getTrafficModel() {
        return TrafficModel.BEST_GUESS;
    }

    public static TravelMode getTravelMode() {
        return TravelMode.DRIVING;
    }
}
