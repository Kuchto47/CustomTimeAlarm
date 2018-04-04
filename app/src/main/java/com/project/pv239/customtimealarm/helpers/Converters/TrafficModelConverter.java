package com.project.pv239.customtimealarm.helpers.Converters;

import android.arch.persistence.room.TypeConverter;

import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;

public class TrafficModelConverter {
    @TypeConverter
    public static TrafficModel toTrafficModel(int ordinal) {
        return TrafficModel.values()[ordinal];
    }

    @TypeConverter
    public static int toOrdinal(TrafficModel trafficModel) {
        return trafficModel.ordinal();
    }
}
