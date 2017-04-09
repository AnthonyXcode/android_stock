package com.example.anthony.stock.RealmClasses;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

/**
 * Created by Anthony on 9/11/16.
 */
public class SaveDataToRealm {
    public SaveDataToRealm(){

    }

    public static class SaveDateDate extends IntentService{

        private JSONObject jsonObject;
        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public SaveDateDate(String name, JSONObject jsonObject) {
            super(name);
            this.jsonObject = jsonObject;
        }

        @Override
        public void onHandleIntent(Intent intent) {
            Realm realm = Realm.getDefaultInstance();
            try {
                final int date = jsonObject.getInt("Date");

                DateData result = realm.where(DateData.class).equalTo("Date", date).findFirst();
                if (result == null) {
                    final int close = jsonObject.getInt("close");
                    final int high = jsonObject.getInt("high");
                    final int low = jsonObject.getInt("low");
                    final int open = jsonObject.getInt("open");
                    final int volume = jsonObject.getInt("volume");
                    realm.beginTransaction();
                    DateData dateData = new DateData();
                    dateData.setDate(date);
                    dateData.setClose(close);
                    dateData.setHigh(high);
                    dateData.setLow(low);
                    dateData.setOpen(open);
                    dateData.setVolume(volume);
                    String stringDate = String.valueOf(date) + " GMT+08:00";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd z");
                    try {
                        Date newDate = sdf.parse(stringDate);
                        String dateAsText = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(newDate);
                        Log.i("TAG new date", dateAsText);
                        dateData.setStrDate(dateAsText);
                        realm.copyToRealmOrUpdate(dateData);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    realm.commitTransaction();
                }

//                realm.executeTransaction(new Realm.Transaction() {
//                    @Override
//                    public void execute(Realm realm) {
//                        DateData dateData = new DateData();
//                        dateData.setDate(date);
//                        dateData.setClose(close);
//                        dateData.setHigh(high);
//                        dateData.setLow(low);
//                        dateData.setOpen(open);
//                        dateData.setVolume(volume);
//                        String stringDate = String.valueOf(date)+" GMT+08:00";
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd z");
//                        try {
//                            Date newDate = sdf.parse(stringDate);
//                            Log.i("TAG new date", String.valueOf(newDate));
//                            dateData.setStrDate(String.valueOf(newDate));
//                            realm.copyToRealmOrUpdate(dateData);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SaveHourData extends IntentService{

        JSONObject jsonObject;
        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public SaveHourData(String name, JSONObject jsonObject) {
            super(name);
            this.jsonObject = jsonObject;
        }

        @Override
        public void onHandleIntent(Intent intent) {
            Realm realm = Realm.getDefaultInstance();

            try {
                final int timestamp = jsonObject.getInt("Timestamp");

                HourData result = realm.where(HourData.class).equalTo("Timestamp", timestamp).findFirst();

                if (result == null) {
                    final int close = jsonObject.getInt("close");
                    final int high = jsonObject.getInt("high");
                    final int low = jsonObject.getInt("low");
                    final int open = jsonObject.getInt("open");

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            HourData hourData = new HourData();
                            hourData.setTimestamp(timestamp);
                            hourData.setClose(close);
                            hourData.setHigh(high);
                            hourData.setLow(low);
                            hourData.setOpen(open);
                            String dateAsText = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(timestamp * 1000L));
                            hourData.setDate(dateAsText);
                            realm.copyToRealmOrUpdate(hourData);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
