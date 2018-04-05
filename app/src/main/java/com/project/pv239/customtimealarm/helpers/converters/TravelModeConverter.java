package com.project.pv239.customtimealarm.helpers.converters;

import android.arch.persistence.room.TypeConverter;

import com.project.pv239.customtimealarm.enums.TravelMode;

public class TravelModeConverter {
    @TypeConverter
    public static TravelMode toTravelMode(int ordinal) {
        return TravelMode.values()[ordinal];
    }

    @TypeConverter
    public static int toOrdinal(TravelMode travelMode) {
        return travelMode.ordinal();
    }
}
