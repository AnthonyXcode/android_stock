package com.example.anthony.stock.Moving;

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
import java.util.StringTokenizer;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class MovingActivity extends AppCompatActivity {
    ListView movingListView;
    TextView movingResultTxt;
    EditText longMaEditTxt;
    EditText shortMaEditTxt;
    EditText movingValidDaysEditTxt;
    EditText cutlossEditTxt;
    EditText movingTargetEditTxt;
    EditText movingNewCloseEditTxt;
    EditText movingDayHighEditTxt;
    EditText movingDayLowEditTxt;
    Button movingUpdateBtn;
    Button movingOKBtn;
    Realm realm;
    RealmResults<DateData> dateDatas;
    RealmResults<HourData> hourDatas;
    int longMoving;
    int shortMoving;
    int validDays;
    int cutlossvalue;
    int targetValue;
    ArrayList<MovingItem> movingItems;
    MovingAdapter adapter;
    private String TAG = MovingActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);
        initValue();
        setupLayout();
        setupClick();
        setupTools();
        setupDatas();
        addHourItem();
        countResult();
        setupListView();
        setupResult();
    }

    private void initValue(){
        longMoving = 27;
        shortMoving = 8;
        validDays = 6;
        cutlossvalue = 250;
        targetValue = 550;
    }

    private void setupLayout(){
        movingListView = (ListView)findViewById(R.id.movingListView);
        movingResultTxt = (TextView)findViewById(R.id.movingResultTxt);
        longMaEditTxt = (EditText) findViewById(R.id.longMaEditTxt);
        shortMaEditTxt = (EditText) findViewById(R.id.shortMaEditTxt);
        movingValidDaysEditTxt = (EditText) findViewById(R.id.movingValidDaysEditTxt);
        cutlossEditTxt = (EditText) findViewById(R.id.cutlossEditTxt);
        movingTargetEditTxt = (EditText) findViewById(R.id.movingTargetEditTxt);
        movingOKBtn = (Button) findViewById(R.id.movingOKBtn);
        movingNewCloseEditTxt = (EditText)findViewById(R.id.movingNewCloseEditTxt);
        movingDayHighEditTxt = (EditText) findViewById(R.id.movingDayHighEditTxt);
        movingDayLowEditTxt = (EditText)findViewById(R.id.movingDayLowEditTxt);
        movingUpdateBtn = (Button)findViewById(R.id.movingUpdateBtn);
        longMaEditTxt.setText(String.valueOf(longMoving));
        shortMaEditTxt.setText(String.valueOf(shortMoving));
        movingValidDaysEditTxt.setText(String.valueOf(validDays));
        cutlossEditTxt.setText(String.valueOf(cutlossvalue));
        movingTargetEditTxt.setText(String.valueOf(targetValue));
    }

    private void setupClick(){
        movingOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupValue();
                setupDatas();
                addHourItem();
                countResult();
                setupListView();
                setupResult();
            }
        });

        movingUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupValue();

                int close = Integer.parseInt(movingNewCloseEditTxt.getText().toString());
                int high = Integer.parseInt(movingDayHighEditTxt.getText().toString());
                int low = Integer.parseInt(movingDayLowEditTxt.getText().toString());

                MovingItem item = movingItems.get(movingItems.size() - 1);
                item.setClose(close);
                item.setHigh(high);
                item.setLow(low);
                item.setBuyPrice(0);
                item.setSellPrice(0);
                movingItems.set(movingItems.size() - 1, item);
                item.setLongMA(countMA(movingItems, longMoving));
                item.setStortMA(countMA(movingItems, shortMoving));

                countResult();
                setupListView();
                setupResult();

            }
        });
    }

    private void setupValue(){
        winNumb = 0;
        loseNumb = 0;
        totalWin = 0;
        longMoving = Integer.parseInt(longMaEditTxt.getText().toString());
        shortMoving = Integer.parseInt(shortMaEditTxt.getText().toString());
        validDays = Integer.parseInt(movingValidDaysEditTxt.getText().toString());
        cutlossvalue = Integer.parseInt(cutlossEditTxt.getText().toString());
        targetValue = Integer.parseInt(movingTargetEditTxt.getText().toString());
    }

    private void setupTools(){
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
        movingItems = new ArrayList<>();
    }

    private void setupDatas(){
        movingItems.clear();
        for (int i = 0; i < dateDatas.size(); i++) {
            DateData data = dateDatas.get(i);
            MovingItem item = initMovingItem(data.getDate(), data.getStrDate(), data.getClose(), data.getOpen(), data.getHigh(), data.getLow());
            if (movingItems.size() > longMoving){
                item.setLongMA(countMA(movingItems, longMoving));
                item.setStortMA(countMA(movingItems, shortMoving));
            }
            movingItems.add(item);
        }
    }

    private void addHourItem() {
        HourData lastData = hourDatas.get(0);
        int date = lastData.getTimestamp();
        String dateStr = lastData.getDate();
        int open = lastData.getOpen();
        int close = lastData.getClose();
        int high = lastData.getHigh();
        int low = lastData.getLow();
        for (HourData item : hourDatas) {
            if (item.getDate().contains(dateStr.substring(0, 10))){
                open = item.getOpen();
                if (high < item.getHigh()){
                    high = item.getHigh();
                }

                if (low > item.getLow()){
                    low = item.getLow();
                }
            }else {
                break;
            }
        }
        MovingItem hourItem = initMovingItem(date, dateStr, close, open, high, low);
        hourItem.setLongMA(countMA(movingItems, longMoving));
        hourItem.setStortMA(countMA(movingItems, shortMoving));
        movingItems.add(hourItem);
        movingNewCloseEditTxt.setText(String.valueOf(close));
        movingDayHighEditTxt.setText(String.valueOf(high));
        movingDayLowEditTxt.setText(String.valueOf(low));
    }

    private void countResult() {
        for (int i = 1; i < movingItems.size(); i++) {
            MovingItem previousItem = movingItems.get(i - 1);
            MovingItem item = movingItems.get(i);
            if (previousItem.getStortMA() > previousItem.getLongMA() && item.getStortMA() < item.getLongMA()){
                item.setSellPrice(item.getClose());
                try {
                    analyseForSell(i, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if (previousItem.getStortMA() < previousItem.getLongMA() && item.getStortMA() > item.getLongMA()){
                item.setBuyPrice(item.getClose());
                try {
                    analyseForBuy(i, item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setupListView() {
        adapter = new MovingAdapter(movingItems, this);
        movingListView.setAdapter(adapter);
        movingListView.setSelection(movingItems.size());
    }

    private MovingItem initMovingItem(int date, String strDate, int close, int open, int high, int low){
        MovingItem item = new MovingItem();
        item.setDate(date);
        item.setStrDate(strDate);
        item.setClose(close);
        item.setOpen(open);
        item.setHigh(high);
        item.setLow(low);
        return item;
    }

    int winNumb = 0;
    int loseNumb = 0;
    int totalWin = 0;
    private void analyseForBuy(int position, MovingItem buyItem) throws Exception{
        for (int i = 1 ; i < validDays; i++){
            MovingItem sellItem = movingItems.get(position + i);
            if (sellItem.getLongMA() > sellItem.getStortMA()){
                sellItem.setSellPrice(sellItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()){
                    winNumb += 1;
                }else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (buyItem.getClose() - sellItem.getClose() > cutlossvalue){
                Log.i(TAG, "analyseForBuy: bug");
                sellItem.setSellPrice(sellItem.getClose());
                loseNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > targetValue){
                sellItem.setSellPrice(sellItem.getClose());
                winNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (i == validDays - 1){
                sellItem.setSellPrice(sellItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()){
                    winNumb += 1;
                }else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
            }
        }
    }

    private void analyseForSell(int position, MovingItem sellItem) throws Exception{
        for (int i = 1; i < validDays; i++) {
            MovingItem buyItem = movingItems.get(position + i);
            if (buyItem.getStortMA() > buyItem.getLongMA()){
                buyItem.setBuyPrice(buyItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()){
                    winNumb += 1;
                }else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (buyItem.getClose() - sellItem.getClose() > cutlossvalue){
                Log.i(TAG, "analyseForSell: sell");
                buyItem.setBuyPrice(buyItem.getClose());
                loseNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > targetValue){
                buyItem.setBuyPrice(buyItem.getClose());
                winNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (i == validDays - 1){
                buyItem.setBuyPrice(buyItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()){
                    winNumb += 1;
                }else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
            }
        }
    }

    private int countMA(ArrayList<MovingItem> items, int days){
        int sum = 0;
        for (int i = 0; i < days; i++) {
            MovingItem item = items.get(items.size() - 1 - i);
            sum += (item.getClose() + item.getOpen() + item.getLow() + item.getHigh()) / 4;
        }
        int movingAverage  = sum / days;
        return movingAverage;
    }

    private void setupResult(){
        movingResultTxt.setText("Win Number: "+ winNumb
        + "\n" + "loss Number: " + loseNumb
        + "\n" + "Total Win: " + totalWin);
    }
}
