package com.project.pv239.customtimealarm.helpers;

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
        int approximatedTravelTime = TravelTimeGetter.secondCall(api, alarm, initialTravelTime);
        boolean isThirdCallNeeded = TravelTimeGetter.isThirdCallNeeded(initialTravelTime, approximatedTravelTime, alarm);
        return isThirdCallNeeded ? TravelTimeGetter.thirdCall(api, alarm, approximatedTravelTime) : approximatedTravelTime;
    }

    private static int firstCall(GoogleMapsApiInformationGetter api, Alarm alarm) {
        return TravelTimeGetter.callApi(api, alarm, UNDEFINED_DEPARTURE_TIME).duration.value;
    }

    private static int secondCall(GoogleMapsApiInformationGetter api, Alarm alarm, int initialTravelTime) {
        long requestedArrivalTime = alarm.getTimeOfArrivalInSeconds();
        long travelTime = (long)initialTravelTime;
        long departureTime = requestedArrivalTime-travelTime;
        return TravelTimeGetter.callApi(api, alarm, departureTime).duration_in_traffic.value;
    }

    private static boolean isThirdCallNeeded(int initialTravelTime, int approximatedTravelTime, Alarm alarm) {
        long initTravelTime = (long)initialTravelTime;
        long travelTime = (long)approximatedTravelTime;
        long arrivalTime = alarm.getTimeOfArrivalInSeconds();
        long oldDepartureTime = arrivalTime-initTravelTime;
        long newDepartureTime = arrivalTime-travelTime;
        //Ak je rozdiel vacsi ako 2 a pol minuty tak to uz povazuje kod za velky rozdiel a este to upresni
        return newDepartureTime-oldDepartureTime > 60 || newDepartureTime-oldDepartureTime < -60;
    }

    private static int thirdCall(GoogleMapsApiInformationGetter api, Alarm alarm, int approximatedTravelTime) {
        long oldTravelTime = (long)approximatedTravelTime;
        long arrivalTime = alarm.getTimeOfArrivalInSeconds();
        long departureTime = arrivalTime-oldTravelTime;
        return TravelTimeGetter.callApi(api, alarm, departureTime).duration_in_traffic.value;
    }

    private static Leg callApi(GoogleMapsApiInformationGetter api, Alarm alarm, long departureTime){
        return api.getDirections(alarm, departureTime);
    }
}
