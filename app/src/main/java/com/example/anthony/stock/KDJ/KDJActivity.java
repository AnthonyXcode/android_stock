package com.example.anthony.stock.KDJ;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class KDJActivity extends AppCompatActivity {


    ArrayList<KDJItem> items;
    Realm realm;
    RealmResults<DateData> dateDatas;
    RealmResults<HourData> hourDatas;
    private String TAG = KDJActivity.class.getName();
    EditText KDJDaysEditTxt;
    EditText KDJValidDaysEditTxt;
    EditText KDJCutlossEditTxt;
    EditText KDJDayHighEditTxt;
    EditText KDJLowEditTxt;
    EditText KDJCloseEditTxt;
    EditText KDJTargetEditTxt;
    Button KDJUpdateBtn;
    Button KDJOKBtn;
    ListView KDJListview;
    TextView KDJResultTxt;
    private int KDJValidDays;
    private int KDJDays;
    private int cutLossValue;
    private int persentValue;
    private int dayHigh;
    private int dayLow;
    private int target;
    private KDJAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kdj);
        setupLayout();
        setupClick();
        initTools();
        initStratege();
        initDataArray();
//        addHourData();
        analysisData();
        initListView();
        printResult();
    }

    private void setupLayout(){
        KDJDaysEditTxt = (EditText) findViewById(R.id.KDJDaysEditTxt);
        KDJValidDaysEditTxt = (EditText) findViewById(R.id.KDJValidDaysEditTxt);
        KDJCutlossEditTxt = (EditText) findViewById(R.id.KDJCutlossEditTxt);
        KDJOKBtn = (Button)findViewById(R.id.KDJOKBtn);
        KDJListview = (ListView)findViewById(R.id.KDJListview);
        KDJDayHighEditTxt = (EditText)findViewById(R.id.KDJDayHighEditTxt);
        KDJLowEditTxt = (EditText)findViewById(R.id.KDJLowEditTxt);
        KDJCloseEditTxt = (EditText)findViewById(R.id.KDJCloseEditTxt);
        KDJUpdateBtn = (Button)findViewById(R.id.KDJUpdateBtn);
        KDJTargetEditTxt = (EditText)findViewById(R.id.KDJTargetEditTxt);
        KDJResultTxt = (TextView) findViewById(R.id.KDJResultTxt);
    }

    private void setupClick(){
        KDJOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupStrategy();
                initDataArray();
//                addHourData();
                analysisData();
                initListView();
                printResult();
            }
        });

        KDJUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupStrategy();
                initDataArray();
