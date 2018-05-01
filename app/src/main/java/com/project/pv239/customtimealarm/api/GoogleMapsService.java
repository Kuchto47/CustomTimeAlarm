package com.project.pv239.customtimealarm.api;

import com.project.pv239.customtimealarm.api.model.directions.DirectionsResponse;
import com.project.pv239.customtimealarm.api.model.geocoding.GeocodingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsService {
    @GET("directions/json?alternatives=false&departure_time=now")
    Call<DirectionsResponse> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode,
            @Query("arrival_time") String time_of_arrival,
            @Query("traffic_model") String model,
            @Query("key") String api_key
    );

    @GET("geocode/json?")
    Call<GeocodingResponse> getLatLon(
            @Query("address") String place,
            @Query("key") String api_key
    );
}
