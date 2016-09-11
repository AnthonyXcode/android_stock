package com.example.anthony.stock.realmclasses;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Anthony on 9/11/16.
 */
public class HourData extends RealmObject {
    @PrimaryKey
    private int Timestamp;
    private int close;
    private int high;
    private int low;
    private int open;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public int getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(int timestamp) {
        Timestamp = timestamp;
    }
}