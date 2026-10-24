package com.example.locationtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    private TextView textViewStatus;
    private LocationTrackerService locationTrackerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = findViewById(R.id.textStatus);
        locationTrackerService = new LocationTrackerService(this);

        if (hasLocationPermission()) {
            startTracking();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTracking();
        } else {
            textViewStatus.setText("Location permission denied");
        }
    }

    private void startTracking() {
        locationTrackerService.startLocationTracking(new LocationTrackerService.LocationCallback() {
            @Override
            public void onLocationUpdate(Location location) {
                String message = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude() + "\nAccuracy: " + location.getAccuracy() + "m";
                textViewStatus.setText(message);
                Log.d(TAG, message);
            }

            @Override
            public void onError(String message) {
                textViewStatus.setText(message);
                Log.e(TAG, message);
            }
        });
    }
}
