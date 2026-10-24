package com.example.locationtracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationTrackerService {

    private static final String TAG = "LocationTrackerService";
    private static final String BASE_URL = "https://your-backend-url.com";
    private static final float MIN_DISTANCE_METERS = 10f;
    private static final String DEVICE_ID = "device-001";

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final OkHttpClient httpClient;
    private Location lastKnownLocation;

    public LocationTrackerService(Context context) {
        this.context = context.getApplicationContext();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.context);
        this.httpClient = new OkHttpClient();
    }

    public interface LocationCallback {
        void onLocationUpdate(Location location);
        void onError(String message);
    }

    @SuppressLint("MissingPermission")
    public void startLocationTracking(LocationCallback callback) {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateDistanceMeters(MIN_DISTANCE_METERS)
                .setWaitForAccurateLocation(true)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location == null) continue;

                    if (shouldSendLocation(location)) {
                        lastKnownLocation = location;
                        callback.onLocationUpdate(location);
                        sendToServer(location);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean shouldSendLocation(Location newLocation) {
        if (lastKnownLocation == null) {
            return true;
        }

        float distance = lastKnownLocation.distanceTo(newLocation);
        return distance >= MIN_DISTANCE_METERS;
    }

    private void sendToServer(Location location) {
        JSONObject payload = new JSONObject();
        try {
            payload.put("deviceId", DEVICE_ID);
            payload.put("latitude", location.getLatitude());
            payload.put("longitude", location.getLongitude());
            payload.put("accuracyMeters", location.getAccuracy());

            RequestBody body = RequestBody.create(
                    payload.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/api/location/update")
                    .post(body)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "Upload failed: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Upload response not successful: " + response.code());
                    }
                    response.close();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error building request: " + e.getMessage());
        }
    }
}
