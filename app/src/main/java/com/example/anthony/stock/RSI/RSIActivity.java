package com.example.anthony.stock.RSI;

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

public class RSIActivity extends AppCompatActivity {
    private int rsiDays = 13;
    private int validDays = 6;
    private double validRSI = 30;
    private String TAG = "RSIActivity";
    private ListView RSIListview;
    private Realm realm;
    private RealmResults<DateData> allDayRealmDatas;
    private RealmResults<HourData> hourDatas;
    private ArrayList<RSIItem> allItems;
    private RSIAdapter adapter;
    private TextView rsiResultTxt;
    private Button rsiDayBtn;
    private EditText rsiDayEditTxt;
    private EditText validDayEditTxt;
    private EditText validRsiTxt;
    private EditText targetValueEditTxt;
    private EditText priceEditTxt;
    private Button priceOKBtn;
    private TextView cutlossRsiTxt;
    private int target = 800;
    private int totalWin = 0;
    private int totalTrade = 0;
    private int winNumber = 0;
    private int lossNumber = 0;
    private int meetTarget = 0;
    private int cutloss = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsi);
        setupLayout();
        setupTools();
        setupItems();
        setupClick();
        analysis();
        printResult();
        predictPrice();
    }

    private void setupLayout(){
        RSIListview = (ListView) findViewById(R.id.RSIListview);
        rsiResultTxt = (TextView)findViewById(R.id.rsiResultTxt);
        rsiDayEditTxt = (EditText)findViewById(R.id.rsiDayEditTxt);
        rsiDayBtn = (Button)findViewById(R.id.rsiDayBtn);
        validDayEditTxt = (EditText)findViewById(R.id.validDayEditTxt);
        validRsiTxt = (EditText)findViewById(R.id.validRsiTxt);
        targetValueEditTxt = (EditText)findViewById(R.id.targetValueEditTxt);
        priceEditTxt = (EditText)findViewById(R.id.priceEditTxt);
        priceOKBtn = (Button)findViewById(R.id.priceOKBtn);
        cutlossRsiTxt = (TextView)findViewById(R.id.cutlossRsiTxt);
        rsiDayEditTxt.setText(String.valueOf(rsiDays));
        validRsiTxt.setText(String.valueOf((int) validRSI));
        validDayEditTxt.setText(String.valueOf(validDays));
        targetValueEditTxt.setText(String.valueOf(target));
    }

    private void setupClick(){
        rsiDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetData();
                rsiDays = Integer.parseInt(rsiDayEditTxt.getText().toString());
                validRSI = Integer.parseInt(validRsiTxt.getText().toString());
                validDays = Integer.parseInt(validDayEditTxt.getText().toString());
                target = Integer.parseInt(targetValueEditTxt.getText().toString());
                setupItems();
                analysis();
                printResult();
                Log.i(TAG, "onCreate: " + rsiDays + " " + validRSI + " " + validDays + " " + target);
                predictPrice();
            }
        });

        priceOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                predictRsi();
            }
        });
    }

    private void resetData(){
        totalWin = 0;
        totalTrade = 0;
        winNumber = 0;
        lossNumber = 0;
        meetTarget = 0;
        cutloss = 0;
        lowerThan10 = 0;
        from10to20 = 0;
        from20to30 = 0;
        from30to40 = 0;
        from40to50 = 0;
        from50to60 = 0;
        from60to70 = 0;
        from70to80 = 0;
        from80to90 = 0;
        from90to100 = 0;
    }

    private void setupTools(){
        realm = Realm.getDefaultInstance();
        allDayRealmDatas = realm.where(DateData.class).findAll().sort("Date");
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
        allItems = new ArrayList<>();
        adapter = new RSIAdapter(this);
        RSIListview.setAdapter(adapter);
    }

    int lowerThan10 = 0;
    int from10to20 = 0;
    int from20to30 = 0;
    int from30to40 = 0;
    int from40to50 = 0;
    int from50to60 = 0;
    int from60to70 = 0;
    int from70to80 = 0;
    int from80to90 = 0;
    int from90to100 = 0;
    private void setupItems(){
        allItems.clear();
        for(DateData realmItem:allDayRealmDatas){
            Log.i(TAG, "setupItems: " + realmItem.getStrDate());
            RSIItem item = initItme(realmItem.getStrDate(), realmItem.getOpen(), realmItem.getClose(), realmItem.getLow(), realmItem.getHigh());
            if (allItems.size() < rsiDays){
                allItems.add(item);
                continue;
            }else if (allItems.size() == rsiDays){
                int totalRaise = 0;
                int totalDrop = 0;
                for (int i = allItems.size() - rsiDays; i < allItems.size(); i++){
                    RSIItem firstItem = allItems.get(i);
                    RSIItem secondItem;
                    if (i != allItems.size() - 1) {
                        secondItem = allItems.get(i + 1);
                    }else {
                        secondItem = item;
                    }
//                    int difference = getAverage(secondItem) - getAverage(firstItem);
                    int difference = secondItem.getDayClose() - firstItem.getDayClose();
                    if (difference > 0) {
                        totalRaise += difference;
                    } else {
                        totalDrop += Math.abs(difference);
                    }
                }
                item.setRaiseAverage(((double) totalRaise)/rsiDays);
                item.setDropAverage(((double) totalDrop)/rsiDays);
                item.setRsi(countRSI(totalRaise, totalDrop));
            }else {
                item = modifyItem(item);
            }
            allItems.add(item);
        }

        String date = hourDatas.get(0).getDate();
        int open = 0;
        int close = hourDatas.get(0).getClose();
        int low = hourDatas.get(0).getLow();
        int high = hourDatas.get(0).getHigh();
        for (HourData hourData:hourDatas){
            if (hourData.getDate().contains(date)){
                open = hourData.getOpen();
                if (low > hourData.getLow()){
                    low = hourData.getLow();
                }
                if (high < hourData.getHigh()){
                    high = hourData.getHigh();
                }
            }else {
                break;
            }
        }
        RSIItem item = initItme(date, open, close, low, high);
        Log.i(TAG, "setupItems: " + item.getDay());
        item = modifyItem(item);
        allItems.add(item);
    }

    private RSIItem initItme (String day, int dayOpen, int dayClose, int dayLow, int dayHigh){
        RSIItem item = new RSIItem();
        item.setDay(day);
        item.setDayOpen(dayOpen);
        item.setDayClose(dayClose);
        item.setDayLow(dayLow);
        item.setDayHigh(dayHigh);
        return item;
    }

    private RSIItem modifyItem(RSIItem item){
        RSIItem previousItme = allItems.get(allItems.size() - 1);
        double different = item.getDayClose() - previousItme.getDayClose();
        double raiseAverage;
        double dropAverage;
        if (different > 0){
            raiseAverage = ((previousItme.getRaiseAverage() * (rsiDays - 1)) + different)/rsiDays;
            dropAverage = previousItme.getDropAverage() * (rsiDays - 1) / rsiDays;
        }else {
            raiseAverage = previousItme.getRaiseAverage() * (rsiDays - 1)/rsiDays;
            dropAverage = (previousItme.getDropAverage() * (rsiDays -1) + Math.abs(different)) / rsiDays;
        }
        double rsi = (raiseAverage / (raiseAverage + dropAverage))*100;
        item.setRaiseAverage(raiseAverage);
        item.setDropAverage(dropAverage);
        item.setRsi(rsi);
        return item;
    }

    private int countRSI(double totalRaise, double totalDrop){
        int rsi = (int) ((totalRaise/(totalDrop + totalRaise)) * 100);
        return rsi;
    }

    private void analysis(){
        for (int i = 0; i < allItems.size() - 1; i++){
            RSIItem firstItem = allItems.get(i);
            RSIItem secondItem = allItems.get(i + 1);
            if (firstItem.getRsi() > 100 - validRSI && secondItem.getRsi() < 100 - validRSI){
                secondItem.setSell(true);
                countWinAndlossForSell(i + 1);
                totalTrade += 1;
            }else if (firstItem.getRsi() < validRSI && secondItem.getRsi() > validRSI){
                secondItem.setBuy(true);
                countWinAndlossForBuy(i + 1);
                totalTrade += 1;
            } else {
                secondItem.setBuy(false);
                secondItem.setSell(false);
            }
        }
        adapter.addAll(allItems, (int) validRSI);
        RSIListview.setSelection(RSIListview.getCount());
    }
    private void countWinAndlossForBuy(int position){
        RSIItem buyItem = allItems.get(position);
        buyItem.setBuyPrice(buyItem.getDayClose());

        loop:for (int i = 1; i < validDays; i++){
            RSIItem movingItem;
            try {
                movingItem = allItems.get(position + i);
            }catch (Exception ex){
                break loop;
            }
            if (movingItem.getDayHigh() - buyItem.getDayClose() > target){
                totalWin += target;
                meetTarget += 1;
                movingItem.setSellPrice(buyItem.getDayClose() + target);
                break loop;
            }

            if (movingItem.getRsi() < validRSI){
                totalWin += movingItem.getDayClose() - buyItem.getDayClose();
                cutloss += 1;
                movingItem.setSellPrice(movingItem.getDayClose());
                break loop;
            }

            if (i == validDays - 1){
                totalWin += movingItem.getDayClose() - buyItem.getDayClose();
                if (movingItem.getDayClose() >= buyItem.getDayClose()){
                    winNumber += 1;
                }else {
                    lossNumber += 1;
                }
                movingItem.setSellPrice(movingItem.getDayClose());
                break loop;
            }
        }
    }

    private void countWinAndlossForSell(int position){
        RSIItem sellItem = allItems.get(position);
        sellItem.setSellPrice(sellItem.getDayClose());

        loop:for (int i = 1; i < validDays ; i++){
            RSIItem movingItem;
            try {
                movingItem = allItems.get(position + i);
            }catch (Exception ex){
                break loop;
            }
            if (sellItem.getDayClose() - movingItem.getDayLow() > target){
                int different = sellItem.getDayClose() - movingItem.getDayOpen();
                if (different > target) totalWin += different;
                else totalWin += target;
                meetTarget += 1;
                movingItem.setBuyPrice(sellItem.getDayClose() - target);
                break loop;
            }

            if (movingItem.getRsi() > 100 - validRSI){
                totalWin += sellItem.getDayClose() - movingItem.getDayClose();
                cutloss += 1;
                movingItem.setBuyPrice(movingItem.getDayClose());
                break loop;
            }

            if (i == validDays - 1){
                totalWin += sellItem.getDayClose() - movingItem.getDayClose();
                if (sellItem.getDayClose() > movingItem.getDayClose()){
                    winNumber += 1;
                }else {
                    lossNumber += 1;
                }
                movingItem.setBuyPrice(movingItem.getDayClose());
                break loop;
            }
        }
    }

    String result = "";
    private void printResult(){
        lowerThan10 = -rsiDays;
        for (RSIItem item : allItems){
            double rsi =  item.getRsi();
            if (rsi < 10){
                lowerThan10 +=1;
            }else if (rsi >= 10 && rsi < 20){
                from10to20 +=1;
            }else if (rsi >= 20 && rsi < 30){
                from20to30 +=1;
            }else if (rsi >= 30 && rsi < 40){
                from30to40 +=1;
            }else if (rsi >= 40 && rsi < 50){
                from40to50 +=1;
            }else if (rsi >= 50 && rsi < 60){
                from50to60 +=1;
            }else if (rsi >= 60 && rsi < 70){
                from60to70 +=1;
            }else if (rsi >= 70 && rsi < 80){
                from70to80 +=1;
            }else if (rsi >= 80 && rsi < 90){
                from80to90 +=1;
            }else if (rsi >= 90 && rsi < 100){
                from90to100 +=1;
            }
        }
        result = "10< " + lowerThan10 +
                "\n10-20:" + from10to20 +
                "\n20-30:" + from20to30 +
                "\n30-40:" + from30to40 +
                "\n40-50:" + from40to50 +
                "\n50-60:" + from50to60 +
                "\n60-70:" + from60to70 +
                "\n70-80:" + from70to80 +
                "\n80-90:" + from80to90 +
                "\n90-100:" + from90to100
                + "\nTotal win:" + totalWin
                + "\nTotal Trade:" + totalTrade
                + "\nTotal meet target:" + meetTarget
                + "\nWin Number:" + winNumber
                + "\nloss Number:" + lossNumber
                + "\nCut loss:" + cutloss;
        rsiResultTxt.setText(result);
    }

    private void predictPrice(){
        RSIItem lastItem = allItems.get(allItems.size() - 1);
        int cutlossValue;
        if (lastItem.getRsi() > 50){
            int different = (int) (((100 - validRSI) / validRSI) * lastItem.getDropAverage() * (validDays - 1)
                                - lastItem.getRaiseAverage() * (validDays - 1));
            cutlossValue = lastItem.getDayClose() + different;
        }else {
            int different = (int) (((100 - validRSI)/ validRSI) * lastItem.getRaiseAverage() * (validDays - 1)
                                - lastItem.getDropAverage() * (validDays - 1));
            cutlossValue = lastItem.getDayClose() - different;
        }
        priceEditTxt.setHint(String.valueOf(cutlossValue));
    }

    private void predictRsi(){
        RSIItem lastItem = allItems.get(allItems.size() - 2);
        Log.i(TAG, "predictRsi: " + lastItem.getDayClose());
        int closeValue = Integer.parseInt(priceEditTxt.getText().toString());
        int rsi;
        if (lastItem.getDayClose() > closeValue){
            int difference = lastItem.getDayClose() - closeValue;
            double averageRaise = lastItem.getRaiseAverage() * (validDays - 1) / validDays;
            double averageDrop = (lastItem.getDropAverage() * (validDays - 1) + difference) / validDays;
            rsi = (int) ((averageRaise / (averageRaise + averageDrop)) * 100);
        }else {
            int difference = closeValue - lastItem.getDayClose();
            double averageRaise = (lastItem.getRaiseAverage() * (validDays - 1) + difference) / validDays;
            double averageDrop = (lastItem.getDropAverage() * (validDays - 1)) / validDays;
            rsi = (int) ((averageRaise / (averageRaise + averageDrop)) * 100);
        }
        cutlossRsiTxt.setText(String.valueOf(rsi));
    }
}
