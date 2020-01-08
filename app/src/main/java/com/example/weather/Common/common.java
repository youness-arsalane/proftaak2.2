package com.example.weather.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class common {
    public static final String API_ID="6e980709e53be6f51457dc27edcc0aaa";
    public static Location current_location=null;

    public static String convertUnixToDate(long dt) {
        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm EEE MM YYYY");
        String formatted=sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date=new Date(dt*1000L);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        String formatted=sdf.format(date);
        return formatted;

    }
}
