package com.example.anthony.stock.All;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anthony.stock.R;
import com.example.anthony.stock.RealmClasses.Model.DateData;

import io.realm.RealmResults;

/**
 * Created by Anthony on 9/11/16.
 */
public class DateListviewAdapter extends BaseAdapter {

    Context context;
    RealmResults<DateData> dateDatas;

    public DateListviewAdapter(Context context, RealmResults<DateData> dateDatas) {
        this.context = context;
        this.dateDatas = dateDatas;
    }

    @Override
    public int getCount() {
        return dateDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return dateDatas.get(position);
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
        DateData dateData = dateDatas.get(position);
        TextView dateInhour = (TextView)view.findViewById(R.id.dateItemView);
        dateInhour.setText(dateData.getStrDate().substring(0,10));

        TextView openInhour = (TextView)view.findViewById(R.id.openItemView);
        openInhour.setText(String.valueOf(dateData.getOpen()));

        TextView closeInhour = (TextView)view.findViewById(R.id.closeItemView);
        closeInhour.setText(String.valueOf(dateData.getClose()));

        TextView lowInhour = (TextView)view.findViewById(R.id.lowItemView);
        lowInhour.setText(String.valueOf(dateData.getLow()));

        TextView hightInhour = (TextView)view.findViewById(R.id.hightItemView);
        hightInhour.setText(String.valueOf(dateData.getHigh()));

        return view;
    }
}
