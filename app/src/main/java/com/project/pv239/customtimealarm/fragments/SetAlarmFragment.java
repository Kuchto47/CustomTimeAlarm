package com.project.pv239.customtimealarm.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.database.entity.Alarm;

public class SetAlarmFragment extends Fragment{

    private static final String ALARM_KEY = "alarm_key";
    private Alarm mAlarm;


    public static SetAlarmFragment newInstance(Alarm alarm) {
        SetAlarmFragment fragment = new SetAlarmFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_KEY, alarm);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mAlarm = (Alarm) getArguments().getSerializable(ALARM_KEY);
        View view = inflater.inflate(R.layout.fragment_set_alarm, container, false);
        Log.d("FRAGMENT CREATION", "i am fragment for alarm id " + mAlarm.getId());
        return view;
    }
}
