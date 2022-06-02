package com.smart.agriculture.industrial.solutions.packages.ui.fromgeofence;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FromGeoFenceFragment extends Fragment {

    private ViewModel mViewModel;
    private List<GeoFence> geoFences;
    private static final String TAG = "FromGeoFenceFragment";
    private GeoFenceFragmentBinding geoFenceFragmentBinding;
    private GeoFenceInterface geoFenceInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GeoFence geoFence = new GeoFence();
        geoFenceFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.geo_fence_fragment, null, false);
        View view = geoFenceFragmentBinding.getRoot();
        geoFenceFragmentBinding.setGeoFence(geoFence);
        return view;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        geoFences = new ArrayList<>();
        init();
    }

    private void init() {

        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Select the Pick Up Point");
        if (Static.checkInternet(Objects.requireNonNull(getContext())))
            mViewModel.getGeoFence().enqueue(new Callback<List<GeoFence>>() {
                @Override
                public void onResponse(Call<List<GeoFence>> call, Response<List<GeoFence>> responseGeoFence) {
                    geoFenceResponse(responseGeoFence);
                }

                @Override
                public void onFailure(Call<List<GeoFence>> call, Throwable t) {
                    final SweetAlertDialog sDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.ERROR_TYPE);
                    sDialog.setTitle("ERROR");
                    sDialog.setContentText(String.valueOf(R.string.common_Error));

                    sDialog.show();
                }
            });
        else {
            final SweetAlertDialog sDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
            sDialog.setTitle("ERROR");
            sDialog.setContentText(String.valueOf(R.string.no_internet_response));

            sDialog.show();

        }

    }

    private void geoFenceResponse(Response<List<GeoFence>> responseGeoFence) {
        if (responseGeoFence.isSuccessful()) {
            if (responseGeoFence.body() == null) {
                final SweetAlertDialog sDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.ERROR_TYPE);
                sDialog.setTitle("ERROR");
                sDialog.setContentText(String.valueOf(R.string.common_Error));

                sDialog.show();
            } else {
                geoFences = responseGeoFence.body();
                Collections.sort(geoFences, new CustomComparator());
                Map<Integer, GeoFence> longGeoFenceModelMap = new LinkedHashMap<>();

                for (GeoFence geoFenceModel : responseGeoFence.body()) {
                    longGeoFenceModelMap.put(geoFenceModel.getId(), geoFenceModel);
                }

                mViewModel.setGeoFences(longGeoFenceModelMap);

                RecyclerView recyclerView = geoFenceFragmentBinding.geoFenceFragmentGeoFenceRecyclerView;
                FromGeoFenceRecyclerView fromGeoFenceRecyclerView = new FromGeoFenceRecyclerView(geoFences, getActivity(), geoFenceInterface);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(fromGeoFenceRecyclerView);
            }
        } else {
            final SweetAlertDialog sDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.ERROR_TYPE);
            sDialog.setTitle("ERROR");
            sDialog.setContentText(String.valueOf(R.string.common_Error));

            sDialog.show();
        }
    }

    public static class CustomComparator implements Comparator<GeoFence> {
        @Override
        public int compare(GeoFence o1, GeoFence o2) {
            return Long.compare(o1.getId(), o2.getId());
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        geoFenceInterface = (GeoFenceInterface) context;
    }

}

