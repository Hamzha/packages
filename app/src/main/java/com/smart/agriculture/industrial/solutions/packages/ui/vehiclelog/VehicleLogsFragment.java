package com.smart.agriculture.industrial.solutions.packages.ui.vehiclelog;

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
import com.smart.agriculture.industrial.solutions.packages.databinding.VehicleLogsFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.URLS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleLogsFragment extends Fragment {

    private ViewModel mViewModel;
    private Device device;
    private Date dateStart;
    private Date dateEnd;
    private static final String TAG = ">>>VehicleLogsFragment";
    static DateFormat format = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss", Locale.ENGLISH);
    private SimpleDateFormat formatTraccar = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    VehicleLogsFragmentBinding vehicleLogsFragmentBinding;
    private RecyclerView recyclerView;

    public static VehicleLogsFragment newInstance() {
        return new VehicleLogsFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Event event = new Event();
        vehicleLogsFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.vehicle_logs_fragment, null, false);
        View view = vehicleLogsFragmentBinding.getRoot();
        vehicleLogsFragmentBinding.setEvent(event);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        // TODO: Use the ViewModel

        Bundle data = getActivity().getIntent().getExtras();
        assert data != null;
//        device = (Device) data.getSerializable("Device");
//        dateStart = (Date) data.getSerializable("StartDate");
//        dateEnd = (Date) data.getSerializable("EndDate");
//        Static.logD(TAG, device.getName());
//        Static.logD(TAG, format.format(device.getName()));
//        Static.logD(TAG, format.format(device.getName()));
        assert getArguments() != null;
        Device device = getArguments().getParcelable("Device");
        String startDate = getArguments().getString("startDate");
        String endDate = getArguments().getString("endDate");


        try {
            if (startDate != null && endDate != null && device != null) {
                Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Logs/" + device.getName());
                Static.logD(TAG, URLS.events((int) device.getId(), startDate, endDate));

                Call<List<Event>> events = mViewModel.getEvents((int) device.getId(), formatTraccar.format(format.parse(startDate).getTime()), formatTraccar.format(format.parse(endDate).getTime()));

                mViewModel.getGeoFence().enqueue(new Callback<List<GeoFence>>() {
                    @Override
                    public void onResponse(Call<List<GeoFence>> call, Response<List<GeoFence>> response) {
                        if (response.body() != null)
                            events.enqueue(new Callback<List<Event>>() {
                                @Override
                                public void onResponse(Call<List<Event>> call, Response<List<Event>> responseEvent) {
                                    if (responseEvent.body() != null) {
                                        List<Event> finalList = new ArrayList<>();
                                        for (Event event : responseEvent.body()) {
                                            if (event.getType() != null && event.getType().toLowerCase().contains("geofence")) {

                                                for (GeoFence geofence : response.body()) {
                                                    if(event.getGeofenceId() == geofence.getId()){
                                                        if(event.getType().contains("Enter")){
                                                            event.setType("Enter " + geofence.getName());
                                                        }else if(event.getType().contains("Exit")){
                                                            event.setType("Exit " + geofence.getName());
                                                        }
                                                        finalList.add(event);
                                                    }
                                                }
                                            }
                                        }
                                        recyclerView = vehicleLogsFragmentBinding.vehicleEventLogsRecyclerView;
                                        VehicleLogRecyclerView vehicleListRecyclerView = new VehicleLogRecyclerView(finalList);

                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                        recyclerView.setLayoutManager(layoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(vehicleListRecyclerView);

                                    } else {
                                        Static.showToast("No Events!", getContext());
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<Event>> call, Throwable t) {

                                }
                            });

                    }

                    @Override
                    public void onFailure(Call<List<GeoFence>> call, Throwable t) {

                    }
                });


            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}