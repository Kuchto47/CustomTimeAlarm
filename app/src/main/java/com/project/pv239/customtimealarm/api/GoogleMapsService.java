package com.project.pv239.customtimealarm.api;

import com.project.pv239.customtimealarm.api.model.GoogleMapsApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GoogleMapsService {
    @GET("json?alternatives=false&departure_time=now")
    Call<GoogleMapsApiResponse> getDirections(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("mode") String mode,
            @Query("arrival_time") String time_of_arrival,
            @Query("traffic_model") String model,
            @Query("key") String api_key
    );
}
