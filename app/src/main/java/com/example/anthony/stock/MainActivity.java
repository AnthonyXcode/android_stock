package com.example.anthony.stock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.anthony.stock.realmclasses.DateData;
import com.example.anthony.stock.realmclasses.HourData;
import com.example.anthony.stock.realmclasses.SaveDataToRealm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    TextView showMsgTxt;
    Realm realm;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Button gotoDetailPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        RealmResults<HourData> results = realm.where(HourData.class).findAll();
        getHourData();
        getMonthData();
        showMsgTxt = (TextView)findViewById(R.id.showMsgTxt);
        gotoDetailPageBtn = (Button)findViewById(R.id.gotoDetailPageBtn);
        gotoDetailPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailPageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getHourData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://chartapi.finance.yahoo.com/instrument/1.0/%5EHSI/chartdata;type=quote;range=15d/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.replace("finance_charts_json_callback( ","");
                        response = response.replace(" )", "");
                        //Log.d("TAG", response);
                        //showMsgTxt.setText(response);
                        try {
                            showMsgTxt.setText(response);
                            JSONObject initialResponse = new JSONObject(response);
                            JSONArray dataArray = initialResponse.getJSONArray("series");
                            int p = dataArray.length();
                            for (int i = p-1; i >= 0; i--){
                                SaveDataToRealm.SaveHourData saveHourData = new SaveDataToRealm.SaveHourData("SaveHour", dataArray.getJSONObject(i));
                                saveHourData.onHandleIntent(getIntent());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        requestQueue.add(stringRequest);
    }

    private void saveHourDataToReal(JSONObject jsonObject){
        try {
            final int timestamp = jsonObject.getInt("Timestamp");
            final int close = jsonObject.getInt("close");
            final int high = jsonObject.getInt("high");
            final int low = jsonObject.getInt("low");
            final int open = jsonObject.getInt("open");

            realm.executeTransactionAsync(new Realm.Transaction() {
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
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getMonthData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://chartapi.finance.yahoo.com/instrument/1.0/%5EHSI/chartdata;type=quote;range=3y/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.replace("finance_charts_json_callback( ","");
                        response = response.replace(" )", "");
                        //Log.d("TAG", response);
                        //showMsgTxt.setText(response);
                        try {
                            showMsgTxt.setText(response);
                            JSONObject initialResponse = new JSONObject(response);
                            JSONArray dataArray = initialResponse.getJSONArray("series");
                            for (int i = 0, p = dataArray.length(); i<p; i++){
                                SaveDataToRealm.SaveDateDate saveDateDate = new SaveDataToRealm.SaveDateDate("saveDate",dataArray.getJSONObject(i));
                                saveDateDate.onHandleIntent(getIntent());
                                //saveDateDataToReal(dataArray.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        requestQueue.add(stringRequest);
    }

    private void saveDateDataToReal(JSONObject jsonObject){
        try {
            final int date = jsonObject.getInt("Date");
            final int close = jsonObject.getInt("close");
            final int high = jsonObject.getInt("high");
            final int low = jsonObject.getInt("low");
            final int open = jsonObject.getInt("open");
            final int volume = jsonObject.getInt("volume");

            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    DateData dateData = new DateData();
                    dateData.setDate(date);
                    dateData.setClose(close);
                    dateData.setHigh(high);
                    dateData.setLow(low);
                    dateData.setOpen(open);
                    dateData.setVolume(volume);
                    String stringDate = String.valueOf(date)+" GMT+08:00";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd z");
                    try {
                        Date newDate = sdf.parse(stringDate);
                        Log.i("TAG new date", String.valueOf(newDate));
                        dateData.setStrDate(String.valueOf(newDate));
                        realm.copyToRealmOrUpdate(dateData);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {

                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
