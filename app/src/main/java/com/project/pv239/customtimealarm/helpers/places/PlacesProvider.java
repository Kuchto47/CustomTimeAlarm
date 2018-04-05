package com.project.pv239.customtimealarm.helpers.places;

public class PlacesProvider {
    public static String getOrigin() {
        return PlacesQueryBuilder.getQueryPlaceString("origin");
    }

    public static String getDestination() {
        return PlacesQueryBuilder.getQueryPlaceString("destination");
    }
}
