package com.example.weather;

import android.app.Instrumentation.*;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.weather.common.Common;
import com.example.weather.model.WeatherResult;
import com.example.weather.retrofit.IOpenWeatherMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainInstrumentedTest {
    private Context appContext;
    private MainActivity mainActivity;

    @Before
    public void setUp() {
        appContext = getInstrumentation().getTargetContext();
    }

    /**
     * Tests if packageName matches with packageName from appContext
     */
    @Test
    public void useAppContext() {
        assertEquals("com.example.weather", appContext.getPackageName());
    }

    /**
     * Tests if appId for OpenWeatherMAP API is valid
     */
    @Test
    public void test_appId() {
        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(Common.baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        IOpenWeatherMap mServices = retrofit.create(IOpenWeatherMap.class);

        mServices.getWeatherBySearchQuery(
                "Tilburg",
                Common.appId,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) {
                        assertTrue(true);
                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        fail();
                    }
                });
    }

    /**
     * Tests if device has Accelerometer and Magnetometer sensors
     */
    @Test
    public void test_sensors() {
        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            if (mAccelerometer != null && mMagnetometer != null) {
                assertTrue(true);
            } else {
                fail();
            }
        }
    }

    public MainActivity getActivity() {
        if (mainActivity == null) {
            Intent intent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ActivityMonitor monitor = getInstrumentation().addMonitor(MainActivity.class.getName(), null, false);
            getInstrumentation().getTargetContext().startActivity(intent);
            mainActivity = (MainActivity) getInstrumentation().waitForMonitor(monitor);
            setActivity(mainActivity);
        }

        return mainActivity;
    }

    public void setActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
