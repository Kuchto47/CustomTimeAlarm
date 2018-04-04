package com.project.pv239.customtimealarm.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.project.pv239.customtimealarm.Fragments.MainFragment;
import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.api.GoogleMapsApiKeyGetter;
import com.project.pv239.customtimealarm.database.AppDatabase;
import com.project.pv239.customtimealarm.database.DatabaseProvider;
import com.project.pv239.customtimealarm.database.Entity.Alarm;
import com.project.pv239.customtimealarm.enums.TrafficModel;
import com.project.pv239.customtimealarm.enums.TravelMode;

public class MainActivity extends AppCompatActivity {
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO consider this to remove in some class that is extending Application/ask tutor how to manage this
        context = getApplicationContext();
        //TODO testing DB
        AppDatabase db = DatabaseProvider.getDatabase();
        db.alarmDao().addAlarm(new Alarm("dest", "09:00", TrafficModel.BEST_GUESS, TravelMode.DRIVING));
        Log.d("========ALARM:", db.alarmDao().getAll().toString());
        db.close();
        //
        setContentView(R.layout.activity_main);
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
