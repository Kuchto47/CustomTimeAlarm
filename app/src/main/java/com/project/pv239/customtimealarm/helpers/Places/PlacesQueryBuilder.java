package com.project.pv239.customtimealarm.helpers.Places;

import android.text.TextUtils;

public class PlacesQueryBuilder {
    public static String getQueryPlaceString(String place) {
        String[] separatedPlace = place.split(" ");
        return TextUtils.join("+", separatedPlace);
    }
}
