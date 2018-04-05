package com.project.pv239.customtimealarm.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;
import com.project.pv239.customtimealarm.helpers.converters.TrafficModelConverter;
import com.project.pv239.customtimealarm.helpers.converters.TravelModeConverter;

import java.util.Objects;

@Entity
public class Alarm {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "destination")
    private String destination;

    @ColumnInfo(name = "time_of_arrival")
    private String timeOfArrival;

    @ColumnInfo(name = "traffic_model")
    @TypeConverters(TrafficModelConverter.class)
    private TrafficModel trafficModel;

    @ColumnInfo(name = "travel_mode")
    @TypeConverters(TravelModeConverter.class)
    private TravelMode travelMode;

    public Alarm(String destination, String timeOfArrival, TrafficModel trafficModel, TravelMode travelMode){
        this.destination = destination;
        this.timeOfArrival = timeOfArrival;
        this.trafficModel = trafficModel;
        this.travelMode = travelMode;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTimeOfArrival() {
        return timeOfArrival;
    }

    public void setTimeOfArrival(String timeOfArrival) {
        this.timeOfArrival = timeOfArrival;
    }

    public TrafficModel getTrafficModel() {
        return trafficModel;
    }

    public void setTrafficModel(TrafficModel trafficModel) {
        this.trafficModel = trafficModel;
    }

    public TravelMode getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(TravelMode travelMode) {
        this.travelMode = travelMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Alarm)) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equals(getDestination(), alarm.getDestination()) &&
                Objects.equals(getTimeOfArrival(), alarm.getTimeOfArrival()) &&
                getTrafficModel() == alarm.getTrafficModel() &&
                getTravelMode() == alarm.getTravelMode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDestination(), getTimeOfArrival(), getTrafficModel(), getTravelMode());
    }
}
