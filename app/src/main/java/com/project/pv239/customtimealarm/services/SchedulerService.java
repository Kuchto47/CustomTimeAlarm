package com.project.pv239.customtimealarm.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
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
    public static final int BEDTIME_NOTIFICATION = 6;
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
            case SCHEDULED:
                break;
            case WAKE_UP:
                Intent i = new Intent(this, WakeUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case BEDTIME_NOTIFICATION:
                createNotification();
                break;
            case ALARM_CREATED:
                break;
            case ALARM_CANCELLED:
                break;
            case ALARM_CHANGED:
                break;
        }
    }

    public void createNotification(){
        //TODO

//                NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(getApplicationContext())
//                            .setContentTitle("My notification")
//                            .setContentText("Hello World!");
//                NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.notify(1, mBuilder.build());
    }

    public void schedlueAlarmList(List<Alarm> list){
        for (Alarm a : list) {
            if (a.isOn()) {
                Log.d("==SERVICE==", "alarm scheduled " + a.getId());
                long alarmTime = AlarmTimeGetter.getAlarmTimeInMilliSeconds(a);
                long timeToAlarm = alarmTime - System.currentTimeMillis();
                if (timeToAlarm > HOUR)
                    setAlarmManager(a.getId(), SCHEDULED, alarmTime - HOUR);
                if (timeToAlarm <= HOUR && timeToAlarm > TEN_MINUTES)
                    setAlarmManager(a.getId(), SCHEDULED, alarmTime - TEN_MINUTES);
                if (timeToAlarm <= TEN_MINUTES) {
                    setAlarmManager(a.getId(), WAKE_UP, alarmTime);
                }
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                int bedtimeMilis = prefs.getInt("sleep_time", 480)*60*1000;
                if (timeToAlarm > bedtimeMilis){
                    setAlarmManager(a.getId(), BEDTIME_NOTIFICATION, alarmTime - bedtimeMilis);
                }
            }
        }
    }

    private void setAlarmManager(int alarmId, int intentFlag, long mills){
        Intent intent = new Intent(this, ScheduleReceiver.class);
        intent.putExtra(INTENT_KEY, intentFlag);
        PendingIntent pIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);
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
