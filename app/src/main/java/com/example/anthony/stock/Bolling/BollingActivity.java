package com.example.anthony.stock.Bolling;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.BaseApplication;
import com.example.anthony.stock.R;
import com.example.anthony.stock.realmclasses.DateData;
import com.example.anthony.stock.realmclasses.HourData;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class BollingActivity extends BaseApplication {

    private Realm realm;
    private RealmResults<DateData> dateDatas;
    private RealmResults<HourData> hourDatas;
    private ArrayList<Integer> typicalC;
    private String TAG = "BollingActivity";
    private ArrayList<BollingItem> bollingItems;
    private ListView bollingListView;
    private BollingAdapter adapter;
    private EditText validDaysEditTxt;
    private EditText cutPercentageEditText;
    private Button okBtn;
    private TextView totalWinNumberTxt;
    private TextView totalWinValueTxt;
    private TextView cutLostNumberTxt;
    private TextView cutLostValueTxt;
    private TextView lostNumbTxt;
    private TextView lostValueTxt;
    private TextView winNumbTxt;
    private TextView winValueTxt;
    private TextView meetTargetNumbTxt;
    private TextView meetTargetValueTxt;
    private TextView buyTxt;
    private TextView sellTxt;
    private TextView tarForeTxt;
    private TextView cutForeTxt;
    private int validDays = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolling);
        setupLayout();
        handleResult();
        setupListview();
        analysize(5);
        setupClick();
        setupForecast();
    }

    private void setupLayout(){
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date", Sort.ASCENDING);
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
        typicalC = new ArrayList<>();
        bollingItems = new ArrayList<>();
        totalWinNumberTxt = (TextView)findViewById(R.id.totalWinNumberTxt);
        cutPercentageEditText = (EditText)findViewById(R.id.cutPercentageEditText);
        totalWinValueTxt = (TextView)findViewById(R.id.totalWinValueTxt);
        cutLostNumberTxt = (TextView)findViewById(R.id.cutLostNumberTxt);
        lostValueTxt = (TextView)findViewById(R.id.lostValueTxt);
        winValueTxt = (TextView)findViewById(R.id.winValueTxt);
        bollingListView = (ListView)findViewById(R.id.bollingListView);
        validDaysEditTxt = (EditText)findViewById(R.id.validDaysEditTxt);
        okBtn = (Button)findViewById(R.id.okBtn);
        lostNumbTxt = (TextView)findViewById(R.id.lostNumbTxt);
        winNumbTxt = (TextView)findViewById(R.id.winNumbTxt);
        meetTargetNumbTxt = (TextView)findViewById(R.id.meetTargetNumbTxt);
        meetTargetValueTxt = (TextView)findViewById(R.id.meetTargetValueTxt);
        cutLostValueTxt = (TextView)findViewById(R.id.cutLostValueTxt);
        buyTxt = (TextView)findViewById(R.id.buyTxt);
        sellTxt = (TextView)findViewById(R.id.sellTxt);
        tarForeTxt = (TextView)findViewById(R.id.tarForeTxt);
        cutForeTxt = (TextView)findViewById(R.id.cutForeTxt);
    }

    private void setupClick(){
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buy = false;
                sell = false;
                totalwinValue = 0;
                cutlostNumber = 0;
                lostNumber = 0;
                winNumber = 0;
                meetTargetNumber = 0;
                meetTargetValue = 0;
                cutlostValue = 0;
                totalWinNumber = 0;
                lostValue = 0;
                winValue = 0;
                handleResult();
                validDays = Integer.parseInt(validDaysEditTxt.getText().toString());
                cutPercentage = Double.parseDouble(cutPercentageEditText.getText().toString());
                analysize(validDays);
                setupForecast();
            }
        });
    }

    private void handleResult(){
        bollingItems.clear();
        typicalC.clear();
        for (DateData data:dateDatas){
            if (typicalC.size() < 20){
                typicalC.add(findTypicalC(data.getHigh(), data.getLow(), data.getClose()));
                continue;
            }else {
                typicalC.remove(0);
                typicalC.add(findTypicalC(data.getHigh(), data.getLow(), data.getClose()));
            }

            int MA20 = findAverage(typicalC);

            int upperDev = MA20 + findSD(typicalC) * 2;

            int lowerDev = MA20 - findSD(typicalC) * 2;

            BollingItem bItem = new BollingItem();
            bItem.setDate(data.getStrDate());
            bItem.setLower(lowerDev);
            bItem.setUpper(upperDev);
            bItem.setLower5Percent((int) ((upperDev -lowerDev) * 0.05 + lowerDev));
            bItem.setUpper5Percent((int) (upperDev - (upperDev - lowerDev) * 0.05));
            Log.i(TAG, "handleResult: "+ String.valueOf((upperDev - lowerDev) * 0.05));
            bItem.setMA20(MA20);
            bItem.setTypicalC(typicalC.get(19));
            bItem.setOpen(data.getOpen());
            bItem.setClose(data.getClose());
            bItem.setDayHigh(data.getHigh());
            bItem.setDayLow(data.getLow());
            bollingItems.add(bItem);
        }

        String date = hourDatas.get(0).getDate().substring(0,10);
        int hight = hourDatas.get(0).getHigh();
        int low = hourDatas.get(0).getLow();
        int open = 0;
        int close = hourDatas.get(0).getClose();
        Log.i(TAG, "handleResult: " + date);
        for (HourData hourData : hourDatas){
            if (hourData.getDate().contains(date)){
                if (hourData.getHigh() > hight){
                    hight = hourData.getHigh();
                }
                if (hourData.getLow() < low){
                    low = hourData.getLow();
                }
                open = hourData.getOpen();
            }else {
                break;
            }
        }
        typicalC.remove(0);
        typicalC.add(findTypicalC(hight, low, close));
        int Ma20 = findAverage(typicalC);
        int upperDev = Ma20 + findSD(typicalC) * 2;
        int lowerDev = Ma20 - findSD(typicalC) * 2;
        BollingItem bItem = new BollingItem();
        bItem.setDate(hourDatas.get(0).getDate());
        bItem.setLower(lowerDev);
        bItem.setUpper(upperDev);
        bItem.setLower5Percent((int)((upperDev - lowerDev)*0.55 + lowerDev));
        bItem.setUpper5Percent((int)((upperDev - (upperDev - lowerDev) * 0.05)));
        bItem.setMA20(Ma20);
        bItem.setTypicalC(typicalC.get(19));
        bItem.setOpen(open);
        bItem.setOpen(open);
        bItem.setClose(close);
        bItem.setDayHigh(hight);
        bItem.setDayLow(low);
        bollingItems.add(bItem);
    }

    double cutPercentage = 0;

    boolean buy = false;
    boolean sell = false;

    int totalWinNumber = 0;
    int totalwinValue = 0;

    int cutlostNumber = 0;
    int cutlostValue = 0;

    int lostNumber = 0;
    int lostValue = 0;

    int winNumber = 0;
    int winValue = 0;

    int meetTargetNumber = 0;
    int meetTargetValue = 0;
    private void analysize(int validDays){
        for (int i = 0; i < bollingItems.size() - validDays; i++){
            BollingItem itemN = bollingItems.get(i);
            BollingItem itemN1 = bollingItems.get(i+1);
            if (itemN.getTypicalC() < itemN.getLower5Percent()){
                if (itemN1.getClose() > itemN1.getOpen()){
                    buy = true;
                    itemN1.setBuy(true);
                    bollingItems.set(i + 1, itemN1);
                }
            }else if (itemN.getTypicalC() > itemN.getUpper5Percent()){
                if (itemN1.getOpen() > itemN1.getClose()){
                    sell = true;
                    itemN1.setSell(true);
                    bollingItems.set(i+1, itemN1);
                }
            }

            if (buy == true){
                setResultForBuy(i, itemN1, validDays);
            }else if (sell == true){
                setResultForSell(i, itemN1, validDays);
            }
        }
        printOutResult();
        adapter.notifyDataSetChanged();
    }

    private void setResultForBuy(int i, BollingItem itemN1, int validDays){
        for (int j = i + 1; j < i + validDays; j++){
            BollingItem movingItem = bollingItems.get(j);
            BollingItem previousItem = bollingItems.get(j - 1);
            if (cutLostBuy(movingItem, previousItem) && j != i + 1){
                buy = false;

                double SD = (previousItem.getUpper() - previousItem.getMA20()) / 2;
                cutlostNumber += 1;
                cutlostValue += previousItem.getLower()  - itemN1.getClose();

                totalWinNumber -= 1;
                totalwinValue += previousItem.getLower()  - itemN1.getClose();

                movingItem.setCutLostBuy(true);
                movingItem.setTotalWin(totalwinValue);
                movingItem.setCutlostValue(previousItem.getLower());
                bollingItems.set(j, movingItem);
                return;
            }

            if (movingItem.getDayHigh() > previousItem.getMA20()){
                buy = false;
                meetTargetNumber += 1;
                meetTargetValue += previousItem.getMA20() - itemN1.getClose();

                totalWinNumber += 1;
                totalwinValue += previousItem.getMA20() - itemN1.getClose();
                movingItem.setCompensateBuy(true);
                movingItem.setTotalWin(totalwinValue);
                bollingItems.set(j, movingItem);
                return;
            }

            if (j == i + validDays - 1){
                buy = false;
                if (movingItem.getClose() > itemN1.getClose()){
                    winNumber += 1;
                    totalWinNumber += 1;
                    winValue += movingItem.getClose() - itemN1.getClose();
                }else {
                    lostNumber += 1;
                    totalWinNumber -= 1;
                    lostValue += itemN1.getClose() - movingItem.getClose();
                }
                totalwinValue += movingItem.getClose() - itemN1.getClose();
                movingItem.setNormalBuy(true);
                movingItem.setTotalWin(totalwinValue);
                bollingItems.set(j, movingItem);
                return;
            }
        }
    }

    private void setResultForSell(int i, BollingItem itemN1, int validDays){
        for (int j = i + 1; j < i + validDays; j++){
            BollingItem movingItem = bollingItems.get(j);
            BollingItem previousItem = bollingItems.get(j - 1);
            if (cutLostSell(movingItem, previousItem) && j != i+1){
                sell = false;
                int SD = (previousItem.getUpper() - previousItem.getMA20()) / 2;

                cutlostNumber += 1;
                cutlostValue += (int) (itemN1.getClose() - (previousItem.getUpper() + SD * cutPercentage));

                totalWinNumber -= 1;
                totalwinValue += (int) (itemN1.getClose() - (previousItem.getUpper() + SD * cutPercentage));

                movingItem.setCutLostSell(true);
                movingItem.setTotalWin(totalwinValue);
                movingItem.setCutlostValue((int) (previousItem.getUpper() + SD * cutPercentage));
                bollingItems.set(j, movingItem);
                return;
            }

            if (movingItem.getDayHigh() < previousItem.getMA20()){
                sell = false;
                meetTargetNumber += 1;
                meetTargetValue += itemN1.getClose() - previousItem.getMA20();

                totalWinNumber += 1;
                totalwinValue += itemN1.getClose() - previousItem.getMA20();

                movingItem.setCompensateSell(true);
                movingItem.setTotalWin(totalwinValue);
                bollingItems.set(j, movingItem);
                return;
            }

            if (j == i + validDays - 1){
                if (itemN1.getClose() > movingItem.getClose()){
                    winNumber += 1;
                    totalWinNumber += 1;
                    winValue += itemN1.getClose() - movingItem.getClose();
                }else {
                    lostNumber += 1;
                    totalWinNumber -= 1;
                    lostValue += movingItem.getClose() - itemN1.getClose();
                }

                totalwinValue += itemN1.getClose() - movingItem.getClose();

                movingItem.setNormalSell(true);
                movingItem.setTotalWin(totalwinValue);
                bollingItems.set(j, movingItem);
                sell = false;
                return;
            }
        }
    }

    private boolean cutLostBuy(BollingItem movingItem, BollingItem previouseItem){
        int SD = (previouseItem.getUpper()- previouseItem.getMA20()) / 2;
        if (movingItem.getDayLow() < previouseItem.getLower() - SD * cutPercentage){
            Log.i(TAG, "cutLostBuy: " + movingItem.getDate());
            return true;
        }else {
            return false;
        }
    }

    private boolean cutLostSell(BollingItem movingItem, BollingItem previousItem){
        int SD = (previousItem.getUpper() - previousItem.getMA20()) / 2;
        if (movingItem.getDayHigh() > previousItem.getUpper() + SD * cutPercentage){
            Log.i(TAG, "cutLostSell: " + movingItem.getDate());
            return true;
        }else {
            return false;
        }
    }

    private void setupForecast(){
        int isbuy = 0;
        for(int i = 0; i <  validDays; i++){
            if (i != validDays - 1) {
                BollingItem itemN = bollingItems.get(bollingItems.size() - validDays + i -1);
                BollingItem itemN1 = bollingItems.get(bollingItems.size() - validDays + i);
                if (itemN.getTypicalC() < itemN.getLower5Percent()){
                    if (itemN1.getClose() > itemN1.getOpen()){
                        buy = true;
                        itemN1.setBuy(true);
                        bollingItems.set(bollingItems.size() - validDays + i, itemN1);
                        isbuy = 1;
                    }
                }else if (itemN.getTypicalC() > itemN.getUpper5Percent()){
                    if (itemN1.getOpen() > itemN1.getClose()){
                        sell = true;
                        itemN1.setSell(true);
                        bollingItems.set(bollingItems.size() - validDays + i, itemN1);
                        isbuy = 2;
                    }
                }
            }else if (i == validDays - 1){
                BollingItem itemN1 = bollingItems.get(bollingItems.size() - validDays + i);
                if (isbuy == 0){
                    sellTxt.setBackgroundColor(Color.GRAY);
                    buyTxt.setBackgroundColor(Color.GRAY);
                    cutForeTxt.setText("");
                }else if (isbuy == 1){
                    buyTxt.setBackgroundColor(Color.RED);
                    sellTxt.setBackgroundColor(Color.GRAY);
                    cutForeTxt.setText(String.valueOf(itemN1.getLower()));
                }else if (isbuy == 2){
                    buyTxt.setBackgroundColor(Color.GRAY);
                    sellTxt.setBackgroundColor(Color.RED);
                    cutForeTxt.setText(String.valueOf(itemN1.getUpper()));
                }

                tarForeTxt.setText(String.valueOf(itemN1.getMA20()));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void printOutResult(){
        totalWinNumberTxt.setText(String.valueOf(totalWinNumber));
        totalWinValueTxt.setText(String.valueOf(totalwinValue));
        cutLostNumberTxt.setText(String.valueOf(cutlostNumber));
        cutLostValueTxt.setText(String.valueOf(cutlostValue));
        lostNumbTxt.setText(String.valueOf(lostNumber));
        lostValueTxt.setText(String.valueOf(lostValue));
        winNumbTxt.setText(String.valueOf(winNumber));
        winValueTxt.setText(String.valueOf(winValue));
        meetTargetNumbTxt.setText(String.valueOf(meetTargetNumber));
        meetTargetValueTxt.setText(String.valueOf(meetTargetValue));
    }

    private void setupListview(){
        adapter = new BollingAdapter(this, bollingItems);
        bollingListView.setAdapter(adapter);
        bollingListView.setSelection(bollingListView.getCount() - 1);
    }

    private int findTypicalC(int dayH, int dayL, int dayClose){
        return (dayH + dayL + dayClose)/3;
    }

    private int findSD(ArrayList<Integer> items){
        int average = findAverage(items);
        int summation = 0;
        for (int value:items){
            summation = summation + (int)Math.pow((value - average), 2);
        }
        int SD = (int) Math.sqrt(summation/items.size());
        return SD;
    }

    private int findAverage(ArrayList<Integer> items){
        int total = 0;
        for (int value:items){
            total = total + value;
        }
        int average = total/items.size();
        return average;
    }
}
