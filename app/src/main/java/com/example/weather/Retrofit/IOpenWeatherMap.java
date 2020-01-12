package com.example.weather.Retrofit;

import com.example.weather.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(
            @Query("lat") String lat,
            @Query("lon") String lng,
            @Query("appid") String appid,
            @Query("units") String unit
    );


    @GET("weather")
    Observable<WeatherResult> getWeatherBySearchQuery(
            @Query("q") String q,
            @Query("appid") String appid,
            @Query("units") String unit
    );
}