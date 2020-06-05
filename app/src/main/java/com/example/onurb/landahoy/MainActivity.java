package com.example.onurb.landahoy;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1)    // Request Code For FINE LOCATION //
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(checkForLocationPermission())
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                markUserLocationOnMap(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        // Requesting Location Of User //
        requestUserLocation();
    }


    public void requestUserLocation()
    {
        if(Build.VERSION.SDK_INT < 23)  // If the SDK < 23; We Directly Request The Location Updates //
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else  // If the SDK >= 23; We Need To Check For Permission //
        {
            boolean permissionGranted = checkForLocationPermission();
            if(!permissionGranted) // We Don't Have Permission //
            {
                ActivityCompat.requestPermissions
                        (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);  // Ask For Permission //
            }
            else  // We Have Permission //
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                markUserLocationOnMap(lastKnownLocation);
            }
        }
    }


    public boolean checkForLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    public void markUserLocationOnMap(Location location)
    {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18), 5000, null);
    }

}
