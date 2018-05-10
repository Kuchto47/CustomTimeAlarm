package com.project.pv239.customtimealarm.helpers.places;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.project.pv239.customtimealarm.App;
import com.project.pv239.customtimealarm.helpers.PermissionChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlacesProvider {
    public static String getOrigin() {
        try{
            return new getLocationOfDeviceTask().execute().get();
            } catch(Exception e){
            Log.d("==EX==", "Exception occurred.");
            return "FAILURE";
        }
    }

    private static class getLocationOfDeviceTask extends AsyncTask<Void, Void, String> {
        private LocationManager mLocationManager;

        private Location getLastKnownLocation() {
            mLocationManager = (LocationManager)App.getInstance().getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);
            Location bestLocation = null;
            try {
                for (String provider : providers) {
                    Location l = mLocationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                    }
                }
            } catch (SecurityException exc) {
                Log.d("SecEx", "Security exception thrown while getting last known location.");
            }
            return bestLocation;

        }

        @Override
        protected String doInBackground(Void... voids) {
            Geocoder geocoder = new Geocoder(App.getInstance(), Locale.getDefault());
            if(PermissionChecker.canAccessLocation()){
                Location location = this.getLastKnownLocation();
                try{
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addresses.size() != 0){
                        Address address = addresses.get(0);
                        ArrayList<String> addressFragments = new ArrayList<>();
                        for(int i = 0; i <= address.getMaxAddressLineIndex(); i++){
                            addressFragments.add(address.getAddressLine(i));
                        }
                        return this.joinAddress(addressFragments);
                    }
                } catch (IOException exc) {
                    Log.d("IOEx", "Exception thrown while getting addresses. Message: "+exc.getMessage());
                }
                return location.getLatitude() +", " + location.getLongitude();
            }
            return "FAILURE_INSIDE";
        }

        private String joinAddress(ArrayList<String> addressLines) {
            return TextUtils.join(System.getProperty("line.separator"), addressLines);
        }
    }
}
