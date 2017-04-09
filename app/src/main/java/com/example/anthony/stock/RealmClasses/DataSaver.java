package com.example.anthony.stock.RealmClasses;

import android.util.Log;

import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.realm.Realm;

/**
 * Created by chanyunyuen on 8/4/2017.
 */

public class DataSaver {
    public static Observable<Boolean> saveDateDate(final JSONObject jsonObject){
        return Observable.just("").observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Object, Boolean>() {
                    @Override
                    public Boolean apply(Object o) throws Exception {
                        Boolean isfinish = false;
                        Realm realm = Realm.getDefaultInstance();
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
                        }else {
                            isfinish = true;
                        }
                        realm.close();
                        return isfinish;
                    }
                });
    }

    public static Observable<Boolean> saveMinsData(final JSONObject jsonObject) {
        return Observable.just("").observeOn(AndroidSchedulers.mainThread()).map(new Function<Object, Boolean>() {
            @Override
            public Boolean apply(Object o) throws Exception {
                Boolean isfinish = false;
                Realm realm = Realm.getDefaultInstance();
                try {
                    final int timestamp = jsonObject.getInt("Timestamp");
                    HourData result = realm.where(HourData.class).equalTo("Timestamp", timestamp).findFirst();
                    if (result == null) {
                        final int close = jsonObject.getInt("close");
                        final int high = jsonObject.getInt("high");
                        final int low = jsonObject.getInt("low");
                        final int open = jsonObject.getInt("open");

                        realm.beginTransaction();
                        HourData hourData = new HourData();
                        hourData.setTimestamp(timestamp);
                        hourData.setClose(close);
                        hourData.setHigh(high);
                        hourData.setLow(low);
                        hourData.setOpen(open);
                        String dateAsText = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(timestamp * 1000L));
                        hourData.setDate(dateAsText);
                        realm.copyToRealmOrUpdate(hourData);
                        realm.commitTransaction();
                    } else {
                        isfinish = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                realm.close();
                return isfinish;
            }
        });
    }
}
