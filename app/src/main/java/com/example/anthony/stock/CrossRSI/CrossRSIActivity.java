package com.example.anthony.stock.CrossRSI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.R;
import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CrossRSIActivity extends AppCompatActivity {

    ListView crossRsiListView;
    EditText crossValidDaysEditTxt;
    EditText crossValidRsiEditTxt;
    EditText crossShortRsiDaysEditTxt;
    EditText crossLongRsiDaysEditTxt;
    Button corssRsiOKBtn;
    TextView crossRSITxt;
    int shortRsi = 7;
    int longRsi = 25;
    int validRsi = 5;
    int validDays = 7;
    ArrayList<CrossRSIItem> items;
    Realm realm;
    RealmResults<DateData> dateDatas;
    RealmResults<HourData> hourDatas;
    CrossRSIAdapter adapter;
    private String TAG = "CrossRSIActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cross_rsi);
        setupLayout();
        setupClick();
        setupTool();
        initValidData();
    }

    private void setupLayout(){
        crossRsiListView = (ListView)findViewById(R.id.crossRsiListView);
        crossValidDaysEditTxt = (EditText) findViewById(R.id.crossValidDaysEditTxt);
        crossValidRsiEditTxt = (EditText) findViewById(R.id.crossValidRsiEditTxt);
        crossShortRsiDaysEditTxt = (EditText) findViewById(R.id.crossShortRsiDaysEditTxt);
        crossLongRsiDaysEditTxt = (EditText) findViewById(R.id.crossLongRsiDaysEditTxt);
        corssRsiOKBtn = (Button)findViewById(R.id.corssRsiOKBtn);
        crossRSITxt = (TextView)findViewById(R.id.crossRSITxt);
        crossShortRsiDaysEditTxt.setText(String.valueOf(shortRsi));
        crossLongRsiDaysEditTxt.setText(String.valueOf(longRsi));
        crossValidRsiEditTxt.setText(String.valueOf(validRsi));
        crossValidDaysEditTxt.setText(String.valueOf(validDays));
    }

    private void setupClick(){
        corssRsiOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                initValidData();
            }
        });
    }

    private void setupTool(){
        items = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
    }

    private void setData(){
        shortRsi = Integer.parseInt(crossShortRsiDaysEditTxt.getText().toString());
        longRsi = Integer.parseInt(crossLongRsiDaysEditTxt.getText().toString());
        validRsi = Integer.parseInt(crossValidRsiEditTxt.getText().toString());
        validDays = Integer.parseInt(crossValidDaysEditTxt.getText().toString());
    }

    private void initValidData(){
        items.clear();
        for (DateData realmItem:dateDatas){
            CrossRSIItem item = initItme(realmItem.getStrDate(), realmItem.getOpen(), realmItem.getClose(), realmItem.getLow(), realmItem.getHigh());
            items.add(item);
        }
        String dateStr = hourDatas.get(0).getDate();
        int open = hourDatas.get(0).getOpen();
        int close = hourDatas.get(0).getClose();
        int low = hourDatas.get(0).getLow();
        int hight = hourDatas.get(0).getHigh();
        for (HourData hourData:hourDatas){
            if (hourData.getDate().contains(dateStr.substring(0,10))){
                if (hourData.getLow() < low){
                    low = hourData.getLow();
                }
                if (hourData.getHigh() > hight){
                    hight = hourData.getHigh();
                }
                open = hourData.getOpen();
            }else {
                break;
            }
        }
        items.add(initItme(dateStr, open, close, low, hight));

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
        adapter = new CrossRSIAdapter(items, this);
        crossRsiListView.setAdapter(adapter);
        crossRsiListView.setSelection(crossRsiListView.getCount());
        analyseResult();
        printResult();
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

    private void printResult(){
        crossRSITxt.setText(
                "Total win: " + totalWin +
                        "\n" + "Win Number: " + winNumb +
                        "\n" + "loss Number: " + lossNumb +
                        "\n" + "Cut loss: " + cutlose);
        Log.i(TAG, "printResult: " + totalTrade + " " + lossbuy + " " + losssell);
    }
}
