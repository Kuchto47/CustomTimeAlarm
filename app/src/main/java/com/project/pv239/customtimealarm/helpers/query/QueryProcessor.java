package com.project.pv239.customtimealarm.helpers.query;

import com.project.pv239.customtimealarm.api.GoogleMapsApiKeyGetter;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;
import com.project.pv239.customtimealarm.helpers.converters.TrafficModelToString;
import com.project.pv239.customtimealarm.helpers.converters.TravelModeToString;
import com.project.pv239.customtimealarm.helpers.places.PlacesProvider;
import com.project.pv239.customtimealarm.helpers.time.TimeHelper;
import com.project.pv239.customtimealarm.helpers.transport.TransportDetailProvider;

public class QueryProcessor {

    private Alarm alarm;

    private String queryTemplate = "https://maps.googleapis.com/maps/api/directions/json?origin={ORIGIN}&destination={DESTINATION}&mode={MODE}&alternatives=false&arrival_time={TIME_OF_ARRIVAL}&traffic_model={MODEL}&key={API_KEY}";
    private final String originHolder = "{ORIGIN}";
    private final String destinationHolder = "{DESTINATION}";
    private final String apiKeyHolder = "{API_KEY}";
    private final String travelModeHolder = "{MODE}";
    private final String arrivalTimeHolder = "{TIME_OF_ARRIVAL}";
    private final String trafficModelHolder = "{MODEL}";

    public QueryProcessor(Alarm alarm) {
        this.alarm = alarm;
    }

    public String getQuery() {
        return replacePlaceHoldersInQuery();
    }

    private String replacePlaceHoldersInQuery() {
        String query = queryTemplate;
        query = putApiKeyIntoQuery(query, GoogleMapsApiKeyGetter.getApiKey());
        query = putArrivalTimeIntoQuery(query, TimeHelper.getTimeOfNextArrivalInSecondsAsString(alarm.getTimeOfArrival()));
        query = putDestinationIntoQuery(query, PlacesProvider.getDestination(alarm.getDestination()));
        query = putOriginIntoQuery(query, PlacesProvider.getOrigin());
        query = putTrafficModelIntoQuery(query, alarm.getTrafficModel());
        query = putTravelModeIntoQuery(query, alarm.getTravelMode());
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

    private String putTravelModeIntoQuery(String query, int travelMode) {
        return query.replace(travelModeHolder, TravelModeToString.get(travelMode));
    }

    private String putArrivalTimeIntoQuery(String query, String arrivalTime) {
        return query.replace(arrivalTimeHolder, arrivalTime);
    }

    private String putTrafficModelIntoQuery(String query, int trafficModel) {
        return query.replace(trafficModelHolder, TrafficModelToString.get(trafficModel));
    }
}
