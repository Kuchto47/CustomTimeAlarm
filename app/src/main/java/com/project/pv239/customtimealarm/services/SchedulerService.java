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
    public static final String INTENT_TYPE_KEY = "type";
    public static final String INTENT_SERIALIZABLE_KEY = "alarm_object";
    public static final String INTENT_ALARM_ID_KEY = "alarm_id";
    private static final int TEN_MINUTES = 1000*10;//60*10;
    private static final int HOUR = 1000*60;//*60;
    private static final int NOTIFICATION_ID = 100000;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("==SERVICE==","work" + intent.getIntExtra(INTENT_TYPE_KEY, -1));
        switch (intent.getIntExtra(INTENT_TYPE_KEY, -1)){
            case SCHEDULE_ALL:
                new GetAlarmsTask(new WeakReference<>(this)).execute();
                break;
            case SCHEDULED:
                scheduleAlarm((Alarm)intent.getSerializableExtra(INTENT_SERIALIZABLE_KEY));
                break;
            case WAKE_UP:
                Intent i = new Intent(this, WakeUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(INTENT_SERIALIZABLE_KEY, intent.getSerializableExtra(INTENT_SERIALIZABLE_KEY));
                startActivity(i);
                break;
            case BEDTIME_NOTIFICATION:
                createNotification();
                break;
            case ALARM_CREATED:
                scheduleAlarm((Alarm)intent.getSerializableExtra(INTENT_SERIALIZABLE_KEY));
                break;
            case ALARM_CANCELLED:
                cancelAlarm(intent.getIntExtra(INTENT_ALARM_ID_KEY, -1));
                break;
            case ALARM_CHANGED:
                scheduleAlarm((Alarm)intent.getSerializableExtra(INTENT_SERIALIZABLE_KEY));
                break;
        }
    }

    public void createNotification() {
        /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shouldShowNotification = pref.getBoolean("bedtime", false);
        if(shouldShowNotification){
            NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("My notification")
                    .setContentText("Hello World!");
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }*/
    }

    public void cancelAlarm(int id){
        Log.d("==SERVICE==", "alarm cancelled " + id);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), id, myIntent, 0);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), id + NOTIFICATION_ID, myIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void scheduleAlarm(Alarm alarm){
        if (alarm.isOn()) {
            Log.d("==SERVICE==", "alarm scheduling " + alarm.toString());
            long alarmTime = AlarmTimeGetter.getAlarmTimeInMilliSeconds(alarm);
            Log.d("==SERVICE==", "TIMES "+ System.currentTimeMillis() + " " + alarmTime);
            long timeToAlarm = alarmTime - System.currentTimeMillis();
            Intent intent = new Intent(this, ScheduleReceiver.class);
            if (timeToAlarm > HOUR) {
                Log.d("==SERVICE==", "TIME TO ALARM MORE THAN 1 HOUR");
                intent.putExtra(INTENT_TYPE_KEY, SCHEDULED);
                intent.putExtra(INTENT_SERIALIZABLE_KEY, alarm);
                setAlarmManager(alarm.getId(), intent, alarmTime - HOUR);
            }
            if (timeToAlarm <= HOUR && timeToAlarm > TEN_MINUTES) {
                Log.d("==SERVICE==", "TIME TO ALARM LESS THAN 1 HOUR");
                intent.putExtra(INTENT_TYPE_KEY, SCHEDULED);
                intent.putExtra(INTENT_SERIALIZABLE_KEY, alarm);
                setAlarmManager(alarm.getId(), intent, alarmTime - TEN_MINUTES);
            }
            if (timeToAlarm <= TEN_MINUTES) {
                Log.d("==SERVICE==", "TIME TO ALARM LESS THAN 10 MINS");
                intent.putExtra(INTENT_TYPE_KEY, WAKE_UP);
                intent.putExtra(INTENT_SERIALIZABLE_KEY, alarm);
                setAlarmManager(alarm.getId(), intent, alarmTime);
            }
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int bedtimeMillis = prefs.getInt("sleep_time", 480)*60*1000;
            if (timeToAlarm > bedtimeMillis){
                intent.putExtra(INTENT_TYPE_KEY, BEDTIME_NOTIFICATION);
                setAlarmManager(alarm.getId() + NOTIFICATION_ID, intent, alarmTime - bedtimeMillis);
            }
            else {//cancel notification
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(), alarm.getId()+NOTIFICATION_ID, intent, 0);
                ((AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
            }
            Log.d("==SERVICE==", "alarm scheduled " + alarm.toString());
        }
        else {

            Log.d("==SERVICE==", "alarm cancelled " + alarm.toString());
            cancelAlarm(alarm.getId());
        }
    }



    private void setAlarmManager(int alarmId, Intent intent, long mills){
        PendingIntent pIntent = PendingIntent.getBroadcast(this, alarmId, intent, 0);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mills, pIntent);
        }
        else {
            am.setExact(AlarmManager.RTC_WAKEUP, mills, pIntent);
        }
    }

    public void scheduleAlarmList(List<Alarm> list){
        for (Alarm alarm : list) {
            scheduleAlarm(alarm);
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
            mScheduler.get().scheduleAlarmList(alarms);
        }
    }
}
