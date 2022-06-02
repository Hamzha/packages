package com.smart.agriculture.industrial.solutions.packages.ui.Route;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.RouteFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.models.GlobalModel;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.models.Route;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import static com.smart.agriculture.industrial.solutions.packages.utils.Static.checkInternet;

public class RouteFragment extends Fragment implements View.OnClickListener {
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private MapView mMapView;
    private RouteFragmentBinding routeFragmentBinding;
    private ViewModel mViewModel;
    private static final String TAG = "RoutesFragment";
    private List<GlobalModel> globalModel;
    private Context activity;
    private GeoFence geoFenceModelFrom;
    private GeoFence geoFenceModelTo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Route route = new Route();
        routeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.route_fragment, null, false);
        View view = routeFragmentBinding.getRoot();
        routeFragmentBinding.setRoute(route);
        mMapView = routeFragmentBinding.routeFragmentMapViewMap;
        routeFragmentBinding.routeFragmentRefreshImageView.setOnClickListener(this);
        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Estimated Time of Arrival");
        assert getArguments() != null;
        geoFenceModelFrom = getArguments().getParcelable(Constant.GEO_FENCE_MODEL_FROM);
        geoFenceModelTo = getArguments().getParcelable(Constant.GEO_FENCE_MODEL_TO);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        Map<Long, Device> devices = null;
        try {
            devices = mViewModel.getDevicesModels();
        } catch (Exception ex) {
            Static.showToast(String.valueOf(R.string.common_Error), activity);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                routeFragmentBinding.routeFragmentRefreshImageView.callOnClick();
            }
        }, 1000);

        this.globalModel = new ArrayList<>();
        Static.logD(">>!" + TAG, "onActivityCreated");

        mMapView.onCreate(savedInstanceState);
        iteration(devices, geoFenceModelFrom, geoFenceModelTo);
    }

    private void iteration(Map<Long, Device> devices, GeoFence geoFenceModelFrom, GeoFence geoFenceModelTo) {
        Static.logD(">>!" + TAG, "iteration");
        myAsyncTask myAsyncTask = new myAsyncTask(mViewModel, devices, globalModel, activity, geoFenceModelFrom, geoFenceModelTo, routeFragmentBinding);
        myAsyncTask.execute();
    }

    private void eventResponse(List<Event> body, GlobalModel globalModel, String myAsyncTask) throws IOException, CloneNotSupportedException {
        Static.logD(">>" + TAG, "eventResponse");
        Static.logD(">>" + TAG, myAsyncTask);

        Map<Integer, GeoFence> geoFences = mViewModel.getGeoFencesModel();
        List<Event> events = new ArrayList<>();
        assert body != null;
        for (Event event : body) {

            for (Map.Entry<Integer, GeoFence> geoFenceModel : geoFences.entrySet()) {
                if (event.getGeofenceId() != null && event.getGeofenceId().equals(geoFenceModel.getKey())) {
                    events.add(eventDate(event));
                }
            }
        }
        reverseEventList(events);

        globalModel.setEventList(events);
        if (events.size() == 0)
            globalModel.setEvent(null);
        else {
            globalModel.setEvent(events.get(0));
            globalModel.setCurrentGeoFence(geoFences.get(events.get(0).getGeofenceId()));
        }


        getPosition(geoFences, globalModel);
    }

    private void setCartDirection(Map<Integer, GeoFence> geoFences, GlobalModel globalModel) throws IOException, CloneNotSupportedException {
        if (globalModel.getEvent() == null) {
            globalModel.setETA(0);
            return;
        }
        List<Integer> keyList = new ArrayList<>();


        for (Map.Entry<Integer, GeoFence> test : geoFences.entrySet()) {
            GeoFence geoFenceModel = test.getValue();
            keyList.add(Integer.valueOf(geoFenceModel.getName().split("-")[1]));
        }
        Collections.sort(keyList);

        Long firstStop = 0L;
        Long secondStop = 0L;
        List<Event> map = globalModel.getEventList();
        for (int i = 0; i < map.size(); i++) {

            Static.logD(">>" + TAG, i + "CAll");

            Event eventModel = map.get(i);
            if (firstStop == 0 && eventModel.getGeofenceId() == globalModel.getGeoFenceFrom().getId() && i == 0) {
                globalModel.setCartDirection("0");
                Static.logD(TAG, "<---IN--->");
                noOfStops(globalModel, geoFences, keyList);
                return;
            } else if (firstStop == 0 && Integer.valueOf(Objects.requireNonNull(geoFences.get(eventModel.getGeofenceId())).getName().split("-")[1]) == 1 && i == 0) {
                globalModel.setCartDirection("+1");
                Static.logD(TAG, "<---IN--->");
                noOfStops(globalModel, geoFences, keyList);
                return;
            } else if (firstStop == 0 && Integer.valueOf(Objects.requireNonNull(geoFences.get(eventModel.getGeofenceId())).getName().split("-")[1]) == keyList.size() && i == 0) {
                globalModel.setCartDirection("-1");
                Static.logD(TAG, "<---IN--->");
                noOfStops(globalModel, geoFences, keyList);
                return;
            } else {
                if (firstStop == 0) {
                    globalModel.setEvent(eventModel);
                    firstStop = Long.valueOf(Objects.requireNonNull(geoFences.get(eventModel.getGeofenceId())).getName().split("-")[1]);
                } else if (secondStop == 0 && !(firstStop.equals(Long.valueOf(Objects.requireNonNull(geoFences.get(eventModel.getGeofenceId())).getName().split("-")[1])))) {
                    secondStop = Long.valueOf(Objects.requireNonNull(geoFences.get(eventModel.getGeofenceId())).getName().split("-")[1]);
                    globalModel.setCartDirection("-1");
                    if (firstStop > secondStop) {
                        globalModel.setCartDirection("+1");
                    }
//                    if (globalModel.getEvent().getType().toLowerCase().contains("exit")) {
//
//                        if (globalModel.getCurrentGeoFence().getName().split("-")[1].equals("2")) {
//                            if (eventPosition == null)
//                                throw new NullPointerException();
//                            else {
//                                direction = getDirection(eventPosition.get(0).getCourse());
//                                if (direction.equals("S") || direction.equals("SE")) {
//                                    globalModel.setCartDirection("+1");
//                                } else if (direction.equals("NE")) {
//                                    globalModel.setCartDirection("-1");
//                                }
//                            }
//
//                        }
//                        if (globalModel.getCurrentGeoFence().getName().split("-")[1].equals("3")) {
//                            if (eventPosition == null)
//                                throw new NullPointerException();
//                            else {
//                                direction = getDirection(eventPosition.get(0).getCourse());
//
//                                if (direction.equals("SE")) {
//                                    globalModel.setCartDirection("+1");
//                                } else if (direction.equals("NW") || direction.equals("N")) {
//                                    globalModel.setCartDirection("-1");
//                                }
//
//                            }
//                        }
//                        if (globalModel.getCurrentGeoFence().getName().split("-")[1].equals("4")) {
//                            if (eventPosition == null)
//                                throw new NullPointerException();
//                            else {
//                                direction = getDirection(eventPosition.get(0).getCourse());
//
//                                if (direction.equals("W") || direction.equals("SW")) {
//                                    globalModel.setCartDirection("+1");
//                                } else if (direction.equals("N")) {
//                                    globalModel.setCartDirection("-1");
//                                }
//
//                            }
//
//                        }
//                    }
                    globalModel.setCartDirection("-1");

                    Static.logD(TAG, "<---IN--->");
                    noOfStops(globalModel, geoFences, keyList);
                    return;
                }
            }
        }
    }

    private void noOfStops(GlobalModel globalModel, Map<Integer, GeoFence> geoFences, List<Integer> keyList) throws IOException, CloneNotSupportedException {

        Static.logD(TAG, "<------->");

        Queue<Integer> q = new LinkedList<>();
        int gap = 0;
        GeoFence geoFence = (GeoFence) globalModel.getCurrentGeoFence().clone();
        geoFence.setId(Integer.parseInt(geoFence.getName().split("-")[1]));
        GeoFence geoFenceModelFrom = (GeoFence) globalModel.getGeoFenceFrom().clone();
        GeoFence geoFenceModelTo = (GeoFence) globalModel.getGeoFenceTo().clone();
        GeoFence geoFenceModelCurrent = (GeoFence) globalModel.getCurrentGeoFence().clone();
        ////////////*/////////////////

        geoFenceModelFrom.setId(Integer.parseInt(geoFenceModelFrom.getName().split("-")[1]));
        geoFenceModelTo.setId(Integer.parseInt(geoFenceModelTo.getName().split("-")[1]));
        geoFenceModelCurrent.setId(Integer.parseInt(geoFenceModelCurrent.getName().split("-")[1]));

        Static.logD(TAG, "Before From:" + geoFenceModelFrom.getId() +
                ", Current:" + geoFenceModelCurrent.getId() +
                ", Direction:" + globalModel.getCartDirection() +
                ", Geofence:" + geoFence.getId()
                + ", Event:" + globalModel.getEvent().getType());
        if (geoFenceModelCurrent.getId() == geoFenceModelFrom.getId()) {

            if (globalModel.getEvent().getType().contains("Exit")) {
                if (globalModel.getCartDirection().equals("+1")) {
                    geoFence.setId(geoFence.getId() + 1);
//
                    if (geoFence.getId() == 4) {
                        geoFence.setId(4);
                        if (globalModel.getCartDirection().equals("+1"))
                            globalModel.setCartDirection("-1");
                        else
                            globalModel.setCartDirection("+1");
                    }
                    Static.logD(TAG, "upper--After From:" + globalModel.getGeoFenceFrom().getId() +
                            ", Current:" + globalModel.getCurrentGeoFence().getId() +
                            ", Direction:" + globalModel.getCartDirection() +
                            ", Geofence:" + geoFence.getId()
                            + ", Event:" + globalModel.getEvent().getType());

                } else {
                    geoFence.setId(geoFence.getId() - 1);
                    if (geoFence.getId() == 0) {
                        geoFence.setId(geoFence.getId() + 2);
                        if (globalModel.getCartDirection().equals("+1"))
                            globalModel.setCartDirection("-1");
                        else
                            globalModel.setCartDirection("+1");
                    }

                    Static.logD(TAG, "lower--After From:" + globalModel.getGeoFenceFrom().getId() +
                            ", Current:" + globalModel.getCurrentGeoFence().getId() +
                            ", Direction:" + globalModel.getCartDirection() +
                            ", Geofence:" + geoFence.getId()
                            + ", Event:" + globalModel.getEvent().getType());
                }
                calQ(globalModel, gap, geoFence, keyList, q);
            } else {
//            calQ(globalModel,gap,geoFence,keyList,q);
                Static.logD(TAG, "NULLLLLLLLLLLL calling calETA");
                calETA(globalModel, null);
            }
        } else if (globalModel.getCartDirection().equals("-1")) {

            gap = -1;
            while (true) {

                int i = geoFence.getId();
                if (i == keyList.get(keyList.size() - 1)) {
                    gap = -1;
                } else if (i == keyList.get(0)) {
                    gap = +1;
                }
                q.add(i);
                geoFence.setId(i + gap);
                if (geoFence.getId() == geoFenceModelFrom.getId()) {
                    q.add(geoFence.getId());
                    calETA(globalModel, q);
                    break;
                }
            }
        }

        if (globalModel.getCartDirection().equals("+1")) {
            gap = +1;
            while (true) {

                int i = geoFence.getId();
                if (i == keyList.get(keyList.size() - 1)) {
                    gap = -1;
                } else if (i == keyList.get(0)) {
                    gap = +1;
                }
                q.add(i);
                geoFence.setId(i + gap);

                if (geoFence.getId() == geoFenceModelFrom.getId()) {
                    q.add(geoFence.getId());
                    calETA(globalModel, q);
                    break;
                }
            }
        }

    }

    private Queue<Integer> calQ(GlobalModel globalModel, int gap, GeoFence geoFence, List<Integer> keyList, Queue<Integer> q) {
        Static.logD(TAG, globalModel.toString());


        if (globalModel.getCurrentGeoFence().getId() == globalModel.getGeoFenceFrom().getId()) {
            calETA(globalModel, null);
        }

        if (globalModel.getCartDirection().equals("-1")) {

            gap = -1;
            while (true) {

                int i = geoFence.getId();
                if (i == keyList.get(keyList.size() - 1)) {
                    gap = -1;
                } else if (i == keyList.get(0)) {
                    gap = +1;
                }
                q.add(i);
                geoFence.setId(i + gap);
                if (geoFence.getId() == geoFenceModelFrom.getId()) {
                    q.add(geoFence.getId());
                    calETA(globalModel, q);
                    break;
                }
            }
        }

        if (globalModel.getCartDirection().equals("+1")) {
            gap = +1;
            while (true) {
                Static.logD(TAG, "test");
                int i = geoFence.getId();
                if (i == keyList.get(keyList.size() - 1)) {
                    gap = -1;
                } else if (i == keyList.get(0)) {
                    gap = +1;
                }
                q.add(i);
                geoFence.setId(i + gap);

                if (geoFence.getId() == geoFenceModelFrom.getId()) {
                    q.add(geoFence.getId());
                    calETA(globalModel, q);
                    break;
                }
            }
        }
        return q;
    }

    private void calETA(GlobalModel globalModel, Queue<Integer> q) {

        globalModel.getCurrentGeoFence().setId(Integer.parseInt(globalModel.getCurrentGeoFence().getName().split("-")[1]));
        globalModel.getGeoFenceFrom().setId(Integer.parseInt(globalModel.getGeoFenceFrom().getName().split("-")[1]));
        globalModel.getGeoFenceTo().setId(Integer.parseInt(globalModel.getGeoFenceTo().getName().split("-")[1]));
        long ETA = 0;
        if (q != null) {

            while (!q.isEmpty()) {

                int pre = q.remove();
                if (!q.isEmpty()) {
                    if (pre == 1) {
                        ETA = ETA + (20 * 60);
                    }
                }
                ETA = ETA + (40 + 60);
                Static.logD(TAG, String.valueOf(pre) + globalModel.getDevice().getName());
            }
            if (globalModel.getCurrentGeoFence().getName().split("-")[1].equals("1")) {
                if (globalModel.getEvent().getType().toLowerCase().contains("exit"))
                    ETA = ETA - (20 * 60);
            }
            if (globalModel.getEvent().getType().toLowerCase().contains("exit") && ETA != 0) {
                ETA = ETA - 60;
            }
            ETA = ETA - 100;
            if (ETA < 0)
                ETA = 0;
        } else {
            Static.logD(TAG, "NULLLLLLLLLLLL");
            globalModel.setETA(0);

        }
        Static.logD(TAG, "NOW : " + ETA);
        globalModel.setETA(ETA);
        return;
    }

    private void getPosition(Map<Integer, GeoFence> geoFences, GlobalModel globalModel) throws IOException, CloneNotSupportedException {

        List<Position> body = mViewModel.getPosition().execute().body();

        if (body == null)
            retryDialogBox(Constant.LATE_RESPONSE_CODE, getContext());
        else {
            for (Position positionModel : body) {
                if (globalModel.getDevice().getId() == positionModel.getDeviceId()) {
                    globalModel.setPosition(positionModel);
                }
            }

            positionResponse(body, globalModel, geoFences);
        }

    }

    private void positionResponse(List<Position> positions, GlobalModel globalModel, Map<Integer, GeoFence> geoFences) throws IOException, CloneNotSupportedException {
        for (Position positionModel : positions) {
            if (globalModel.getDevice().getId() == positionModel.getDeviceId()) {
                globalModel.setPosition(positionModel);
            }
        }
        Static.logD(">>" + TAG, "positionResponse");
        setCartDirection(geoFences, globalModel);
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) Objects.requireNonNull(activity).getDrawable(id);

            assert vectorDrawable != null;
            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }


    //    @Override
    public void onClick(View v) {
        Handler handler = new Handler();


        handler.postDelayed(() -> {
            routeFragmentBinding.routeFragmentRefreshImageView.callOnClick();
        }, 10000);

        if (getActivity() != null)
            if (checkInternet(getActivity())) {
                Static.logD(">>" + TAG, "onClick ");
                RetryAsyncTask retryAsyncTask = new RetryAsyncTask(mViewModel, globalModel, activity, routeFragmentBinding);

                retryAsyncTask.execute();

            } else {
                retryDialogBox(Constant.NO_INTERNET_CODE, getContext());
            }

    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = context;
    }

    private Event eventDate(Event event) {
        try {
            if (event.getServerTime().split("\\.").length == 2) {
                String tmp1 = event.getServerTime().split("\\.")[0];
                String[] tmp2 = tmp1.split("T");
                event.setServerTime(tmp2[0] + " " + tmp2[1]);
            }
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            event.setDate(Objects.requireNonNull(format.parse(event.getServerTime())).getTime() + 18000000);
            return event;
        } catch (Exception e) {
            return null;
        }
    }

    private String[] getTimeInterval() {

        Date today = new Date();

        Calendar cal = new GregorianCalendar();
        cal.setTime(today);
        cal.add(Calendar.DAY_OF_MONTH, -1);

        String startDate = sdf.format(cal.getTime());
        String endDate = sdf.format(today.getTime());
        return new String[]{startDate, endDate};
    }

    private String[] getTimeIntervalCurrent(String time) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat par = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastEventTime = null;
        try {
            lastEventTime = par.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currentTime = new Date();

        String startDate = sdf.format(lastEventTime.getTime());
        String endDate = sdf.format(currentTime.getTime());
        return new String[]{startDate, endDate};
    }


    private void reverseEventList(List<Event> events) {
        int size = events.size();
        for (int i = 0; i < size / 2; i++) {
            final Event food = events.get(i);
            events.set(i, events.get(size - i - 1)); // swap
            events.set(size - i - 1, food); // swap

        }
    }

    private void retryDialogBox(int code, Context context) {
        Static.retry(context, code, bool -> {
            if (!bool) {
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();
            }
        });
    }

    private String getDirection(Float course) {

        if (course >= 337.50 && course < 22.50) {
            return "N";
        } else if (course >= 22.50 && course < 67.50) {
            return "NE";
        } else if (course >= 67.50 && course < 112.50) {
            return "E";
        } else if (course >= 112.50 && course < 157.50) {
            return "SE";
        } else if (course >= 157.50 && course < 202.5) {
            return "S";
        } else if (course >= 202.50 && course < 247.50) {
            return "SW";
        } else if (course >= 247.50 && course < 292.50) {
            return "W";
        } else if (course >= 292.50 && course < 337.50) {
            return "NW";
        }
        // 'N' => array(348.75-11.25, 11.25+11.25),
        // 'NE' => array(33.75-11.25, 56.25+11.25),
        // 'E' => array(78.75-11.25, 101.25+11.25),
        // 'SE' => array(123.75-11.25, 146.25+11.25),
        // 'S' => array(168.75-11.25, 191.25+11.25),
        // 'SW' => array(213.75-11.25, 236.25+11.25),
        // 'W' => array(258.75-11.25, 281.25+11.25),
        // 'NW' => array(303.75-11.25, 326.25+11.25),
        return null;
    }

    @SuppressLint("DefaultLocale")
    private void setupMap(List<GlobalModel> globalModels) {

        StringBuilder text = new StringBuilder();

        for (GlobalModel globalModel : globalModels) {
            text.append(globalModel.getDevice().getName()).append("\n");
            if (globalModel.getCurrentGeoFence() != null && globalModel.getGeoFenceFrom() != null)
                if (globalModel.getCurrentGeoFence().getId() == globalModel.getGeoFenceFrom().getId() && !globalModel.getEvent().getType().toLowerCase().contains("exit"))
                    globalModel.setETA(00);

            double totalMilli = globalModel.getETA();

            int[] time = miliSecConversion((long) totalMilli);
            if (globalModel.getDevice().getAttributes().getStatus().toLowerCase().equals("active")) {
                if (globalModel.getPosition().getPositionAttributes() != null && globalModel.getETA() == totalMilli && (globalModel.getPosition().getPositionAttributes().getIgnition() || globalModel.getPosition().getPositionAttributes().getMotion()))
                {

                    text.append("ETA : ").append(time[1]).append(" min: ").append(time[0]).append(" sec");
                    Static.logD(TAG, "globalModel.getCartDirection()" + globalModel.getCartDirection());

                    if (globalModel.getCartDirection() == null) {
                        text.append("\nDirection : NULL\n\n");

                    } else if (globalModel.getCartDirection().equals("-1")) {
                        text.append("\nDirection : Towards Mall\n\n");
                    } else if (globalModel.getCartDirection().equals("+1")) {
                        text.append("\nDirection : Towards Offices\n\n");

                    }
                }
                if (globalModel.getPosition().getPositionAttributes() == null || ! (globalModel.getPosition().getPositionAttributes().getIgnition() || globalModel.getPosition().getPositionAttributes().getMotion()) ) {
                    text.append("ETA : ").append("N/A").append("\n");
                    text.append("Ignition is Off ").append("\n\n");
                }
            } else
                text.append("Disabled").append("\n\n");
        }
        routeFragmentBinding.routeFragmentRouteTextView.setText(text);
        this.globalModel = globalModels;

        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(googleMap1 -> {
            MarkerOptions markerOptions;
            googleMap1.clear();

            for (GlobalModel globalModel : globalModels) {
                Static.logD("<><>" + TAG, globalModel.toString());

                markerOptions = new MarkerOptions()
                        .position(new LatLng(globalModel.getPosition().getLatitude(), globalModel.getPosition().getLongitude()))
                        .icon(getBitmapDescriptor(R.drawable.ic_live_car_online))
                        .rotation(globalModel.getPosition().getCourse());
                googleMap1.addMarker(markerOptions);

            }
            googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.468421, 74.351717), 16));
            Static.addGeoFences(activity, googleMap1, mViewModel.getGeoFencesModel());

            googleMap1.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        });
    }

    @SuppressLint("StaticFieldLeak")
    class myAsyncTask extends AsyncTask<Void, Void, List<GlobalModel>> {
        Context activity;
        List<GlobalModel> globalModel;
        RouteFragmentBinding routeFragmentBinding;

        myAsyncTask(ViewModel mViewModel, Map<Long, Device> devices, List<GlobalModel> globalModel, Context activity, GeoFence geoFenceModelFrom, GeoFence geoFenceModelTo, RouteFragmentBinding routeFragmentBinding) {
            WeakReference<Context> activityReference = new WeakReference<>(activity);
            this.activity = activityReference.get();
            this.globalModel = globalModel;
            this.routeFragmentBinding = routeFragmentBinding;

            for (Map.Entry<Long, Device> device : devices.entrySet()) {
                if (device.getValue().getCategory().toLowerCase().contains("trolleybus")) {
                    Static.logD(">>" + TAG, "myAsyncTask");
                    GlobalModel globalModel1 = new GlobalModel();
                    globalModel1.setGeoFenceFrom(geoFenceModelFrom);
                    globalModel1.setGeoFenceTo(geoFenceModelTo);
                    globalModel1.setDevice(device.getValue());
                    this.globalModel.add(globalModel1);
                }
            }

            for (int i = 0; i < this.globalModel.size(); i++) {
                Static.logD(TAG, this.globalModel.get(i).toString());
            }

        }

        @Override
        protected List<GlobalModel> doInBackground(Void... voids) {
            String[] time = getTimeInterval();
            for (GlobalModel globalModel : this.globalModel) {
                Static.logD(">>" + TAG, "myAsyncTask > DoinBackground");
                try {
                    List<Event> body = mViewModel.getEvents((int) globalModel.getDevice().getId(), time[0], time[1]).execute().body();
                    if (body == null)
                        return null;
                    eventResponse(body, globalModel, "myAsyncTask");
                } catch (IOException e) {
                    return null;
                } catch (CloneNotSupportedException e) {
                    return null;
                }
            }
            return globalModel;
        }

        @Override
        protected void onPostExecute(List<GlobalModel> globalModels) {
            super.onPostExecute(globalModels);
            this.globalModel = globalModels;
            if (globalModels == null) {
                Static.logD(">>" + TAG, "myAsyncTask > DoinBackground");

                retryDialogBox(Constant.LATE_RESPONSE_CODE, this.activity);
            }
            setupMap(this.globalModel);

        }
    }

