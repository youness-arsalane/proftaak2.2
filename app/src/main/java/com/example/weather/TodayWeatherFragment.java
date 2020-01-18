package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.weather.adapter.ViewPagerAdapter;
import com.example.weather.common.Common;
import com.example.weather.dbHandler.RecentSearchesDBHelper;
import com.example.weather.model.WeatherResult;
import com.example.weather.retrofit.IOpenWeatherMap;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodayWeatherFragment extends Fragment {
    private static final String SHARED_PREFERENCES = "sharedPreferences";
    private static final String LAST_LOCATION = "lastLocation";

    private TextView txtCityName;
    private TextView txtHumidity;
    private TextView txtSunrise;
    private TextView txtSunset;
    private TextView txtPressure;
    private TextView txtTemperature;
    private TextView txtDateTime;
    private TextView txtGeoCoordinates;
    private TextView txtWind;

    private ImageView imgWeather;

    private CompositeDisposable compositeDisposable;
    private IOpenWeatherMap mServices;

    @SuppressLint("StaticFieldLeak")
    private static TodayWeatherFragment instance;

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        mServices = retrofit.create(IOpenWeatherMap.class);
    }

    static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);
        imgWeather = itemView.findViewById(R.id.img_weather);
        txtCityName = itemView.findViewById(R.id.txt_city_name);
        txtHumidity = itemView.findViewById(R.id.txt_humidity);
        txtSunrise = itemView.findViewById(R.id.txt_sunrise);
        txtSunset = itemView.findViewById(R.id.txt_sunset);
        txtPressure = itemView.findViewById(R.id.txt_pressure);
        txtTemperature = itemView.findViewById(R.id.txt_temperature);
        txtDateTime = itemView.findViewById(R.id.txt_date_time);
        txtGeoCoordinates = itemView.findViewById(R.id.txt_geo_coord);
        txtWind = itemView.findViewById(R.id.txt_wind);
        getWeatherInformationByLatLng();

        return itemView;
    }

    /**
     * @param string
     * Updates lastLocation using SharedPreferences
     */
    private void updateLastLocation(String string) {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_LOCATION, string);
        editor.apply();

        String lastLocationSaved = sharedPreferences.getString(LAST_LOCATION, "");

        ViewPager viewPager = Objects.requireNonNull(getActivity()).findViewById(R.id.viewpager);

        TodayWeatherFragment todayWeatherFragment = (TodayWeatherFragment) ((ViewPagerAdapter) Objects.requireNonNull(viewPager.getAdapter())).getItem(0);
        TextView lastLocation = Objects.requireNonNull(todayWeatherFragment.getView()).findViewById(R.id.lastLocation);

        String lastLocationText = "Laatst geregistreerde locatie:\n" + lastLocationSaved;
        lastLocation.setText(lastLocationText);
    }

    /**
     * Requests weather data by current location and displays it
     * Also saves the search to Recent Searches
     */
    private void getWeatherInformationByLatLng() {
        compositeDisposable.add(
                mServices.getWeatherByLatLng(String.valueOf(Common.currentLocation.getLatitude()),
                        String.valueOf(Common.currentLocation.getLongitude()),
                        Common.API_ID,
                        "metric")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherResult>() {
                            @Override
                            public void accept(WeatherResult weatherResult) {
                                displayInformation(weatherResult);
                                updateLastLocation(weatherResult.getName() + ", " + weatherResult.getSys().getCountry());
                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    /**
     * @param city
     * Requests weather data by city and displays it
     * Also saves the search to Recent Searches
     */
    public void getWeatherInformationByCity(String city) {
        compositeDisposable.add(
                mServices.getWeatherBySearchQuery(
                        city,
                        Common.API_ID,
                        "metric")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherResult>() {
                            @Override
                            public void accept(WeatherResult weatherResult) {
                                displayInformation(weatherResult);
                            }

                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) {
                                Toast.makeText(getActivity(), "Geen resultaat gevonden", Toast.LENGTH_SHORT).show();
                            }
                        })
        );


        new RecentSearchesDBHelper(Objects.requireNonNull(getActivity()).getApplicationContext()).addKeyword(city);
    }

    /**
     * @param weatherResult
     * Displays new weather data
     */
    private void displayInformation(WeatherResult weatherResult) {
        Picasso.get().load("https://openweathermap.org/img/w/" + weatherResult.getWeather().get(0).getIcon() + ".png").into(imgWeather);

        txtCityName.setText(weatherResult.getName() + ", " + weatherResult.getSys().getCountry());
        txtTemperature.setText(weatherResult.getMain().getTemp() + "°C");
        txtDateTime.setText(Common.convertUnixToDate(weatherResult.getDt()));
        txtPressure.setText(weatherResult.getMain().getPressure() + "hpa");
        txtHumidity.setText(weatherResult.getMain().getHumidity() + "%");
        txtSunrise.setText(Common.convertUnixToTime(weatherResult.getSys().getSunrise()));
        txtSunset.setText(Common.convertUnixToTime(weatherResult.getSys().getSunset()));
        txtGeoCoordinates.setText(weatherResult.getCoord().toString());
        txtWind.setText(weatherResult.getWind().getSpeed() + " km/u " + Common.convertWindDegreesToDirection(weatherResult.getWind().getDeg()));
    }
}
