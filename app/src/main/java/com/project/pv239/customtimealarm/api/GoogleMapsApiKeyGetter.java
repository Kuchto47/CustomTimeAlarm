package com.project.pv239.customtimealarm.api;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.project.pv239.customtimealarm.Activities.MainActivity;

import static android.content.ContentValues.TAG;

public class GoogleMapsApiKeyGetter {
    private static final String EMPTY_STRING = "";
    private static final String key = "com.google.android.geo.API_KEY";

    public static String getApiKey(){
        return getMetaDataApiKey(MainActivity.context, key);
    }

    private static String getMetaDataApiKey(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(key);
        } catch(PackageManager.NameNotFoundException e) {
            Log.e(TAG, "You forgot to configure com.google.android.geo.API_KEY in your AndroidManifest.xml file.");
        }
        return EMPTY_STRING;
    }
}
