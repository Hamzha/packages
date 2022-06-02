package com.smart.agriculture.industrial.solutions.packages.ui.vehiclelog;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.VehicleLogsRecyclerViewBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VehicleLogRecyclerView extends RecyclerView.Adapter<VehicleLogRecyclerView.VehicleLogAdaptorHolder> {
    List<Event> eventList;
    private static final String TAG = ">>>VehicleLogRecyclerView";
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat formatTraccar = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static DateFormat format = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss", Locale.ENGLISH);

    public VehicleLogRecyclerView(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public VehicleLogAdaptorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleLogsRecyclerViewBinding vehicleListAdaptorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.vehicle_logs_recycler_view, parent, false);
        return new VehicleLogRecyclerView.VehicleLogAdaptorHolder(vehicleListAdaptorBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VehicleLogAdaptorHolder holder, int position) {
        Event event = eventList.get(position);
        holder.vehicleListAdaptorBinding.vehicleListFragmentRecyclerViewEvent.setText(event.getType());


        String[] date = event.getServerTime().split("T");
        Static.logD(TAG, Arrays.toString(date));
        //            holder.vehicleListAdaptorBinding.vehicleListFragmentRecyclerViewEventTime.setText(format.format(formatTraccar.parse(event.getServerTime()).getTime()));
        holder.vehicleListAdaptorBinding.vehicleListFragmentRecyclerViewEventTime.setText(date[0] +" "+ date[1].split("\\.")[0]
//            holder.vehicleListAdaptorBinding.vehicleListFragmentRecyclerViewEventTime.setText(format.format(formatTraccar.parse(event.getServerTime()).getTime()));
        );

    }

    @Override
    public int getItemCount() {
        return this.eventList.size();
    }


    class VehicleLogAdaptorHolder extends RecyclerView.ViewHolder {
        VehicleLogsRecyclerViewBinding vehicleListAdaptorBinding;

        VehicleLogAdaptorHolder(VehicleLogsRecyclerViewBinding vehicleListAdaptorBinding) {
            super(vehicleListAdaptorBinding.getRoot());
            this.vehicleListAdaptorBinding = vehicleListAdaptorBinding;
            this.vehicleListAdaptorBinding.executePendingBindings();
        }
    }

}
