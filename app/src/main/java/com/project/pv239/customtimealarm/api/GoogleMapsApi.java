package com.project.pv239.customtimealarm.api;

import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.helpers.query.QueryProcessor;

public class GoogleMapsApi {
    public int getTimeOfTravelInSeconds(Alarm alarm) {
        QueryProcessor queryProcessor = new QueryProcessor(alarm);
        String query = queryProcessor.getQuery();
        //TODO process JSON response and return seconds as int
        return 1234;
    }
}
