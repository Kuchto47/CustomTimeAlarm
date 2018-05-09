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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    public static final String INTENT_ALARM_ID_KEY = "alarm_id";
    private static final int MINUTE = 1000*60;
    private static final int HOUR = 1000*60*60;
    private static final int NOTIFICATION_ID = 100000;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("==SERVICE==","work" + intent.getIntExtra(INTENT_TYPE_KEY, -1));
        switch (intent.getIntExtra(INTENT_TYPE_KEY, -1)){
            case SCHEDULE_ALL:
                new GetAlarmsTask(new WeakReference<>(this)).execute();
                break;
            case WAKE_UP:
                Intent i = new Intent(this, WakeUpActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(INTENT_ALARM_ID_KEY, intent.getIntExtra(INTENT_ALARM_ID_KEY,-1));
                startActivity(i);
                break;
            case BEDTIME_NOTIFICATION:
                createNotification();
                break;
            case ALARM_CANCELLED:
                cancelAlarm(intent.getIntExtra(INTENT_ALARM_ID_KEY, -1));
                break;
            case SCHEDULED:
            case ALARM_CREATED:
            case ALARM_CHANGED:
                List<Alarm> list = null;
                try {
                    list = new GetAlarms().execute().get();
                }catch (InterruptedException|ExecutionException e){
                    Log.e("EX","load alarm failed");
                }
                if (list != null)
                    for(Alarm a : list) {
                        Log.d("==SERVICE==", "" + a.getId() +  " "+ intent.getIntExtra(INTENT_ALARM_ID_KEY, -1));
                        if (a.getId() == intent.getIntExtra(INTENT_ALARM_ID_KEY, -1))
                            scheduleAlarm(a);
                    }
                break;
        }
    }

    public void createNotification() {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean shouldShowNotification = pref.getBoolean("bedtime", false);
//        if(shouldShowNotification){
//            NotificationCompat.Builder mBuilder =
//            new NotificationCompat.Builder(getApplicationContext())
//                    .setContentTitle("My notification")
//                    .setContentText("Hello World!");
//            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(1, mBuilder.build());
//        }
    }

    public void cancelAlarm(int id){
        Log.d("==SERVICE==", "alarm cancelled " + id);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), ScheduleReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), id + NOTIFICATION_ID, myIntent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void scheduleAlarm(Alarm alarm) {
        if (alarm.isOn()) {
            Log.d("==SERVICE==", "alarm scheduling " + alarm.getId());
            long alarmTime = AlarmTimeGetter.getAlarmTimeInMilliSeconds(alarm);
            if(alarmTime == -1){
                Log.d("==ALARMTIME==", "equal to -1, we should probably set default wakeuptime here.");
            }
            Log.d("==SERVICE==", "TIMES "+ System.currentTimeMillis() + " " + alarmTime);
            long timeToAlarm = alarmTime - System.currentTimeMillis();
            Intent intent = new Intent(this, ScheduleReceiver.class);
            if (timeToAlarm <= MINUTE*11) {
                Log.d("==SERVICE==", "TIME TO ALARM LESS THAN 10 MINS");
                intent.putExtra(INTENT_TYPE_KEY, WAKE_UP);
                intent.putExtra(INTENT_ALARM_ID_KEY, alarm.getId());
                setAlarmManager(alarm.getId(), intent, alarmTime);
            } else {
                if (timeToAlarm > HOUR + MINUTE*5) {
                    Log.d("==SERVICE==", "TIME TO ALARM MORE THAN 1 HOUR CHECK IN " + (alarmTime - HOUR));
                    intent.putExtra(INTENT_TYPE_KEY, SCHEDULED);
                    intent.putExtra(INTENT_ALARM_ID_KEY, alarm.getId());
                    setAlarmManager(alarm.getId(), intent, alarmTime - HOUR);
                } else {
                    Log.d("==SERVICE==", "TIME TO ALARM LESS THAN 1 HOUR CHECK IN " + (alarmTime - MINUTE*10));
                    intent.putExtra(INTENT_TYPE_KEY, SCHEDULED);
                    intent.putExtra(INTENT_ALARM_ID_KEY, alarm.getId());
                    setAlarmManager(alarm.getId(), intent, alarmTime - MINUTE*10);
                }
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
        PendingIntent pIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        am.cancel(pIntent);

        Log.d("==SERVICE==", "set with intent " + intent.getIntExtra(INTENT_TYPE_KEY, -1));
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

    static class GetAlarms extends AsyncTask<Void,Void,List<Alarm>>{

        @Override
        protected List<Alarm> doInBackground(Void... voids) {
            return new AlarmFacade().getAllAlarms();
        }
    }
}
