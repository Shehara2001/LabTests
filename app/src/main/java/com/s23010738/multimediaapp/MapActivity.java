package com.s23010738.multimediaapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ExtendedFloatingActionButton tempButton;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private TextInputEditText addressInput;
    private Button showLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        addressInput        = findViewById(R.id.addressInput);
        showLocationButton  = findViewById(R.id.showLocationButton);
        tempButton         = findViewById(R.id.tempButton);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        showLocationButton.setOnClickListener(v -> {
            if (mMap == null) {
                Toast.makeText(this, "Map is still loading…", Toast.LENGTH_SHORT).show();
                return;
            }

            String address = addressInput.getText() == null
                    ? "" : addressInput.getText().toString().trim();

            if (address.isEmpty()) {
                Toast.makeText(this, "Please enter an address", Toast.LENGTH_SHORT).show();
            } else {
                geocodeAddress(address);
            }
        });   // Navigate to TemperatureActivity when Temp FAB is pressed
        tempButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, TemperatureActivity.class);
            startActivity(intent);});}

    @Override public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    private void enableMyLocation() {
        if (mMap != null &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }


    private void geocodeAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                List<Address> results = geocoder.getFromLocationName(address, 1);
                runOnUiThread(() -> {
                    if (results == null || results.isEmpty()) {
                        Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Address loc = results.get(0);
                    LatLng   p  = new LatLng(loc.getLatitude(), loc.getLongitude());

                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(p).title(address));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 15f));
                });
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error finding address", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
