package com.project.pv239.customtimealarm.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
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
import com.project.pv239.customtimealarm.api.GoogleMapsApi;
import com.project.pv239.customtimealarm.api.GoogleMapsApiInformationGetter;
import com.project.pv239.customtimealarm.api.GoogleMapsApiKeyGetter;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;
import com.project.pv239.customtimealarm.helpers.objects.Tuple;
import com.project.pv239.customtimealarm.helpers.places.PlacesProvider;
import com.project.pv239.customtimealarm.services.WakeService;

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
    @BindView(R.id.morning)
    protected TextView mMorningView;
    @BindView(R.id.morningSet)
    protected SeekBar mMorningSet;
    @BindView(R.id.set_layout)
    FrameLayout mLayout;
    private Unbinder mUnbinder;
    private boolean mCreate;


    public static SetAlarmFragment newInstance(Alarm alarm, boolean create) {
        SetAlarmFragment fragment = new SetAlarmFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ALARM_KEY, alarm);
        fragment.setArguments(bundle);
        fragment.mCreate = create;
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
        if (mCreate) {
           addFloadingButton();
        }
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mMorningSet.setProgress(mAlarm.getMorningRoutine());
        mMorningSet.setMax(240);
        mMorningView.setText(String.valueOf(mAlarm.getMorningRoutine()));
        mMorningSet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAlarm.setMorningRoutine(progress);
                mMorningView.setText(String.valueOf(mAlarm.getMorningRoutine()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        Log.d("FRAGMENT CREATION", "i am fragment for alarm id " + mAlarm.getId());
        return view;
    }

    public void addFloadingButton(){
        FloatingActionButton button = new FloatingActionButton(getContext());
        button.setImageResource(R.drawable.ic_done_white_24dp);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM + Gravity.END);
        Resources r = getContext().getResources();
        int dpMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 25, r.getDisplayMetrics());
        p.setMargins(dpMargin, dpMargin, dpMargin, dpMargin);
        mLayout.addView(button, p);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationTextChanged(mDest);
                if (mAlarm.getLatitude() != 0.0 || mAlarm.getLongitude() != 0.0) {//what are the odds :)
                    new CreateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
                    closeFragment();
                } else {
                    Toast.makeText(getContext(), "Destination not set", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public boolean destinationTextChanged(TextView v){
        String text = v.getText().toString();
        GoogleMapsApiInformationGetter gm = new GoogleMapsApiInformationGetter();
        Tuple<Double> t = gm.getLanLonOfPlace(PlacesProvider.getDestination(text));
        if (t != null) {
            mAlarm.setDestination(text);
            final LatLng ll = new LatLng(t.getFirst(), t.getSecond());
            mAlarm.setLatitude(ll.latitude);
            mAlarm.setLongitude(ll.longitude);
            mMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(ll));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
                }
            });
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mCreate && (mAlarm.getLongitude() != 0 || mAlarm.getLatitude() != 0)){
            new UpdateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
            Intent i = new Intent(getActivity(), WakeService.class);
            i.putExtra("Alarm deleted",mAlarm.getId());
            getContext().startService(i);
        }
    }

    public void closeFragment(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng pos = new LatLng(mAlarm.getLatitude(), mAlarm.getLongitude());
        if (mAlarm.getLatitude() != 0 || mAlarm.getLongitude() != 0) {
            googleMap.addMarker(new MarkerOptions().position(pos));
        }
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

    static class CreateAlarmInDbTask extends AsyncTask<Void,Void,Void>{

        private WeakReference<Alarm> mAlarm;

        CreateAlarmInDbTask(WeakReference<Alarm> alarm){
            mAlarm = alarm;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AlarmFacade alarmFacade = new AlarmFacade();
            alarmFacade.addAlarm(mAlarm.get());
            return null;
        }
    }
}
