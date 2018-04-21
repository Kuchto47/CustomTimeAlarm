package com.project.pv239.customtimealarm.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.project.pv239.customtimealarm.api.model.directions.DirectionsResponse;
import com.project.pv239.customtimealarm.api.model.geocoding.GeocodingResponse;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.helpers.converters.TrafficModelToString;
import com.project.pv239.customtimealarm.helpers.converters.TravelModeToString;
import com.project.pv239.customtimealarm.helpers.objects.Tuple;
import com.project.pv239.customtimealarm.helpers.places.PlacesProvider;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;

public class GoogleMapsApiInformationGetter {
    private GoogleMapsApi googleMapsApi;

    public GoogleMapsApiInformationGetter(){
        this.googleMapsApi = new GoogleMapsApi();
    }

    public int getTimeToDestinationInSeconds(@NonNull Alarm alarm){
        final Call<DirectionsResponse> responseCall = googleMapsApi.getService().getDirections(
                PlacesProvider.getOrigin(),
                alarm.getDestination(),
                TravelModeToString.get(alarm.getTravelMode()),
                alarm.getTimeOfArrival(),
                TrafficModelToString.get(alarm.getTrafficModel()),
                GoogleMapsApiKeyGetter.getApiKey()
        );

        try{
            return new GetTimeTask(this, responseCall).execute().get();
        } catch (Exception e){
            return -1;
            //TODO maybe throw some error?...
        }
    }

    public Tuple<Double> getLanLonOfPlace(String place){
        final Call<GeocodingResponse> responseCall = googleMapsApi.getService().getLatLon(
                place,
                GoogleMapsApiKeyGetter.getLatLonApiKey()
        );

        try{
            return new GetLatLonTask(this, responseCall).execute().get();
        } catch (Exception e){
            return null;
            //TODO maybe throw some error?...
        }
    }

    private static class GetTimeTask extends AsyncTask<Void, Void, Integer> {
        private WeakReference<GoogleMapsApiInformationGetter> mContext;
        private Call<DirectionsResponse> responseCall;

        private GetTimeTask(GoogleMapsApiInformationGetter context, Call<DirectionsResponse> responseCall){
            this.mContext = new WeakReference<>(context);
            this.responseCall = responseCall;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try{
                Response<DirectionsResponse> gResponse = responseCall.execute();
                DirectionsResponse responseBody = gResponse.body();
                return responseBody.routes[0].legs[0].duration_in_traffic.value;
            } catch (Exception e) {
                return -1;
            }
        }
    }

    private static class GetLatLonTask extends AsyncTask<Void, Void, Tuple<Double>> {
        private WeakReference<GoogleMapsApiInformationGetter> mContext;
        private Call<GeocodingResponse> responseCall;

        private GetLatLonTask(GoogleMapsApiInformationGetter context, Call<GeocodingResponse> responseCall){
            this.mContext = new WeakReference<>(context);
            this.responseCall = responseCall;
        }

        @Override
        protected Tuple<Double> doInBackground(Void... voids) {
            try{
                Response<GeocodingResponse> gResponse = responseCall.execute();
                GeocodingResponse responseBody = gResponse.body();
                Double lat = responseBody.results[0].geometry.location.lat;
                Double lng = responseBody.results[0].geometry.location.lng;
                return new Tuple<>(lat, lng);
            } catch (Exception e) {
                return null;//TODO we might want to avoid returning nulls
            }
        }
    }
}
