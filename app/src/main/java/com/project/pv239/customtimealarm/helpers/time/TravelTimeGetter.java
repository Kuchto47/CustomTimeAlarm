package com.project.pv239.customtimealarm.helpers.time;

import com.project.pv239.customtimealarm.api.GoogleMapsApiInformationGetter;
import com.project.pv239.customtimealarm.api.model.directions.Leg;
import com.project.pv239.customtimealarm.database.entity.Alarm;


public class TravelTimeGetter {
    private static final long UNDEFINED_DEPARTURE_TIME = -1;

    /**
     * Retrieves travelling time for given alarm
     * @param alarm alarm for which calculation is taking place
     * @return travel time in seconds
     */
    public static int getEstimatedTravelTimeForAlarm(Alarm alarm){
        GoogleMapsApiInformationGetter api = new GoogleMapsApiInformationGetter();
        int initialTravelTime = TravelTimeGetter.firstCall(api, alarm);
        if(initialTravelTime != -1) {
            int approximatedTravelTime = TravelTimeGetter.secondCall(api, alarm, initialTravelTime);
            if(approximatedTravelTime != -1) {
                return approximatedTravelTime;
            }
        }
        return initialTravelTime;
    }

    private static int firstCall(GoogleMapsApiInformationGetter api, Alarm alarm) {
        Leg response = TravelTimeGetter.callApi(api, alarm, UNDEFINED_DEPARTURE_TIME);
        if(response != null){
            return response.duration.value;
        }
        return -1;
    }

    private static int secondCall(GoogleMapsApiInformationGetter api, Alarm alarm, int initialTravelTime) {
        long requestedArrivalTime = alarm.getTimeOfArrivalInSeconds();
        long travelTime = (long)initialTravelTime;
        long departureTime = requestedArrivalTime-travelTime;
        Leg response = TravelTimeGetter.callApi(api, alarm, departureTime);
        if(response != null) {
            if(response.duration_in_traffic != null){
                return response.duration_in_traffic.value;
            }
            return response.duration.value;
        }
        return -1;
    }

    private static Leg callApi(GoogleMapsApiInformationGetter api, Alarm alarm, long departureTime){
        return api.getDirections(alarm, departureTime);
    }
}
