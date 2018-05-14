package com.project.pv239.customtimealarm.database.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;
import com.project.pv239.customtimealarm.helpers.converters.TrafficModelToString;
import com.project.pv239.customtimealarm.helpers.converters.TravelModeToString;
import com.project.pv239.customtimealarm.helpers.time.TimeHelper;
import com.project.pv239.customtimealarm.helpers.time.TimeToString;

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

    @ColumnInfo(name = "hour_arrival")
    private int hourOfArrival;

    @ColumnInfo(name = "minute_arrival")
    private int minuteOfHourOfArrival;

    @ColumnInfo(name = "hour_default")
    private int hourOfDefaultAlarm;

    @ColumnInfo(name = "minute_default")
    private int minuteOfHourOfDefaultAlarm;

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

    public Alarm() {
        this("", 0, 0, 0, 0, TrafficModel.BEST_GUESS, TravelMode.DRIVING, 0d, 0d, true, 30);
    }

    public Alarm(String destination, int hourArrival, int minuteArrival, int hourDefault, int minuteDefault, int trafficModel, int travelMode,
                 double latitude, double longitude, boolean on, int morningRoutine){
        this.destination = destination;
        this.hourOfArrival = hourArrival;
        this.minuteOfHourOfArrival = minuteArrival;
        this.trafficModel = trafficModel;
        this.travelMode = travelMode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.on = on;
        this.morningRoutine = morningRoutine;
        this.hourOfDefaultAlarm = hourDefault;
        this.minuteOfHourOfDefaultAlarm = minuteDefault;
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

    public int getHourOfArrival() {
        return hourOfArrival;
    }

    public void setHourOfArrival(int hour) {
        this.hourOfArrival = hour;
    }

    public int getMinuteOfHourOfArrival() {
        return minuteOfHourOfArrival;
    }

    public void setMinuteOfHourOfArrival(int minute) {
        this.minuteOfHourOfArrival = minute;
    }

    public String getTimeOfArrival(){
        return TimeToString.convert(hourOfArrival, minuteOfHourOfArrival);
    }

    public long getTimeOfArrivalInSeconds() {
        return TimeHelper.getTimeInSeconds(hourOfArrival, minuteOfHourOfArrival);
    }

    public int getHourOfDefaultAlarm() {
        return hourOfDefaultAlarm;
    }

    public void setHourOfDefaultAlarm(int hourOfDefaultAlarm) {
        this.hourOfDefaultAlarm = hourOfDefaultAlarm;
    }

    public int getMinuteOfHourOfDefaultAlarm() {
        return minuteOfHourOfDefaultAlarm;
    }

    public void setMinuteOfHourOfDefaultAlarm(int minuteOfHourOfDefaultAlarm) {
        this.minuteOfHourOfDefaultAlarm = minuteOfHourOfDefaultAlarm;
    }

    public String getTimeOfDefaultAlarm(){
        return TimeToString.convert(hourOfDefaultAlarm, minuteOfHourOfDefaultAlarm);
    }

    public long getTimeOfDefaultAlarmInSeconds() {
        return TimeHelper.getTimeInSeconds(hourOfDefaultAlarm, minuteOfHourOfDefaultAlarm);
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

    /**
     * @return number of seconds for morning routine
     */
    public int getMorningRoutine() {
        return morningRoutine;
    }

    /**
     * @param morningRoutine number of seconds for morning routine
     */
    public void setMorningRoutine(int morningRoutine) {
        this.morningRoutine = morningRoutine;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Alarm)) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equals(getDestination(), alarm.getDestination()) &&
                getHourOfArrival() == alarm.getHourOfArrival() &&
                getMinuteOfHourOfArrival() == alarm.getMinuteOfHourOfArrival() &&
                getTrafficModel() == alarm.getTrafficModel() &&
                getTravelMode() == alarm.getTravelMode() &&
                getLatitude() == alarm.getLatitude() &&
                getLongitude() == alarm.getLongitude() &&
                isOn() == alarm.isOn() &&
                getMorningRoutine() == alarm.getMorningRoutine();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDestination(), getHourOfArrival(), getMinuteOfHourOfArrival(), getTrafficModel(),
                getTravelMode(), getLatitude(), getLongitude(), isOn(), getMorningRoutine());
    }

    @Override
    public String toString() {
        return "Alarm with ID "+this.getId()+":\n"+
                "Destination: "+this.getDestination()+"\n"+
                "Time of arrival: "+this.getTimeOfArrival()+"\n"+
                "Time of default alarm: "+this.getTimeOfDefaultAlarm()+"\n"+
                "Traffic model: "+ TrafficModelToString.get(this.getTrafficModel())+"\n"+
                "Travel mode: "+ TravelModeToString.get(this.getTravelMode())+"\n"+
                "Is alarm active: "+this.isOn()+"\n"+
                "Morning routine time in seconds: "+this.getMorningRoutine()+".";
    }
}
