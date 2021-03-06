package com.project.pv239.customtimealarm.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.adapters.AlarmsAdapter;
import com.project.pv239.customtimealarm.database.Alarm;
import com.project.pv239.customtimealarm.database.AlarmFacade;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainFragment extends Fragment{

    private AlarmsAdapter mAdapter;
    private Unbinder mUnbinder;
    @BindView(android.R.id.list)
    RecyclerView mList;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mAdapter = new AlarmsAdapter(new ArrayList<Alarm>(), this);
        mList.setAdapter(mAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        mList.setLayoutManager(manager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mList.getContext(),
                manager.getOrientation());
        mList.addItemDecoration(dividerItemDecoration);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm alarm = new Alarm();
                SetAlarmFragment setFragment = SetAlarmFragment.newInstance(alarm,true);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.beginTransaction()
                            .replace(android.R.id.content, setFragment, SetAlarmFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                }

            }
        });
        new LoadAlarmsTask(new WeakReference<>(mAdapter)).execute();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUnbinder.unbind();
    }

    public void onItemClicked(Alarm alarm) {
        SetAlarmFragment setFragment = SetAlarmFragment.newInstance(alarm,false);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, setFragment, SetAlarmFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
        Log.d("ITEM CLICKED", "clicked alarm with id " + alarm.getId());
    }

    public static class LoadAlarmsTask extends AsyncTask<Void, Void, List<Alarm>> {
        private WeakReference<AlarmsAdapter> mAdapter;

        public LoadAlarmsTask(WeakReference<AlarmsAdapter> adapter){
            mAdapter = adapter;
        }

        protected List<Alarm> doInBackground(Void... voids) {
            AlarmFacade alarmFacade = new AlarmFacade();
            return alarmFacade.getAllAlarms();
        }

        @Override
        protected void onPostExecute(List<Alarm> alarms) {
            mAdapter.get().getAlarmsFromDb(alarms);
        }
    }


}
