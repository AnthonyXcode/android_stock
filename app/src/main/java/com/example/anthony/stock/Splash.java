package com.example.anthony.stock;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Splash extends BaseApplication {

    Realm realm;
    private String TAG = "Splash";
    Intent bootCompletedIntent;
    SharedPreferences sharedPreferences;

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
        getDateDataModifier();
//        getHourData();
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getMonthData();
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
                        DataSaver.saveData(20170515, 25371, 25385, 25213, 25233, 0);

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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getDateDataModifier();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gotoMain();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void getDateDataModifier(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest("http://hq.sinajs.cn/list=hkHSI", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " + response);
                String[] stringData = response.split("\"");
                Log.i(TAG, "onResponse: " + stringData.length);
                Log.i(TAG, "onResponse: " + stringData[1]);
                String[] stringElement = stringData[1].split(",");
                Log.i(TAG, "onResponse: open: " + stringElement[2]);
                Log.i(TAG, "onResponse: close: " + stringElement[6]);
                Log.i(TAG, "onResponse: high: " + stringElement[4]);
                Log.i(TAG, "onResponse: low: " + stringElement[5]);
                Log.i(TAG, "onResponse: volum" + Integer.parseInt(stringElement[11]) * 1000);
                Log.i(TAG, "onResponse: Date: " + stringElement[17]);

                String date = formatDateString(stringElement[17]);
                String close = stringElement[6];
                String high = stringElement[4];
                String low = stringElement[5];
                String open = stringElement[2];
                String volum = stringElement[11];
                DataSaver.saveData((int) Double.parseDouble(date), (int)Double.parseDouble(close),
                        (int)Double.parseDouble(high), (int)Double.parseDouble(low), (int)Double.parseDouble(open), (int)Double.parseDouble(volum))
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    gotoMain();
                                } else {
                                    Toast.makeText(Splash.this, "Cannot update!", Toast.LENGTH_SHORT).show();
                                    gotoMain();
                                }
                            }
                        });
                DataSaver.saveData(20170516,25335,25413,25228,25413, 78040000).subscribe();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gotoMain();
            }
        });
        requestQueue.add(stringRequest);
    }

    private String formatDateString (String obj) {
        String stringDate = String.valueOf(obj);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date newDate = sdf.parse(stringDate);
            String dateAsText = new SimpleDateFormat("yyyyMMdd").format(newDate);
            return dateAsText;
        } catch (ParseException e) {
            e.printStackTrace();
            return obj;
        }
    }

    private void gotoMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
