package com.example.gregory.miletrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


/**
 * Created by Gregory on 11/6/2017.
 */

public class AddEventFragment extends AppCompatActivity{

    private static final String TAG = "AddEventFragment";
    private Button button;
    private DatabaseHandler db;
    private LatLng origin, end;
    private double startLat, startLong, endLat, endLong;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_addevent);

        db = new DatabaseHandler(getApplicationContext());
        //creates fragments
        PlaceAutocompleteFragment autocompleteOrigin = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);

        PlaceAutocompleteFragment autocompleteDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment2);

        button = (Button) findViewById(R.id.add_event);

        //sets listener for 1st address
        autocompleteOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                origin = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

        //Sets listener for 2nd address
        autocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                end = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.

            }
        });

    }

    public void onClickBtn(View view) throws InterruptedException{
        if (origin != null && end != null){

            startLat = origin.latitude;
            startLong= origin.longitude;
            endLat = end.latitude;
            endLong= end.longitude;
            Date date = new Date();

            MileEvent event = new MileEvent();

            event.setStartLat(startLat);
            event.setStartLong(startLong);
            event.setEndLat(endLat);
            event.setEndLong(endLong);
            event.setEventDate(date);
            event.setDistance(getDistanceOnRoad(startLat,startLong,endLat,endLong));

            Toast toast = Toast.makeText(getApplicationContext(),"Event Added",Toast.LENGTH_LONG);

            toast.show();
            Log.d(TAG,"Event Added");
            db.addEvent(event);

            startActivity(new Intent( getApplicationContext(), EventViewerActivity.class));
        }
    }

    String response;
    String parsedDistance;
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
}


