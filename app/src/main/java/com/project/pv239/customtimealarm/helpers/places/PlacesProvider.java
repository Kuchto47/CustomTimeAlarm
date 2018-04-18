package com.project.pv239.customtimealarm.helpers.places;

import android.text.TextUtils;

public class PlacesProvider {
    public static String getOrigin() {
        return PlacesProvider.getQueryPlaceString("tehelna 5 dunajska luzna");
    }

    public static String getDestination(String dest) {
        return PlacesProvider.getQueryPlaceString(dest);
    }

    private static String getQueryPlaceString(String place) {
        String[] separatedPlace = place.split(" ");
        return TextUtils.join("+", separatedPlace);
    }
}
