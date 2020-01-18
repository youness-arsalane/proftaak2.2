package com.example.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.weather.adapter.ViewPagerAdapter;
import com.example.weather.common.Common;
import com.example.weather.dbHandler.RecentSearchesDBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Objects;

import static java.sql.DriverManager.println;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private float currentDegree = 0f;
    private TextView tvHeading;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.root_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = Objects.requireNonNull(mSensorManager).getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        RecentSearchesDBHelper db = new RecentSearchesDBHelper(this);
        db.onCreate(db.getWritableDatabase());

        // Requesting permission
        try {
            Dexter
                    .withActivity(this)
                    .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                buildLocationRequest();
                                buildLocationCallBack();

                                if (
                                        checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    return;
                                }

                                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            Snackbar.make(coordinatorLayout, "permission denied", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .check();
        } catch (Exception e) {
            println("Something went wrong. Your Issue :" + e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mLastAccelerometerSet = false;
        mLastMagnetometerSet = false;
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Builds LocationRequest with default values
     */
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }

    /**
     * Builds LocationCallback with default values
     */
    private void buildLocationCallBack() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Common.currentLocation = locationResult.getLastLocation();
                viewPager = findViewById(R.id.viewpager);
                setupViewPager(viewPager);
                tabLayout = findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);



            }
        };
    }

    /**
     * @param viewPager
     * Adds an Adapter of Fragments in the a given ViewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(TodayWeatherFragment.getInstance(), getString(R.string.today));
        adapter.addFragment(CompassFragment.getInstance(), getString(R.string.compass));
        adapter.addFragment(RecentSearchesFragment.getInstance(), getString(R.string.recent));
        viewPager.setAdapter(adapter);
    }

    /**
     * @param view
     * Gets city from EditText and makes request by calling getWeatherInformationByCity()
     */
    public void searchByCity(View view) {
        EditText city = findViewById(R.id.weather_input);

        TodayWeatherFragment todayWeatherFragment = (TodayWeatherFragment) ((ViewPagerAdapter) Objects.requireNonNull(viewPager.getAdapter())).getItem(0);
        todayWeatherFragment.getWeatherInformationByCity(city.getText().toString());

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);

        view.requestFocus();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        viewPager = findViewById(R.id.viewpager);

        if (viewPager.getAdapter() != null) {
            float degree = 0;
            if (sensorEvent.sensor == mAccelerometer) {
                System.arraycopy(sensorEvent.values, 0, mLastAccelerometer, 0, sensorEvent.values.length);
                mLastAccelerometerSet = true;
            } else if (sensorEvent.sensor == mMagnetometer) {
                System.arraycopy(sensorEvent.values, 0, mLastMagnetometer, 0, sensorEvent.values.length);
                mLastMagnetometerSet = true;
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
                SensorManager.getOrientation(mR, mOrientation);

                double degreeDouble = ( Math.toDegrees( mOrientation[0] ) + 360 ) % 360;
                degree = (float) degreeDouble;
            }

            CompassFragment compassFragment = (CompassFragment) ((ViewPagerAdapter) viewPager.getAdapter()).getItem(1);

            ImageView compassImage = Objects.requireNonNull(compassFragment.getView()).findViewById(R.id.imageViewCompass);
            tvHeading = compassFragment.getView().findViewById(R.id.tvHeading);

            String tvHeadingText = "Rotation: " + degree + " degrees";
            tvHeading.setText(tvHeadingText);

            RotateAnimation ra = new RotateAnimation(
                    currentDegree,
                    -degree,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            );

            ra.setDuration(120);
            ra.setFillAfter(true);
            compassImage.startAnimation(ra);
            currentDegree = -degree;
        } else{
            println("Your viewPager is already filled,try again");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_delete_recent_searches) {
            RecentSearchesDBHelper db = new RecentSearchesDBHelper(this);
            db.deleteAll();

            RecentSearchesFragment recentSearchesFragment = (RecentSearchesFragment) ((ViewPagerAdapter) Objects.requireNonNull(viewPager.getAdapter())).getItem(2);

            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(recentSearchesFragment);
            ft.attach(recentSearchesFragment);
            ft.commit();
        }
        return super.onOptionsItemSelected(item);
    }
}