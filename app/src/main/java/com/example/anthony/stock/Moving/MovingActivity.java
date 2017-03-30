package com.example.anthony.stock.Moving;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.R;
import com.example.anthony.stock.realmclasses.DateData;
import com.example.anthony.stock.realmclasses.HourData;

import java.util.ArrayList;

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
    Button movingOKBtn;
    Realm realm;
    RealmResults<DateData> dateDatas;
    RealmResults<HourData> hourDatas;
    int longMoving;
    int shortMoving;
    int validDays;
    int cutlossvalue;
    ArrayList<MovingItem> movingItems;
    MovingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moving);
        initValue();
        setupLayout();
        setupClick();
        setupTools();
        setupDatas();
        setupResult();
    }

    private void initValue(){
        longMoving = 23;
        shortMoving = 8;
        validDays = 18;
        cutlossvalue = 100;
    }

    private void setupLayout(){
        movingListView = (ListView)findViewById(R.id.movingListView);
        movingResultTxt = (TextView)findViewById(R.id.movingResultTxt);
        longMaEditTxt = (EditText) findViewById(R.id.longMaEditTxt);
        shortMaEditTxt = (EditText) findViewById(R.id.shortMaEditTxt);
        movingValidDaysEditTxt = (EditText) findViewById(R.id.movingValidDaysEditTxt);
        cutlossEditTxt = (EditText) findViewById(R.id.cutlossEditTxt);
        movingOKBtn = (Button) findViewById(R.id.movingOKBtn);
        longMaEditTxt.setText(String.valueOf(longMoving));
        shortMaEditTxt.setText(String.valueOf(shortMoving));
        movingValidDaysEditTxt.setText(String.valueOf(validDays));
        cutlossEditTxt.setText(String.valueOf(cutlossvalue));
    }

    private void setupClick(){
        movingOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupValue();
                setupDatas();
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
            MovingItem item = initMovingItem(dateDatas.get(i));
            if (movingItems.size() > longMoving){
                item.setLongMA(countMA(movingItems, longMoving));
                item.setStortMA(countMA(movingItems, shortMoving));
            }
            movingItems.add(item);
        }

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
        adapter = new MovingAdapter(movingItems, this);
        movingListView.setAdapter(adapter);
        movingListView.setSelection(movingItems.size());
    }

    private MovingItem initMovingItem(DateData dateData){
        MovingItem item = new MovingItem();
        item.setDate(dateData.getDate());
        item.setStrDate(dateData.getStrDate());
        item.setClose(dateData.getClose());
        item.setOpen(dateData.getOpen());
        item.setHigh(dateData.getHigh());
        item.setLow(dateData.getLow());
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
                totalWin += buyItem.getClose() - sellItem.getClose();
                break;
            }
            if (i == validDays - 1){
                buyItem.setBuyPrice(buyItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()){
                    winNumb += 1;
                }else {
                    loseNumb += 1;
                }
                totalWin += buyItem.getClose() - sellItem.getClose();
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
