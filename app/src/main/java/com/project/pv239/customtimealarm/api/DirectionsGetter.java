package com.project.pv239.customtimealarm.api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.project.pv239.customtimealarm.api.model.GoogleMapsApiResponse;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.helpers.converters.TrafficModelToString;
import com.project.pv239.customtimealarm.helpers.converters.TravelModeToString;
import com.project.pv239.customtimealarm.helpers.places.PlacesProvider;

import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Response;

public class DirectionsGetter {
    private GoogleMapsApi googleMapsApi;

    public DirectionsGetter(){
        this.googleMapsApi = new GoogleMapsApi();
    }

    public int getTimeToDestinationInSeconds(@NonNull Alarm alarm){
        final Call<GoogleMapsApiResponse> responseCall = googleMapsApi.getService().getDirections(
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

    private static class GetTimeTask extends AsyncTask<Void, Void, Integer> {
        private WeakReference<DirectionsGetter> mContext;
        private Call<GoogleMapsApiResponse> responseCall;

        private GetTimeTask(DirectionsGetter context, Call<GoogleMapsApiResponse> responseCall){
            this.mContext = new WeakReference<>(context);
            this.responseCall = responseCall;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            try{
                Response<GoogleMapsApiResponse> gResponse = responseCall.execute();
                GoogleMapsApiResponse responseBody = gResponse.body();
                return responseBody.routes[0].legs[0].duration_in_traffic.value;
            } catch (Exception e) {
                return -1;
            }
        }
    }
}
