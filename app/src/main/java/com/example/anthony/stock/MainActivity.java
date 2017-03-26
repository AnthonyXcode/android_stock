package com.example.anthony.stock;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.anthony.stock.Bolling.BollingActivity;
import com.example.anthony.stock.Moving.MovingActivity;
import com.example.anthony.stock.RSI.RSIActivity;

public class MainActivity extends BaseApplication {

    ListView mainListView;
    ListViewAdapter listViewAdapter;
    String[] items = {"All", "MACD", "RSI", "Bolling", "Moving"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLayout();
        setupClick();
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
}
