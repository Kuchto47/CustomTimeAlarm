package com.project.pv239.customtimealarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.putExtra("type", SchedulerService.SCHEDULE_ALL);
            SchedulerService.enqueueWork(context,SchedulerService.class,SchedulerService.JOB_ID,i);
        }
        Log.d("==SERVICE==", "boot completed");
    }
}
