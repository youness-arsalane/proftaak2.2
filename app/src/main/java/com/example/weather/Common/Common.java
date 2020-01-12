package com.example.weather.Common;

import android.annotation.SuppressLint;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String API_ID = "6e980709e53be6f51457dc27edcc0aaa";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEEE DD MMMM YYYY HH:mm");
        return sdf.format(date);
    }

    public static String convertUnixToTime(long dt) {
        Date date = new Date(dt * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    public static String convertWindDegreesToDirection(double degrees) {
        String[] directions = {
                "N", "NNE", "NE", "ENE",
                "E", "ESE", "SE", "SSE",
                "S", "SSW", "SW", "WSW",
                "W", "WNW", "NW", "NNW",
                "N"};
        return directions[(int) Math.floor(((degrees + 11.25) % 360) / 22.5)];
    }
}