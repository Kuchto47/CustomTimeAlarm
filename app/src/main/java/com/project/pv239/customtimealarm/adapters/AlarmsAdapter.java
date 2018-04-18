package com.project.pv239.customtimealarm.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.pv239.customtimealarm.R;
import com.project.pv239.customtimealarm.database.entity.Alarm;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.ViewHolder>{

    private Context mContext;
    private List<Alarm> mAlarms;

    public AlarmsAdapter(@NonNull List<Alarm> alarms) {
        mAlarms = alarms;
    }

    public void refreshUsers(@NonNull List<Alarm> alarms) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        Alarm alarm = mAlarms.get(position);
        holder.mDestination.setText(alarm.getDestination());
        holder.mTime.setText(alarm.getTimeOfArrival());

    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        final Alarm alarm = holder.mAlarm;

        if (alarm != null) {
            holder.getMapFragmentAndCallback(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    LatLng position = new LatLng(alarm.getLatitude(),alarm.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(position));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));
                }
            });
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder.mAlarm != null) {
            // If error still occur unpredictably, it's best to remove fragment here
            //holder.removeMapFragment();
        }
    }


    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.destination)
        TextView mDestination;
        @BindView(R.id.time)
        TextView mTime;
        private SupportMapFragment mapFragment;
        Alarm mAlarm;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        public SupportMapFragment getMapFragmentAndCallback(OnMapReadyCallback callback) {
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                mapFragment.getMapAsync(callback);
            }

            // for fragment
            // FragmentManager fragmentManager = getChildFragmentManager();
            // for activity
            FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit();
            return mapFragment;
        }

        public void removeMapFragment() {
            if (mapFragment != null) {
                FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(mapFragment).commitAllowingStateLoss();
                mapFragment = null;
            }
        }
    }
}