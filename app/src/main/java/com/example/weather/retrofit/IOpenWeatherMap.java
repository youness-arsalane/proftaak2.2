package com.example.weather.retrofit;

import com.example.weather.model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    /**
     * @param lat Latitude
     * @param lng Longitude
     * @param appid appId
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
     * @param appid appId
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