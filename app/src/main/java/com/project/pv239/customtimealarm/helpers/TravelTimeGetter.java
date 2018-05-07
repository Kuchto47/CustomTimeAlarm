package com.project.pv239.customtimealarm.helpers;

import com.project.pv239.customtimealarm.api.GoogleMapsApiInformationGetter;
import com.project.pv239.customtimealarm.api.model.directions.Leg;
import com.project.pv239.customtimealarm.database.entity.Alarm;

import java.util.Calendar;

public class TravelTimeGetter {
    private static final long UNDEFINED_DEPARTURE_TIME = -1;

    public static int getEstimatedTravelTimeForAlarm(Alarm alarm){
        GoogleMapsApiInformationGetter api = new GoogleMapsApiInformationGetter();
        int initialTravelTime = TravelTimeGetter.firstCall(api, alarm);
        int approximatedTravelTime = TravelTimeGetter.secondCall(api, alarm, initialTravelTime);
        boolean isThirdCallNeeded = TravelTimeGetter.isThirdCallNeeded(initialTravelTime, approximatedTravelTime, alarm);
        return isThirdCallNeeded ? TravelTimeGetter.thirdCall(api, alarm, approximatedTravelTime) : approximatedTravelTime;
    }

    public static long getTimeInSeconds(int hour, int minute) {
        boolean isTimeTomorrow = TravelTimeGetter.isTimeTomorrow(hour, minute);
        Calendar requestedTime = TravelTimeGetter.getActualTime();
        requestedTime.set(Calendar.HOUR_OF_DAY, hour);
        requestedTime.set(Calendar.MINUTE, minute);
        requestedTime.set(Calendar.SECOND, 0);
        long requestedTimeInSeconds = requestedTime.getTime().getTime()/1000;
        return requestedTimeInSeconds + (isTimeTomorrow ? 24*60*60 : 0);
    }

    private static int firstCall(GoogleMapsApiInformationGetter api, Alarm alarm) {
        return TravelTimeGetter.callApi(api, alarm, UNDEFINED_DEPARTURE_TIME).duration.value;
    }

    private static int secondCall(GoogleMapsApiInformationGetter api, Alarm alarm, int initialTravelTime) {
        long requestedArrivalTime = alarm.getTimeOfArrivalInSeconds();
        long morningRoutineInSeconds = (long)alarm.getMorningRoutine()*60;
        long travelTime = (long)initialTravelTime;
        long departureTime = requestedArrivalTime-travelTime-morningRoutineInSeconds;
        return TravelTimeGetter.callApi(api, alarm, departureTime).duration_in_traffic.value;
    }

    private static boolean isThirdCallNeeded(int initialTravelTime, int approximatedTravelTime, Alarm alarm) {
        long initTravelTime = (long)initialTravelTime;
        long travelTime = (long)approximatedTravelTime;
        long morningRoutineInSeconds = (long)alarm.getMorningRoutine()*60;
        long arrivalTime = alarm.getTimeOfArrivalInSeconds();
        long oldDepartureTime = arrivalTime-initTravelTime-morningRoutineInSeconds;
        long newDepartureTime = arrivalTime-travelTime-morningRoutineInSeconds;
        //Ak je rozdiel vacsi ako 2 a pol minuty tak to uz povazuje kod za velky rozdiel a este to upresni
        return newDepartureTime-oldDepartureTime > 150 || newDepartureTime-oldDepartureTime < -150;
    }

    private static int thirdCall(GoogleMapsApiInformationGetter api, Alarm alarm, int approximatedTravelTime) {
        long oldTravelTime = (long)approximatedTravelTime;
        long morningRoutine = (long)alarm.getMorningRoutine()*60;
        long arrivalTime = alarm.getTimeOfArrivalInSeconds();
        long departureTime = arrivalTime-oldTravelTime-morningRoutine;
        return TravelTimeGetter.callApi(api, alarm, departureTime).duration_in_traffic.value;
    }

    private static Leg callApi(GoogleMapsApiInformationGetter api, Alarm alarm, long departureTime){
        return api.getDirections(alarm, departureTime);
    }

    private static boolean isTimeTomorrow(int wakeUpHour, int wakeUpMinute) {
        Calendar actualTime = TravelTimeGetter.getActualTime();
        int actualHour = actualTime.get(Calendar.HOUR_OF_DAY);
        int actualMinute = actualTime.get(Calendar.MINUTE);
        return actualHour > wakeUpHour || (actualHour == wakeUpHour && actualMinute > wakeUpMinute);
    }

    private static Calendar getActualTime() {
        return Calendar.getInstance();
    }


}
