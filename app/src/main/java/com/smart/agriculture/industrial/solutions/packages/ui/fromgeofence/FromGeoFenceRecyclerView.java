package com.smart.agriculture.industrial.solutions.packages.ui.fromgeofence;

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
import com.smart.agriculture.industrial.solutions.packages.ui.togeofence.ToGeoFence;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.GeoFenceInterface;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FromGeoFenceRecyclerView extends RecyclerView.Adapter<FromGeoFenceRecyclerView.GeoFenceAdaptorHolder> {
    private static final String TAG = "FromGeoFenceRecyclerView";
    private ArrayList<GeoFence> geoFenceList;
    private GeoFenceInterface geoFenceInterface;
    private Context context;
    FromGeoFenceRecyclerView(List<GeoFence> geoFences, Context context, GeoFenceInterface geoFenceInterface) {
        this.geoFenceList = (ArrayList<GeoFence>) geoFences;
        this.geoFenceInterface = geoFenceInterface;
        this.context = context;
        for (GeoFence geoFenceModel : geoFences) {
            Static.logD(TAG, geoFenceModel.toString());
        }
    }

    @SuppressLint("UseSparseArrays")
    @NonNull
    @Override
    public GeoFenceAdaptorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GeoFenceAdaptorBinding geoFenceAdaptorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.geo_fence_adaptor, parent, false);
        return new FromGeoFenceRecyclerView.GeoFenceAdaptorHolder(geoFenceAdaptorBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull GeoFenceAdaptorHolder holder, int position) {
        if (geoFenceList.size() != 0) {
            GeoFence geoFence = this.geoFenceList.get(position);
            holder.geoFenceAdaptorBinding.geoFenceAdaptorDeviceNameTextView.setText(geoFence.getName());
            holder.geoFenceAdaptorBinding.geoFenceAdaptorDeviceDescriptionTextView.setText(geoFence.getDescription());
            holder.geoFenceAdaptorBinding.setOnItemClick(() -> {
                geoFenceInterface.geoFenceModelFromToUpper(geoFence);

                Bundle bundle = new Bundle();
                bundle.putParcelable(Constant.GEO_FENCE_MODEL, geoFence);

                ToGeoFence finalGeoFenceFragment = new ToGeoFence();
                finalGeoFenceFragment.setArguments(bundle);
                if(Static.checkInternet(context)) {

                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .addToBackStack(Constant.TO_GEO_FENCE_FRAGMENT)
                            .replace(R.id.activity_main_2_lower_fragment, finalGeoFenceFragment)
                            .commit();
                }else{
                    final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    sDialog.setTitle("ERROR");
                    sDialog.setContentText(String.valueOf(R.string.no_internet_response));
                    sDialog.show();
                }

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
