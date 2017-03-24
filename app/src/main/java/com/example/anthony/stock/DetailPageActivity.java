package com.example.anthony.stock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.realmclasses.DateData;
import com.example.anthony.stock.realmclasses.HourData;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailPageActivity extends BaseApplication {

    ListView inhourListView;
    ListView dateListView;
    private InhourListviewAdapter inhourListviewAdapter;
    private DateListviewAdapter dateListviewAdapter;
    private TextView numOfHour;
    private TextView numOfDate;
    private TextView highDifValueTxt;
    private TextView difValueDateTxt;
    private TextView sixtyPercentBetweenTxt;
    private TextView avargeTxt;
    private int numberOfDateForRef = 20;
    private TextView validDateTxt;
    private TextView validhighTxt;
    private TextView validLowTxt;
    private TextView validTrigerValueTxt;
    private TextView validDateTxt2;
    private TextView validhighTxt2;
    private TextView validLowTxt2;
    private TextView validTrigerValueTxt2;
    private TextView buyOrSellTxt;
    private TextView targetTxt;
    private TextView cutLostTxt;
    private TextView buyNumberTxt;
    private TextView sellNumberTxt;
    private TextView winNumberTxt;
    private TextView lostNumberTxt;
    private TextView forceSellNumberTxt;
    private TextView winValueTxt;
    private TextView lostValueTxt;
    private TextView forceSellValueTxt;


    Realm realm;
    private String TAG = "DetailPageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        setupLayout();
    }

    private void setupLayout(){
        buyNumberTxt = (TextView)findViewById(R.id.buyNumberTxt);
        sellNumberTxt = (TextView)findViewById(R.id.sellNumberTxt);
        winNumberTxt = (TextView)findViewById(R.id.winNumberTxt);
        lostNumberTxt = (TextView)findViewById(R.id.lostNumberTxt);
        forceSellNumberTxt = (TextView)findViewById(R.id.forceSellNumberTxt);
        winValueTxt = (TextView)findViewById(R.id.winValueTxt);
        lostValueTxt = (TextView)findViewById(R.id.lostValueTxt);
        forceSellValueTxt = (TextView)findViewById(R.id.forceSellValueTxt);


        realm = Realm.getDefaultInstance();
        inhourListView = (ListView)findViewById(R.id.inhourListView);
        RealmResults<HourData> hourDatas = realm.where(HourData.class).findAll();
        inhourListviewAdapter = new InhourListviewAdapter(this,hourDatas);
        inhourListView.setAdapter(inhourListviewAdapter);

        RealmResults<DateData> dateDatas = realm.where(DateData.class).findAll();
        dateListviewAdapter = new DateListviewAdapter(this, dateDatas);
        dateListView = (ListView)findViewById(R.id.dateListView);
        dateListView.setAdapter(dateListviewAdapter);
        dateListView.setSelection(dateDatas.size() - 1);

        numOfHour = (TextView)findViewById(R.id.numOfHour);
        numOfDate = (TextView)findViewById(R.id.numOfDate);

        numOfDate.setText(String.valueOf(hourDatas.size()));
        numOfHour.setText(String.valueOf(dateDatas.size()));

        highDifValueTxt = (TextView)findViewById(R.id.highDifValueTxt);
        difValueDateTxt = (TextView)findViewById(R.id.difValueDateTxt);
        sixtyPercentBetweenTxt = (TextView)findViewById(R.id.sixtyPercentBetweenTxt);
        avargeTxt = (TextView)findViewById(R.id.avargeTxt);
        int valueDif = 0;
        String date = "";
        ArrayList<Integer> dataArray = new ArrayList<>();
        int highSum = 0;
        int lowSum = 0;
        for (int i = 0; i < numberOfDateForRef; i++){
            if (valueDif < dateDatas.get(dateDatas.size() - 1 - i).getHigh()- dateDatas.get(dateDatas.size() - 1 - i).getLow()){
                valueDif = dateDatas.get(dateDatas.size() - 1 - i).getHigh()- dateDatas.get(dateDatas.size() - 1 - i).getLow();
                date = dateDatas.get(dateDatas.size() - 1 - i).getStrDate();
            }
            highSum = highSum +dateDatas.get(dateDatas.size() - 1 - i).getHigh();
            lowSum = lowSum + dateDatas.get(dateDatas.size() - 1 - i).getLow();
            dataArray.add(dateDatas.get(dateDatas.size() - 1 - i).getHigh()- dateDatas.get(dateDatas.size() - 1 - i).getLow());
        }

        avargeTxt.setText(String.valueOf(lowSum/numberOfDateForRef) + " - " + String.valueOf(highSum/numberOfDateForRef));

        ArrayList<Integer> finalData = new ArrayList<>();
        for (int i = 0 , p = dataArray.size(); i < p; i ++){
            if (i == 0){
                finalData.add(dataArray.get(i));
            }else {
                int addplace = finalData.size();
                for (int j = 0 , k = finalData.size(); j < k ; j++){
                    if (dataArray.get(i) > finalData.get(j)){
                        addplace = j;
                        break;
                    }
                }
                finalData.add(addplace, dataArray.get(i));
            }
        }




        Log.i(TAG, "setupLayout: " + finalData.toString());

        highDifValueTxt.setText(String.valueOf(valueDif));
        difValueDateTxt.setText(date);
        Log.i(TAG, "setupLayout: " + String.valueOf((int)(numberOfDateForRef*0.8)));
        Log.i(TAG, "setupLayout: " + String.valueOf((int)(numberOfDateForRef * 0.2)));
        sixtyPercentBetweenTxt.setText(String.valueOf(finalData.get((int) (numberOfDateForRef*0.8 - 1))) + " - " + String.valueOf(finalData.get((int) (numberOfDateForRef * 0.2))));

        setUpValidData(finalData, dateDatas, finalData.get((int)(numberOfDateForRef * 0.8 - 1)));
    }

    private void setUpValidData(ArrayList<Integer> data, RealmResults<DateData> dateDatas, int validLow){
        int validValue = 0;
        for (int i = (int)(numberOfDateForRef * 0.2); i < data.size() * 0.8; i++){
            Log.i(TAG, "setUpValidData: " + String.valueOf(data.get(i)));
            validValue = validValue + data.get(i);
        }
        validValue = validValue/(int)(data.size() * 0.6);

        int dataPosition1 = 0;
        int dataPosition2 = 0;
        findPositionLoop : for (int i = 0 ; i < dateDatas.size(); i++){
            Log.i(TAG, "setUpValidData: " + dateDatas.get(dateDatas.size() - 1 - i).getDate());
            if (dataPosition1 != 0 && dateDatas.get(dateDatas.size() - 1 - i).getHigh() - dateDatas.get(dateDatas.size() - 1 - i).getLow() > validValue){
                dataPosition2 = dateDatas.size() - 1 - i;
                break findPositionLoop;
            }else if (dateDatas.get(dateDatas.size() - 1 - i).getHigh() - dateDatas.get(dateDatas.size() - 1 - i).getLow() > validValue){
                dataPosition1 = dateDatas.size() - 1 - i;
            }
        }
        Log.i(TAG, "setUpValidData: " + String.valueOf(dataPosition1));
        validDateTxt = (TextView)findViewById(R.id.validDateTxt);
        validhighTxt = (TextView)findViewById(R.id.validhighTxt);
        validLowTxt = (TextView)findViewById(R.id.validLowTxt);
        validTrigerValueTxt = (TextView)findViewById(R.id.validTrigerValueTxt);

        validDateTxt.setText(dateDatas.get(dataPosition1).getStrDate().substring(0, 10));
        validhighTxt.setText(String.valueOf(dateDatas.get(dataPosition1).getHigh()));
        validLowTxt.setText(String.valueOf(dateDatas.get(dataPosition1).getLow()));
        validTrigerValueTxt.setText(String.valueOf(validValue));

        validDateTxt2 = (TextView)findViewById(R.id.validDateTxt2);
        validhighTxt2 = (TextView)findViewById(R.id.validhighTxt2);
        validLowTxt2 = (TextView)findViewById(R.id.validLowTxt2);
        validTrigerValueTxt2 = (TextView)findViewById(R.id.validTrigerValueTxt2);

        validDateTxt2.setText(dateDatas.get(dataPosition2).getStrDate().substring(0, 10));
        validhighTxt2.setText(String.valueOf(dateDatas.get(dataPosition2).getHigh()));
        validLowTxt2.setText(String.valueOf(dateDatas.get(dataPosition2).getLow()));
        validTrigerValueTxt2.setText(String.valueOf(validValue));

        buyOrSellTxt = (TextView)findViewById(R.id.buyOrSellTxt);
        if(dateDatas.get(dataPosition1).getClose() > dateDatas.get(dataPosition1).getOpen()){
            buyOrSellTxt.setText("Buy");
        }else {
            buyOrSellTxt.setText("Sell");
        }

        targetTxt = (TextView)findViewById(R.id.targetTxt);
        cutLostTxt = (TextView)findViewById(R.id.cutLostTxt);

        Log.i(TAG, "setUpValidData: " + String.valueOf(validValue));
        Log.i(TAG, "setUpValidData: " + String.valueOf(dateDatas.get(dataPosition1).getClose()));

        targetTxt.setText(String.valueOf((dateDatas.get(dataPosition1).getClose()) + validValue));
        cutLostTxt.setText(String.valueOf(dateDatas.get(dataPosition1).getClose() - validLow));

        int validPosition = -1;
        ArrayList<Integer> twentyValueLow = new ArrayList<>();
        ArrayList<Integer> twentyValueHigh = new ArrayList<>();
        ArrayList<Integer> movingAverage = new ArrayList<>();
        ArrayList<Integer> finalMovingAverage = new ArrayList<>();
        int buyNumber = 0;
        int sellNumber = 0;
        int lost = 0;
        int win = 0;
        int winValue = 0;
        int lostValue = 0;
        int forceSell = 0;
        int forceSellNumber = 0;
        Log.i(TAG, "setUpValidData: size:" + String.valueOf(dateDatas.size()));
        for (int i = dateDatas.size() - 100 , p = dateDatas.size(); i < p ; i++){
            if (twentyValueLow.size() < 20){
                twentyValueLow.add(dateDatas.get(i).getLow());
                twentyValueHigh.add(dateDatas.get(i).getHigh());
            }else {
                twentyValueLow.add(dateDatas.get(i).getLow());
                twentyValueLow.remove(0);
                twentyValueHigh.add(dateDatas.get(i).getHigh());
                twentyValueHigh.remove(0);
            }

            if (twentyValueLow.size() == 20) {
                movingAverage.clear();
                for (int l = 0, m = twentyValueLow.size(); l < m; l++) {
                    movingAverage.add(twentyValueHigh.get(l) - twentyValueLow.get(l));
                }

                finalMovingAverage.clear();
                for (int n = 0, r = movingAverage.size(); n < r; n++) {
                    if (n == 0) {
                        finalMovingAverage.add(movingAverage.get(n));
                    } else {
                        int addplace = finalMovingAverage.size();
                        for (int o = 0, q = finalMovingAverage.size(); o < q; o++) {
                            if (movingAverage.get(n) > finalMovingAverage.get(o)) {
                                addplace = o;
                                break;
                            }
                        }
                        finalMovingAverage.add(addplace, movingAverage.get(n));
                    }
                }

                ArrayList<Integer> validMovingAverage = new ArrayList<>();
                for (int fin = (int) (finalMovingAverage.size() * 0.2); fin < finalMovingAverage.size() * 0.8; fin++) {
                    validMovingAverage.add(finalMovingAverage.get(fin));
                }
                int rangeHigh = validMovingAverage.get(0);
                int rangeLow = validMovingAverage.get(validMovingAverage.size() - 1);
                Log.i(TAG, "setUpValidData: " + String.valueOf(rangeHigh) + " - " + String.valueOf(rangeLow));
                int range = 0;

                for (int k = 0; k < validMovingAverage.size(); k++){
                    range = range + validMovingAverage.get(k);
                }
                range = range/validMovingAverage.size();

                for (int firstTwenty = 0; firstTwenty < 20; firstTwenty++) {
                    if (dateDatas.get(i - firstTwenty).getHigh() - dateDatas.get(i - firstTwenty).getLow() > rangeLow
                            && dateDatas.get(i - firstTwenty).getHigh() - dateDatas.get(i - firstTwenty).getLow() < rangeHigh) {
                            validPosition = i - firstTwenty;
                            break;
                        } else {
                        break;
                    }
                }

                boolean isWin = false;
                boolean isLost = false;
                if (validPosition == i && i < dateDatas.size() - 2) {
                    DateData nextDayData = dateDatas.get(i+1);
                    DateData todayData = dateDatas.get(i);

                    if (todayData.getClose() > todayData.getOpen()){
                        if (nextDayData.getOpen() > todayData.getClose()){
                            sellNumber = sellNumber + 1;
                            if (nextDayData.getOpen() - nextDayData.getLow() > rangeLow){
                                win = win + 1;
                                winValue = winValue + rangeLow;
                                isWin = true;
                            }

                            if (nextDayData.getHigh() - nextDayData.getOpen() > range){
                                lost = lost + 1;
                                lostValue = lostValue + range;
                                isLost = true;
                            }

                            if (isLost == false && isWin == false){
                                if (nextDayData.getHigh() - nextDayData.getLow() > range) {
                                    forceSellNumber = forceSellNumber + 1;
                                    forceSell = forceSell + nextDayData.getOpen() - nextDayData.getClose();
                                }else {
                                    DateData thirdDay = dateDatas.get(i+2);
                                    if (thirdDay.getOpen() - nextDayData.getLow() > rangeLow){
                                        win = win + 1;
                                        winValue = winValue + rangeLow;
                                        isWin = true;
                                    }

                                    if (nextDayData.getHigh() - thirdDay.getOpen() > range){
                                        lost = lost + 1;
                                        lostValue = lostValue + range;
                                        isLost = true;
                                    }

                                    if (isWin == false && isLost == false){
                                        forceSellNumber = forceSellNumber + 1;
                                        forceSell = forceSell + nextDayData.getOpen() - thirdDay.getClose();
                                    }
                                }
                            }
                        }else if (nextDayData.getOpen()< todayData.getClose()){
                            buyNumber = buyNumber + 1;
                            if (nextDayData.getHigh() - nextDayData.getOpen() > rangeLow){
                                win = win + 1;
                                winValue = winValue + rangeLow;
                                isWin =true;
                            }

                            if (nextDayData.getOpen() - nextDayData.getLow() > range){
                                lost = lost + 1;
                                lostValue = lostValue + range;
                                isLost = true;
                            }

                            if (isLost == false && isWin == false){
                                if (nextDayData.getHigh() - nextDayData.getLow() >range) {
                                    forceSellNumber = forceSellNumber + 1;
                                    forceSell = forceSell + nextDayData.getClose() - nextDayData.getOpen();
                                }else {
                                    DateData thirdDay = dateDatas.get(i+2);
                                    if (thirdDay.getHigh() - nextDayData.getOpen() > rangeLow){
                                        win = win + 1;
                                        winValue = winValue + rangeLow;
                                        isWin = true;
                                    }

                                    if (nextDayData.getOpen() - thirdDay.getLow() > range){
                                        lost = lost + 1;
                                        lostValue = lostValue + range;
                                        isLost = true;
                                    }

                                    if (isWin == false && isLost == false){
                                        forceSellNumber = forceSellNumber + 1;
                                        forceSell = forceSell + thirdDay.getClose() - nextDayData.getOpen();
                                    }
                                }
                            }
                        }
                    }else {
                        if (nextDayData.getOpen() > todayData.getClose()){
                            sellNumber = sellNumber + 1;
                            if (nextDayData.getOpen() - nextDayData.getClose() > rangeLow){
                                win = win + 1;
                                winValue = winValue + rangeLow;
                                isWin = true;
                            }

                            if (nextDayData.getHigh() - nextDayData.getOpen() > range){
                                lost = lost+1;
                                lostValue = lostValue + range;
                                isLost = true;
                            }

                            if (isLost == false && isWin == false){
                                if (nextDayData.getHigh() - nextDayData.getLow() > range) {
                                    forceSellNumber = forceSellNumber + 1;
                                    forceSell = forceSell + nextDayData.getOpen() - nextDayData.getClose();
                                }else {
                                    DateData thirdDay = dateDatas.get(i+2);
                                    if (nextDayData.getOpen() - thirdDay.getLow() > rangeLow){
                                        win = win + 1;
                                        winValue = winValue + rangeLow;
                                        isWin = true;
                                    }

                                    if (thirdDay.getHigh() - nextDayData.getOpen() > range){
                                        lost = lost+1;
                                        lostValue = lostValue + range;
                                        isLost = true;
                                    }

                                    if (isLost == false && isWin == false){
                                        forceSellNumber = forceSellNumber + 1;
                                        forceSell = forceSell + nextDayData.getOpen() - thirdDay.getClose();
                                    }
                                }
                            }
                        }else if (nextDayData.getOpen() < todayData.getClose()){
                            buyNumber = buyNumber + 1;
                            if (nextDayData.getHigh() - nextDayData.getOpen() > rangeLow){
                                win = win + 1;
                                winValue = winValue + rangeLow;
                                isWin = true;
                            }
                            if (nextDayData.getOpen() - nextDayData.getLow() > range){
                                lost = lost+1;
                                lostValue = lostValue + range;
                                isLost = true;
                            }
                            if (isLost == false && isWin == false){
                                if (nextDayData.getHigh() - nextDayData.getLow() > range) {
                                    forceSellNumber = forceSellNumber + 1;
                                    forceSell = forceSell + nextDayData.getClose() - nextDayData.getOpen();
                                }else {
                                    DateData thirdDay = dateDatas.get(i+2);
                                    if (thirdDay.getHigh() - nextDayData.getOpen() > rangeLow){
                                        win = win + 1;
                                        winValue = winValue + rangeLow;
                                        isWin = true;
                                    }

                                    if (nextDayData.getOpen() - thirdDay.getLow() > range){
                                        lost = lost+1;
                                        lostValue = lostValue + range;
                                        isLost = true;
                                    }

                                    if (isWin == false && isLost == false){
                                        forceSellNumber = forceSellNumber + 1;
                                        forceSell = forceSell + thirdDay.getClose() - nextDayData.getOpen();
                                    }
                                }
                            }
                        }
                    }

//                    if (nextDayData.getOpen() < todayData.getLow()){
//                        sellNumber  = sellNumber + 1;
//                        if (nextDayData.getOpen() - nextDayData.getLow() > range){
//                            win = win + 1;
//                            winValue = winValue + range;
//                            isWin = true;
//                        }
//                        if (nextDayData.getHigh() - nextDayData.getOpen() > range){
//                            lost = lost + 1;
//                            lostValue = lostValue + range;
//                            isLost = true;
//                        }
//
//                        if (isWin == false && isLost == false){
//                            forceSell = forceSell + nextDayData.getClose() - nextDayData.getOpen();
//                        }
//                    }else if (nextDayData.getOpen() > todayData.getHigh()) {
//                        buyNumber = buyNumber + 1;
//                        if (nextDayData.getHigh() - nextDayData.getOpen() > range) {
//                            win = win + 1;
//                            winValue = winValue + range;
//                            isWin = true;
//                        }
//
//                        if (nextDayData.getOpen() - nextDayData.getLow() > range) {
//                            lost = lost + 1;
//                            lostValue = lostValue + range;
//                            isLost = true;
//                        }
//
//                        if (isWin == false && isLost == false){
//                            forceSell = forceSell + nextDayData.getOpen() - nextDayData.getClose();
//                        }
//                    }

                    //如果連升2日有效，第2日買跌 ＋ 如果連跌2日有效，第2日買跌
//                    if (todayData.getClose() > todayData.getOpen()){
//                        if (nextDayData.getOpen() - nextDayData.getLow() >range){
//                            win = win+1;
//                            winValue = winValue +range;
//                            isWin = true;
//                        }
//
//                        if (nextDayData.getHigh() - nextDayData.getOpen() > rangeLow){
//                            lost = lost + 1;
//                            lostValue = lostValue + rangeLow;
//                            isLost = true;
//                        }
//
//                        if (isLost == false && isWin == false){
//                            forceSell = forceSell + nextDayData.getClose() - nextDayData.getOpen();
//                        }
//                    } else if (todayData.getClose() < todayData.getOpen()){
//                        if (nextDayData.getOpen() - nextDayData.getLow() > range){
//                            win = win + 1;
//                            winValue = winValue + range;
//                            isWin = true;
//                        }
//                        if (nextDayData.getHigh() - nextDayData.getOpen() > rangeLow){
//                            lost = lost + 1;
//                            lostValue = lostValue + rangeLow;
//                            isLost = true;
//                        }
//
//                        if (isLost == false && isWin == false){
//                            forceSell = forceSell + nextDayData.getClose() - nextDayData.getOpen();
//                        }
//                    }

                    //如果有效變動為升，即收市時買升
//                    if (todayData.getClose() > todayData.getOpen()){
//                        if (nextDayData.getHigh() - todayData.getClose() > range){
//                            win = win+1;
//                            winValue = winValue + range;
//                            isWin = true;
//                        }
//
//                        if (todayData.getClose() - nextDayData.getLow() > range){
//                            lost = lost + 1;
//                            lostValue = lostValue + range;
//                            isLost = true;
//                        }
//
//                        if (isLost == false && isWin == false){
//                            forceSell = forceSell + nextDayData.getClose() - nextDayData.getClose();
//                        }
//                    }else {
//                        if (nextDayData.getLow() - nextDayData.getClose() > range){
//                            win = win + 1;
//                            winValue = winValue + range;
//                            isWin = true;
//                        }
//                        if (todayData.getClose() - nextDayData.getHigh() > range){
//                            lost = lost + 1;
//                            lostValue = lostValue + range;
//                            isLost = true;
//                        }
//
//                        if (isLost == false && isWin == false){
//                            forceSell = forceSell + todayData.getClose() - nextDayData.getClose();
//                        }
//                    }


                    //跌市
//                    if (dateDatas.get(i).getOpen() > dateDatas.get(i).getClose()){
//                        if (dateDatas.get(i+1).getHigh() > dateDatas.get(i).getOpen()){
//                            buyNumber = buyNumber + 1;
//                            if (dateDatas.get(i+1).getHigh() - dateDatas.get(i).getOpen()>range){
//                                win = win + 1;
//                                winValue = winValue + range;
//                                isWin = true;
//                            }
//
//                            if (dateDatas.get(i).getClose() - dateDatas. (i+1).getLow() > rangeLow){
//                                lost = lost + 1;
//                                lostValue = lostValue + rangeLow;
//                                isLost = true;
//                            }
//
//                            if (isWin == false && isLost == false){
//                                forceSell = forceSell + (dateDatas.get(i).getOpen() - dateDatas.get(i + 1).getClose());
//                            }
//                        }else if (dateDatas.get(i).getLow()){
//
//                        }
//                    }
//
//                    //升市
//                    if (dateDatas.get(i).getOpen() < dateDatas.get(i).getClose()) {
//                        if (dateDatas.get(i + 1).getHigh() - dateDatas.get(i).getClose() > 0) {
//                            buyNumber = buyNumber + 1;
//                            if (dateDatas.get(i + 1).getHigh() - dateDatas.get(i).getClose() > range){
//                                win = win + 1;
//                                winValue = winValue + range;
//                                isWin = true;
//                            }
//                            if (dateDatas.get(i).getClose() - dateDatas.get(i+1).getLow() > rangeLow){
//                                lost = lost + 1;
//                                lostValue = lostValue + rangeLow;
//                                isLost = true;
//                            }
//                            if (isWin == false && isLost == false) {
//                                forceSell = forceSell + (dateDatas.get(i + 1).getClose()) - (dateDatas.get(i).getClose());
//                            }
//                        } else if (dateDatas.get(i + 1).getLow() < dateDatas.get(i).getOpen()) {
//                            sellNumber = sellNumber + 1;
//                            if (dateDatas.get(i).getOpen() - dateDatas.get(i+1).getLow() > range){
//                                win = win + 1;
//                                winValue = winValue + range;
//                                isWin = true;
//                            }
//                            if (dateDatas.get(i+1).getHigh() - dateDatas.get(i).getOpen() > rangeLow){
//                                lost = lost + 1;
//                                lostValue = lostValue + rangeLow;
//                                isLost = true;
//                            }
//                            if (isWin == false && isLost == false){
//                                forceSell = forceSell + (dateDatas.get(i).getOpen() - dateDatas.get(i + 1).getClose());
//                            }
//                        }
//                    }
                }
            }
        }

        Log.i(TAG, "setUpValidData: buyNumber: " + String.valueOf(buyNumber));
        Log.i(TAG, "setUpValidData: sellNumber" + String.valueOf(sellNumber));
        Log.i(TAG, "setUpValidData: lost"  + String.valueOf(lost));
        Log.i(TAG, "setUpValidData: win"  + String.valueOf(win));
        Log.i(TAG, "setUpValidData: winValue"  + String.valueOf(winValue));
        Log.i(TAG, "setUpValidData: lostValue"  + String.valueOf(lostValue));
        Log.i(TAG, "setUpValidData: forceSell" + String.valueOf(forceSell));
        Log.i(TAG, "setUpValidData: forceSellNumber"+String.valueOf(forceSellNumber));
        Log.i(TAG, "setUpValidData: total" + String.valueOf(buyNumber + sellNumber) );

        buyNumberTxt.setText(String.valueOf(buyNumber));
        sellNumberTxt.setText(String.valueOf(sellNumber));
        winNumberTxt.setText(String.valueOf(win));
        lostNumberTxt.setText(String.valueOf(lost));
        forceSellNumberTxt.setText(String.valueOf(forceSellNumber));
        winValueTxt.setText(String.valueOf(winValue));
        lostValueTxt.setText(String.valueOf(lostValue));
        forceSellValueTxt.setText(String.valueOf(forceSell));

//        int buyNumber = 0;
//        int sellNumber = 0;
//        int lost = 0;
//        int win = 0;
//        int winValue = 0;
//        int lostValue = 0;
//        int forceSell = 0;
    }
}
