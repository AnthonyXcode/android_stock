package com.example.anthony.stock.FirebaseModel;

/**
 * Created by user on 25/5/2017.
 */

public class HourFBModel {
    private int Timestamp;
    private int close;
    private int high;
    private int low;
    private int open;
    private String date;

    public int getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(int timestamp) {
        Timestamp = timestamp;
    }

    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