//                addHourData();
                updateData();
                analysisData();
                initListView();
                printResult();
            }
        });
    }

    private void initTools() {
        items = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
    }

    private void setupStrategy(){
        KDJDays = Integer.parseInt(KDJDaysEditTxt.getText().toString());
        KDJValidDays = Integer.parseInt(KDJValidDaysEditTxt.getText().toString());
        cutLossValue = Integer.parseInt(KDJCutlossEditTxt.getText().toString());
        target = Integer.parseInt(KDJTargetEditTxt.getText().toString());
    }

    private void initStratege() {
        KDJValidDays = 5;
        cutLossValue = 10000;
        target = 500;
        KDJDays = 9;
        KDJValidDaysEditTxt.setText(String.valueOf(KDJValidDays));
        KDJCutlossEditTxt.setText(String.valueOf(cutLossValue));
        KDJTargetEditTxt.setText(String.valueOf(target));
        KDJDaysEditTxt.setText(String.valueOf(KDJDays));
    }

    private void initDataArray() {
        items.clear();
        for (DateData data : dateDatas) {
            KDJItem kdjItem = initKDJItem(data.getDate(), data.getClose(), data.getHigh(), data.getLow(), data.getOpen(), data.getStrDate());
            if (items.size() < KDJDays) {
                kdjItem.setValueK(50.0);
                kdjItem.setValueD(50.0);
                kdjItem.setValueJ(50.0);
            } else {
                kdjItem = setKDJ(data.getHigh(), data.getLow(), data.getClose(), kdjItem);
            }
            Log.i(TAG, "initDataArray: " + kdjItem.getValueK());
            items.add(kdjItem);
        }
    }

    private void updateData(){
        KDJItem lastItem = items.get(items.size() - 1);
        int close = Integer.parseInt(KDJCloseEditTxt.getText().toString());
        int high = Integer.parseInt(KDJDayHighEditTxt.getText().toString());
        int low = Integer.parseInt(KDJLowEditTxt.getText().toString());
        KDJItem item = initKDJItem(lastItem.getDate(), close, high, low, lastItem.getOpen(), lastItem.getStrDate());
        item = setKDJ(high, low, close, item);
        items.set(items.size() - 1, item);
    }

    private void addHourData() {
        HourData lastItem = hourDatas.get(0);
        int dateStamp = lastItem.getTimestamp();
        int close = lastItem.getClose();
        int h = lastItem.getHigh();
        int l = lastItem.getLow();
        int open = lastItem.getOpen();
        String date = lastItem.getDate();
        for (HourData hourData : hourDatas) {
            if (hourData.getDate().contains(date.substring(0, 11))) {
                if (hourData.getHigh() > h) {
                    h = hourData.getHigh();
                }
                if (hourData.getLow() < l) {
                    l = hourData.getLow();
                }
                open = hourData.getOpen();
            } else {
                break;
            }
        }
        KDJLowEditTxt.setText(String.valueOf(l));
        KDJDayHighEditTxt.setText(String.valueOf(h));
        KDJCloseEditTxt.setText(String.valueOf(close));
        KDJItem item = initKDJItem(dateStamp, close, h, l, open, date);
        item = setKDJ(h, l, close, item);
        items.add(item);
    }

    private void initListView() {
        adapter = new KDJAdapter(items, this);
        KDJListview.setAdapter(adapter);
        KDJListview.setSelection(KDJListview.getCount());
    }

    private KDJItem setKDJ(int h, int l, int c, KDJItem preparedItem) {
        for (int i = 0; i < KDJDays; i++) {
            KDJItem item = items.get(items.size() - KDJDays + i);
            if (item.getHigh() > h) {
                h = item.getHigh();
            }

            if (item.getLow() < l) {
                l = item.getLow();
            }
        }
        KDJItem previousItem = items.get(items.size() - 1);
        double rsv = ((double) (c - l) / (double) (h - l)) * 100;
        double k = (2.0 / 3.0) * previousItem.getValueK() + (1.0 / 3.0) * rsv;
        double d = (2.0 / 3.0) * previousItem.getValueD() + (1.0 / 3.0) * k;
        double j = 3.0 * k - 2.0 * d;
        preparedItem.setValueK(k);
        preparedItem.setValueD(d);
        preparedItem.setValueJ(j);
        return preparedItem;
    }

    private KDJItem initKDJItem(int date, int close, int high, int low, int open, String strDate) {
        KDJItem kdjItem = new KDJItem();
        kdjItem.setDate(date);
        kdjItem.setClose(close);
        kdjItem.setHigh(high);
        kdjItem.setLow(low);
        kdjItem.setOpen(open);
        kdjItem.setStrDate(strDate);
        return kdjItem;
    }

    int totalWin = 0;
    int cutLostNumb = 0;
    int winNumb = 0;
    int meetTargetNumb = 0;
    int lossNumb = 0;
    int tradeNumb = 0;
    private void analysisData(){
        totalWin = 0;
        cutLostNumb = 0;
        winNumb = 0;
        meetTargetNumb = 0;
        lossNumb = 0;
        tradeNumb = 0;
        for (int i = 1; i < items.size(); i++) {
            KDJItem item = items.get(i);
            KDJItem previousItem = items.get(i - 1);
//            if ((item.getValueK() > 60 || item.getValueK() < 40)
//                    && (item.getValueD() > 60 || item.getValueD() < 40)){
//                if (previousItem.getValueK() < previousItem.getValueD() && item.getValueK() > item.getValueD()){
//                    item.setBuyPrice(item.getClose());
//                    analysisForBuy(i, item);
//                }else if (previousItem.getValueK() > previousItem.getValueD() && item.getValueK() < item.getValueD()){
//                    item.setSellPrice(item.getClose());
//                    analysisForSell(i, item);
//                }
//            }

            if (item.getValueJ() > 85){
                if (Math.abs(item.getValueJ() - item.getValueD()) < 5 && Math.abs(item.getValueJ() - item.getValueK()) < 5){
                    item.setSellPrice(item.getClose());
                    analysisForSellToJ(i, item);
                    tradeNumb += 1;
                    Log.i(TAG, "analysisData: " + item.getDate());
                    Log.i(TAG, "analysisData trade: " + tradeNumb);
                }
            }else if (item.getValueJ() < 15){
                if (Math.abs(item.getValueJ() - item.getValueD()) < 5 && Math.abs(item.getValueJ() - item.getValueK()) < 5){
                    item.setBuyPrice(item.getClose());
                    analysisForBuyToJ(i, item);
                    tradeNumb += 1;
                    Log.i(TAG, "analysisData: " + item.getDate());
                    Log.i(TAG, "analysisData trade: " + tradeNumb);
                }
            }

//            if (previousItem.getValueJ() > 100 && item.getValueJ() < 100 &&
//                    item.getValueD() < item.getValueJ() &&
//                    item.getValueK() < item.getValueJ()) {
//                item.setSellPrice(item.getClose());
//                analysisForSellToJ(i, item);
//            } else if (previousItem.getValueJ() < 0 && item.getValueJ() > 0 &&
//                    item.getValueD() > item.getValueJ() &&
//                    item.getValueK() > item.getValueJ()) {
//                item.setBuyPrice(item.getClose());
//                analysisForBuyToJ(i, item);
//            }
        }
    }

    private void analysisForBuyToJ(int position, KDJItem buyItem){
        for (int i = 1; i < KDJValidDays; i++) {
            KDJItem sellItem;
            try{
                sellItem = items.get(position+i);
            }catch (Exception ex){
                ex.printStackTrace();
                break;
            }

            if (sellItem.getValueJ() < 0 || buyItem.getClose() - sellItem.getClose() > cutLossValue){
                Log.i(TAG, "analysisForBuyToJ: date " + sellItem.getDate());
                Log.i(TAG, "analysisForBuyToJ: j " + sellItem.getValueJ());
                Log.i(TAG, "analysisForBuyToJ: buy close " + buyItem.getClose());
                Log.i(TAG, "analysisForBuyToJ: sell close " + sellItem.getClose());
                Log.i(TAG, "analysisForBuyToJ: cutloss " + cutLossValue);
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                Log.i(TAG, "analysisData cut: " + cutLostNumb);
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > target){
                meetTargetNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                Log.i(TAG, "analysisData meet: " + meetTargetNumb);
                break;
            }

            if (i == KDJValidDays - 1){
                if (sellItem.getClose() > buyItem.getClose()){
                    winNumb += 1;
                    Log.i(TAG, "analysisData win: " + winNumb);
                } else {
                    lossNumb +=1;
                    Log.i(TAG, "analysisData loss: " + lossNumb);
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
        }
    }

    private void analysisForSellToJ(int position, KDJItem sellItem){
        for (int i = 1; i < KDJValidDays; i++) {
            KDJItem buyItem;
            try{
                buyItem = items.get(position + i);
            }catch (Exception ex){
                ex.printStackTrace();
                break;
            }

            if (buyItem.getValueJ() > 100 || buyItem.getClose() - sellItem.getClose() > cutLossValue){
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                Log.i(TAG, "analysisData cut: " + cutLostNumb);
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > target){
                meetTargetNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(buyItem.getWinOrLossValue() + sellItem.getClose() - buyItem.getClose());
                Log.i(TAG, "analysisData meet: " + meetTargetNumb);
                break;
            }

            if (i == KDJValidDays - 1){
                if (sellItem.getClose() > buyItem.getClose()){
                    winNumb += 1;
                    Log.i(TAG, "analysisData win: " + winNumb);
                } else {
                    lossNumb += 1;
                    Log.i(TAG, "analysisData loss: " + lossNumb);
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
        }
    }

    private void analysisForBuy(int position, KDJItem buyItem){
        for (int i = 1; i < KDJValidDays; i++) {
            KDJItem sellItem;
            try {
                sellItem = items.get(position + i);
            }catch (Exception ex){
                ex.printStackTrace();
                break;
            }
            if (sellItem.getValueK() < sellItem.getValueD()){
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (buyItem.getClose() - sellItem.getClose() > cutLossValue){
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > target){
                meetTargetNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (i == KDJValidDays - 1){
                if (sellItem.getClose() > buyItem.getClose()){
                    winNumb += 1;
                }else {
                    lossNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
        }
    }

    private void analysisForSell(int position, KDJItem sellItem){
        for (int i = 1; i < KDJValidDays; i++) {
            KDJItem buyItem;
            try {
                buyItem = items.get(position + i);
            }catch (Exception ex){
                ex.printStackTrace();
                break;
            }

            if (buyItem.getValueK() > buyItem.getValueD()){
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (buyItem.getClose() - sellItem.getClose() > cutLossValue){
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (sellItem.getClose() - buyItem.getClose() > target){
                meetTargetNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }

            if (i == KDJValidDays - 1){
                if (sellItem.getClose() > buyItem.getClose()){
                    winNumb += 1;
                }else {
                    lossNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
        }
    }

    private void printResult() {
        KDJResultTxt.setText("Total win: " + totalWin +
                "\n" + "Cut Loss Number: " + cutLostNumb +
                "\n" + "Meet target Number: " + meetTargetNumb +
                "\n" + "Loss Number: " + lossNumb +
                "\n" + "Win Number: " + winNumb +
                "\n" + "Trade Number: " + tradeNumb);
    }

}
