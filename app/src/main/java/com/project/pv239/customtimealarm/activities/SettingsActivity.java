package com.project.pv239.customtimealarm.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.project.pv239.customtimealarm.fragments.SettingsFragment;
import com.project.pv239.customtimealarm.services.SchedulerService;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, SettingsFragment.newInstance())
                    .commit();
            handleSharedPreferencesOnCreate();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void handleSharedPreferencesOnCreate(){
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d("==SERVICE==", "changed preferences");
                Intent i = new Intent();
                i.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.SCHEDULE_ALL);
                SchedulerService.enqueueWork(getApplicationContext(), SchedulerService.class, SchedulerService.JOB_ID, i);
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefs.unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
