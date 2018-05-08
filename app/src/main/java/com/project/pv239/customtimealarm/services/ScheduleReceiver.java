package com.project.pv239.customtimealarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScheduleReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.SCHEDULE_ALL);
            SchedulerService.enqueueWork(context,SchedulerService.class,SchedulerService.JOB_ID,i);
            Log.d("==SERVICE==","boot and pcg replace");
        }
        else {//alarm manager called us
            Log.d("==SERVICE==", "alarm manager tick " + intent.getIntExtra(SchedulerService.INTENT_TYPE_KEY, -1));
            SchedulerService.enqueueWork(context,SchedulerService.class,SchedulerService.JOB_ID,intent);
        }
    }
}
