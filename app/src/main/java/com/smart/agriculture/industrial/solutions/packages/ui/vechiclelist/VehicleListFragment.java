package com.smart.agriculture.industrial.solutions.packages.ui.vechiclelist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
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
import com.smart.agriculture.industrial.solutions.packages.databinding.VehicleListFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.ui.admin.AdminPanelFragment;
import com.smart.agriculture.industrial.solutions.packages.ui.admin.AdminPanelRecyclerView;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Response;

public class VehicleListFragment extends Fragment {

    private com.smart.agriculture.industrial.solutions.packages.ui.ViewModel mViewModel;
    private VehicleListFragmentBinding vehicleListFragmentBinding;
    private Map<Long, Device> devices;
    private RecyclerView recyclerView;
    private VehicleListRecyclerView vehicleListRecyclerView;
    public static VehicleListFragment newInstance() {
        return new VehicleListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Device device = new Device();
        vehicleListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.vehicle_list_fragment, null, false);
        View view = vehicleListFragmentBinding.getRoot();
        vehicleListFragmentBinding.setDevice(device);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);

        recyclerView = vehicleListFragmentBinding.vehicleListFragmentRecyclerView;
        // TODO: Use the ViewModel
        devices = mViewModel.getDevicesModels();
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Select the Vehicle");
        init();
    }

    private void init() {
        if (Static.checkInternet(Objects.requireNonNull(getContext()))) {
            List<Device> deviceList = getDeviceList(devices);
            vehicleListRecyclerView = new VehicleListRecyclerView(deviceList, getActivity());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(vehicleListRecyclerView);
        }
    }

    private List<Device> getDeviceList(Map<Long, Device> devices) {
        List<Device> deviceList = new ArrayList<>();
        for (Map.Entry<Long, Device> longDeviceMap : devices.entrySet()) {
            deviceList.add(longDeviceMap.getValue());
        }
        return deviceList;
    }

}