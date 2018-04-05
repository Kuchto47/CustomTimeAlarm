package com.project.pv239.customtimealarm.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;
import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;
import com.project.pv239.customtimealarm.fragments.MainFragment;
import com.project.pv239.customtimealarm.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO consider this to remove in some class that is extending Application/ask tutor how to manage this
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        loadFragment(savedInstanceState);
        //TODO test log
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... voids) {
                AlarmFacade alarmFacade = new AlarmFacade();
                alarmFacade.addAlarm(new Alarm("TestDest", "12:47", TrafficModel.BEST_GUESS, TravelMode.DRIVING));
                List<Alarm> obts = alarmFacade.getAllAlarms();
                for (Alarm obt : obts) {
                    Log.d("== ALARM ==", obt.getDestination()+" "+obt.getTimeOfArrival()+" "+obt.getTrafficModel()+" "+obt.getTravelMode());
                }
                return null;
            }
        }.execute();
    }

    private void loadFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {       // Important, otherwise there'd be a new Fragment created with every orientation change
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings)
            startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }
}
