package com.project.pv239.customtimealarm.api;

import com.project.pv239.customtimealarm.helpers.Query.QueryProcessor;

public class GoogleMapsApi {
    public int getTimeOfTravelInSeconds() {
        QueryProcessor queryProcessor = new QueryProcessor();
        String query = queryProcessor.getQuery();
        //TODO process JSON response and return seconds as int
        return 1234;
    }
}
