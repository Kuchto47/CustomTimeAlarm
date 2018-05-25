package com.project.pv239.customtimealarm.fragments;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.project.pv239.customtimealarm.database.Alarm;
import com.project.pv239.customtimealarm.database.AlarmFacade;
import com.project.pv239.customtimealarm.helpers.Tuple;
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
    protected LinearLayout mLayout;
    @BindView(R.id.ok_button)
    protected Button mSaveButton;
    @BindView(R.id.search)
    protected Button mSearchButton;
    private ProgressDialog mProgress;
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

    private AppCompatActivity getActiveActivity() {
        return (AppCompatActivity)getActivity();
    }

    private void toggleHomeButton(boolean toggle){
        this.getActiveActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(toggle);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.toggleHomeButton(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.toggleHomeButton(false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null){
            return null;
        }
        mAlarm = (Alarm) getArguments().getSerializable(ALARM_KEY);
        if (mAlarm == null){
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_set_alarm, container, false);
        setHasOptionsMenu(true);
        mUnbinder = ButterKnife.bind(this, view);
        setupMap();
        setupDestination();
        setupArrivalTime();
        setupDefaultTime();
        setupTravelMode();
        setupTrafficModel();
        setupMorningRoutine();
        setupButtons();
        return view;
    }

    private void setupButtons(){
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationTextChanged(mDest, true);
            }
        });
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationTextChanged(mDest, false);
            }
        });
    }

    private void setupMap(){
        FragmentManager f = getChildFragmentManager();
        mMap = (SupportMapFragment) f.findFragmentById(R.id.map);
        mMap.getMapAsync(this);
    }

    private void setupDestination(){
        mDest.setText(mAlarm.getDestination());
        mDest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    destinationTextChanged(v, false);
                    return true;
                }
                return false;
            }
        });
        mDest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (((TextView)v).getText().length() != 0)
                    destinationTextChanged((TextView) v, false);
            }
        });
    }

    private void setupArrivalTime(){
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
    }

    private void setupDefaultTime(){
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
    }

    private void setupTravelMode(){
        if (getActivity() == null)
            return;
        final ArrayAdapter<CharSequence> travelAdapter = ArrayAdapter.createFromResource(getActivity(),
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
    }

    private void setupTrafficModel(){
        if (getActivity() == null)
            return;
        final ArrayAdapter<CharSequence> trafficAdapter = ArrayAdapter.createFromResource(getActivity(),
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
    }

    private void setupMorningRoutine(){
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
    }

    private void destinationTextChanged(TextView v, boolean closingFragment) {
        String text = v.getText().toString();
        if (!text.equals(mAlarm.getDestination()) || closingFragment) {
            mProgress = new ProgressDialog(getContext());
            mProgress.setTitle(R.string.loading);
            mProgress.setMessage(getString(R.string.loading_text));
            mProgress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            mProgress.show();
            LoadDestinationTask task = new LoadDestinationTask(new WeakReference<>(v), new WeakReference<>(mAlarm),
                    new WeakReference<>(mMap), closingFragment, new WeakReference<>(text),
                    new WeakReference<>(this), mCreate, new WeakReference<>(mProgress));
            task.execute();
            mDest.clearFocus();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgress != null)
            mProgress.dismiss();
    }

    private static class LoadDestinationTask extends AsyncTask<Void,Void,Void>{

        private WeakReference<TextView> mTextView;
        private WeakReference<String> mText;
        private WeakReference<Alarm> mAlarm;
        private WeakReference<SupportMapFragment> mMap;
        private Tuple<Double> mTuple;
        private WeakReference<SetAlarmFragment> mFragment;
        private WeakReference<ProgressDialog> mProgress;
        boolean mClosingFragment;
        boolean mCreate;

        LoadDestinationTask(WeakReference<TextView> textView, WeakReference<Alarm> alarm, WeakReference<SupportMapFragment> map,
                            boolean closingFragment, WeakReference<String> text, WeakReference<SetAlarmFragment> fragment,
                            boolean create, WeakReference<ProgressDialog> progress){
            mTextView = textView;
            mAlarm = alarm;
            mMap = map;
            mClosingFragment = closingFragment;
            mText = text;
            mFragment = fragment;
            mCreate = create;
            mProgress = progress;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            GoogleMapsApiInformationGetter gm = new GoogleMapsApiInformationGetter();
            mTuple = gm.getLatLonOfPlaceSync(mText.get());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mTuple != null) {//if we got location
                mAlarm.get().setDestination(mTextView.get().getText().toString());
                final LatLng ll = new LatLng(mTuple.getFirst(), mTuple.getSecond());
                mAlarm.get().setLatitude(ll.latitude);
                mAlarm.get().setLongitude(ll.longitude);
                if (mClosingFragment) {//update db and close fragment
                    try {
                        if (mCreate)
                            new CreateAlarmInDbTask(mAlarm).execute().get();
                        else
                            new UpdateAlarmInDbTask(mAlarm).execute().get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mProgress.get().dismiss();
                    mFragment.get().closeFragment();
                    return;
                } else {//update map with location
                    mMap.get().getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(ll));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(ll));
                        }
                    });
                }
            }
            else {//show error
                mTextView.get().setText(mAlarm.get().getDestination());
                Toast.makeText(App.getInstance().getApplicationContext(), R.string.dest_not_found, Toast.LENGTH_LONG).show();
            }
            mProgress.get().dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!mCreate) {
            MenuItem delete = menu.getItem(0).setVisible(true);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (getContext() != null)
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
        if (getActivity() == null)
            return;
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getActivity().getSupportFragmentManager().popBackStack();
        this.toggleHomeButton(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng pos = new LatLng(mAlarm.getLatitude(), mAlarm.getLongitude());
        if (mAlarm.getLatitude() != 0 || mAlarm.getLongitude() != 0) {
            googleMap.addMarker(new MarkerOptions().position(pos));
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
    }

    private static class UpdateAlarmInDbTask extends AsyncTask<Void, Void, Void> {

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

    private static class CreateAlarmInDbTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Alarm> mAlarm;


        CreateAlarmInDbTask(WeakReference<Alarm> alarm) {
            mAlarm = alarm;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AlarmFacade alarmFacade = new AlarmFacade();
            long id = alarmFacade.addAlarm(mAlarm.get());
            Intent intent = new Intent();
            intent.putExtra(SchedulerService.INTENT_ALARM_ID_KEY, (int)id);
            intent.putExtra(SchedulerService.INTENT_TYPE_KEY, SchedulerService.ALARM_CREATED);
            SchedulerService.enqueueWork(App.getInstance().getApplicationContext(), SchedulerService.class, SchedulerService.JOB_ID, intent);
            return null;
        }
    }
}
