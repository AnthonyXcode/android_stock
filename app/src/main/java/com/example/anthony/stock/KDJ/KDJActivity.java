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
    EditText KDJTargetEditTxt;
    EditText shortMADyasEditTxt;
    EditText longMAdaysEditTxt;
    Button KDJOKBtn;
    ListView KDJListview;
    TextView KDJResultTxt;
    private int KDJValidDays;
    private int KDJDays;
    private int shortMADays;
    private int longMADays;
    private int cutLossValue;
    private int target;
    private KDJAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kdj);
        setupLayout();
        setupClick();
        initTools();
        initStrategy();
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
        KDJTargetEditTxt = (EditText)findViewById(R.id.KDJTargetEditTxt);
        KDJResultTxt = (TextView) findViewById(R.id.KDJResultTxt);
        longMAdaysEditTxt = (EditText) findViewById(R.id.longMAdaysEditTxt);
        shortMADyasEditTxt = (EditText) findViewById(R.id.shortMADyasEditTxt);
    }

    private void setupClick(){
        KDJOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupStrategy();
                initDataArray();
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
        shortMADays = Integer.parseInt(shortMADyasEditTxt.getText().toString());
        longMADays = Integer.parseInt(longMAdaysEditTxt.getText().toString());
    }

    private void initStrategy() {
        KDJValidDays = 4;
        cutLossValue = 100;
        target = 200;
        KDJDays = 9;
        shortMADays = 5;
        longMADays = 20;
        KDJValidDaysEditTxt.setText(String.valueOf(KDJValidDays));
        KDJCutlossEditTxt.setText(String.valueOf(cutLossValue));
        KDJTargetEditTxt.setText(String.valueOf(target));
        KDJDaysEditTxt.setText(String.valueOf(KDJDays));
        longMAdaysEditTxt.setText(String.valueOf(longMADays));
        shortMADyasEditTxt.setText(String.valueOf(shortMADays));
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
            items.add(kdjItem);

            if (items.size() >= longMADays){
                kdjItem.setShortMA(countMA(items, shortMADays));
                kdjItem.setLongMA(countMA(items, longMADays));
            }
        }
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
    int lostNumb = 0;
    int tradeNumb = 0;
    private void analysisData(){
        totalWin = 0;
        cutLostNumb = 0;
        winNumb = 0;
        meetTargetNumb = 0;
        lostNumb = 0;
        tradeNumb = 0;

        for(int i = 1; i < items.size(); i++){
            KDJItem item = items.get(i);
            if (item.isStockOnHand()) continue;
            if (Math.abs(item.getShortMA() - item.getLongMA()) > item.getClose() * 0.0075 && Math.abs(item.getLow() - item.getHigh()) < 800){
                if (item.getShortMA() > item.getLongMA()){
                    if (item.getClose() - item.getShortMA() > item.getClose() * 0.0075) {
                        tradeNumb += 1;
                        item.setBuyPrice(item.getClose());
                        movingAnalysisForBuy(i, item);
                    }
                }else {
                    if (item.getShortMA() - item.getClose() > item.getClose() * 0.0075){
                        tradeNumb += 1;
                        item.setSellPrice(item.getClose());
                        movingAnalysisForSell(i, item);
                    }
                }
            }
        }

//        for (int i = 1; i < items.size(); i++) {
//            KDJItem item = items.get(i);
//            KDJItem previousItem = items.get(i - 1);
//
//            if (item.getValueJ() > 85){
//                if (Math.abs(item.getValueJ() - item.getValueD()) < 5 && Math.abs(item.getValueJ() - item.getValueK()) < 5){
//                    item.setSellPrice(item.getClose());
//                    analysisForSellToJ(i, item);
//                    tradeNumb += 1;
//                    Log.i(TAG, "analysisData: " + item.getDate());
//                    Log.i(TAG, "analysisData trade: " + tradeNumb);
//                }
//            }else if (item.getValueJ() < 15){
//                if (Math.abs(item.getValueJ() - item.getValueD()) < 5 && Math.abs(item.getValueJ() - item.getValueK()) < 5){
//                    item.setBuyPrice(item.getClose());
//                    analysisForBuyToJ(i, item);
//                    tradeNumb += 1;
//                    Log.i(TAG, "analysisData: " + item.getDate());
//                    Log.i(TAG, "analysisData trade: " + tradeNumb);
//                }
//            }
//        }
    }

    private void movingAnalysisForBuy(int position, KDJItem buyItem){
        for (int i = position + 1; i < position + KDJValidDays; i++){
            KDJItem sellItem;
            try{
                sellItem = items.get(i);
            }catch (Exception ex){
                return;
            }
            if (Math.abs(buyItem.getClose() - sellItem.getClose()) > 300)
                Log.i(TAG, "movingAnalysisForBuy: " + buyItem.getStrDate());
            sellItem.setStockOnHand(true);
            if (buyItem.getClose() - sellItem.getClose() > cutLossValue){
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                break;
            } else if (sellItem.getClose() - buyItem.getClose() > target){
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                meetTargetNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                break;
            }else if (i == position + KDJValidDays - 1){
                if (sellItem.getClose() > buyItem.getClose()){
                    winNumb += 1;
                }else {
                    lostNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                sellItem.setSellPrice(sellItem.getClose());
                sellItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
        }
    }

    private void movingAnalysisForSell(int position, KDJItem sellItem){
        for (int i = position + 1; i < position + KDJValidDays; i++){
            KDJItem buyItem;
            try{
                buyItem= items.get(i);
            }catch (Exception ex){
                return;
            }
            if (Math.abs(buyItem.getClose() - sellItem.getClose()) > 300)
                Log.i(TAG, "movingAnalysisForBuy: " + sellItem.getStrDate());
            buyItem.setStockOnHand(true);

            if (buyItem.getClose() - sellItem.getClose() > cutLossValue){
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                cutLostNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                break;
            }else if (sellItem.getClose() - buyItem.getClose() > target){
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                meetTargetNumb += 1;
                totalWin += sellItem.getClose() - buyItem.getClose();
                break;
            }else if (i == position + KDJValidDays - 1){
                if (sellItem.getClose() > buyItem.getClose()){
                    winNumb += 1;
                }else {
                    lostNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
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
                    lostNumb +=1;
                    Log.i(TAG, "analysisData loss: " + lostNumb);
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
                    lostNumb += 1;
                    Log.i(TAG, "analysisData loss: " + lostNumb);
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
                    lostNumb += 1;
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
                    lostNumb += 1;
                }
                totalWin += sellItem.getClose() - buyItem.getClose();
                buyItem.setBuyPrice(buyItem.getClose());
                buyItem.setWinOrLossValue(sellItem.getClose() - buyItem.getClose());
                break;
            }
        }
    }

    private int countMA(ArrayList<KDJItem> items, int days) {
        int sum = 0;
        for (int i = 0; i < days; i++) {
            KDJItem item = items.get(items.size() - 1 - i);
            sum += item.getClose();
        }
        int movingAverage = sum / days;
        return movingAverage;
    }

    private void printResult() {
        KDJResultTxt.setText("Total win: " + totalWin +
                "\n" + "Cut Loss Number: " + cutLostNumb +
                "\n" + "Meet target Number: " + meetTargetNumb +
                "\n" + "Loss Number: " + lostNumb +
                "\n" + "Win Number: " + winNumb +
                "\n" + "Trade Number: " + tradeNumb);
    }

}
