package com.project.pv239.customtimealarm.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.project.pv239.customtimealarm.activities.WakeUpActivity;

public class WakeUpService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("==SERVICE==","started");
        Intent i = new Intent(this, WakeUpActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
