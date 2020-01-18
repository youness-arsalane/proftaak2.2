package com.example.weather.common;

import android.annotation.SuppressLint;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String baseURL = "http://api.openweathermap.org/data/2.5/";
    public static final String appId = "6e980709e53be6f51457dc27edcc0aaa";
    public static Location currentLocation = null;

    /**
     * @param dt Unix Timestamp
     * @return returns to String formatted Date object
     *
     * Converts Unix Timestamp to Date and then formats it to a readable date
     */
    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE DD MMMM YYYY HH:mm");
        return sdf.format(date);
    }

    /**
     * @param dt Unix Timestamp
     * @return returns to String formatted Date object
     *
     * Converts Unix Timestamp to Date and then formats it to a readable time
     */
    public static String convertUnixToTime(long dt) {
        Date date = new Date(dt * 1000L);

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * @param degrees *
     * @return Readable direction
     * Converts a double to a readable direction
     */
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