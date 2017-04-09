package com.example.anthony.stock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anthony.stock.RealmClasses.Model.HourData;

import io.realm.RealmResults;

/**
 * Created by Anthony on 9/11/16.
 */
public class InhourListviewAdapter extends BaseAdapter {

    Context context;
    RealmResults<HourData> hourDatas;

    public InhourListviewAdapter(Context context, RealmResults<HourData> hourDatas) {
        this.context = context;
        this.hourDatas = hourDatas;
    }

    @Override
    public int getCount() {
        return hourDatas.size();
    }

    @Override
    public HourData getItem(int position) {
        return hourDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        if (convertView == null){
            view = new LinearLayout(context);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.list_item_inhour, view, true);
        }else {
            view = (LinearLayout)convertView;
        }
        HourData hourData = hourDatas.get(position);
        TextView dateInhour = (TextView)view.findViewById(R.id.dateItemView);
        dateInhour.setText(hourData.getDate().substring(0,10)+"\n"+hourData.getDate().substring(11,19));

        TextView openInhour = (TextView)view.findViewById(R.id.openItemView);
        openInhour.setText(String.valueOf(hourData.getOpen()));

        TextView closeInhour = (TextView)view.findViewById(R.id.closeItemView);
        closeInhour.setText(String.valueOf(hourData.getClose()));

        TextView lowInhour = (TextView)view.findViewById(R.id.lowItemView);
        lowInhour.setText(String.valueOf(hourData.getLow()));

        TextView hightInhour = (TextView)view.findViewById(R.id.hightItemView);
        hightInhour.setText(String.valueOf(hourData.getHigh()));

        return view;
    }
}
