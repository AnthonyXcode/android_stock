package com.example.anthony.stock.Utility;

import android.util.Log;

import com.example.anthony.stock.CheckData.UncheckedItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by test on 28/5/2017.
 */

public class DataHandler {
    private final static String TAG = DataHandler.class.getName();
    public static UncheckedItem handlerSinaData(String response){
        UncheckedItem item = new UncheckedItem();
        String[] stringData = response.split("\"");
        String[] stringElement = stringData[1].split(",");
        generateLog(stringData, stringElement);

        String date = formatDateString(stringElement[17]);
        String close = stringElement[6];
        String high = stringElement[4];
        String low = stringElement[5];
        String open = stringElement[2];
        String volume = stringElement[11];
        String dateStr = getDateString(date);
        item.setDate(Integer.parseInt(date));
        item.setStrDate(dateStr);
        item.setVolume((int) Double.parseDouble(volume));
        item.setOpen((int) Double.parseDouble(open));
        item.setClose((int) Double.parseDouble(close));
        item.setLow((int) Double.parseDouble(low));
        item.setHigh((int) Double.parseDouble(high));
        return item;
    }

    private static void generateLog(String[] stringData, String[] stringElement) {
        Log.i(TAG, "onResponse: " + stringData.length);
        Log.i(TAG, "onResponse: open: " + stringElement[2]);
        Log.i(TAG, "onResponse: close: " + stringElement[6]);
        Log.i(TAG, "onResponse: high: " + stringElement[4]);
        Log.i(TAG, "onResponse: low: " + stringElement[5]);
        Log.i(TAG, "onResponse: volume" + Integer.parseInt(stringElement[11]) * 1000);
        Log.i(TAG, "onResponse: Date: " + stringElement[17]);
    }

    private static String formatDateString (String obj) {
        String stringDate = String.valueOf(obj);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date newDate = sdf.parse(stringDate);
            String dateAsText = new SimpleDateFormat("yyyyMMdd").format(newDate);
            return dateAsText;
        } catch (ParseException e) {
            e.printStackTrace();
            return "0";
        }
    }

    private static String getDateString (String date){
        String stringDate = String.valueOf(date) + " GMT+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd z");
        String dateAsText = "";
        try {
            Date newDate = sdf.parse(stringDate);
            dateAsText = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateAsText;
    }
}
