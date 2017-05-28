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
import com.example.anthony.stock.CheckData.UncheckedItem;
import com.example.anthony.stock.RealmClasses.DataSaver;
import com.example.anthony.stock.Service.BootCompletedService;
import com.example.anthony.stock.Utility.CommonTools;
import com.example.anthony.stock.Utility.DataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
        setupTool();
        startIntent();
        getDateDataModifier();
//        getHourData();
    }

    private void setupTool() {
        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
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
                final UncheckedItem item = DataHandler.handlerSinaData(response);
                Observable.just(item)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Consumer<UncheckedItem>() {
                            @Override
                            public void accept(UncheckedItem uncheckedItem) throws Exception {
                                DataSaver.saveData(uncheckedItem.getDate(), uncheckedItem.getStrDate(), item.getVolume(),
                                        uncheckedItem.getOpen(), uncheckedItem.getClose(), uncheckedItem.getLow(), uncheckedItem.getHigh());
                            }
                        })
                        .subscribe(new Consumer<UncheckedItem>() {
                            @Override
                            public void accept(UncheckedItem uncheckedItem) throws Exception {
                                gotoMain();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(Splash.this, "Update Error", Toast.LENGTH_SHORT).show();
                                gotoMain();
                            }
                        });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Splash.this, "API Error", Toast.LENGTH_SHORT).show();
                gotoMain();
            }
        });
        requestQueue.add(stringRequest);
    }

    private void gotoMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
