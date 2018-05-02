package com.project.pv239.customtimealarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PackageReplaceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,SchedulerService.class));
        Log.d("==SERVICE==", "package_replace");
    }
}
