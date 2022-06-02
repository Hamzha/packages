package com.smart.agriculture.industrial.solutions.packages.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PositionAttributes implements Parcelable {

    @SerializedName("alarm")
    @Expose
    private String alarm;
    @SerializedName("ignition")
    @Expose
    private Boolean ignition;
    @SerializedName("door")
    @Expose
    private Boolean door;
    @SerializedName("fuel1")
    @Expose
    private Double fuel1;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("totalDistance")
    @Expose
    private Double totalDistance;
    @SerializedName("motion")
    @Expose
    private Boolean motion;
    @SerializedName("hours")
    @Expose
    private Double hours;
    @SerializedName("power")
    @Expose
    private Double power;
    @SerializedName("out1")
    @Expose
    private boolean out1;

    public final static Creator<PositionAttributes> CREATOR = new Creator<PositionAttributes>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PositionAttributes createFromParcel(Parcel in) {
            return new PositionAttributes(in);
        }

        public PositionAttributes[] newArray(int size) {
            return (new PositionAttributes[size]);
        }

    };

    private PositionAttributes(Parcel in) {
        this.alarm = ((String) in.readValue((String.class.getClassLoader())));
        this.ignition = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.door = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.fuel1 = ((Double) in.readValue((Double.class.getClassLoader())));
        this.distance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.totalDistance = ((Double) in.readValue((Double.class.getClassLoader())));
        this.motion = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.hours = ((Double) in.readValue((Integer.class.getClassLoader())));
        this.power = ((Double) in.readValue((Double.class.getClassLoader())));
        this.out1 = ((boolean) in.readValue((boolean.class.getClassLoader())));

    }

    public PositionAttributes() {
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public Boolean getIgnition() {
        return ignition;
    }

    public void setIgnition(Boolean ignition) {
        this.ignition = ignition;
    }

    public Boolean getDoor() {
        return door;
    }

    public void setDoor(Boolean door) {
        this.door = door;
    }

    public Double getFuel1() {
        return fuel1;
    }

    public void setFuel1(Double fuel1) {
        this.fuel1 = fuel1;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Boolean getMotion() {
        return motion;
    }

    public void setMotion(Boolean motion) {
        this.motion = motion;
    }

    public Double getHours() {
        return hours;
    }

    public void setHours(Double hours) {
        this.hours = hours;
    }

    public Double getPower() {
        return power;
    }

    public void setPower(Double power) {
        this.power = power;
    }

    public boolean isOut1() {
        return out1;
    }

    public void setOut1(boolean out1) {
        this.out1 = out1;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(alarm);
        dest.writeValue(ignition);
        dest.writeValue(door);
        dest.writeValue(fuel1);
        dest.writeValue(distance);
        dest.writeValue(totalDistance);
        dest.writeValue(motion);
        dest.writeValue(hours);
        dest.writeValue(power);
        dest.writeValue(out1);
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "PositionAttributes{" +
                "alarm='" + alarm + '\'' +
                ", ignition=" + ignition +
                ", door=" + door +
                ", fuel1=" + fuel1 +
                ", distance=" + distance +
                ", totalDistance=" + totalDistance +
                ", motion=" + motion +
                ", hours=" + hours +
                ", power=" + power +
                ", out1=" + out1 +
                '}';
    }
}