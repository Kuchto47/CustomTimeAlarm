package com.project.pv239.customtimealarm.helpers.Places;

public class PlacesProvider {
    public static String getOrigin() {
        return PlacesQueryBuilder.getQueryPlaceString("origin");
    }

    public static String getDestination() {
        return PlacesQueryBuilder.getQueryPlaceString("destination");
    }
}
