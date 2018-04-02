package com.project.pv239.customtimealarm.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.project.pv239.customtimealarm.Activities.MainActivity;

public class GoogleMapsApiKeyGetter {
    private static String EMPTY_STRING = "";
    private static String key = "com.google.android.geo.API_KEY";

    public static String getApiKey() {
        return GoogleMapsApiKeyGetter.getMetaDataApiKey(MainActivity.context);
    }

    private static String getMetaDataApiKey(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(key);
        } catch(PackageManager.NameNotFoundException e) {
            Log.e("GoogleMapsApiKeyGetter", "Failed to load API KEY from meta-data");
            return EMPTY_STRING;
        }
    }
}
