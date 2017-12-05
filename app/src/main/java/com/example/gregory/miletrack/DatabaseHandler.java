package com.example.gregory.miletrack;

import com.example.gregory.miletrack.MileEvent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TableLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Gregory on 11/20/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private int eventid = 1;
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "eventsManager";

    private static final String TABLE_EVENTS = "events";

    // events Table Columns names
    public static final String KEY_ID = "event_id";            //int
    public static final String KEY_START_LAT = "start_lat";    //double
    public static final String KEY_START_LONG = "start_long"; //double
    public static final String KEY_END_LAT = "end_lat";        //double
    public static final String KEY_END_LONG = "end_long";      //double
    public static final String KEY_DISTANCE = "distance";      //double
    public static final String KEY_EVENT_DATE = "event_date";  //datetime

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + KEY_ID + " INTEGER AUTOINCREMENT," + KEY_START_LAT + " DOUBLE,"
                + KEY_START_LONG + " DOUBLE," + KEY_END_LAT + " DOUBLE,"
                + KEY_END_LONG + " DOUBLE," + KEY_DISTANCE + " DOUBLE,"
                + KEY_EVENT_DATE + " DATETIME" + ")";
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    public void deleteAll(){
        String selectQuery = "DELETE FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(selectQuery);
    }

    public void setEventid(int i){
        eventid = i;
    }

    // Upgrading DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

        onCreate(db);
    }

    public void addEvent(MileEvent event) {
        Log.d(TAG, "addEvent: Should be added");
        String sql = "INSERT INTO events ";
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_START_LAT, event.getStartLat());
        values.put(KEY_START_LONG, event.getStartLong());
        values.put(KEY_END_LAT, event.getEndLat());
        values.put(KEY_END_LONG, event.getEndLong());
        values.put(KEY_DISTANCE, event.getDistance());
        values.put(KEY_EVENT_DATE, event.getSQLeventDate());
        db.insert(TABLE_EVENTS, null, values);
        db.close();

    }

    public MileEvent getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Date temp = new Date();

        Cursor cursor = db.query(TABLE_EVENTS, new String[]{KEY_ID,
                        KEY_START_LAT, KEY_START_LONG, KEY_END_LAT, KEY_END_LONG,
                        KEY_DISTANCE, KEY_EVENT_DATE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        MileEvent event = new MileEvent(Integer.parseInt(cursor.getString(0)),
                Double.parseDouble(cursor.getString(1)),
                Double.parseDouble(cursor.getString(2)),
                Double.parseDouble(cursor.getString(3)),
                Double.parseDouble(cursor.getString(4)),
                Double.parseDouble(cursor.getString(5)),
                parseDate(cursor.getString(6))
        );

        return event;
    }

    public Cursor fetch(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "select " + KEY_ID + " _id,* from " + TABLE_EVENTS, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursor;
    }

    public List<MileEvent> getAllEvents() {
        List<MileEvent> eventList = new ArrayList<MileEvent>();

        String selectQuery = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MileEvent event = new MileEvent();
                event.setEventId(Integer.parseInt(cursor.getString(0)));
                event.setStartLat(Double.parseDouble(cursor.getString(1)));
                event.setStartLong(Double.parseDouble(cursor.getString(2)));
                event.setEndLat(Double.parseDouble(cursor.getString(3)));
                event.setEndLong(Double.parseDouble(cursor.getString(4)));
                event.setDistance(Double.parseDouble(cursor.getString(5)));
                event.setEventDate(parseDate(cursor.getString(6)));
                eventList.add(event);
            } while (cursor.moveToNext());

        }
        return eventList;
    }

    public int getEventsCount() {
        String countQuery = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    public int updateEvent(MileEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, event.getEventId());
        values.put(KEY_START_LAT, event.getStartLat());
        values.put(KEY_START_LONG, event.getStartLong());
        values.put(KEY_END_LAT, event.getEndLat());
        values.put(KEY_END_LONG, event.getEndLong());
        values.put(KEY_DISTANCE, event.getDistance());
        values.put(KEY_EVENT_DATE, event.getSQLeventDate());

        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(event.getEventId())});
    }

    public void deleteEvent(MileEvent event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + " = ?",
                new String[] {String.valueOf(event.getEventId())});
    }


    // For parsing the sqlite date string
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

    public int getEventID(){
        return eventid;
    }
}
