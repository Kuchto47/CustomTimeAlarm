package com.project.pv239.customtimealarm.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.project.pv239.customtimealarm.activities.WakeUpActivity;

public class WakeUpService extends IntentService {

    public WakeUpService() {
        super("WakeUpService");

        Log.d("==SERVICE==","created instance");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("==SERVICE==","started");
        Intent i = new Intent(this, WakeUpActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
