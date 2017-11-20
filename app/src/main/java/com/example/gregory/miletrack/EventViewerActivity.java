package com.example.gregory.miletrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Created by Gregory on 11/6/2017.
 */

public class EventViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_viewer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch( item.getItemId()){
            case R.id.home:
                startActivity(new Intent( getApplicationContext(), MapsActivity.class));
                return true;
            case R.id.analytics:
                startActivity(new Intent( getApplicationContext(), AnalyticsActivity.class));
                return true;
            case R.id.advanced_event:
                startActivity(new Intent( getApplicationContext(), AddEventFragment.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
