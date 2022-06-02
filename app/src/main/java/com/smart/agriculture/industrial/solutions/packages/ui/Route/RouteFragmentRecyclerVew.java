package com.smart.agriculture.industrial.solutions.packages.ui.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.RouteAdaptorBinding;
import com.smart.agriculture.industrial.solutions.packages.models.GlobalModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.util.ArrayList;
import java.util.List;

public class RouteFragmentRecyclerVew extends RecyclerView.Adapter<RouteFragmentRecyclerVew.RouteFragmentAdaptorHolder> {

    private static final String TAG = "RouteFragmentRecyclerVew";
    private final Context context;
    private ArrayList<GlobalModel> globalModel;
    private List<Integer> keyList;

    public RouteFragmentRecyclerVew(ArrayList<GlobalModel> globalModel, Context context) {
        this.globalModel = globalModel;
        this.context = context;


    }


    public void updateData(ArrayList<GlobalModel> globalModel) {
        this.globalModel = globalModel;
    }

    @NonNull
    @Override
    public RouteFragmentAdaptorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RouteAdaptorBinding routeAdaptorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.route_adaptor, parent, false);
        return new RouteFragmentRecyclerVew.RouteFragmentAdaptorHolder(routeAdaptorBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull RouteFragmentAdaptorHolder holder, int position) {
        if (this.globalModel.size() != 0) {
            GlobalModel tmp = globalModel.get(position);

            Static.logD(">>>"+TAG,tmp.toString());

            long[] date = conversion(tmp.getETA());
            holder.routeAdaptorBinding.routeFragmentEtaTextView.setText(tmp.getEvent().getType() + tmp.getPosition().getServerTime());
            holder.routeAdaptorBinding.routeFragmentIgnitionTextView.setText("On");

            if (!globalModel.get(position).getPosition().getPositionAttributes().getIgnition())
                holder.routeAdaptorBinding.routeFragmentIgnitionTextView.setText("Off");
        }
    }

    @Override
    public int getItemCount() {
        return this.globalModel.size();
    }

    public void setGeoFences(List<Integer> keyList) {
        this.keyList = keyList;
    }

    class RouteFragmentAdaptorHolder extends RecyclerView.ViewHolder {
        RouteAdaptorBinding routeAdaptorBinding;

        RouteFragmentAdaptorHolder(RouteAdaptorBinding routeAdaptorBinding) {
            super(routeAdaptorBinding.getRoot());
            this.routeAdaptorBinding = routeAdaptorBinding;
            this.routeAdaptorBinding.executePendingBindings();
        }
    }


    private long[] conversion(double totalSecs) {
        long minutes = (long) (totalSecs / 60);
        long seconds = (long) (totalSecs - (minutes * 60));
        return new long[]{minutes, seconds};
    }

}
