package com.example.weather.Model;

import androidx.annotation.NonNull;

public class Coord {
    public double lon;
    public double lat;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @NonNull
    @Override
    public String toString() {
        return "lat: " + this.lat + ", lon: " + this.lon;
    }
}