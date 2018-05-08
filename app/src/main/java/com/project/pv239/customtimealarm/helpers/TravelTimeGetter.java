package com.project.pv239.customtimealarm.helpers;

import android.util.Log;

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
                //boolean isThirdCallNeeded = TravelTimeGetter.isThirdCallNeeded(initialTravelTime, approximatedTravelTime, alarm);
                //return isThirdCallNeeded ? TravelTimeGetter.thirdCall(api, alarm, approximatedTravelTime) : approximatedTravelTime;
                return approximatedTravelTime;
            }
        }
        return initialTravelTime;
    }

    private static int firstCall(GoogleMapsApiInformationGetter api, Alarm alarm) {
        Log.d("==FIRSTCALL==", "values: "+UNDEFINED_DEPARTURE_TIME+" alarm: "+alarm.toString());
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

    private static boolean isThirdCallNeeded(int initialTravelTime, int approximatedTravelTime, Alarm alarm) {
        //TODO improve logic here so it amkes sense to use it.
        long initTravelTime = (long)initialTravelTime;
        long travelTime = (long)approximatedTravelTime;
        long arrivalTime = alarm.getTimeOfArrivalInSeconds();
        long oldDepartureTime = arrivalTime-initTravelTime;
        long newDepartureTime = arrivalTime-travelTime;
        //Ak je rozdiel vacsi ako minuta tak to uz povazuje kod za velky rozdiel a este to upresni
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
