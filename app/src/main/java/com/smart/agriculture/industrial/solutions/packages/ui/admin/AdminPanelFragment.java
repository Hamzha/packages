package com.smart.agriculture.industrial.solutions.packages.ui.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.AdminPanelFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPanelFragment extends Fragment {
    private ViewModel mViewModel;
    private AdminPanelFragmentBinding adminPadenAdaptorBinding;
    private Map<Long, Device> devices;
    private static final String TAG = "AdminPanelFragment";
    private SwipeRefreshLayout swipeContainer;
    private RecyclerView recyclerView;
    private AdminPanelRecyclerView adminPanelRecyclerView;

    public static AdminPanelFragment newInstance() {
        return new AdminPanelFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Device device = new Device();
        adminPadenAdaptorBinding = DataBindingUtil.inflate(inflater, R.layout.admin_panel_fragment, null, false);
        View view = adminPadenAdaptorBinding.getRoot();
        adminPadenAdaptorBinding.setDevice(device);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        devices = mViewModel.getDevicesModels();
        recyclerView = adminPadenAdaptorBinding.adminPanelFragmentGeoFenceRecyclerView;
        swipeContainer = adminPadenAdaptorBinding.swipeContainer;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(() -> {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully..
            List<Device> deviceList = getDeviceList(devices);
            MyAsyncTaskRefresh myAsyncTask = new MyAsyncTaskRefresh(mViewModel, deviceList, getActivity());
            myAsyncTask.execute();
        });

        init();
    }

    private void init() {

        if (Static.checkInternet(Objects.requireNonNull(getContext()))) {
            List<Device> deviceList = getDeviceList(devices);
            MyAsyncTask myAsyncTask = new MyAsyncTask(this.mViewModel, deviceList, getActivity());
            myAsyncTask.execute();
        } else {
            final SweetAlertDialog sDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);

            Static.logD(TAG, "init");

            sDialog.setTitle("ERROR");
            sDialog.setContentText(String.valueOf(R.string.no_internet_response));
            sDialog.show();
        }
    }


    private List<Device> getDeviceList(Map<Long, Device> devices) {
        List<Device> deviceList = new ArrayList<>();
        for (Map.Entry<Long, Device> longDeviceMap : devices.entrySet()) {
            deviceList.add(longDeviceMap.getValue());
        }
        return deviceList;
    }


    @SuppressLint("StaticFieldLeak")
    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        ViewModel viewModel;
        List<Device> deviceList;
        Activity context;

        MyAsyncTask(ViewModel viewModel, List<Device> devices, Activity context) {
            this.deviceList = devices;
            this.viewModel = viewModel;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                List<Position> body = mViewModel.getPosition().execute().body();

                mViewModel.getPosition().enqueue(new Callback<List<Position>>() {
                    @Override
                    public void onResponse(Call<List<Position>> call, Response<List<Position>> response) {
                        Static.logD(TAG,"Here" + response.message() + "<->" + response.headers());
                    }

                    @Override
                    public void onFailure(Call<List<Position>> call, Throwable t) {
                        Static.logD(TAG,t.getLocalizedMessage());
                    }
                });



                List<Position> positionList = new ArrayList<>();
                for (Device device : deviceList) {
                    Static.logD(TAG, device.toString());
                    if (body != null) {
                        for (Position position : body) {
                            if (position.getDeviceId() == device.getId()) {
                                positionList.add(position);
                                Static.logD(TAG, position.toString());
                            }
                        }
                    } else {
                        context.runOnUiThread(() -> {
                            final SweetAlertDialog sDialog = new SweetAlertDialog(this.context, SweetAlertDialog.ERROR_TYPE);
                            sDialog.setTitle("ERROR");
                            Static.logD(TAG, "here here" + body);

                            sDialog.setContentText(String.valueOf(R.string.common_Error));
                            sDialog.show();
                        });
                    }
                }

                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {

                    // Stuff that updates the UI
                    setUpRecyclerView(deviceList, positionList);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void setUpRecyclerView(List<Device> deviceList, List<Position> positionList) {
        adminPanelRecyclerView = new AdminPanelRecyclerView(deviceList, getActivity(), mViewModel, positionList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adminPanelRecyclerView);
    }

    @SuppressLint("StaticFieldLeak")
    public class MyAsyncTaskRefresh extends AsyncTask<Void, Void, Void> {
        ViewModel viewModel;
        List<Device> deviceList;
        Activity context;

        MyAsyncTaskRefresh(ViewModel viewModel, List<Device> devices, Activity context) {
            this.deviceList = devices;
            this.viewModel = viewModel;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                if (Static.checkInternet(context)) {
                    Response<List<Device>> response = viewModel.getDevices().execute();
                    if (response.code() == 200)
                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                            // Stuff that updates the UI
                            setUpRecyclerViewRefresh(response.body());
                        });
                    else {
                        context.runOnUiThread(() -> {
                            final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                            sDialog.setTitle("ERROR");
                            sDialog.setContentText(String.valueOf(R.string.common_Error));
                            sDialog.show();
                            swipeContainer.setRefreshing(false);
                        });

                    }
                } else {

                    context.runOnUiThread(() -> {
                        final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                        sDialog.setTitle("ERROR");
                        sDialog.setContentText(String.valueOf(R.string.no_internet_response));
                        sDialog.show();
                        swipeContainer.setRefreshing(false);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setUpRecyclerViewRefresh(List<Device> deviceList) {

        adminPanelRecyclerView.setData(deviceList);
        adminPanelRecyclerView.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
    }

}
