package com.project.pv239.customtimealarm.helpers.Query;

import com.project.pv239.customtimealarm.api.GoogleMapsAPIKeyGetter;
import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;
import com.project.pv239.customtimealarm.helpers.Places.PlacesProvider;

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
        putApiKeyIntoQuery(GoogleMapsAPIKeyGetter.getApiKey());
        putArrivalTimeIntoQuery("123345");
        putDestinationIntoQuery(PlacesProvider.getDestination());
        putOriginIntoQuery(PlacesProvider.getOrigin());
        putTrafficModelIntoQuery(TrafficModel.BEST_GUESS);
        putTravelModeIntoQuery(TravelMode.DRIVING);
        return queryTemplate;
    }

    private void putOriginIntoQuery(String origin) {
        queryTemplate = queryTemplate.replace(originHolder, origin);
    }

    private void putDestinationIntoQuery(String destination) {
        queryTemplate = queryTemplate.replace(destinationHolder, destination);
    }

    private void putApiKeyIntoQuery(String apiKey) {
        queryTemplate = queryTemplate.replace(apiKeyHolder, apiKey);
    }

    private void putTravelModeIntoQuery(TravelMode travelMode) {
        queryTemplate = queryTemplate.replace(travelModeHolder, travelMode.toString());
    }

    //TODO: assure time is passed as string in SECONDS SINCE MIDNIGHT JANUARY 1 1970 UTC
    private void putArrivalTimeIntoQuery(String arrivalTime) {
        queryTemplate = queryTemplate.replace(arrivalTimeHolder, arrivalTime);
    }

    private void putTrafficModelIntoQuery(TrafficModel trafficModel) {
        queryTemplate = queryTemplate.replace(trafficModelHolder, trafficModel.toString());
    }
}
