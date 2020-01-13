package com.example.weather.Retrofit;

import com.example.weather.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    /**
     * @param lat Latitude
     * @param lng Longitude
     * @param appid APP_ID
     * @param unit Unit measurement
     * @return WeatherResult containing response data by
     * Uses Retrofit2 to make API call with given parameters
     */
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(
            @Query("lat") String lat,
            @Query("lon") String lng,
            @Query("appid") String appid,
            @Query("units") String unit
    );

    /**
     * @param q Query
     * @param appid APP_ID
     * @param unit Unit measurement
     * @return WeatherResult containing response data by
     * Uses Retrofit2 to make API call with given parameters
     */
    @GET("weather")
    Observable<WeatherResult> getWeatherBySearchQuery(
            @Query("q") String q,
            @Query("appid") String appid,
            @Query("units") String unit
    );
}