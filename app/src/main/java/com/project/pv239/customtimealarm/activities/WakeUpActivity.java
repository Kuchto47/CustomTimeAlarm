package com.project.pv239.customtimealarm.activities;

import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.fragments.MainFragment;
import com.project.pv239.customtimealarm.fragments.SettingsFragment;

public class WakeUpActivity extends AppCompatActivity{

    MediaPlayer mMp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake_up_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().setFormat(PixelFormat.OPAQUE);
        Button up = findViewById(R.id.im_am_up);
        Button snooze = findViewById(R.id.snooze);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            KeyguardManager m = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            m.requestDismissKeyguard(this, null);
        }
        else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        mMp = MediaPlayer.create(this, R.raw.alarm_clock);
        mMp.start();
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMp.stop();
                Log.d("==SERVICE==", "clicked");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
