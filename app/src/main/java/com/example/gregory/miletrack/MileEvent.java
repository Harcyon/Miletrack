package com.example.gregory.miletrack;

import java.util.Date;

/**
 * Created by Gregory on 11/20/2017.
 */

public class MileEvent {
    int eventId;
    double startLat;
    double startLong;
    double endLat;
    double endLong;
    double distance;
    Date eventDate;

    // Empty constructor
    public MileEvent() {

    }

    // Parameterized constructor
    public MileEvent(int eventId, double startLat, double startLong, double endLat,
                     double endLong, double distance, Date eventDate) {
        this.eventId = eventId;
        this.startLat = startLat;
        this.endLat = endLat;
        this.startLong = startLong;
        this.endLong = endLong;
        this.distance = distance;
        this.eventDate = eventDate;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double endLong) {
        this.endLong = endLong;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getEventId() {

        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getSQLeventDate() {
        return "";
    }
}
