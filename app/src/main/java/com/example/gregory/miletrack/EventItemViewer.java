package com.example.gregory.miletrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;

/**
 * Created by Gregory on 12/3/2017.
 */

public class EventItemViewer extends AppCompatActivity {

    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap map;
    private double itemId;
    private DatabaseHandler db;
    private LatLng start, end;
    private MileEvent event;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        db = new DatabaseHandler(getApplicationContext());
        itemId = Double.parseDouble(getIntent().getStringExtra("dbid"));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);


        event = db.getEvent((int)itemId);

        start = new LatLng(event.getStartLat(),event.getStartLong());
        end = new LatLng(event.getEndLat(),event.getEndLong());

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        start, DEFAULT_ZOOM));
                map.addMarker(new MarkerOptions()
                        .position(start)
                        .title("Starting Point"));
                map.addMarker(new MarkerOptions()
                        .position(end)
                        .title("Stopping Point"));
            }
        });

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        TextView date = (TextView) findViewById(R.id.date_view);
        date.setText(df.format(event.getEventDate()));
        TextView startPoint = (TextView) findViewById(R.id.starting_view);
        startPoint.setText("" + Double.toString(round(start.latitude,2)) + ", " +Double.toString(round(start.longitude,2)) );
        TextView stopPoint = (TextView) findViewById(R.id.stopping_view);
        stopPoint.setText("" + Double.toString(round(end.latitude,2)) + ", " +Double.toString(round(end.longitude,2)) );
        TextView distance = (TextView) findViewById(R.id.distance_view);
        distance.setText(Double.toString(event.getDistance()));


    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void goToStart(View view){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                start, DEFAULT_ZOOM));
    }

    public void goToStop(View view){
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                end, DEFAULT_ZOOM));
    }

    public void deleteItem(View view){
        db.deleteEvent(event);
        startActivity(new Intent( getApplicationContext(), EventViewerActivity.class));
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

}