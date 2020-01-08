package com.example.weather;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.Common.common;
import com.example.weather.Model.WeatherResult;
import com.example.weather.Retrofit.IOpenWeatherMap;
import com.example.weather.Retrofit.RetrofitClient;
import com.google.android.gms.common.internal.service.Common;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    ImageView img_weather;
    TextView txt_cityname,txt_humidity,txt_sunrise,txt_sunset,txt_pressure,txt_temperature,txt_description,txt_date_time,txt_geo_coord,txt_wind;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mservices;

static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if(instance==null)
            instance=new TodayWeatherFragment();
        return instance;
    }

    public TodayWeatherFragment() {
        // Required empty public constructor
        compositeDisposable=new CompositeDisposable();
        Retrofit retrofit= RetrofitClient.getInstance();
        mservices=retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =inflater.inflate(R.layout.fragment_today_weather, container, false);
        img_weather=(ImageView)itemView.findViewById(R.id.img_weather);
        txt_cityname=(TextView)itemView.findViewById(R.id.txt_city_name);
        txt_humidity=(TextView)itemView.findViewById(R.id.txt_humidity);
        txt_sunrise=(TextView)itemView.findViewById(R.id.txt_sunrise);
        txt_sunset=(TextView)itemView.findViewById(R.id.txt_sunset);
        txt_pressure=(TextView)itemView.findViewById(R.id.txt_pressure);
        txt_temperature=(TextView)itemView.findViewById(R.id.txt_temperature);
        txt_description=(TextView)itemView.findViewById(R.id.txt_description);
        txt_date_time=(TextView)itemView.findViewById(R.id.txt_date_time);
        txt_geo_coord=(TextView)itemView.findViewById(R.id.txt_geo_coord);
        txt_wind=(TextView)itemView.findViewById(R.id.txt_wind);
        weather_panel=(LinearLayout)itemView.findViewById(R.id.weather_panel);
        loading=(ProgressBar)itemView.findViewById(R.id.loading);
        getWeatherInformation();









        return itemView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mservices.getWeatherByLatLng(String.valueOf(common.current_location.getLatitude()),
                String.valueOf(common.current_location.getLongitude()),
                common.API_ID,
                "metric")
                        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        //Load image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                        .append(".png").toString()).into(img_weather);
                        //Load Info
                        txt_cityname.setText(weatherResult.getName());
                        txt_description.setText(new StringBuilder("Weather in").append(weatherResult.getName()).toString());
                        txt_temperature.setText(new StringBuilder(
                                String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        txt_date_time.setText(common.convertUnixToDate(weatherResult.getDt()));
                        txt_pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append("hpa").toString());
                        txt_humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append("%").toString());

                        txt_sunrise.setText(common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txt_sunset.setText(common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txt_geo_coord.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());
                        //Display panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);













                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                })
                );
    }

}
