package com.uni.treest.models;

import com.google.android.gms.maps.model.LatLng;

public class Station {
    private String sName;
    private LatLng latLng;

    public Station(String sName, LatLng latLng) {
        this.sName = sName;
        this.latLng = latLng;
    }

    public String getsName() {
        return sName;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String toString() {
        return "Station{" +
                "sName='" + sName + '\'' +
                ", latLng=" + latLng +
                '}';
    }
}
