package com.example.gregory.miletrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Gregory on 11/6/2017.
 */

public class AnalyticsActivity extends AppCompatActivity {

    private DatabaseHandler db;
    private Spinner spinner;
    private List<MileEvent> list, usable;
    private double total, largest, avg = 0;
    private Calendar cal, listcal;
    TextView totalMiles, largestMiles, avgMiles;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(getApplicationContext());
        setContentView(R.layout.activity_analytics);
        spinner = (Spinner) findViewById(R.id.duration);
        totalMiles =  (TextView) findViewById(R.id.total_mile_change);
        largestMiles = (TextView) findViewById(R.id.long_mile_change);
        avgMiles    = (TextView) findViewById(R.id.avg_mile_change);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                list = db.getAllEvents();
                total = 0;
                largest = 0;
                Date today = new Date();
                usable = new ArrayList<MileEvent>();
                switch (position){
                    case 0:
                        //today
                        total = 0;
                        largest = 0;
                        cal = Calendar.getInstance();
                        cal.setTime(today);
                        listcal = Calendar.getInstance();

                        for (int i = 0; i < list.size(); i++){
                            listcal.setTime(list.get(i).getEventDate());
                            if (listcal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)){
                                usable.add(list.get(i));
                            }
                        }
                        for (int i = 0; i < usable.size();i++){
                            Double x = usable.get(i).getDistance();
                            total += x;
                            if (x > largest)
                                largest = x;
                        }
                        avg = total / usable.size();


                        totalMiles.setText(Double.toString(total));
                        largestMiles.setText(Double.toString(largest));
                        avgMiles.setText(Double.toString(avg));
                        break;

                    case 1:
                        //yesterday
                        total = 0;
                        largest = 0;
                        cal = Calendar.getInstance();
                        cal.setTime(today);
                        listcal = Calendar.getInstance();

                        for (int i = 0; i < list.size(); i++){
                            listcal.setTime(list.get(i).getEventDate());
                            if (listcal.get(Calendar.DAY_OF_YEAR) == (cal.get(Calendar.DAY_OF_YEAR)-1))
                                usable.add(list.get(i));
                        }
                        for (int i = 0; i < usable.size();i++){
                            total += usable.get(i).getDistance();
                            if (usable.get(i).getDistance() > largest )
                                largest = usable.get(i).getDistance();
                        }

                        avg = total / usable.size();

                        if (largest == Double.NaN){
                            largest = 0;
                        }
                        totalMiles.setText(Double.toString(total));
                        largestMiles.setText(Double.toString(largest));
                        avgMiles.setText(Double.toString(avg));
                        break;
                    case 2:
                        //week
                        total = 0;
                        largest = 0;
                        cal = Calendar.getInstance();
                        cal.setTime(today);
                        listcal = Calendar.getInstance();
                        listcal = Calendar.getInstance();
                        for (int i = 0; i < list.size(); i++){
                            listcal.setTime(list.get(i).getEventDate());
                            if (listcal.get(Calendar.DAY_OF_YEAR) <= cal.get(Calendar.DAY_OF_YEAR) ||
                                listcal.get(Calendar.DAY_OF_YEAR) >= (cal.get(Calendar.DAY_OF_YEAR)-7)){
                                usable.add(list.get(i));
                            }
                        }
                        for (int i = 0; i < usable.size();i++){
                            Double x = 0.0;
                            x += usable.get(i).getDistance();
                            total += x;
                            if (x > largest)
                                largest = x;
                        }
                        avg = total / usable.size();


                        totalMiles.setText(Double.toString(total));
                        largestMiles.setText(Double.toString(largest));
                        avgMiles.setText(Double.toString(avg));
                        break;
                    case 3:
                        total = 0;
                        largest = 0;
                        cal = Calendar.getInstance();
                        cal.setTime(today);
                        listcal = Calendar.getInstance();
                        for (int i = 0; i < list.size(); i++){
                            listcal.setTime(list.get(i).getEventDate());
                            if (listcal.get(Calendar.DAY_OF_YEAR) <= cal.get(Calendar.DAY_OF_YEAR) ||
                                    listcal.get(Calendar.DAY_OF_YEAR) >= (cal.get(Calendar.DAY_OF_YEAR-30))){
                                usable.add(list.get(i));
                            }
                        }
                        for (int i = 0; i < usable.size();i++){
                            Double x = usable.get(i).getDistance();
                            total += x;
                            if (x > largest)
                                largest = x;
                        }
                        avg = total / usable.size();
                        totalMiles.setText(Double.toString(total));
                        largestMiles.setText(Double.toString(largest));
                        avgMiles.setText(Double.toString(avg));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

    public void setViews(double avg, double largest, double total){

    }
}
