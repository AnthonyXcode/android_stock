package com.example.anthony.stock.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.LoginFilter;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.anthony.stock.CheckData.UncheckedItem;
import com.example.anthony.stock.CrossRSI.CrossRSIItem;
import com.example.anthony.stock.R;
import com.example.anthony.stock.RealmClasses.DataSaver;
import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;
import com.example.anthony.stock.Splash;
import com.example.anthony.stock.Utility.DataHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by chanyunyuen on 8/4/2017.
 */

public class BootCompletedService extends Service{
    private String TAG = "BootCompletedService";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setupTools();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: stop");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    Timer scheduleTimer;
    TimerTask timerTask;
    int timeInterval;
    Realm realm;
    RequestQueue requestQueue;
    private NotificationManager manager;

    private void setupTools(){
        Log.i(TAG, "setupTools: ");
        scheduleTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (ifNeedToUpdate()) {
                    updateData();
                }
            }
        };
        timeInterval = 60000;
        try{
            realm = Realm.getDefaultInstance();
        }catch (Exception ex){
            RealmConfiguration config = new RealmConfiguration.Builder(this)
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
        }
        requestQueue = Volley.newRequestQueue(this);
        items = new ArrayList<>();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        scheduleTimer.schedule(timerTask, 0, timeInterval);
    }

    private boolean ifNeedToUpdate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Log.i(TAG, "ifNeedToUpdate: " + calendar.get(Calendar.MINUTE));
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek < Calendar.MONDAY || dayOfWeek > Calendar.FRIDAY){
            return false;
        }
        boolean need = false;
        if (hourOfDay == 9){
            if (minute > 30){
                need = true;
            }
        }else if (hourOfDay == 15){
            if (minute > 30 ){
                need = true;
            }
        }else {
            need = false;
        }
        return need;
    }

    private void updateData(){
        StringRequest updateData = new StringRequest("http://hq.sinajs.cn/list=hkHSI", new Response.Listener<String>() {
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
                                daysDataGot = true;
                                startCal();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(BootCompletedService.this, "Update Error", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(updateData);
    }

    boolean minsDataGot = false;
    boolean daysDataGot = false;


    private void startCal(){
        calResult();
        successNotification();
    }

    int shortRsi = 7;
    int longRsi = 25;
    int validRsi = 5;
    int validDays = 7;
    ArrayList<CrossRSIItem> items;
    RealmResults<DateData> dateDatas;
    RealmResults<HourData> hourDatas;
    private void calResult(){
        items.clear();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
        for (DateData realmItem:dateDatas){
            CrossRSIItem item = initItme(realmItem.getStrDate(), realmItem.getOpen(), realmItem.getClose(), realmItem.getLow(), realmItem.getHigh());
            items.add(item);
        }
        for (int i = 0; i < items.size(); i++){
            if (i == shortRsi -1){
                initRsi(items, shortRsi, true);
            }
            if (i == longRsi - 1){
                initRsi(items, longRsi, false);
            }
            if (i > shortRsi - 1){
                modifyItem(i, shortRsi, true);
            }

            if (i > longRsi - 1){
                modifyItem(i , longRsi, false);
            }
        }
        
        analyseResult();
    }

    private CrossRSIItem initItme (String day, int dayOpen, int dayClose, int dayLow, int dayHigh){
        CrossRSIItem item = new CrossRSIItem();
        item.setDay(day);
        item.setDayOpen(dayOpen);
        item.setDayClose(dayClose);
        item.setDayLow(dayLow);
        item.setDayHigh(dayHigh);
        return item;
    }

    private void initRsi(ArrayList<CrossRSIItem> items, int rsiDays, boolean isShort){
        int totalRaise = 0;
        int totalDrop = 0;
        for (int i = 0; i < items.size(); i++){
            if (i == rsiDays - 1) break;
            CrossRSIItem firstItem = items.get(i);
            CrossRSIItem secondItem = items.get(i+1);
            int difference = secondItem.getDayClose() - firstItem.getDayClose();
            if (difference > 0) {
                totalRaise += difference;
            } else {
                totalDrop += Math.abs(difference);
            }
        }
        CrossRSIItem lastItem = items.get(rsiDays);
        int rsi = countRSI(totalRaise, totalDrop);
        if (isShort){
            lastItem.setRaiseShortAverage(((double) totalRaise)/rsiDays);
            lastItem.setDropShortAverage(((double) totalDrop)/rsiDays);
            lastItem.setShortRsi(rsi);
        }else {
            lastItem.setRaiseLongAverage(((double) totalRaise)/rsiDays);
            lastItem.setDropLongAverage(((double) totalDrop)/rsiDays);
            lastItem.setLongRsi(rsi);
        }
    }

    private int countRSI(double totalRaise, double totalDrop){
        int rsi = (int) ((totalRaise/(totalDrop + totalRaise)) * 100);
        return rsi;
    }

    private void modifyItem(int position, int rsiDays, boolean isShort){
        CrossRSIItem item = items.get(position);
        CrossRSIItem previousItme = items.get(position - 1);
        double different = item.getDayClose() - previousItme.getDayClose();
        double raiseAverage;
        double dropAverage;
        if (isShort) {
            if (different > 0) {
                raiseAverage = ((previousItme.getRaiseShortAverage() * (rsiDays - 1)) + different) / rsiDays;
                dropAverage = previousItme.getDropShortAverage() * (rsiDays - 1) / rsiDays;
            } else {
                raiseAverage = previousItme.getRaiseShortAverage() * (rsiDays - 1) / rsiDays;
                dropAverage = (previousItme.getDropShortAverage() * (rsiDays - 1) + Math.abs(different)) / rsiDays;
            }
            double rsi = (raiseAverage / (raiseAverage + dropAverage)) * 100;
            item.setRaiseShortAverage(raiseAverage);
            item.setDropShortAverage(dropAverage);
            item.setShortRsi(rsi);
        }else {
            if (different > 0) {
                raiseAverage = ((previousItme.getRaiseLongAverage() * (rsiDays - 1)) + different) / rsiDays;
                dropAverage = previousItme.getDropLongAverage() * (rsiDays - 1) / rsiDays;
            } else {
                raiseAverage = previousItme.getRaiseLongAverage() * (rsiDays - 1) / rsiDays;
                dropAverage = (previousItme.getDropLongAverage() * (rsiDays - 1) + Math.abs(different)) / rsiDays;
            }
            double rsi = (raiseAverage / (raiseAverage + dropAverage)) * 100;
            item.setRaiseLongAverage(raiseAverage);
            item.setDropLongAverage(dropAverage);
            item.setLongRsi(rsi);
        }
    }

    int totalWin;
    int winNumb;
    int lossNumb;
    int cutlose;
    int lossbuy;
    int losssell;
    int totalTrade;
    private void analyseResult(){
        totalWin = 0;
        winNumb = 0;
        lossNumb = 0;
        cutlose = 0;
        lossbuy = 0;
        losssell = 0;
        totalTrade = 0;

        for(int i = 0; i < items.size(); i++) {
            CrossRSIItem item = items.get(i);
            CrossRSIItem previousItem;
            try{
                previousItem = items.get(i - 1);
            }catch (Exception ex){
                continue;
            }
            if (previousItem.getLongRsi() > previousItem.getShortRsi() && item.getLongRsi() < item.getShortRsi()){
                if (Math.abs(item.getLongRsi() - item.getShortRsi()) > validRsi){
                    totalTrade += 1;
                    item.setBuyPrice(item.getDayClose());
                    analyseForBuy(i);
                }
            }else if (previousItem.getLongRsi() < previousItem.getShortRsi() && item.getLongRsi() > item.getShortRsi()){
                if (Math.abs(item.getLongRsi() - item.getShortRsi()) > validRsi){
                    totalTrade += 1;
                    item.setSellPrice(item.getDayClose());
                    analyseForSell(i);
                }
            }

        }
    }
    private void analyseForBuy(int position){
        CrossRSIItem buyItem = items.get(position);
        for (int i = position + 1; i < position + validDays; i++){
            CrossRSIItem movingItem;
            try {
                movingItem = items.get(i);
            }catch (Exception ex){
                break;
            }
            if (movingItem.getLongRsi() > movingItem.getShortRsi() || buyItem.getDayClose() - movingItem.getDayClose() > 150){
                cutlose += 1;
                if (movingItem.getDayClose() < buyItem.getDayClose()){
                    lossNumb += 1;
                }else {
                    winNumb += 1;
                }
                totalWin += movingItem.getDayClose() - buyItem.getDayClose();
                lossbuy += movingItem.getDayClose() - buyItem.getDayClose();
                movingItem.setSellPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(movingItem.getDayClose()-buyItem.getDayClose());
                break;
            }

            if (i == position + validDays - 1){
                if (movingItem.getDayClose() < buyItem.getDayClose()){
                    lossNumb += 1;
                }else {
                    winNumb += 1;
                }
                totalWin += movingItem.getDayClose() - buyItem.getDayClose();
                movingItem.setSellPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(movingItem.getDayClose()-buyItem.getDayClose());
                break;
            }
        }
    }

    private void analyseForSell(int position){
        CrossRSIItem sellItem = items.get(position);
        for (int i = position + 1; i < position + validDays; i++){
            CrossRSIItem movingItem;
            try {
                movingItem = items.get(i);
            }catch (Exception ex){
                break;
            }
            if (movingItem.getLongRsi() < movingItem.getShortRsi() || movingItem.getDayClose() - sellItem.getDayClose() > 150){
                cutlose += 1;
                if (movingItem.getDayClose() > sellItem.getDayClose()){
                    lossNumb += 1;
                }else {
                    winNumb += 1;
                }
                totalWin += sellItem.getDayClose() - movingItem.getDayClose();
                losssell += sellItem.getDayClose() - movingItem.getDayClose();
                movingItem.setBuyPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(sellItem.getDayClose() - movingItem.getDayClose());
                break;
            }

            if (i == position + validDays - 1){
                if (movingItem.getDayClose() > sellItem.getDayClose()){
                    lossNumb += 1;
                }else {
                    winNumb += 1;
                }
                totalWin += sellItem.getDayClose() - movingItem.getDayClose();
                movingItem.setBuyPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(sellItem.getDayClose() - movingItem.getDayClose());
                break;
            }
        }
    }

    private void successNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        String info;
        int buy = 0;
        int sell = 0;
        int winOrLost = 0;
        String title = "";
        String content;
        CrossRSIItem lastItem;
        if (items.size() != 0) {
            lastItem = items.get(items.size() - 1);
            info = items.get(items.size() - 1).getDay().substring(0, 19);
            buy = items.get(items.size() - 1).getBuyPrice();
            sell = items.get(items.size() - 1).getSellPrice();
            winOrLost = items.get(items.size() - 1).getWinOrloss();
        }else {
            info = "";
            lastItem = null;
        }

        if (lastItem != null) {
            if (buy == 0 && sell == 0 && winOrLost == 0) {
                title = "No Action";
            } else if (buy != 0 && sell == 0 && winOrLost == 0) {
                title = "Buy. At price: " + lastItem.getBuyPrice();
            } else if (buy != 0 && sell == 0 && winOrLost != 0) {
                title = "Liquidation. At price: " + lastItem.getDayClose() + " win of loss: " + lastItem.getWinOrloss();
            } else if (buy == 0 && sell != 0 && winOrLost == 0) {
                title = "Sell. At price: " + lastItem.getSellPrice();
            } else if (buy == 0 && sell != 0 && winOrLost != 0) {
                title = "Liquidation. At price: " + lastItem.getDayClose() + " win or loss: " + lastItem.getWinOrloss();
            }
            content = "Long Rsi = " + String.valueOf((int)lastItem.getLongRsi()) + " Short Rsi = " + String.valueOf((int)lastItem.getShortRsi());
        }else {
            title = "Error";
            info = "Error";
            content = "Error";
        }
        builder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo(info)
                .setCategory(Notification.CATEGORY_ALARM)
                .setColor(Color.WHITE)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX);
        Notification notification = builder.build();
        manager.notify(1, notification);
    }
}
