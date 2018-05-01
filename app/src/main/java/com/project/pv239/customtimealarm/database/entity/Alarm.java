package com.project.pv239.customtimealarm.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

@Entity
public class Alarm implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "destination")
    private String destination;

    @ColumnInfo(name = "hour")
    private int hour;
    @ColumnInfo(name = "minute")
    private int minute;

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

    @ColumnInfo(name = "morning_routine")
    private int morningRoutine;

    public Alarm(String destination, int hour, int minute, int trafficModel, int travelMode,
                 double latitude, double longitude, boolean on, int morningRoutine){
        this.destination = destination;
        this.hour = hour;
        this.minute = minute;
        this.trafficModel = trafficModel;
        this.travelMode = travelMode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.on = on;
        this.morningRoutine = morningRoutine;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTimeOfArrival(){
        return String.format(Locale.getDefault(),"%02d:%02d", hour, minute);
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

    public int getMorningRoutine() {
        return morningRoutine;
    }

    public void setMorningRoutine(int morningRoutine) {
        this.morningRoutine = morningRoutine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Alarm)) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equals(getDestination(), alarm.getDestination()) &&
                getHour() == alarm.getHour() &&
                getMinute() == alarm.getMinute() &&
                getTrafficModel() == alarm.getTrafficModel() &&
                getTravelMode() == alarm.getTravelMode() &&
                getLatitude() == alarm.getLatitude() &&
                getLongitude() == alarm.getLongitude() &&
                isOn() == alarm.isOn() &&
                getMorningRoutine() == alarm.getMorningRoutine();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDestination(), getHour(), getMinute(), getTrafficModel(),
                getTravelMode(), getLatitude(), getLongitude(), isOn(), getMorningRoutine());
    }

    @Override
    public String toString() {
        return this.getDestination()+" "+this.getHour()+ ":"+ this.getMinute()+" "+this.getTrafficModel()+" "+this.getTravelMode()+" "+this.isOn() + " " +this.getMorningRoutine();
    }
}
