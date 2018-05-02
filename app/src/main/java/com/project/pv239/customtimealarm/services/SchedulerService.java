package com.project.pv239.customtimealarm.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SchedulerService extends IntentService {


    public SchedulerService() {
        super("SchedulerService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onCreate() {
        Log.d("==SERVICE==","created");
    }
}
