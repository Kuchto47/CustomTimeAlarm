package com.project.pv239.customtimealarm.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class Alarm implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "destination")
    private String destination;

    @ColumnInfo(name = "time_of_arrival")
    private String timeOfArrival;

    @ColumnInfo(name = "traffic_model")
    @TrafficModel
    private int trafficModel;

    @ColumnInfo(name = "travel_mode")
    @TravelMode
    private int travelMode;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "on")
    private boolean on;

    public Alarm(String destination, String timeOfArrival, int trafficModel, int travelMode,
                 double latitude, double longitude, boolean on){
        this.destination = destination;
        this.timeOfArrival = timeOfArrival;
        this.trafficModel = trafficModel;
        this.travelMode = travelMode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.on = on;
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

    public int getTrafficModel() {
        return trafficModel;
    }

    public void setTrafficModel(int trafficModel) {
        this.trafficModel = trafficModel;
    }

    public int getTravelMode() {
        return travelMode;
    }

    public void setTravelMode(int travelMode) {
        this.travelMode = travelMode;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Alarm)) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equals(getDestination(), alarm.getDestination()) &&
                Objects.equals(getTimeOfArrival(), alarm.getTimeOfArrival()) &&
                getTrafficModel() == alarm.getTrafficModel() &&
                getTravelMode() == alarm.getTravelMode() &&
                getLatitude() == alarm.getLatitude() &&
                getLongitude() == alarm.getLongitude() &&
                isOn() == alarm.isOn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDestination(), getTimeOfArrival(), getTrafficModel(),
                getTravelMode(), getLatitude(), getLongitude(), isOn());
    }

    @Override
    public String toString() {
        return this.getDestination()+" "+this.getTimeOfArrival()+" "+this.getTrafficModel()+" "+this.getTravelMode()+" "+this.isOn();
    }
}
