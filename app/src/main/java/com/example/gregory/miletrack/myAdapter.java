package com.example.gregory.miletrack;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Gregory on 11/27/2017.
 */

public class myAdapter extends CursorAdapter {

    public myAdapter (Context context, Cursor cursor){
        super(context,cursor,0);
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.simple_list, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor){
        TextView dbid = (TextView) view.findViewById(R.id.db_id);
        TextView distance = (TextView) view.findViewById(R.id.distance);
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("event_id"));
        double dist = cursor.getDouble(cursor.getColumnIndexOrThrow("distance"));

        dbid.setText(Integer.toString(id));
        distance.setText(Double.toString(dist));

    }


}
