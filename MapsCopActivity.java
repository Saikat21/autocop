package com.example.mahfuj.autocop;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class MapsCopActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    private static final int PERMISSION_REQUESNT_CODE = 999;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 998;
    private static final long LOCATION_UPDATE_INTERVAL = 5000; // 5 sec
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 1000; // 1 sec
    private static final long LOCATION_UPDATE_MIN_DISPLACEMENT = 10;

   // private final double CONVERT_METER_TO = 3.6;

    private final double SPEED_LIMIT_TRACKER = 0; // km\h
    private final double SPEED_LIMIT_VIOLATION = 71; // km\h
    private final int MONITOR_RADIUS = 200; // meter


    private GoogleMap mMap;
    private DatabaseReference locationDBRef;
    private DatabaseReference speedDBRef;
    Marker currMarker;
    GeoFire geoFire;
    private FirebaseAuth mAuth;

    private boolean shouldZoom = true;

    //long lastUpdateTime;

    Context ctx;
    GoogleApiClient mGoogleApliClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    //private String speed = "0";
    private Circle monitorCircle;

    List<Marker> speedingMarkers = new ArrayList<>();
    List<String> speedingMarkersTags = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        ctx = this;
        progressDialog = new ProgressDialog(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        speedDBRef = FirebaseDatabase.getInstance().getReference("Speed");
        locationDBRef = FirebaseDatabase.getInstance().getReference("Location");
        geoFire = new GeoFire(locationDBRef);

        setupLocation();

        Query query = speedDBRef.orderByChild("speed").startAt(SPEED_LIMIT_TRACKER);


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    Log.v("mytag", "SPEEDING");

                    Log.v("mytag", "CARS " + dataSnapshot.getValue() + "  KEY " + dataSnapshot.getKey());

                    for (Marker m : speedingMarkers) {
                        m.remove();
                    }

                    speedingMarkers.clear();
                    speedingMarkersTags.clear();

                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        OverSpeed overSpeed = data.getValue(OverSpeed.class);

                        //Log.v("mytag", "KEY " + data.getKey());
                        getSpeedingLocation(data.getKey(), overSpeed.getSpeed(), data.getKey());
                    }

                } else {
                    Log.v("mytag", "SPEEDING DOESNT EXIST");
                    for (Marker m : speedingMarkers) {
                        m.remove();
                    }

                    speedingMarkers.clear();
                    speedingMarkersTags.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    private void getSpeedingLocation(String key, final double speed, final String dataKey) {

        locationDBRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    GeoFireLocation geoFireLocation = dataSnapshot.getValue(GeoFireLocation.class);

                    Marker m;

                    if (speed < SPEED_LIMIT_VIOLATION) {
                        Log.v("mytag", " speeding  " + speed);

                        m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(geoFireLocation.getL().get(0), geoFireLocation.getL().get(1)))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                .title("Vehicle")

                        );
                    } else {
                        Log.v("mytag", " not speed  " + speed);

                        m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(geoFireLocation.getL().get(0), geoFireLocation.getL().get(1)))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .title("Speeding")
                        );

                        m.showInfoWindow();
                        speedingMarkersTags.add(dataKey);
                    }

                    m.setTag(dataKey);
                    speedingMarkers.add(m);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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


            if (currMarker != null)
                currMarker.remove();

            // Add marker
            /*currMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("COP")
            );

            currMarker.setAlpha(0.5f);
            currMarker.setTag("COP");*/
            //currMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_loc));

            if (shouldZoom) {
                // Move Cam
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16.0f));
                shouldZoom = false;
            }


            displayMonitorArea();


        } else {

            Log.v("mytag", "Unable to get Last Location data");
        }


    }

    private void displayMonitorArea() {

        if (monitorCircle != null)
            monitorCircle.remove();

        monitorCircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .radius(MONITOR_RADIUS)
                .strokeColor(Color.GREEN)
                .strokeWidth(2.0f)

        );

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
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SEND_SMS
            }, PERMISSION_REQUESNT_CODE);

        } else {

            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
                displayLocation();
            }
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
        mMap.setOnMarkerClickListener(this);

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
                    shouldZoom = true;
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

        mLastLocation = location;
        displayLocation();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        Log.v("mytag", "MARKER CLICKED " + marker.getTag() + " TITLE " + marker.getTitle());

        final String id = marker.getTag().toString();

        if (speedingMarkersTags.contains(id)) {

            AlertDialog.Builder builder;
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }*/
            builder = new AlertDialog.Builder(ctx);

            final EditText input = new EditText(ctx);
            builder.setView(input);


            builder.setTitle("Send Warning")
                    .setMessage("Warn Speeding Driver?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            progressDialog.setMessage("Sending Warning SMS To Speeding Vehicle");
                            progressDialog.show();

                            // get user info
                            DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference("Owner");
                            ownerRef.orderByKey().equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {

                                        Log.v("mytag", "SPEEDING OWNER INFO : " + dataSnapshot.getValue());

                                        RegistrationInfo model = new RegistrationInfo();

                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            model = data.getValue(RegistrationInfo.class);
                                        }

                                        Log.v("mytag", "PHN " + model.getPhone_no());

                                        //sendSMS(model.getPhone_no(), "You are speeding over the max limit. Kindly slow down your vehicle.\nYou have been warned!\n\n-AUTO COP: " + mAuth.getCurrentUser().getEmail());
                                        sendSMS(model.getPhone_no(), input.getText().toString().trim()+"Your vehicle is overspeeding. Please slow down\n-AUTO COP: " + mAuth.getCurrentUser().getUid());
                                        //FirebaseDatabase.getInstance().getReference("Violation").child(id).setValue(mAuth.getCurrentUser().getUid());
                                    } else {
                                        progressDialog.dismiss();
                                        Log.v("mytag", "NO SPEEDING OWNER INFO : " + dataSnapshot.getValue());
                                        Toast.makeText(ctx, "Unable To Fetch Vehicle Owner Info", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    progressDialog.dismiss();
                                    Toast.makeText(ctx, "Unable To Fetch Account Info", Toast.LENGTH_LONG).show();
                                }
                            });


                        }
                    })

                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false);
            builder.show();


        }

        return true;
    }


    public void sendSMS(String phn, String msg) {

        try {
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(phn, null, msg, null, null);

            progressDialog.dismiss();
            Toast.makeText(ctx, "WARNING SMS SENT", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {
            progressDialog.dismiss();
            Toast.makeText(ctx, "Unable To Send SMS Warning. Error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}