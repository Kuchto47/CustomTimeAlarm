package com.project.pv239.customtimealarm.activities;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;
import com.project.pv239.customtimealarm.services.ScheduleReceiver;
import com.project.pv239.customtimealarm.services.SchedulerService;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WakeUpActivity extends AppCompatActivity{

    private static PowerManager.WakeLock sWakeLock;
    private MediaPlayer mMediaPlayer;
    //fragment cannot be shown over locked screen so we have to use only activity
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake_up_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().setFormat(PixelFormat.OPAQUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager m = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            if (m != null)
                m.requestDismissKeyguard(this, null);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        this.setVolumeControlStream(AudioManager.STREAM_ALARM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)//for some unknown reason not working
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_ALARM)
                    .build();
            mMediaPlayer = MediaPlayer.create(this,R.raw.alarm_clock,attributes,AudioManager.AUDIO_SESSION_ID_GENERATE);
        }else {
            mMediaPlayer = MediaPlayer.create(this,R.raw.alarm_clock);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        }
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        Button up = findViewById(R.id.im_am_up);
        Button snooze = findViewById(R.id.snooze);
        int alarmId = getIntent().getIntExtra(SchedulerService.INTENT_ALARM_ID_KEY, -1);
        List<Alarm> list = null;
        try {
            list = new GetAlarms().execute().get();
        }catch (InterruptedException|ExecutionException e){
            e.printStackTrace();
        }
        if (list != null)
            for(Alarm a : list) {
                if (a.getId() == alarmId) {
                    final Alarm alarm = a;
                    up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alarm.setOn(false);
                            new UpdateAlarmInDbTask(new WeakReference<>(alarm)).execute();
                            mMediaPlayer.stop();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                finishAndRemoveTask();
                            }else {
                                finish();
                            }
                        }
                    });

                    snooze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), ScheduleReceiver.class);
                            intent.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.WAKE_UP);
                            intent.putExtra(SchedulerService.INTENT_ALARM_ID_KEY, alarm.getId());
                            PendingIntent pIntent = PendingIntent.getBroadcast(getApplicationContext(), alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            int snoozeTime = Integer.parseInt(prefs.getString("snooze","5"))*60*1000;
                            if (am != null) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + snoozeTime, pIntent);
                                } else {
                                    am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + snoozeTime, pIntent);
                                }
                            }
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                finishAndRemoveTask();
                            }else {
                                finish();
                            }
                        }
                    });
                }
            }
    }

    @Override
    public void onBackPressed() {
    }

    public static void acquireLock(Context context) {
        PowerManager pm = (PowerManager)  context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "alarm lock");
            sWakeLock.acquire(10000);
            sWakeLock.setReferenceCounted(false);
        }
    }

    private static void releaseLock() {
        try {
            if (sWakeLock != null)
                sWakeLock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        @Override
    protected void onResume() {
        releaseLock();
        super.onResume();
    }


    static class GetAlarms extends AsyncTask<Void,Void,List<Alarm>> {

        @Override
        protected List<Alarm> doInBackground(Void... voids) {
            return new AlarmFacade().getAllAlarms();
        }
    }

    private static class UpdateAlarmInDbTask extends AsyncTask<Void,Void,Void>{
        WeakReference<Alarm> mAlarm;

        UpdateAlarmInDbTask(WeakReference<Alarm> alarmWeakReference) {
            mAlarm = alarmWeakReference;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new AlarmFacade().updateAlarm(mAlarm.get());
            return null;
        }
    }
}
