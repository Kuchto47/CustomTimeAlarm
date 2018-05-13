package com.project.pv239.customtimealarm.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.activities.WakeUpActivity;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;
import com.project.pv239.customtimealarm.helpers.AlarmTimeGetter;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;
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
            case BEDTIME_NOTIFICATION:
                createBedTimeNotification();
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

    public void createBedTimeNotification() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shouldShowNotification = pref.getBoolean("bedtime", false);
        if(shouldShowNotification){
            int sleepTimeInMinutes = pref.getInt("sleep_time", 480);
            String sleepTime = this.convertMinutesIntoHours(sleepTimeInMinutes);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.bed)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                            .setContentTitle(getResources().getString(R.string.bedtime_notification_title))
                            .setContentText(getResources().getString(R.string.bedtime_notification_body))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getResources().getString(R.string.bedtime_notification_body_big_text)
                                            .replace("%s1", sleepTime)))
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setVibrate(new long[] {0, 100, 0, 0})
                            .setLights(0xff0000, 3000, 3000)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    private String convertMinutesIntoHours(int mins){
        int hours = mins/60;
        int minutes = mins % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
    }

    public void cancelAlarm(int id){
        Log.d("==SERVICE==", "alarm with id " + id + " cancelling");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent myIntent = new Intent(getApplicationContext(), ScheduleReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
            pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), id + NOTIFICATION_ID, myIntent, 0);
            alarmManager.cancel(pendingIntent);
        }
    }

    public void scheduleAlarm(Alarm alarm) {
        if (alarm.isOn()) {
            Log.d("==SERVICE==", "alarm scheduling " + alarm.getId());
            long alarmTime = AlarmTimeGetter.getAlarmTimeInMilliSeconds(alarm);
            Log.d("==SERVICE==", "TIMES "+ System.currentTimeMillis() + " " + alarmTime);
            long timeToAlarm = alarmTime - System.currentTimeMillis();
            Intent intent = new Intent(this, ScheduleReceiver.class);
            if (timeToAlarm <= MINUTE*11) {
                intent.putExtra(INTENT_TYPE_KEY, WAKE_UP);
                intent.putExtra(INTENT_ALARM_ID_KEY, alarm.getId());
                setAlarmManager(alarm.getId(), intent, alarmTime);
            } else {
                if (timeToAlarm > HOUR + MINUTE*5) {
                    intent.putExtra(INTENT_TYPE_KEY, SCHEDULED);
                    intent.putExtra(INTENT_ALARM_ID_KEY, alarm.getId());
                    setAlarmManager(alarm.getId(), intent, alarmTime - HOUR);
                } else {
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
            Log.d("==SERVICE==", "calling cancelAlarm() for Alarm: " + alarm.toString());
            cancelAlarm(alarm.getId());
        }
    }



    private void setAlarmManager(int alarmId, Intent intent, long mills){
        PendingIntent pIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        if (am != null) {
            am.cancel(pIntent);

            Log.d("==SERVICE==", "set with intent " + intent.getIntExtra(INTENT_TYPE_KEY, -1));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, mills, pIntent);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, mills, pIntent);
            }
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
