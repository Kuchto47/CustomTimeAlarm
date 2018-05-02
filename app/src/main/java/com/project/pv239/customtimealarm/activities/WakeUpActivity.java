package com.project.pv239.customtimealarm.activities;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.fragments.MainFragment;

public class WakeUpActivity extends AppCompatActivity{

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wake_up_layout);
        loadFragment(savedInstanceState);

        setShowWhenLocked(true);
        setTurnScreenOn(true);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR|
                WindowManager.LayoutParams.FLAG_FULLSCREEN  |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

    }

    private void loadFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.beginTransaction()
                        .replace(android.R.id.content,
                                MainFragment.newInstance(),
                                MainFragment.class.getSimpleName())
                        .commit();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
