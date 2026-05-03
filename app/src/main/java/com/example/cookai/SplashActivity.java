package com.example.cookai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    // =========================
    // CONSTANT
    // =========================
    private static final int SPLASH_DELAY = 4500;
    private static final int LOCATION_PERMISSION_CODE = 101;

    // =========================
    // VIEW
    // =========================
    private TextView locationText;

    // =========================
    // LOCATION
    // =========================
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // =========================
        // LAYOUT
        // =========================
        setContentView(R.layout.activity_splash);

        // =========================
        // VIEW BINDING
        // =========================
        locationText = findViewById(R.id.locationText);

        // =========================
        // LOCATION INIT
        // =========================
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        // =========================
        // GET LOCATION
        // =========================
        getUserLocation();

        // =========================
        // SPLASH DELAY
        // =========================
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (FirebaseAuth.getInstance()
                    .getCurrentUser() != null) {

                Intent intent = new Intent(
                        SplashActivity.this,
                        MainActivity.class
                );

                startActivity(intent);

            } else {

                Intent intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class
                );

                startActivity(intent);
            }

            finish();

        }, SPLASH_DELAY);
    }

    // =========================
    // LOCATION FUNCTION
    // =========================
    private void getUserLocation() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_CODE
            );

            locationText.setText("📍 Izin lokasi diperlukan");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        detectDistrict(location);

                    } else {

                        locationText.setText(
                                "📍 Lokasi tidak tersedia"
                        );
                    }
                });
    }

    // =========================
    // DETECT KECAMATAN
    // =========================
    private void detectDistrict(Location location) {

        Geocoder geocoder =
                new Geocoder(this, Locale.getDefault());

        try {

            List<Address> addresses =
                    geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            1
                    );

            if (addresses != null
                    && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                String district =
                        address.getSubLocality();

                String city =
                        address.getLocality();

                if (district != null) {

                    locationText.setText(
                            "📍 " + district + ", " + city
                    );

                } else {

                    locationText.setText(
                            "📍 " + city
                    );
                }

            }

        } catch (IOException e) {

            locationText.setText(
                    "📍 Gagal mendeteksi lokasi"
            );
        }
    }

    // =========================
    // PERMISSION RESULT
    // =========================
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                getUserLocation();

            } else {

                locationText.setText(
                        "📍 Lokasi dinonaktifkan"
                );
            }
        }
    }
}