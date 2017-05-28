package com.example.anthony.stock.All;

import android.os.Bundle;
import android.widget.ListView;

import com.example.anthony.stock.BaseApplication;
import com.example.anthony.stock.R;
import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class DetailPageActivity extends BaseApplication {

    ListView inhourListView;
    ListView dateListView;
    private InhourListviewAdapter inhourListviewAdapter;
    private DateListviewAdapter dateListviewAdapter;



    Realm realm;
    private String TAG = "DetailPageActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        setupLayout();
    }

    private void setupLayout(){
        realm = Realm.getDefaultInstance();
        inhourListView = (ListView)findViewById(R.id.inhourListView);
        RealmResults<HourData> hourDatas = realm.where(HourData.class).findAll().sort("Timestamp", Sort.DESCENDING);
        inhourListviewAdapter = new InhourListviewAdapter(this,hourDatas);
        inhourListView.setAdapter(inhourListviewAdapter);

        RealmResults<DateData> dateDatas = realm.where(DateData.class).findAll().sort("Date");;
        dateListviewAdapter = new DateListviewAdapter(this, dateDatas);
        dateListView = (ListView)findViewById(R.id.dateListView);
        dateListView.setAdapter(dateListviewAdapter);
        dateListView.setSelection(dateDatas.size() - 1);
    }
}
