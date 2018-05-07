package com.project.pv239.customtimealarm.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.project.pv239.customtimealarm.activities.WakeUpActivity;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;
import com.project.pv239.customtimealarm.helpers.AlarmTimeGetter;

import java.lang.ref.WeakReference;
import java.util.List;

public class SchedulerService extends JobIntentService {

    public static final int SCHEDULE_ALL = 0;
    public static final int ALARM_CANCELLED = 1;
    public static final int ALARM_CREATED = 2;
    public static final int ALARM_CHANGED = 3;
    public static final int SCHEDULED = 4;
    public static final int WAKE_UP = 5;
    public static final int JOB_ID = 1000;
    public static final String INTENT_KEY= "type";
    private static final int TEN_MINUTES = 1000*60*10;
    private static final int HOUR = 1000*60*60;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("==SERVICE==","work");
        switch (intent.getExtras().getInt(INTENT_KEY)){
            case SCHEDULE_ALL:
                new GetAlarmsTask(new WeakReference<>(this)).execute();
                break;
            case WAKE_UP:
                Intent i = new Intent(this, WakeUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
        }
    }

    public void schedlueAlarmList(List<Alarm> list){
        for (Alarm a : list) {
            if (a.isOn()) {
                Log.d("==SERVICE==", "alarm scheduled " +a.getId() );
                long alarmTime = AlarmTimeGetter.getAlarmTimeInMilliSeconds(a);
                long timeToAlarm = alarmTime - System.currentTimeMillis();
                Intent intent = new Intent(this, ScheduleReceiver.class);
                intent.putExtra(INTENT_KEY, SCHEDULED);
                PendingIntent pIntent = PendingIntent.getBroadcast(this, a.getId(), intent, 0);
                if (timeToAlarm > HOUR)
                    setAlarmManager(pIntent, alarmTime - HOUR);
                if (timeToAlarm <= HOUR && timeToAlarm > TEN_MINUTES)
                    setAlarmManager(pIntent, alarmTime - 1000*60*10);
                if (timeToAlarm <= TEN_MINUTES)
                    intent.putExtra(INTENT_KEY, WAKE_UP);
                    pIntent = PendingIntent.getBroadcast(this, a.getId(), intent, 0);
                    setAlarmManager(pIntent, alarmTime);
            }
        }
    }

    private void setAlarmManager(PendingIntent pIntent, long mills){
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mills, pIntent);
        }
        else {
            am.setExact(AlarmManager.RTC_WAKEUP, mills, pIntent);
        }
    }


    static class GetAlarmsTask extends AsyncTask<Void,Void,List<Alarm>>{

        WeakReference<SchedulerService> mScheduler;
        GetAlarmsTask(WeakReference<SchedulerService> scheduler){
            mScheduler = scheduler;
        }

        @Override
        protected List<Alarm> doInBackground(Void... voids) {
            return new AlarmFacade().getAllAlarms();
        }

        @Override
        protected void onPostExecute(List<Alarm> alarms) {
            mScheduler.get().schedlueAlarmList(alarms);
        }
    }
}
