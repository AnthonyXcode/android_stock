package com.example.anthony.stock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.realmclasses.DateData;
import com.example.anthony.stock.realmclasses.HourData;

import io.realm.Realm;
import io.realm.RealmResults;

public class DetailPageActivity extends AppCompatActivity {

    ListView inhourListView;
    ListView dateListView;
    private InhourListviewAdapter inhourListviewAdapter;
    private DateListviewAdapter dateListviewAdapter;
    private TextView numOfHour;
    private TextView numOfDate;
    Realm realm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        setupLayout();
    }

    private void setupLayout(){
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
    }
}
