package com.example.gregory.miletrack;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;


    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private AsyncTask tracking;
    private static Button trackButton;
    private Boolean flag,flag1;
    private LatLng start, stop;
    private double dist;

    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        db = new DatabaseHandler(getApplicationContext());

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        trackButton = (Button) findViewById(R.id.tracking_button);

        mapFragment.getMapAsync(this);
        flag = true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public void onClickTrack(View viw){
        Button btn = (Button) findViewById(R.id.tracking_button);
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = new Location("");
        try {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (SecurityException e){
            e.printStackTrace();
        }
        flag1 = false;
        if (flag) {
            btn.setText("Stop Tracking");
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),mLastKnownLocation.getLongitude()))
                    .title("Origin"));
            tracking = getTask();
            tracking.execute();
            flag = false;
        } else {
            flag = true;
            btn.setText("Track");
            tracking.cancel(true);
            mMap.clear();
        }

    }


    public AsyncTask<Object,Void,Void> getTask (){
        return new MyTask();
    }

    private class MyTask extends AsyncTask<Object, Void, Void>{
        LatLng origin, temp, current;
        double dist, miles;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location;

        protected void onPreExecute(){
            try {
                location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            if (location == null){
                this.cancel(true);
            } else {
                origin = new LatLng(location.getLatitude(), location.getLongitude());
            }
        }



        @Override
        protected Void doInBackground(Object... voids) {

            dist = 0;
            temp = origin;


            Button btn1 = (Button) findViewById(R.id.tracking_button);
            while (isCancelled() == false){
                try {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e){
                    e.printStackTrace();
                }
                current = new LatLng(location.getLatitude(),location.getLongitude());
                try{
                    Thread.sleep(10000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                miles = getDistanceOnRoad(temp.latitude,temp.longitude,current.latitude,current.longitude);
                if (miles > .001) {
                    dist += miles;
                    temp = current;
                    publishProgress();
                }

            }

            if (dist == 0){
                try {
                    location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (SecurityException e){
                    e.printStackTrace();
                }
                dist = getDistanceOnRoad(origin.latitude,origin.longitude,current.latitude,current.longitude);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

        }

        @Override
        protected void onCancelled(Void result) {
            flag1 = true;
            MileEvent event = new MileEvent();
            Date today = new Date();

            event.setEventDate(today);
            event.setStartLong(origin.longitude);
            event.setStartLat(origin.latitude);
            event.setEndLong(current.longitude);
            event.setEndLat(current.latitude);
            event.setDistance(dist);
            db.addEvent(event);
        }

        protected void publishProgress(){

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(current.latitude,current.longitude))
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(current.latitude,
                                    current.longitude), DEFAULT_ZOOM));
                }
            });
        }
    }


    String response, parsedDistance;
    private Double getDistanceOnRoad(final double oLat, final double oLong, final double eLat, final double eLong){


        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + oLat + "," + oLong + "&destination=" + eLat + "," + eLong + "&sensor=false&units=imperial&mode=driving");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance=distance.getString("text");
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String[] words = parsedDistance.split("\\s+");
        return Double.parseDouble(words[0]);
    }





    public boolean onOptionsItemSelected(MenuItem item){
        switch( item.getItemId()){
            case R.id.event_viewer:
                startActivity(new Intent( getApplicationContext(), EventViewerActivity.class));
                return true;
            case R.id.analytics:
                startActivity(new Intent( getApplicationContext(), AnalyticsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }


    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;



        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation == null){
                                mLastKnownLocation = new Location("");
                            }
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


}
