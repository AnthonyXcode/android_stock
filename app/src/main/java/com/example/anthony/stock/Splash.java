package com.example.anthony.stock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.anthony.stock.realmclasses.SaveDataToRealm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Splash extends AppCompatActivity {

    Realm realm;
    private String TAG = "Splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();


        getHourData();
    }

    private void getHourData(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://chartapi.finance.yahoo.com/instrument/1.0/%5EHSI/chartdata;type=quote;range=20d/json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response = response.replace("finance_charts_json_callback( ","");
                        response = response.replace(" )", "");
                        Log.i(TAG, "onResponse: " + response);
                        //Log.d("TAG", response);
                        //showMsgTxt.setText(response);
                        try {
                            JSONObject initialResponse = new JSONObject(response);
                            JSONArray dataArray = initialResponse.getJSONArray("series");
                            int p = dataArray.length();
                            for (int i = p-1; i >= 0; i--){
                                SaveDataToRealm.SaveHourData saveHourData = new SaveDataToRealm.SaveHourData("SaveHour", dataArray.getJSONObject(i));
                                saveHourData.onHandleIntent(getIntent());
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
                        Log.i(TAG, "onResponse: " + response);
                        //Log.d("TAG", response);
                        //showMsgTxt.setText(response);
                        try {
                            JSONObject initialResponse = new JSONObject(response);
                            JSONArray dataArray = initialResponse.getJSONArray("series");
                            for (int i = 0, p = dataArray.length(); i<p; i++){
                                SaveDataToRealm.SaveDateDate saveDateDate = new SaveDataToRealm.SaveDateDate("saveDate",dataArray.getJSONObject(i));
                                saveDateDate.onHandleIntent(getIntent());
                                //saveDateDataToReal(dataArray.getJSONObject(i));
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
