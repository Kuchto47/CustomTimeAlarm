package com.project.pv239.customtimealarm.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;

import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class SchedulerService extends IntentService {

    public static final int RECHECK_DELTA = 60*60*10;
    public SchedulerService() {
        super("SchedulerService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("==SERVICE==","created");
        new GetAlarmsTask(new WeakReference<>(this)).execute();

    }

    public void schedlueAlarmList(List<Alarm> list){
        for (Alarm a : list) {
            if (a.isOn()) {
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), SchedulerService.class);

                PendingIntent pIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);
                int estimatedTravelTime = 1;//TODO
                Calendar date = new GregorianCalendar();
                date.set(Calendar.HOUR_OF_DAY, a.getHour());
                date.set(Calendar.MINUTE, a.getMinute());
                date.set(Calendar.SECOND, 0);
                date.set(Calendar.MILLISECOND, 0);
                long alarmTime = date.getTime().getTime() - estimatedTravelTime*60 - a.getMorningRoutine()*60*60;
                am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarmTime-RECHECK_DELTA, pIntent);
            }
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