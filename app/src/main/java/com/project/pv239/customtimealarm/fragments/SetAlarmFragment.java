package com.project.pv239.customtimealarm.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.database.entity.Alarm;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SetAlarmFragment extends Fragment implements OnMapReadyCallback{

    private static final String ALARM_KEY = "alarm_key";
    private Alarm mAlarm;
    private Unbinder mUnbinder;


    public static SetAlarmFragment newInstance(Alarm alarm) {
        SetAlarmFragment fragment = new SetAlarmFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_KEY, alarm);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mAlarm = (Alarm) getArguments().getSerializable(ALARM_KEY);
        View view = inflater.inflate(R.layout.fragment_set_alarm, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        FragmentManager f = getChildFragmentManager();
        SupportMapFragment mMap = (SupportMapFragment) f.findFragmentById(R.id.map);
        mMap.getMapAsync(this);
        Log.d("FRAGMENT CREATION", "i am fragment for alarm id " + mAlarm.getId());
        return view;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng pos = new LatLng(mAlarm.getLatitude(), mAlarm.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(pos));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
    }
}
