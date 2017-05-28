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

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MovingActivity extends AppCompatActivity {
    ListView movingListView;
    TextView movingResultTxt;
    EditText longMaEditTxt;
    EditText shortMaEditTxt;
    EditText movingValidDaysEditTxt;
    EditText cutlossEditTxt;
    EditText movingTargetEditTxt;
    EditText meddleMaEditTxt;
    Button movingOKBtn;
    Realm realm;
    RealmResults<DateData> dateDatas;
    int longMoving;
    int meddleMoving;
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
        setupData();
        countResult();
        setupListView();
        setupResult();
    }

    private void initValue() {
        longMoving = 20;
        meddleMoving = 10;
        shortMoving = 5;
        validDays = 6;
        cutlossvalue = 250;
        targetValue = 550;
    }

    private void setupLayout() {
        movingListView = (ListView) findViewById(R.id.movingListView);
        movingResultTxt = (TextView) findViewById(R.id.movingResultTxt);
        longMaEditTxt = (EditText) findViewById(R.id.longMaEditTxt);
        shortMaEditTxt = (EditText) findViewById(R.id.shortMaEditTxt);
        movingValidDaysEditTxt = (EditText) findViewById(R.id.movingValidDaysEditTxt);
        cutlossEditTxt = (EditText) findViewById(R.id.cutlossEditTxt);
        movingTargetEditTxt = (EditText) findViewById(R.id.movingTargetEditTxt);
        meddleMaEditTxt = (EditText) findViewById(R.id.meddleMaEditTxt);
        movingOKBtn = (Button) findViewById(R.id.movingOKBtn);
        longMaEditTxt.setText(String.valueOf(longMoving));
        meddleMaEditTxt.setText(String.valueOf(meddleMoving));
        shortMaEditTxt.setText(String.valueOf(shortMoving));
        movingValidDaysEditTxt.setText(String.valueOf(validDays));
        cutlossEditTxt.setText(String.valueOf(cutlossvalue));
        movingTargetEditTxt.setText(String.valueOf(targetValue));
    }

    private void setupClick() {
        movingOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupValue();
                setupData();
                countResult();
                setupListView();
                setupResult();
            }
        });
    }

    private void setupValue() {
        winNumb = 0;
        loseNumb = 0;
        totalWin = 0;
        tradeCount = 0;
        totalLost = 0;
        sum = 0;
        longMoving = Integer.parseInt(longMaEditTxt.getText().toString());
        shortMoving = Integer.parseInt(shortMaEditTxt.getText().toString());
        validDays = Integer.parseInt(movingValidDaysEditTxt.getText().toString());
        cutlossvalue = Integer.parseInt(cutlossEditTxt.getText().toString());
        targetValue = Integer.parseInt(movingTargetEditTxt.getText().toString());
    }

    private void setupTools() {
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
        movingItems = new ArrayList<>();
    }

    private void setupData() {
        movingItems.clear();
        for (int i = 0; i < dateDatas.size(); i++) {
            DateData data = dateDatas.get(i);
            MovingItem item = initMovingItem(data.getDate(), data.getStrDate(), data.getClose(), data.getOpen(), data.getHigh(), data.getLow());
            movingItems.add(item);
            if (movingItems.size() > longMoving) {
                item.setLongMA(countMA(movingItems, longMoving));
                item.setMeddleMA(countMA(movingItems, meddleMoving));
                item.setShortMA(countMA(movingItems, shortMoving));
            }
        }
    }

    boolean stockOnHand;

    private void countResult() {
        for (int i = 1; i < movingItems.size(); i++) {
            MovingItem previousItem = movingItems.get(i - 1);
            MovingItem item = movingItems.get(i);

            if (Math.abs(item.getShortMA() - item.getLongMA()) > 150) {
                if (item.getMeddleMA() < item.getShortMA() && item.getMeddleMA() > item.getLongMA()
                        && previousItem.getOpen() < previousItem.getShortMA()
                        && item.getOpen() > item.getShortMA()) {
                    tradeCount += 1;
                    item.setBuyPrice(item.getOpen());
                    try {
                        analyseForBuy3(i, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (item.getMeddleMA() > item.getShortMA() && item.getMeddleMA() < item.getLongMA()
                        && previousItem.getOpen() > previousItem.getShortMA()
                        && item.getOpen() < item.getShortMA()) {
                    tradeCount += 1;
                    item.setSellPrice(item.getOpen());
                    try {
                        analyseForSell3(i, item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

//            if ((previousItem.getMeddleMA() > previousItem.getShortMA() && previousItem.getMeddleMA() > previousItem.getLongMA()) ||
//                    (previousItem.getMeddleMA() < previousItem.getShortMA() && previousItem.getMeddleMA() < previousItem.getLongMA())) {
//                if ((item.getMeddleMA() > item.getShortMA() && item.getMeddleMA() < item.getLongMA()) ||
//                        (item.getMeddleMA() < item.getShortMA() && item.getMeddleMA() > item.getLongMA())) {
//                    tradeCount += 1;
//                    if (item.getOpen() > previousItem.getShortMA() && Math.abs(item.getShortMA() - item.getLongMA()) > 150) {
//                        item.setBuyPrice(item.getOpen());
//                        try {
//                            analyseForBuy2(i, item);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else if (item.getOpen() < previousItem.getShortMA() && Math.abs(item.getShortMA() - item.getLongMA()) > 150) {
//                        item.setSellPrice(item.getOpen());
//                        try {
//                            analyseForSell2(i, item);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        tradeCount -= 1;
//                    }
//                }
//            }


//            if (previousItem.getShortMA() > previousItem.getLongMA() && item.getShortMA() + 50 < item.getLongMA()) {
//                item.setSellPrice(item.getClose());
//                try {
//                    analyseForSell(i, item);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (previousItem.getShortMA() < previousItem.getLongMA() && item.getShortMA() - 40 > item.getLongMA()) {
//                item.setBuyPrice(item.getClose());
//                try {
//                    analyseForBuy(i, item);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private void setupListView() {
        adapter = new MovingAdapter(movingItems, this);
        movingListView.setAdapter(adapter);
        movingListView.setSelection(movingItems.size());
    }

    private MovingItem initMovingItem(int date, String strDate, int close, int open, int high, int low) {
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
    int totalLost = 0;
    int sum = 0;
    int tradeCount = 0;

    private void analyseForBuy3 (int position, MovingItem buyItem) throws Exception {
        for (int i = 1; i < validDays; i++){
            MovingItem sellItem = movingItems.get(position + i);
            if (sellItem.getClose() < sellItem.getShortMA()){
                sum += sellItem.getClose() - buyItem.getOpen();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getOpen());
                if (sellItem.getClose() > buyItem.getOpen()){
                    winNumb += 1;
                    totalWin += sellItem.getClose() - buyItem.getOpen();
                }else {
                    loseNumb += 1;
                    totalLost += buyItem.getOpen() - sellItem.getClose();
                }
                break;
            } else if (i == validDays - 1){
                sum += sellItem.getClose() - buyItem.getOpen();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getOpen());
                if (sellItem.getClose() > buyItem.getOpen()){
                    winNumb += 1;
                    totalWin += sellItem.getClose() - buyItem.getOpen();
                }else {
                    loseNumb += 1;
                    totalLost += buyItem.getOpen() - sellItem.getClose();
                }
                break;
            }
        }
    }

    private void analyseForSell3(int position, MovingItem sellItem) throws Exception {
        for (int i = 1; i < validDays; i++){
            MovingItem buyItem = movingItems.get(position + i);
            if(buyItem.getClose() > buyItem.getShortMA()){
                sum += sellItem.getOpen() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLoss(sellItem.getOpen() - buyItem.getClose());
                if (buyItem.getClose() < sellItem.getOpen()){
                    winNumb += 1;
                    totalWin += sellItem.getOpen() - buyItem.getClose();
                }else {
                    loseNumb += 1;
                    totalLost += buyItem.getClose() - sellItem.getOpen();
                }
                break;
            } else if (i == validDays - 1){
                sum += sellItem.getOpen() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLoss(sellItem.getOpen() - buyItem.getClose());
                if (buyItem.getClose() < sellItem.getOpen()){
                    winNumb += 1;
                    totalWin += sellItem.getOpen() - buyItem.getClose();
                }else {
                    loseNumb += 1;
                    totalLost += buyItem.getClose() - sellItem.getOpen();
                }
                break;
            }
        }
    }

    private void analyseForBuy2(int position, MovingItem buyItem) throws Exception {
        for (int i = 1; i < movingItems.size(); i++) {
            MovingItem sellItem = movingItems.get(position + i);
            MovingItem previousItem = movingItems.get(position + i -1);
            if (sellItem.getMeddleMA() > sellItem.getShortMA()){
                totalWin += sellItem.getClose() - buyItem.getOpen();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getOpen());
                break;
            } else if (sellItem.getShortMA() < previousItem.getShortMA()){
                totalWin += sellItem.getClose() - buyItem.getOpen();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getOpen());
                break;
            }

//            if (sellItem.getClose() - buyItem.getOpen() > targetValue){
//                totalWin += sellItem.getClose() - buyItem.getOpen();
//                sellItem.setSellPrice(sellItem.getClose());
//                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getOpen());
//                break;
//            } else if (buyItem.getOpen() - sellItem.getClose() > cutlossvalue){
//                totalWin += sellItem.getClose() - buyItem.getOpen();
//                sellItem.setSellPrice(sellItem.getClose());
//                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getOpen());
//                break;
//            }
        }
    }

    private void analyseForSell2(int position, MovingItem sellItem) throws Exception {
        for (int i = 1; i < movingItems.size(); i++) {
            MovingItem buyItem = movingItems.get(position + i);
            MovingItem previousItem = movingItems.get(position + i -1);

            if (buyItem.getMeddleMA() < buyItem.getShortMA()){
                totalWin += sellItem.getOpen() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLoss(sellItem.getOpen() - buyItem.getClose());
                break;
            } else if (buyItem.getShortMA() > previousItem.getShortMA()){
                totalWin += sellItem.getOpen() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLoss(sellItem.getOpen() - buyItem.getClose());
                break;
            }

//            if (sellItem.getOpen() - buyItem.getClose() > targetValue){
//                totalWin += sellItem.getOpen() - buyItem.getClose();
//                buyItem.setBuyPrice(buyItem.getClose());
//                buyItem.setWinOrLoss(sellItem.getOpen() - buyItem.getClose());
//                break;
//            } else if (buyItem.getClose() - sellItem.getOpen() > cutlossvalue){
//                totalWin += sellItem.getOpen() - buyItem.getClose();
//                buyItem.setBuyPrice(buyItem.getClose());
//                buyItem.setWinOrLoss(sellItem.getOpen() - buyItem.getClose());
//                break;
//            }
        }
    }

    private void analyseForBuy(int position, MovingItem buyItem) throws Exception {
        for (int i = 1; i < validDays; i++) {
            MovingItem sellItem = movingItems.get(position + i);
            if (sellItem.getLongMA() > sellItem.getShortMA()) {
                sellItem.setSellPrice(sellItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()) {
                    winNumb += 1;
                } else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (buyItem.getClose() - sellItem.getClose() > cutlossvalue) {
                Log.i(TAG, "analyseForBuy: bug");
                sellItem.setSellPrice(sellItem.getClose());
                loseNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > targetValue) {
                sellItem.setSellPrice(sellItem.getClose());
                winNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (i == validDays - 1) {
                sellItem.setSellPrice(sellItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()) {
                    winNumb += 1;
                } else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
            }
        }
    }

    private void analyseForSell(int position, MovingItem sellItem) throws Exception {
        for (int i = 1; i < validDays; i++) {
            MovingItem buyItem = movingItems.get(position + i);
            if (buyItem.getShortMA() > buyItem.getLongMA()) {
                buyItem.setBuyPrice(buyItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()) {
                    winNumb += 1;
                } else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (buyItem.getClose() - sellItem.getClose() > cutlossvalue) {
                Log.i(TAG, "analyseForSell: sell");
                buyItem.setBuyPrice(buyItem.getClose());
                loseNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > targetValue) {
                buyItem.setBuyPrice(buyItem.getClose());
                winNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (i == validDays - 1) {
                buyItem.setBuyPrice(buyItem.getClose());
                if (buyItem.getClose() < sellItem.getClose()) {
                    winNumb += 1;
                } else {
                    loseNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setWinOrLoss(sellItem.getClose() - buyItem.getClose());
            }
        }
    }

    private int countMA(ArrayList<MovingItem> items, int days) {
        int sum = 0;
        for (int i = 0; i < days; i++) {
            MovingItem item = items.get(items.size() - 1 - i);
            sum += item.getClose();
//            sum += (item.getClose() + item.getOpen() + item.getLow() + item.getHigh()) / 4;
        }
        int movingAverage = sum / days;
        return movingAverage;
    }

    private void setupResult() {
        movingResultTxt.setText("Trade Count: " + tradeCount
                + "\n" + "Win Number: " + winNumb
                + "\n" + "Total Win: " + totalWin
                + "\n" + "loss Number: " + loseNumb
                + "\n" + "Total Lost: " + totalLost
                + "\n" + "Sum: " + sum);
    }
}
