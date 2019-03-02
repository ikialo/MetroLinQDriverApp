package com.example.isaac.metrolinqdriverapp;

public class LatLong {


    Double lat, lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public LatLong(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
