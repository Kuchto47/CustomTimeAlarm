package com.project.pv239.customtimealarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.project.pv239.customtimealarm.activities.WakeUpActivity;

public class ScheduleReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()) ||
                Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()) ||
                Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.SCHEDULE_ALL);
            SchedulerService.enqueueWork(context,SchedulerService.class,SchedulerService.JOB_ID,i);
            Log.d("==SERVICE==","boot and pcg replace + timezone change");
        }
        else {//alarm manager called us
            Log.d("==SERVICE==", "alarm manager tick " + intent.getIntExtra(SchedulerService.INTENT_TYPE_KEY, -1));
            if (intent.getIntExtra(SchedulerService.INTENT_TYPE_KEY, -1) == SchedulerService.WAKE_UP) {
                Intent i = new Intent(context, WakeUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(SchedulerService.INTENT_ALARM_ID_KEY, intent.getIntExtra(SchedulerService.INTENT_ALARM_ID_KEY, -1));
                context.startActivity(i);
                WakeUpActivity.acquireLock(context);
            } else {
                SchedulerService.enqueueWork(context, SchedulerService.class, SchedulerService.JOB_ID, intent);
            }
        }
    }
}