//    private void eventResponseRetry(List<Event> body, GlobalModel globalModel) throws IOException {
//
//        Map<Integer, GeoFence> geoFences = mViewModel.getGeoFencesModel();
//        List<Event> events = new ArrayList<>();
//        assert body != null;
//        for (Event event : body) {
//
//            for (Map.Entry<Integer, GeoFence> geoFenceModel : geoFences.entrySet()) {
//                if (event.getGeofenceId() != null && event.getGeofenceId().equals(geoFenceModel.getKey())) {
//                    events.add(eventDate(event));
//                }
//            }
//        }
//
//        reverseEventList(events);
//
//        globalModel.setEventList(body);
//        getPosition(geoFences, globalModel);
//    }

    @SuppressLint("StaticFieldLeak")
    private
    class RetryAsyncTask extends AsyncTask<Void, Void, List<GlobalModel>> {
        Context activity;
        List<GlobalModel> globalModel;
        RouteFragmentBinding routeFragmentBinding;

        public RetryAsyncTask(ViewModel mViewModel, List<GlobalModel> globalModel, Context activity, RouteFragmentBinding routeFragmentBinding) {
            WeakReference<Context> activityReference = new WeakReference<>(activity);
            this.activity = activityReference.get();

            this.globalModel = globalModel;
            this.routeFragmentBinding = routeFragmentBinding;
        }

        @Override
        protected List<GlobalModel> doInBackground(Void... voids) {

            for (GlobalModel globalModel : this.globalModel) {
                if (globalModel.getEvent() == null) {
                    globalModel.setETA(0);
                    String[] times = getTimeInterval();
                    try {
                        List<Event> events = mViewModel.getEvents((int) globalModel.getDevice().getId(), times[0], times[1]).execute().body();
                        Static.logD(">>" + TAG, "RetryAsyncTask > DoinBackground");

                        eventResponse(events, globalModel, "RetryAsyncTask doInBackground");
                    } catch (IOException e) {
                        return null;
                    } catch (CloneNotSupportedException e) {
                        return null;
                    }
                } else {

                    String[] time = getTimeInterval();

                    try {
                        List<Event> events = mViewModel.getEvents((int) globalModel.getDevice().getId(), time[0], time[1]).execute().body();



                        eventResponse(events, globalModel, "RetryAsyncTask doInBackground");
//                        eventResponseRetry(events, globalModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return this.globalModel;
        }

        @Override
        protected void onPostExecute(List<GlobalModel> globalModels) {
            super.onPostExecute(globalModels);
            this.globalModel = globalModels;
            if (globalModels == null) {
                retryDialogBox(Constant.LATE_RESPONSE_CODE, this.activity);
            }
            setupMap(this.globalModel);
        }
    }


    private int[] miliSecConversion(long milliseconds) {
        int[] time = new int[2];
        time[0] = (int) ((milliseconds) % 60);
        time[1] = (int) ((milliseconds) / 60);
        return time;
    }

}