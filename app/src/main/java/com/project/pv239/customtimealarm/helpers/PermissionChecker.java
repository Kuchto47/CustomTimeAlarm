package com.project.pv239.customtimealarm.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.project.pv239.customtimealarm.App;

public class PermissionChecker {
    public static final int LOCATION_REQUEST_CODE = 1;

    public static void getLocationPermissionIfNotGranted(Activity activity) {
        if (!PermissionChecker.canAccessLocation()) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    public static boolean canAccessLocation() {
        return ContextCompat.checkSelfPermission(App.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
