package com.project.pv239.customtimealarm.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.database.entity.Alarm;
import com.project.pv239.customtimealarm.database.facade.AlarmFacade;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder>{

    private Context mContext;
    private List<Alarm> mAlarms;
    private AlarmsAdapter.AdapterListener mListener;

    public AlarmsAdapter(@NonNull List<Alarm> alarms, @NonNull AdapterListener listener) {
        mAlarms = alarms;
        mListener = listener;
    }

    public void refreshAlarms(@NonNull List<Alarm> alarms) {
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.alarm_item;
        mContext = parent.getContext();
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Alarm alarm = mAlarms.get(position);
        holder.mDestination.setText(alarm.getDestination());
        holder.mTime.setText(alarm.getTimeOfArrival());
        holder.mAlarm = alarm;
        holder.mSwitch.setChecked(alarm.isOn());
        holder.mSwitch.setOnCheckedChangeListener(new SwitchListener(this,holder));
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    public class SwitchListener implements CompoundButton.OnCheckedChangeListener {

        AlarmsAdapter mAdapter;
        AlarmsAdapter.ViewHolder mHolder;

        public SwitchListener(AlarmsAdapter adapter, ViewHolder holder){
            mAdapter = adapter;
            mHolder = holder;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            (new UpdateAlarmTask(new WeakReference<>(mHolder.mAlarm), new WeakReference<>(mAdapter))).execute();
        }
    }

    public static class UpdateAlarmTask extends AsyncTask<Void, Void, Alarm>{
        private WeakReference<Alarm> mAlarm;
        private WeakReference<AlarmsAdapter> mAdapter;
        private List<Alarm> mAlarms;

        public UpdateAlarmTask(WeakReference<Alarm> alarm, WeakReference<AlarmsAdapter> adapter){
            mAlarm = alarm;
            mAdapter = adapter;
        }
        @Override
        protected Alarm doInBackground(Void... voids) {
            AlarmFacade alarmFacade = new AlarmFacade();
            alarmFacade.updateAlarm(mAlarm.get());
            mAlarms = alarmFacade.getAllAlarms();
            return null;
        }

        @Override
        protected void onPostExecute(Alarm alarm) {
            mAdapter.get().refreshAlarms(mAlarms);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.destination)
        TextView mDestination;
        @BindView(R.id.time)
        TextView mTime;
        @BindView(R.id.on_switch)
        Switch mSwitch;
        Alarm mAlarm;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(mAlarm);
                }
            });
        }
    }

    public interface AdapterListener {
        void onItemClicked(Alarm alarm);
    }
}