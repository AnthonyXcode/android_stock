package com.example.anthony.stock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.All.DetailPageActivity;
import com.example.anthony.stock.Bolling.BollingActivity;
import com.example.anthony.stock.CheckData.CheckDataActivity;
import com.example.anthony.stock.CrossRSI.CrossRSIActivity;
import com.example.anthony.stock.FirebaseModel.DateFBModel;
import com.example.anthony.stock.KDJ.KDJActivity;
import com.example.anthony.stock.Moving.MovingActivity;
import com.example.anthony.stock.RSI.RSIActivity;
import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.realm.Realm;

public class MainActivity extends BaseApplication {

    ListView mainListView;
    ListViewAdapter listViewAdapter;
    String[] items = {"All", "KDJ", "RSI", "Bolling", "Moving", "Cross RSI", "Check Data"};
    Intent bootCompletedIntent;
    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayout();
        setupClick();
        downDataFromFirebase();
    }

    private void setupLayout(){
        mainListView = (ListView)findViewById(R.id.mainListView);
        listViewAdapter = new ListViewAdapter(this);
        mainListView.setAdapter(listViewAdapter);
    }

    private void setupClick(){
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = items[i];
                if (name.equals("All")){
                    Intent intent = new Intent(MainActivity.this, DetailPageActivity.class);
                    startActivity(intent);
                }else if (name.equals("Bolling")){
                    Intent intent = new Intent(MainActivity.this, BollingActivity.class);
                    startActivity(intent);
                }else if (name.equals("RSI")){
                    Intent intent = new Intent(MainActivity.this, RSIActivity.class);
                    startActivity(intent);
                }else if (name.equals("Moving")){
                    Intent intent = new Intent(MainActivity.this, MovingActivity.class);
                    startActivity(intent);
                }else if (name.equals("Cross RSI")){
                    Intent intent = new Intent(MainActivity.this, CrossRSIActivity.class);
                    startActivity(intent);
                }else if (name.equals("KDJ")){
                    Intent intent = new Intent(MainActivity.this, KDJActivity.class);
                    startActivity(intent);
                }else if (name.equals("Check Data")){
                    Intent intent = new Intent(MainActivity.this, CheckDataActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private class ListViewAdapter extends BaseAdapter{

        LayoutInflater layoutInflater;
        public ListViewAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null){
                viewHolder = new ViewHolder();
                view = layoutInflater.inflate(R.layout.layout_main, null);
                initView(viewHolder, view);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            String name = items[i];
            viewHolder.mainItemTxt.setText(name);
            return view;
        }

        private void initView(ViewHolder holder, View view){
            holder.mainItemTxt = (TextView)view.findViewById(R.id.mainItemTxt);
        }

        private class ViewHolder{
            TextView mainItemTxt;
        }
    }

    private void downDataFromFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dateRef = database.getReference("Date Data");

        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> yearData = dataSnapshot.getChildren();
                ArrayList<DateFBModel> fbModelArr = new ArrayList<>();
                for (DataSnapshot year:yearData){
                    Iterable<DataSnapshot> monthData = year.getChildren();
                    for (DataSnapshot month : monthData) {
                        Iterable<DataSnapshot> dateData = month.getChildren();
                        for (DataSnapshot date : dateData) {
                            DateFBModel model = date.getValue(DateFBModel.class);
                            fbModelArr.add(model);
                        }
                    }
                }
                addFbDataToRealm(fbModelArr);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addFbDataToRealm(final ArrayList<DateFBModel> fbModelArr){
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(fbModelsToRealmModels(fbModelArr));
                        realm.commitTransaction();
                        realm.close();
                    }
                })
                .subscribe();
    }

    private ArrayList<DateData> fbModelsToRealmModels(ArrayList<DateFBModel> fbModelArr){
        ArrayList<DateData> realmDataArr = new ArrayList<>();
        for (DateFBModel model : fbModelArr) {
            DateData realmData = new DateData();
            realmData.setStrDate(model.getStrDate());
            realmData.setDate(model.getDate());
            realmData.setVolume(model.getVolume());
            realmData.setFromFirebase(true);
            realmData.setOpen(model.getOpen());
            realmData.setClose(model.getClose());
            realmData.setLow(model.getLow());
            realmData.setHigh(model.getHigh());
            realmDataArr.add(realmData);
        }
        return realmDataArr;
    }
}
