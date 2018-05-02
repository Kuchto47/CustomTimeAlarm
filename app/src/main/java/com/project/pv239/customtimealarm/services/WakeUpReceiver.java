package com.project.pv239.customtimealarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WakeUpReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("==SERVICE==", "received");
        context.startService(new Intent(context,WakeUpService.class));
    }
}
