package com.project.pv239.customtimealarm.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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
import com.project.pv239.customtimealarm.App;
import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.adapters.AlarmsAdapter;
import com.project.pv239.customtimealarm.api.GoogleMapsApiInformationGetter;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;
import com.project.pv239.customtimealarm.helpers.objects.Tuple;
import com.project.pv239.customtimealarm.services.SchedulerService;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SetAlarmFragment extends Fragment implements OnMapReadyCallback {

    private static final String ALARM_KEY = "alarm_key";
    private Alarm mAlarm;
    protected SupportMapFragment mMap;
    @BindView(R.id.destEdit)
    protected EditText mDest;
    @BindView(R.id.timeEdit)
    protected TextView mTime;
    @BindView(R.id.timeDefaultEdit)
    protected TextView mTimeDefault;
    @BindView(R.id.travelMode)
    protected Spinner mTravelMode;
    @BindView(R.id.trafficModel)
    protected Spinner mTrafficModel;
    @BindView(R.id.morning)
    protected TextView mMorningView;
    @BindView(R.id.morningSet)
    protected SeekBar mMorningSet;
    @BindView(R.id.set_layout)
    LinearLayout mLayout;
    @BindView(R.id.ok_button)
    Button mButton;
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
        super.onCreateView(inflater,container,savedInstanceState);
        mAlarm = (Alarm) getArguments().getSerializable(ALARM_KEY); //TODO:somehow close fragment
        View view = inflater.inflate(R.layout.fragment_set_alarm, container, false);
        setHasOptionsMenu(true);
        mUnbinder = ButterKnife.bind(this, view);
        FragmentManager f = getChildFragmentManager();
        mMap = (SupportMapFragment) f.findFragmentById(R.id.map);
        mMap.getMapAsync(this);
        mDest.setText(mAlarm.getDestination());
        mDest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return destinationTextChanged(v, false);
            }
        });
        mDest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (((TextView)v).getText().length() != 0)
                    destinationTextChanged((TextView) v, false);
            }
        });
        mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mAlarm.setHourOfArrival(hourOfDay);
                        mAlarm.setMinuteOfHourOfArrival(minute);
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, mAlarm.getHourOfArrival(), mAlarm.getMinuteOfHourOfArrival(), true);
                dialog.updateTime(mAlarm.getHourOfArrival(),mAlarm.getMinuteOfHourOfArrival());
                dialog.show();
            }
        });
        mTimeDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mAlarm.setHourOfDefaultAlarm(hourOfDay);
                        mAlarm.setMinuteOfHourOfDefaultAlarm(minute);
                    }
                };
                TimePickerDialog dialog = new TimePickerDialog(getContext(), listener, mAlarm.getHourOfArrival(), mAlarm.getMinuteOfHourOfArrival(), true);
                dialog.updateTime(mAlarm.getHourOfDefaultAlarm(),mAlarm.getMinuteOfHourOfDefaultAlarm());
                dialog.show();
            }
        });
        final ArrayAdapter<CharSequence> travelAdapter = ArrayAdapter.createFromResource(getContext(), //TODO: is it okay to use appcontext?
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
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationTextChanged(mDest, true);
                if (mAlarm.getLatitude() != 0.0 || mAlarm.getLongitude() != 0.0) {//what are the odds :)
                    if (mCreate)
                        new CreateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
                    else
                        new UpdateAlarmInDbTask(new WeakReference<>(mAlarm)).execute();
                    closeFragment();
                } else {
                    Toast.makeText(getContext(), R.string.dest_not_set, Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.d("FRAGMENT CREATION", "i am fragment for alarm id " + mAlarm.getId());
        return view;
    }

    public boolean destinationTextChanged(TextView v, boolean closingFragment) {
        String text = v.getText().toString();
        GoogleMapsApiInformationGetter gm = new GoogleMapsApiInformationGetter();
        Tuple<Double> t = gm.getLanLonOfPlace(text);
        if (t != null) {
            mAlarm.setDestination(text);
            final LatLng ll = new LatLng(t.getFirst(), t.getSecond());
            mAlarm.setLatitude(ll.latitude);
            mAlarm.setLongitude(ll.longitude);
            if (!closingFragment) {
                mMap.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(ll));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
                    }
                });
            }
        }
        else {
            v.setText(mAlarm.getDestination());
            if (!closingFragment){
                Toast.makeText(getContext(), R.string.dest_not_found, Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!mCreate) {
            MenuItem delete = menu.getItem(0).setVisible(true);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.delete_dialog_delete)
                            .setMessage(R.string.delete_dialog_text)
                            .setPositiveButton(R.string.delete_dialog_delete, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    new AlarmsAdapter.DeleteTaskAsync(new WeakReference<>(mAlarm)).execute();
                                    Intent intent = new Intent();
                                    intent.putExtra(SchedulerService.INTENT_ALARM_ID_KEY, mAlarm.getId());
                                    intent.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.ALARM_CANCELLED);
                                    SchedulerService.enqueueWork(App.getInstance().getApplicationContext(), SchedulerService.class, SchedulerService.JOB_ID, intent);
                                    dialog.dismiss();
                                    closeFragment();
                                }
                            })
                            .setNegativeButton(R.string.delete_dialog_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                    return false;
                }
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void closeFragment() {
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

    public static class UpdateAlarmInDbTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Alarm> mAlarm;

        UpdateAlarmInDbTask(WeakReference<Alarm> alarm) {
            mAlarm = alarm;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AlarmFacade alarmFacade = new AlarmFacade();
            alarmFacade.updateAlarm(mAlarm.get());
            Intent intent = new Intent();
            intent.putExtra(SchedulerService.INTENT_ALARM_ID_KEY, mAlarm.get().getId());
            intent.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.ALARM_CHANGED);
            SchedulerService.enqueueWork(App.getInstance().getApplicationContext(), SchedulerService.class, SchedulerService.JOB_ID, intent);
            return null;
        }
    }

    static class CreateAlarmInDbTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Alarm> mAlarm;


        CreateAlarmInDbTask(WeakReference<Alarm> alarm) {
            mAlarm = alarm;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AlarmFacade alarmFacade = new AlarmFacade();
            long id = alarmFacade.addAlarm(mAlarm.get());
            Log.d("==SERVICE==", ""+id);
            Intent intent = new Intent();
            intent.putExtra(SchedulerService.INTENT_ALARM_ID_KEY, (int)id);
            intent.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.ALARM_CREATED);
            SchedulerService.enqueueWork(App.getInstance().getApplicationContext(), SchedulerService.class, SchedulerService.JOB_ID, intent);
            return null;
        }
    }
}
