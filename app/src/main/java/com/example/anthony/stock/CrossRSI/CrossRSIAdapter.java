package com.example.anthony.stock.CrossRSI;

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
 * Created by anthony on 28/3/2017.
 */

public class CrossRSIAdapter extends BaseAdapter {
    ArrayList<CrossRSIItem> items;
    LayoutInflater layoutInflater;
    Context context;

    public CrossRSIAdapter(ArrayList<CrossRSIItem> items, Context context) {
        this.items = items;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null){
            holder = new Holder();
            convertView = layoutInflater.inflate(R.layout.layout_cross_rsi_item, null);
            initLayout(convertView, holder);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        CrossRSIItem item = items.get(position);
        setupLayout(item, holder, convertView);
        return convertView;
    }

    private void setupLayout(CrossRSIItem item, Holder holder, View view) {
        holder.crossRsiDateTxt.setText(item.getDay().substring(0, 10));
        holder.crossRsiShortTxt.setText(String.valueOf((int) item.getShortRsi()));
        holder.crossRsiLongTxt.setText(String.valueOf((int) item.getLongRsi()));

        if (item.getShortRsi() > item.getLongRsi()) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.readyBuyRed));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.readySellBlue));
        }
        holder.crossRsiBuyTxt.setText(String.valueOf(item.getBuyPrice()));
        holder.crossRsiSellTxt.setText(String.valueOf(item.getSellPrice()));
    }

    private void initLayout(View view, Holder holder){
        holder.crossRsiBuyTxt = (TextView) view.findViewById(R.id.crossRsiBuyTxt);
        holder.crossRsiDateTxt = (TextView)view.findViewById(R.id.crossRsiDateTxt);
        holder.crossRsiLongTxt = (TextView) view.findViewById(R.id.crossRsiLongTxt);
        holder.crossRsiSellTxt = (TextView) view.findViewById(R.id.crossRsiSellTxt);
        holder.crossRsiShortTxt = (TextView) view.findViewById(R.id.crossRsiShortTxt);
    }

    private class Holder{
        TextView crossRsiSellTxt;
        TextView crossRsiBuyTxt;
        TextView crossRsiShortTxt;
        TextView crossRsiLongTxt;
        TextView crossRsiDateTxt;
    }
}
