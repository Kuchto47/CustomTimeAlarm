package com.project.pv239.customtimealarm.helpers.Query;

import com.project.pv239.customtimealarm.api.GoogleMapsAPIKeyGetter;
import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;
import com.project.pv239.customtimealarm.helpers.Places.PlacesProvider;
import com.project.pv239.customtimealarm.helpers.Time.TimeHelper;
import com.project.pv239.customtimealarm.helpers.Transport.TransportDetailProvider;

public class QueryProcessor {
    private String queryTemplate = "https://maps.googleapis.com/maps/api/directions/json?origin={ORIGIN}&destination={DESTINATION}&mode={MODE}&alternatives=false&arrival_time={TIME_OF_ARRIVAL}&traffic_model={MODEL}&key={API_KEY}";
    private final String originHolder = "{ORIGIN}";
    private final String destinationHolder = "{DESTINATION}";
    private final String apiKeyHolder = "{API_KEY}";
    private final String travelModeHolder = "{MODE}";
    private final String arrivalTimeHolder = "{TIME_OF_ARRIVAL}";
    private final String trafficModelHolder = "{MODEL}";

    public String getQuery() {
        return replacePlaceHoldersInQuery();
    }

    private String replacePlaceHoldersInQuery() {
        String query = queryTemplate;
        query = putApiKeyIntoQuery(query, GoogleMapsAPIKeyGetter.getApiKey());
        query = putArrivalTimeIntoQuery(query, TimeHelper.getTimeOfNextArrivalInSecondsAsString());
        query = putDestinationIntoQuery(query, PlacesProvider.getDestination());
        query = putOriginIntoQuery(query, PlacesProvider.getOrigin());
        query = putTrafficModelIntoQuery(query, TransportDetailProvider.getTrafficModel());
        query = putTravelModeIntoQuery(query, TransportDetailProvider.getTravelMode());
        return query;
    }

    private String putOriginIntoQuery(String query, String origin) {
        return query.replace(originHolder, origin);
    }

    private String putDestinationIntoQuery(String query, String destination) {
        return query.replace(destinationHolder, destination);
    }

    private String putApiKeyIntoQuery(String query, String apiKey) {
        return query.replace(apiKeyHolder, apiKey);
    }

    private String putTravelModeIntoQuery(String query, TravelMode travelMode) {
        return query.replace(travelModeHolder, travelMode.toString());
    }

    private String putArrivalTimeIntoQuery(String query, String arrivalTime) {
        return query.replace(arrivalTimeHolder, arrivalTime);
    }

    private String putTrafficModelIntoQuery(String query, TrafficModel trafficModel) {
        return query.replace(trafficModelHolder, trafficModel.toString());
    }
}
