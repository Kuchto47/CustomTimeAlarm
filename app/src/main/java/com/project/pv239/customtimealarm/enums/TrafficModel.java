package com.project.pv239.customtimealarm.enums;

public enum TrafficModel {
    BEST_GUESS, OPTIMISTIC, PESSIMISTIC;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}