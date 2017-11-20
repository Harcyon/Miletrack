package com.example.gregory.miletrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by Gregory on 11/6/2017.
 */

public class AnalyticsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_analytics);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.analytics_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch( item.getItemId()){
            case R.id.home:
                startActivity(new Intent( getApplicationContext(), MapsActivity.class));
                return true;
            case R.id.event_viewer:
                startActivity(new Intent( getApplicationContext(), EventViewerActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
