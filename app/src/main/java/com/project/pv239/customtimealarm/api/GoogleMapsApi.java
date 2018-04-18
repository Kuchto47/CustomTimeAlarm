package com.project.pv239.customtimealarm.api;
import com.project.pv239.customtimealarm.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleMapsApi {

    private final static String GOOGLE_MAPS_API_ENDPOINT = "https://maps.googleapis.com/maps/api/directions/";
    private final GoogleMapsService mService;

    public GoogleMapsApi(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }

        final OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GOOGLE_MAPS_API_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mService = retrofit.create(GoogleMapsService.class);
    }

    public GoogleMapsService getService(){
        return mService;
    }

    //private int result;

    /*public Future<Integer> getTimeOfTravelInSeconds(Alarm alarm) {
        QueryProcessor queryProcessor = new QueryProcessor(alarm);
        String request = queryProcessor.getQuery();
        final int result;
        this.constructRequest(request);
        return 1234;
    }*/
}
