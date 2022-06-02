package com.smart.agriculture.industrial.solutions.packages.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.android.gms.maps.MapView;

public class Route extends BaseObservable {
    private String ETA;
    private MapView mapView;
    private String Time;
    private String ignition;

    @Bindable
    public String getETA() {
        return ETA;
    }


    public void setETA(String ETA) {
        this.ETA = ETA;
    }

    @Bindable
    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    @Bindable
    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Bindable
    public String getIgnition() {
        return ignition;
    }

    public void setIgnition(String ignition) {
        this.ignition = ignition;
    }
}
