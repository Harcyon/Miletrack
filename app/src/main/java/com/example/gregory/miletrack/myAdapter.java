package com.example.gregory.miletrack;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        TextView dateView = (TextView) view.findViewById(R.id.date);
        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("event_date"));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("event_id"));
        double dist = cursor.getDouble(cursor.getColumnIndexOrThrow("distance"));
        Date date = parseDate(dateStr);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        dateView.setText(df.format(date));
        dbid.setText(Integer.toString(id));
        distance.setText(Double.toString(dist) + " miles");

    }

    public Date parseDate(String str){
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = sm.parse(str);
        } catch (ParseException e){
            e.printStackTrace();
            date = new Date();
        }
        return date;
    }


}
