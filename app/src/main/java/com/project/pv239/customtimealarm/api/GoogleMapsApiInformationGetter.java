package com.project.pv239.customtimealarm.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.project.pv239.customtimealarm.api.model.directions.DirectionsResponse;
import com.project.pv239.customtimealarm.api.model.directions.Leg;
import com.project.pv239.customtimealarm.api.model.geocoding.GeocodingResponse;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.helpers.converters.TrafficModelToString;
import com.project.pv239.customtimealarm.helpers.converters.TravelModeToString;
import com.project.pv239.customtimealarm.helpers.Tuple;
import com.project.pv239.customtimealarm.helpers.PlacesProvider;

import retrofit2.Call;
import retrofit2.Response;

public class GoogleMapsApiInformationGetter {
    private GoogleMapsApi googleMapsApi;

    public GoogleMapsApiInformationGetter(){
        this.googleMapsApi = new GoogleMapsApi();
    }

    public Leg getDirections(@NonNull Alarm alarm, long timeOfDeparture){
        final Call<DirectionsResponse> responseCall = googleMapsApi.getService().getDirections(
                PlacesProvider.getOrigin(),
                alarm.getDestination(),
                TravelModeToString.get(alarm.getTravelMode()),
                timeOfDeparture != -1 ? Long.toString(timeOfDeparture) : "now",
                String.valueOf(alarm.getTimeOfArrivalInSeconds()),
                TrafficModelToString.get(alarm.getTrafficModel()),
                GoogleMapsApiKeyGetter.getApiKey()
        );
        try{
            return new GetTimeTask(responseCall).execute().get();
        } catch (Exception e){
            return null;
        }
    }

    public Tuple<Double> getLatLonOfPlaceSync(String place){
        final Call<GeocodingResponse> responseCall = googleMapsApi.getService().getLatLon(
                place,
                GoogleMapsApiKeyGetter.getLatLonApiKey()
        );
        try{
            Response<GeocodingResponse> gResponse = responseCall.execute();
            GeocodingResponse responseBody = gResponse.body();
            Double lat = responseBody.results[0].geometry.location.lat;
            Double lng = responseBody.results[0].geometry.location.lng;
            return new Tuple<>(lat, lng);
        } catch (Exception e) {
            return null;
        }
    }

    private static class GetTimeTask extends AsyncTask<Void, Void, Leg> {
        private Call<DirectionsResponse> responseCall;

        private GetTimeTask(Call<DirectionsResponse> responseCall){
            this.responseCall = responseCall;
        }

        @Override
        protected Leg doInBackground(Void... voids) {
            try{
                Response<DirectionsResponse> gResponse = responseCall.execute();
                DirectionsResponse responseBody = gResponse.body();
                return responseBody.routes[0].legs[0];
            } catch (Exception e) {
                return null;
            }
        }
    }
}
