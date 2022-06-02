package com.smart.agriculture.industrial.solutions.packages;

import android.app.Application;

import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.models.Position;

import java.net.CookieManager;
import java.util.Map;

public class Singleton extends Application {

    Map<Long, Position> positionsFinal;
    Map<Long, Device> devicesFinal;
    Map<Integer, GeoFence> geoFences;

    public Map<Long, Position> getPositionsFinal() {
        return positionsFinal;
    }

    public void setPositionsFinal(Map<Long, Position> positionsFinal) {
        this.positionsFinal = positionsFinal;
    }

    public Map<Long, Device> getDevicesFinal() {
        return devicesFinal;
    }

    public void setDevicesFinal(Map<Long, Device> devicesFinal) {
        this.devicesFinal = devicesFinal;
    }

    public Map<Integer, GeoFence> getGeoFences() {
        return geoFences;
    }

    public void setGeoFences(Map<Integer, GeoFence> geoFences) {
        this.geoFences = geoFences;
    }

}
