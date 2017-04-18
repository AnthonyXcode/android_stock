package com.example.anthony.stock.KDJ;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.R;
import com.example.anthony.stock.R2;
import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    EditText KDJPersentValueEditTxt;
    Button KDJOKBtn;
    ListView KDJListview;
    private int KDJValidDays;
    private int KDJDays;
    private int cutLossValue;
    private int persentValue;
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
        initListView();
    }

    private void setupLayout(){
        KDJDaysEditTxt = (EditText) findViewById(R.id.KDJDaysEditTxt);
        KDJValidDaysEditTxt = (EditText) findViewById(R.id.KDJValidDaysEditTxt);
        KDJCutlossEditTxt = (EditText) findViewById(R.id.KDJCutlossEditTxt);
        KDJPersentValueEditTxt = (EditText) findViewById(R.id.KDJPersentValueEditTxt);
        KDJOKBtn = (Button)findViewById(R.id.KDJOKBtn);
        KDJListview = (ListView)findViewById(R.id.KDJListview);
    }

    private void setupClick(){
        KDJOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initTools() {
        items = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        dateDatas = realm.where(DateData.class).findAll().sort("Date");
        hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
    }

    private void initStratege() {
        KDJValidDays = Integer.parseInt(KDJValidDaysEditTxt.getText().toString());
        KDJDays = Integer.parseInt(KDJDaysEditTxt.getText().toString());
        cutLossValue = Integer.parseInt(KDJCutlossEditTxt.getText().toString());
        persentValue = Integer.parseInt(KDJPersentValueEditTxt.getText().toString());
        KDJDays = 9;
    }

    private void initDataArray() {
        for (DateData data : dateDatas) {
            KDJItem kdjItem = initKDJItem(data.getDate(), data.getClose(), data.getHigh(), data.getLow(), data.getOpen(), data.getStrDate());
            if (items.size() < KDJDays) {
                kdjItem.setValueK(50);
                kdjItem.setValueD(50);
                kdjItem.setValueJ(50);
            } else {
                kdjItem = setKDJ(data.getHigh(), data.getLow(), data.getClose(), kdjItem);
            }
            items.add(kdjItem);
        }


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
        double k = (2 / 3) * previousItem.getValueK() + (1 / 3) * rsv;
        double d = (2 / 3) * previousItem.getValueD() + (1 / 3) * k;
        double j = 3 * d - 2 * k;
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

}
