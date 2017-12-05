package com.example.gregory.miletrack;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Gregory on 11/6/2017.
 */

public class EventViewerActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseHandler db;
    private myAdapter adapter;
    final String[] from = new String[]{db.KEY_ID,db.KEY_START_LAT,db.KEY_START_LONG,db.KEY_END_LAT,db.KEY_END_LONG,db.KEY_DISTANCE,db.KEY_EVENT_DATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DatabaseHandler(getApplicationContext());
        setContentView(R.layout.activity_event_viewer);

        Cursor cursor = db.fetch();
        listView = (ListView) findViewById(R.id.db_list);
        listView.setEmptyView(findViewById(R.id.empty));

        adapter = new myAdapter(this,cursor);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(view.getContext(),EventItemViewer.class);
//                i.putExtra("position",position);
//                startActivityForResult(i,0);
            }
        });

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
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
