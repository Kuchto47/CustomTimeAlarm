package com.project.pv239.customtimealarm.fragments;

import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.adapters.AlarmsAdapter;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

public class SetAlarmFragment extends Fragment implements OnMapReadyCallback{

    private static final String ALARM_KEY = "alarm_key";
    private Alarm mAlarm;
    protected SupportMapFragment mMap;
    @BindView(R.id.destEdit)
    protected EditText mDest;
    @BindView(R.id.timeEdit)
    protected TextView mTime;
    @BindView(R.id.travelMode)
    protected Spinner mTravelMode;
    @BindView(R.id.trafficModel)
    protected Spinner mTrafficModel;
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
        mMap = (SupportMapFragment) f.findFragmentById(R.id.map);
        mMap.getMapAsync(this);
        mDest.setText(mAlarm.getDestination());
        mDest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return destinationTextChanged(v);
            }
        });
        mDest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                destinationTextChanged((TextView) v);
            }
        });
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mAlarm.setHour(hourOfDay);
                        mAlarm.setMinute(minute);
                        new UpdateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
                    }
                };
                new TimePickerDialog(getContext(), listener, mAlarm.getHour(), mAlarm.getMinute(), true).show();
            }
        });
        final ArrayAdapter<CharSequence> travelAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.travel_mode, android.R.layout.simple_spinner_item);
        travelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTravelMode.setAdapter(travelAdapter);
        mTravelMode.setSelection(mAlarm.getTravelMode());
        mTravelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAlarm.setTravelMode(position);
                new UpdateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ArrayAdapter<CharSequence> trafficAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.traffic_model, android.R.layout.simple_spinner_item);
        trafficAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTrafficModel.setAdapter(trafficAdapter);
        mTrafficModel.setSelection(mAlarm.getTrafficModel());
        mTrafficModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAlarm.setTrafficModel(position);
                new UpdateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.d("FRAGMENT CREATION", "i am fragment for alarm id " + mAlarm.getId());
        return view;
    }

    public boolean destinationTextChanged(TextView v){
        mAlarm.setDestination(v.getText().toString());
        mMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                //TODO:update map according to query
            }
        });
        //TODO:query and handle (return false and do not update map)
        new UpdateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng pos = new LatLng(mAlarm.getLatitude(), mAlarm.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(pos));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
    }

    static class UpdateAlarmInDbTask extends AsyncTask<Void,Void,Void>{

        private WeakReference<Alarm> mAlarm;

        UpdateAlarmInDbTask(WeakReference<Alarm> alarm){
            mAlarm = alarm;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            AlarmFacade alarmFacade = new AlarmFacade();
            alarmFacade.updateAlarm(mAlarm.get());
            return null;
        }
    }
}
