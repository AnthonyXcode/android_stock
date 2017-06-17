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

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class CrossRSIActivity extends AppCompatActivity {

    ListView crossRsiListView;
    EditText crossValidDaysEditTxt;
    EditText crossValidRsiDiffEditTxt;
    EditText crossShortRsiDaysEditTxt;
    EditText crossLongRsiDaysEditTxt;
    EditText cutLostValueEditTxt;
    Button crossRsiOKBtn;
    TextView crossRSITxt;
    int shortRsi = 7;
    int longRsi = 25;
    int validRsi = 5;
    int validDays = 7;
    int cutlostValue = 200;
    ArrayList<CrossRSIItem> items;
    Realm realm;
    RealmResults<DateData> dateDatas;
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
        countRsi();
        setupListView();
        analyseResult();
        printResult();
    }

    private void setupLayout() {
        crossRsiListView = (ListView) findViewById(R.id.crossRsiListView);
        crossValidDaysEditTxt = (EditText) findViewById(R.id.crossValidDaysEditTxt);
        crossValidRsiDiffEditTxt = (EditText) findViewById(R.id.crossValidRsiEditTxt);
        crossShortRsiDaysEditTxt = (EditText) findViewById(R.id.crossShortRsiDaysEditTxt);
        crossLongRsiDaysEditTxt = (EditText) findViewById(R.id.crossLongRsiDaysEditTxt);
        cutLostValueEditTxt = (EditText) findViewById(R.id.cutLostValueEditTxt);
        crossRsiOKBtn = (Button) findViewById(R.id.corssRsiOKBtn);
        crossRSITxt = (TextView) findViewById(R.id.crossRSITxt);
        crossShortRsiDaysEditTxt.setText(String.valueOf(shortRsi));
        crossLongRsiDaysEditTxt.setText(String.valueOf(longRsi));
        crossValidRsiDiffEditTxt.setText(String.valueOf(validRsi));
        crossValidDaysEditTxt.setText(String.valueOf(validDays));
        cutLostValueEditTxt.setText(String.valueOf(cutlostValue));
    }

    private void setupClick() {
        crossRsiOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setData();
                initValidData();
                countRsi();
                setupListView();
                analyseResult();
                printResult();
            }
        });
    }

    private void setupTool() {
        items = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
    }

    private void setData() {
        shortRsi = Integer.parseInt(crossShortRsiDaysEditTxt.getText().toString());
        longRsi = Integer.parseInt(crossLongRsiDaysEditTxt.getText().toString());
        validRsi = Integer.parseInt(crossValidRsiDiffEditTxt.getText().toString());
        validDays = Integer.parseInt(crossValidDaysEditTxt.getText().toString());
        cutlostValue = Integer.parseInt(cutLostValueEditTxt.getText().toString());
    }

    private void initValidData() {
        items.clear();
        for (DateData realmItem : dateDatas) {
            CrossRSIItem item = initItem(realmItem.getStrDate(), realmItem.getDate(), realmItem.getOpen(), realmItem.getClose(), realmItem.getLow(), realmItem.getHigh());
            items.add(item);
        }
    }

    private void setupListView() {
        adapter = new CrossRSIAdapter(items, this);
        crossRsiListView.setAdapter(adapter);
        crossRsiListView.setSelection(crossRsiListView.getCount());
    }

    private void countRsi() {
        for (int i = 0; i < items.size(); i++) {
            if (i == shortRsi - 1) {
                initRsi(items, shortRsi, true);
            }
            if (i == longRsi - 1) {
                initRsi(items, longRsi, false);
            }
            if (i > shortRsi - 1) {
                modifyItem(i, shortRsi, true);
            }

            if (i > longRsi - 1) {
                modifyItem(i, longRsi, false);
            }
        }
    }

    private CrossRSIItem initItem(String dayStr, int day, int dayOpen, int dayClose, int dayLow, int dayHigh) {
        CrossRSIItem item = new CrossRSIItem();
        item.setDayStr(dayStr);
        item.setDay(day);
        item.setDayOpen(dayOpen);
        item.setDayClose(dayClose);
        item.setDayLow(dayLow);
        item.setDayHigh(dayHigh);
        return item;
    }

    private void initRsi(ArrayList<CrossRSIItem> items, int rsiDays, boolean isShort) {
        int totalRaise = 0;
        int totalDrop = 0;
        for (int i = 0; i < items.size(); i++) {
            if (i == rsiDays - 1) break;
            CrossRSIItem firstItem = items.get(i);
            CrossRSIItem secondItem = items.get(i + 1);
            int difference = secondItem.getDayClose() - firstItem.getDayClose();
            if (difference > 0) {
                totalRaise += difference;
            } else {
                totalDrop += Math.abs(difference);
            }
        }
        CrossRSIItem lastItem = items.get(rsiDays);
        int rsi = countRSI(totalRaise, totalDrop);
        if (isShort) {
            lastItem.setRaiseShortAverage(((double) totalRaise) / rsiDays);
            lastItem.setDropShortAverage(((double) totalDrop) / rsiDays);
            lastItem.setShortRsi(rsi);
        } else {
            lastItem.setRaiseLongAverage(((double) totalRaise) / rsiDays);
            lastItem.setDropLongAverage(((double) totalDrop) / rsiDays);
            lastItem.setLongRsi(rsi);
        }
    }

    private int countRSI(double totalRaise, double totalDrop) {
        int rsi = (int) ((totalRaise / (totalDrop + totalRaise)) * 100);
        return rsi;
    }

    private void modifyItem(int position, int rsiDays, boolean isShort) {
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
        } else {
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

    int Sum;
    int totalWin;
    int totalLost;
    int totalCutLost;
    int winNumb;
    int lostNumb;
    int cutLostNumb;
    int totalTrade;

    private void analyseResult() {
        Sum = 0;
        winNumb = 0;
        lostNumb = 0;
        cutLostNumb = 0;
        totalTrade = 0;
        totalWin = 0;
        totalLost = 0;
        totalCutLost = 0;

        for (int i = 0; i < items.size(); i++) {
            CrossRSIItem item = items.get(i);
            CrossRSIItem previousItem;
            try {
                previousItem = items.get(i - 1);
            } catch (Exception ex) {
                continue;
            }
            if (previousItem.getLongRsi() > previousItem.getShortRsi() && item.getLongRsi() < item.getShortRsi()) {
                if (Math.abs(item.getLongRsi() - item.getShortRsi()) > validRsi) {
                    totalTrade += 1;
                    item.setBuyPrice(item.getDayClose());
                    analyseForBuy(i);
                }
            } else if (previousItem.getLongRsi() < previousItem.getShortRsi() && item.getLongRsi() > item.getShortRsi()) {
                if (Math.abs(item.getLongRsi() - item.getShortRsi()) > validRsi) {
                    totalTrade += 1;
                    item.setSellPrice(item.getDayClose());
                    analyseForSell(i);
                }
            }

        }
    }

    private void analyseForBuy(int position) {
        CrossRSIItem buyItem = items.get(position);
        for (int i = position + 1; i < position + validDays; i++) {
            CrossRSIItem movingItem;
            try {
                movingItem = items.get(i);
            } catch (Exception ex) {
                break;
            }
            if (movingItem.getLongRsi() > movingItem.getShortRsi() || buyItem.getDayClose() - movingItem.getDayClose() > cutlostValue) {
                cutLostNumb += 1;
                totalCutLost += movingItem.getDayClose() - buyItem.getDayClose();
                Sum += movingItem.getDayClose() - buyItem.getDayClose();
                movingItem.setSellPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(movingItem.getDayClose() - buyItem.getDayClose());
                break;
            }

            if (i == position + validDays - 1) {
                if (movingItem.getDayClose() < buyItem.getDayClose()) {
                    lostNumb += 1;
                    totalLost += movingItem.getDayClose() - buyItem.getDayClose();
                } else {
                    winNumb += 1;
                    totalWin += movingItem.getDayClose() - buyItem.getDayClose();
                }
                Sum += movingItem.getDayClose() - buyItem.getDayClose();
                movingItem.setSellPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(movingItem.getDayClose() - buyItem.getDayClose());
                break;
            }
        }
    }

    private void analyseForSell(int position) {
        CrossRSIItem sellItem = items.get(position);
        for (int i = position + 1; i < position + validDays; i++) {
            CrossRSIItem movingItem;
            try {
                movingItem = items.get(i);
            } catch (Exception ex) {
                break;
            }
            if (movingItem.getLongRsi() < movingItem.getShortRsi() || movingItem.getDayClose() - sellItem.getDayClose() > cutlostValue) {
                cutLostNumb += 1;
                totalCutLost += sellItem.getDayClose() - movingItem.getDayClose();
                Sum += sellItem.getDayClose() - movingItem.getDayClose();
                movingItem.setBuyPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(sellItem.getDayClose() - movingItem.getDayClose());
                break;
            }

            if (i == position + validDays - 1) {
                if (movingItem.getDayClose() > sellItem.getDayClose()) {
                    lostNumb += 1;
                    totalLost += sellItem.getDayClose() - movingItem.getDayClose();
                } else {
                    winNumb += 1;
                    totalWin += sellItem.getDayClose() - movingItem.getDayClose();
                }
                Sum += sellItem.getDayClose() - movingItem.getDayClose();
                movingItem.setBuyPrice(movingItem.getDayClose());
                movingItem.setWinOrloss(sellItem.getDayClose() - movingItem.getDayClose());
                break;
            }
        }
    }

    private void printResult() {
        crossRSITxt.setText("Sum: " + Sum +
                "\n" + "Trade Number: " + totalTrade +
                "\n" + "Win Number: " + winNumb +
                "\n" + "Lost Number: " + lostNumb +
                "\n" + "Cut loss Number: " + cutLostNumb +
                "\n" + "Total Win: " + totalWin +
                "\n" + "Total Lost: " + String.valueOf(totalLost) +
                "\n" + "Total Cut Lost: " + totalCutLost +
                "\n" + "交易規則： " +
                "\n" + "1. 收市時，短期RSI ＋ 有效RSI > 長期RSI，則買入。反之亦然。" +
                "\n" + "2. 如收市時虧損" + cutlostValue + "點以上，則止蝕。" +
                "\n" + "3. 在有效日期內（7 - 10日），收市時完成一次交易。");
    }
}
