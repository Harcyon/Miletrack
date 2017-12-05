package com.example.gregory.miletrack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

/**
 * Created by Gregory on 12/3/2017.
 */

public class EventItemViewer extends AppCompatActivity {

    private int itemPos;
    private DatabaseHandler db;
    private LatLng start, end;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        itemPos = getIntent().getIntExtra("postion",0);
        TextView id = (TextView) findViewById(R.id.list_id);

        db = new DatabaseHandler(getApplicationContext());
        MileEvent event = db.getEvent(itemPos);
        id.setText(Integer.toString(event.eventId));
        start = new LatLng(event.getStartLat(),event.getStartLong());
        end = new LatLng(event.getEndLat(),event.getEndLong());

    }
}
