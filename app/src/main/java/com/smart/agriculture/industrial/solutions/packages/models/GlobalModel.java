package com.smart.agriculture.industrial.solutions.packages.models;

import java.util.List;

public class GlobalModel implements Cloneable {
    private GeoFence currentGeoFence;
    private String cartDirection;
    private Event event;
    private GeoFence geoFenceFrom;
    private GeoFence geoFenceTo;
    private String userDirection;
    private Device device;
    private Position position;
    private List<Event> eventList;
    private double ETA;

    public String getCartDirection() {
        return cartDirection;
    }

    public void setCartDirection(String cartDirection) {
        this.cartDirection = cartDirection;
    }

    public GeoFence getCurrentGeoFence() {
        return currentGeoFence;
    }

    public void setCurrentGeoFence(GeoFence currentGeoFence) {
        this.currentGeoFence = currentGeoFence;
    }

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public GeoFence getGeoFenceFrom() {
        return geoFenceFrom;
    }

    public void setGeoFenceFrom(GeoFence geoFenceFrom) {
        this.geoFenceFrom = geoFenceFrom;
    }

    public GeoFence getGeoFenceTo() {
        return geoFenceTo;
    }

    public void setGeoFenceTo(GeoFence geoFenceTo) {
        this.geoFenceTo = geoFenceTo;
    }

    public void setUserDirection(String userDirection) {
        this.userDirection = userDirection;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getUserDirection(){
        String direction  = "0";
        if(this.getGeoFenceFrom().getId() < this.getGeoFenceTo().getId()){
            direction = "+1";
        }else{
            direction = "-1";
        }
        return direction;
    }

    public double getETA() {
        return ETA;
    }

    public void setETA(double ETA) {
        this.ETA = ETA;
    }

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    @Override
    public String toString() {
        return "GlobalModel{" +
                "\ncartDirection='" + cartDirection + '\'' +
                ", \nuserDirection='" + getUserDirection() + '\'' +
                ", \ncurrentGeoFence=" + currentGeoFence +
                ", \nevent=" + event +
                ", \ngeoFenceFrom=" + geoFenceFrom +
                ", \ngeoFenceTo=" + geoFenceTo +
                ", \nposition=" + position +
                ", \ndevice=" + device +
                ", \nETA=" + ETA +
                '}';
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
