package com.project.pv239.customtimealarm.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.fragments.MainFragment;
import com.project.pv239.customtimealarm.fragments.SetAlarmFragment;
import com.project.pv239.customtimealarm.helpers.PermissionChecker;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadFragment(savedInstanceState);
        PermissionChecker.getLocationPermissionIfNotGranted(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PermissionChecker.LOCATION_REQUEST_CODE:
                if(!PermissionChecker.canAccessLocation()){
                    this.showCancellationAlertDialog();
                }
        }
    }

    private void showCancellationAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        this.setContentOfDialog(builder, this);
        builder.create().show();
    }

    private void setContentOfDialog(AlertDialog.Builder builder, final Activity activity){
        builder.setTitle(R.string.cancellation_dialog_title).setMessage(R.string.cancellation_dialog_body);
        builder.setPositiveButton(R.string.cancellation_dialog_grant_permission_again_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PermissionChecker.getLocationPermissionIfNotGranted(activity);
            }
        });
        builder.setNegativeButton(R.string.cancellation_dialog_close_app_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
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
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
        switch(item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case android.R.id.home:
                SetAlarmFragment frag = (SetAlarmFragment) getSupportFragmentManager().findFragmentByTag(SetAlarmFragment.class.getSimpleName());
                frag.closeFragment();
        }
        return super.onOptionsItemSelected(item);
    }
}
