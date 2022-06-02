package com.smart.agriculture.industrial.solutions.packages.ui.togeofence;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.GeoFenceFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.GeoFenceInterface;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ToGeoFence extends Fragment {
    private List<GeoFence> geoFences;
    private static final String TAG = "FinalGeoFenceFragment";
    private ViewModel mViewModel;
    private GeoFenceInterface geoFenceInterface;
    private ToGeoFenceRecyclerView finalGeoFenceRecyclerView;
    private GeoFenceFragmentBinding geoFenceFragmentBinding;
    private List<GeoFence> geoFenceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GeoFence geoFence = new GeoFence();
        geoFenceFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.geo_fence_fragment, null, false);
        View view = geoFenceFragmentBinding.getRoot();
        geoFenceFragmentBinding.setGeoFence(geoFence);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        init();
    }


    private void init() {
        assert getArguments() != null;
        GeoFence geoFenceModelPass = getArguments().getParcelable(Constant.GEO_FENCE_MODEL);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Select the Drop Up Point");

        mViewModel.getGeoFence().enqueue(new Callback<List<GeoFence>>() {
            @Override
            public void onResponse(Call<List<GeoFence>> call, Response<List<GeoFence>> responseGeoFence) {

                geofenceResponse(responseGeoFence, geoFenceModelPass);
            }

            @Override
            public void onFailure(Call<List<GeoFence>> call, Throwable t) {
                retryDialogBox(Constant.NO_INTERNET_CODE, getContext());
            }
        });
    }

    private void geofenceResponse(Response<List<GeoFence>> responseGeoFence, GeoFence geoFenceModelPass) {

        if (responseGeoFence.isSuccessful() && responseGeoFence.body() != null) {

            geoFences = responseGeoFence.body();

            for (int i = 0; i < geoFences.size(); i++) {
                if (geoFences.get(i).getId() == (geoFenceModelPass.getId())) {
                    geoFences.remove(i);
                }
            }
            this.geoFenceList = geoFences;
            RecyclerView recyclerView = geoFenceFragmentBinding.geoFenceFragmentGeoFenceRecyclerView;
            finalGeoFenceRecyclerView = new ToGeoFenceRecyclerView(geoFences, getActivity(), geoFenceInterface, geoFenceModelPass);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(finalGeoFenceRecyclerView);

        } else {
            retryDialogBox(Constant.LATE_RESPONSE_CODE, getContext());
        }
    }

    private void retryDialogBox(int code, Context context) {
        Static.retry(context, code, bool -> {
            if (!bool) {
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        geoFenceInterface = (GeoFenceInterface) context;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.finalGeoFenceRecyclerView != null) {
            this.finalGeoFenceRecyclerView.setGeoFenceList(this.geoFences);
            finalGeoFenceRecyclerView.notifyDataSetChanged();
        }
    }
}
