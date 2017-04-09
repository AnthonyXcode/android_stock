package com.example.anthony.stock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.anthony.stock.RealmClasses.DataSaver;
import com.example.anthony.stock.Service.BootCompletedService;
import com.example.anthony.stock.Utility.CommonTools;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Splash extends BaseApplication {

    Realm realm;
    private String TAG = "Splash";
    Intent bootCompletedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
        startIntent();
        getHourData();
    }

    private void startIntent(){
        if (!CommonTools.checkServiceRunning(BootCompletedService.class, this)) {
            bootCompletedIntent = new Intent(this, BootCompletedService.class);
            startService(bootCompletedIntent);
        }
    }

    private void getHourData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://chartapi.finance.yahoo.com/instrument/1.0/%5EHSI/chartdata;type=quote;range=20d/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.replace("finance_charts_json_callback( ","");
                        response = response.replace(" )", "");
                        try {
                            JSONObject initialResponse = new JSONObject(response);
                            JSONArray dataArray = initialResponse.getJSONArray("series");
                            int p = dataArray.length();
                            for (int i = p-1; i >= 0; i--){
                                DataSaver.saveMinsData(dataArray.getJSONObject(i)).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                    }
                                });
                            }
                            getMonthData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                gotoMain();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getMonthData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://chartapi.finance.yahoo.com/instrument/1.0/%5EHSI/chartdata;type=quote;range=3y/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.replace("finance_charts_json_callback( ","");
                        response = response.replace(" )", "");
                        try {
                            JSONObject initialResponse = new JSONObject(response);
                            JSONArray dataArray = initialResponse.getJSONArray("series");
                            for (int i = 0, p = dataArray.length(); i<p; i++){
                                DataSaver.saveDateDate(dataArray.getJSONObject(i)).subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                    }
                                });
                            }
                            gotoMain();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gotoMain();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void gotoMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
