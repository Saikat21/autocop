package com.example.mahfuj.autocop;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int PERMISSION_REQUESNT_CODE = 999;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 998;
    private static final long LOCATION_UPDATE_INTERVAL = 5000; // 5 sec
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 1000; // 1 sec
    private static final long LOCATION_UPDATE_MIN_DISPLACEMENT = 1; // meter






    private GoogleMap mMap;
    private DatabaseReference locationDBRef;
    private DatabaseReference speedDBRef;
    Marker currMarker;
    GeoFire geoFire;
    private FirebaseAuth mAuth;

    //long lastUpdateTime;

    Context ctx;
    GoogleApiClient mGoogleApliClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    //private String speed = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ctx = this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        speedDBRef = FirebaseDatabase.getInstance().getReference("Speed");
        locationDBRef = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(locationDBRef);

        setupLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUESNT_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                        displayLocation();
                    }

                }
                break;
        }


    }

    private void setupLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS,
            }, PERMISSION_REQUESNT_CODE);

        } else {

            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
        }
    }


    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(ctx, "Please Enable Location Permissions", Toast.LENGTH_LONG).show();
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApliClient);

        if (mLastLocation != null) {
            final double lat = mLastLocation.getLatitude();
            final double lng = mLastLocation.getLongitude();

            Log.v("mytag", "Location Changed LAT,LNG : " + lat + "," + lng +
                    " SPEED : " + mLastLocation.getSpeed() + " PROVIDER : " + mLastLocation.getProvider() +
                    " ACCURACY : " + mLastLocation.getAccuracy() + " TIME : " + mLastLocation.getTime());

            // Update to Firebase
            geoFire.setLocation(mAuth.getCurrentUser().getUid(), new GeoLocation(lat, lng), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {

                    // Remove Old Marker
                    if (currMarker != null)
                        currMarker.remove();

                    // Add marker
                    currMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("ME")
                    );

                    // Move Cam
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f));

                }
            });



                OverSpeed overSpeed = new OverSpeed(mLastLocation.getSpeed(), mLastLocation.getTime());

                speedDBRef.child(mAuth.getCurrentUser().getUid()).setValue(overSpeed);




        } else {

            Log.v("mytag", "Unable to get Last Location data");
        }


    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(LOCATION_UPDATE_MIN_DISPLACEMENT);
    }

    private void buildGoogleApiClient() {

        mGoogleApliClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApliClient.connect();

    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(ctx, "Device doesn't support Google Play Services", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }

        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(ctx, "Please Enable Location Permissions", Toast.LENGTH_LONG).show();
            return;
        }

        if (mMap != null) {

            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    displayLocation();
                    return true;
                }
            });
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApliClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApliClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //lastUpdateTime = location.getTime();
        mLastLocation = location;

        displayLocation();
    }





}
