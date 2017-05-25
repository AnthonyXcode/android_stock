package com.example.anthony.stock;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.Bolling.BollingActivity;
import com.example.anthony.stock.CheckData.CheckDataActivity;
import com.example.anthony.stock.CrossRSI.CrossRSIActivity;
import com.example.anthony.stock.FirebaseModel.DateFBModel;
import com.example.anthony.stock.FirebaseModel.HourFBModel;
import com.example.anthony.stock.KDJ.KDJActivity;
import com.example.anthony.stock.Moving.MovingActivity;
import com.example.anthony.stock.RSI.RSIActivity;
import com.example.anthony.stock.RealmClasses.Model.DateData;
import com.example.anthony.stock.RealmClasses.Model.HourData;
import com.example.anthony.stock.Service.BootCompletedService;
import com.example.anthony.stock.Utility.CommonTools;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends BaseApplication {

    ListView mainListView;
    ListViewAdapter listViewAdapter;
    String[] items = {"All", "KDJ", "RSI", "Bolling", "Moving", "Cross RSI", "check data"};
    Intent bootCompletedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayout();
        setupClick();
        uploadDataToFirebase();
    }

    private void setupLayout(){
        mainListView = (ListView)findViewById(R.id.mainListView);
        listViewAdapter = new ListViewAdapter(this);
        mainListView.setAdapter(listViewAdapter);
    }

    private void setupTool(){
        if (!CommonTools.checkServiceRunning(BootCompletedService.class, this)){
            initBootServiceIntent();
            startService(bootCompletedIntent);
        }
    }

    private void initBootServiceIntent(){
        if (bootCompletedIntent == null){
            bootCompletedIntent = new Intent(this, BootCompletedService.class);
        }
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
                }else if (name.equals("check data")){
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

    private void uploadDataToFirebase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dateRef = database.getReference("Date Data");

        Realm realm = Realm.getDefaultInstance();
        RealmResults<DateData> datas = realm.where(DateData.class).findAll();
//        for (DateData data :
//                datas) {
//            DateFBModel model = new DateFBModel();
//            model.setDate(data.getDate());
//            model.setStrDate(data.getStrDate());
//            model.setOpen(data.getOpen());
//            model.setClose(data.getClose());
//            model.setHigh(data.getHigh());
//            model.setLow(data.getLow());
//            model.setVolume(data.getVolume());
//            dateRef.child(data.getStrDate()).setValue(model);
//        }
        realm.close();
    }
}
