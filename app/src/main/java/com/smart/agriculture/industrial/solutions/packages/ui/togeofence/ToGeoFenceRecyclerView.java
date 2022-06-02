package com.smart.agriculture.industrial.solutions.packages.ui.togeofence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.GeoFenceAdaptorBinding;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.ui.Route.RouteFragment;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.GeoFenceInterface;

import java.util.List;

public class ToGeoFenceRecyclerView extends RecyclerView.Adapter<ToGeoFenceRecyclerView.GeoFenceAdaptorHolder> {

    private List<GeoFence> geoFenceList;
    private GeoFenceInterface geoFenceInterface;
    private GeoFence geoFenceModelFrom;
    private Context context;

    ToGeoFenceRecyclerView(List<GeoFence> geoFences, Context context, GeoFenceInterface geoFenceInterface, GeoFence geoFenceModelPass) {
        this.geoFenceList = geoFences;
        this.geoFenceInterface = geoFenceInterface;
        this.geoFenceModelFrom = geoFenceModelPass;
        this.context = context;
    }


    public List<GeoFence> getGeoFenceList() {
        return geoFenceList;
    }

    public void setGeoFenceList(List<GeoFence> geoFenceList) {
        this.geoFenceList = geoFenceList;
    }

    @SuppressLint("UseSparseArrays")
    @NonNull
    @Override
    public GeoFenceAdaptorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GeoFenceAdaptorBinding geoFenceAdaptorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.geo_fence_adaptor, parent, false);
        return new ToGeoFenceRecyclerView.GeoFenceAdaptorHolder(geoFenceAdaptorBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GeoFenceAdaptorHolder holder, int position) {
        if (geoFenceList.size() != 0) {
            GeoFence geoFence = this.geoFenceList.get(position);
            holder.geoFenceAdaptorBinding.geoFenceAdaptorDeviceNameTextView.setText(geoFence.getName());
            holder.geoFenceAdaptorBinding.geoFenceAdaptorDeviceDescriptionTextView.setText(geoFence.getDescription());
            holder.geoFenceAdaptorBinding.setOnItemClick(() -> {
                geoFenceInterface.geoFenceModelUpperToTo(geoFence);

                Bundle bundle = new Bundle();

                bundle.putParcelable(Constant.GEO_FENCE_MODEL_FROM, geoFenceModelFrom);
                bundle.putParcelable(Constant.GEO_FENCE_MODEL_TO, geoFence);

                RouteFragment routesFragment = new RouteFragment();
                routesFragment.setArguments(bundle);
                ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                        .addToBackStack(Constant.TO_GEO_FENCE_FRAGMENT)
                        .replace(R.id.activity_main_2_lower_fragment, routesFragment)
                        .commit();
            });
        }

    }

    @Override
    public int getItemCount() {
        return geoFenceList.size();
    }

    class GeoFenceAdaptorHolder extends RecyclerView.ViewHolder {
        GeoFenceAdaptorBinding geoFenceAdaptorBinding;

        GeoFenceAdaptorHolder(GeoFenceAdaptorBinding geoFenceAdaptorBinding) {
            super(geoFenceAdaptorBinding.getRoot());
            this.geoFenceAdaptorBinding = geoFenceAdaptorBinding;
            this.geoFenceAdaptorBinding.executePendingBindings();
        }
    }
}
