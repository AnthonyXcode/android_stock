package com.example.anthony.stock.Moving;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anthony.stock.R;

import java.util.ArrayList;

/**
 * Created by chanyunyuen on 26/3/2017.
 */

public class MovingAdapter extends BaseAdapter {
    ArrayList<MovingItem> items;
    Context context;
    LayoutInflater layoutInflater;
    int validDays;

    public MovingAdapter(ArrayList<MovingItem> items, Context context) {
        this.items = items;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null){
            view = layoutInflater.inflate(R.layout.layout_moving_item, null);
            holder = new Holder();
            initView(holder, view);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        MovingItem item = items.get(i);
        setView(holder, item, view);
        return view;
    }

    private void setView(Holder holder, MovingItem item, View view){
        holder.movingDate.setText(item.getStrDate().substring(0,10));
        holder.movingWinOrLoss.setText(String.valueOf(item.getWinOrLoss()));
        if (item.getLongMA() > item.getStortMA()){
            view.setBackgroundColor(ContextCompat.getColor(context,R.color.readySellBlue));
        }else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.readyBuyRed));
        }
        holder.movingBuy.setText(String.valueOf(item.getBuyPrice()));
        holder.movingSell.setText(String.valueOf(item.getSellPrice()));
    }

    private void initView(Holder holder, View view){
        holder.movingDate = (TextView)view.findViewById(R.id.movingDate);
        holder.movingWinOrLoss = (TextView) view.findViewById(R.id.movingWinOrLoss);
        holder.movingBuy = (TextView) view.findViewById(R.id.movingBuy);
        holder.movingSell = (TextView) view.findViewById(R.id.movingSell);
    }

    private class Holder{
        TextView movingDate;
        TextView movingWinOrLoss;
        TextView movingBuy;
        TextView movingSell;
    }
}
