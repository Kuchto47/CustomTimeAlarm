package com.project.pv239.customtimealarm.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.project.pv239.customtimealarm.App;

public class GoogleMapsApiKeyGetter {
    private static String EMPTY_STRING = "";
    private static String gMapsKeyIdentifier = "com.google.android.geo.API_KEY";
    private static String gMapsLatLonKeyIdentifier = "lat.lon.API_KEY";

    public static String getApiKey() {
        return GoogleMapsApiKeyGetter.getApiKey(App.getInstance(), gMapsKeyIdentifier);
    }

    public static String getLatLonApiKey() {
        return GoogleMapsApiKeyGetter.getApiKey(App.getInstance(), gMapsLatLonKeyIdentifier);
    }

    private static String getApiKey(Context context, String keyIdentifier) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(keyIdentifier);
        } catch(PackageManager.NameNotFoundException e) {
            Log.e("GoogleMapsApiKeyGetter", "Failed to load API KEY from meta-data");
            return EMPTY_STRING;
        }
    }
}
