package com.project.pv239.customtimealarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WakeUpReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,WakeUpService.class);
        context.startService(i);
        Log.d("==SERVICE==", "reschedule");
    }
}
