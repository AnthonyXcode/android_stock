package com.example.anthony.stock.Bolling;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anthony.stock.R;

import java.util.ArrayList;

/**
 * Created by chanyunyuen on 2/1/2017.
 */

public class BollingAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<BollingItem> items;
    private String TAG = "BollingAdapter";

    public BollingAdapter(Context content, ArrayList<BollingItem> items) {
        layoutInflater = LayoutInflater.from(content);
        this.items = items;
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
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.layout_bolling, null);
            initView(holder, view);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        BollingItem item = items.get(i);
        holder.dateItemViewTxt.setText(item.getDate().substring(2, 10));
        holder.openItemViewTxt.setText(String.valueOf(item.getOpen()));
        holder.closeItemViewTxt.setText(String.valueOf(item.getClose()));
        holder.MA20ItemViewTxt.setText(String.valueOf(item.getMA20()));
        holder.cutlossItemViewTxt.setText(String.valueOf(item.getCutlossValue()));
        holder.totalWinItemViewTxt.setText(String.valueOf(item.getTotalWin()));

        if (item.getTypicalC() < item.getLower5Percent()){
            view.setBackgroundColor(Color.RED);
        }else if (item.getTypicalC() > item.getUpper5Percent()){
            view.setBackgroundColor(Color.BLUE);
        }else {
            view.setBackgroundColor(Color.LTGRAY);
        }
        setColor(holder, item);
        return view;
    }

    private void setColor(ViewHolder holder, BollingItem item){
        holder.openItemViewTxt.setTextColor(Color.DKGRAY);

        if (item.isBuy()){
            holder.closeItemViewTxt.setTextColor(Color.GREEN);
        }else if (item.isSell()){
            holder.closeItemViewTxt.setTextColor(Color.GREEN);
        }else if (item.isNormalBuy()){
            holder.closeItemViewTxt.setTextColor(Color.YELLOW);
        } else if (item.isNormalSell()){
            holder.closeItemViewTxt.setTextColor(Color.YELLOW);
        }else {
            holder.closeItemViewTxt.setTextColor(Color.DKGRAY);
        }

        if (item.isCutlossSell()){
            holder.cutlossItemViewTxt.setTextColor(Color.YELLOW);
        }else if (item.isCutlossBuy()){
            holder.cutlossItemViewTxt.setTextColor(Color.YELLOW);
        }else {
            holder.cutlossItemViewTxt.setTextColor(Color.DKGRAY);
        }

        if (item.isCompensateBuy()){
            holder.MA20ItemViewTxt.setTextColor(Color.YELLOW);
        }else if (item.isCompensateSell()) {
            holder.MA20ItemViewTxt.setTextColor(Color.YELLOW);
        }else {
            holder.MA20ItemViewTxt.setTextColor(Color.DKGRAY);
        }

//        if (item.isNormalBuy()){
//            holder.closeItemViewTxt.setTextColor(Color.YELLOW);
//        }else if (item.isNormalSell()){
//            holder.closeItemViewTxt.setTextColor(Color.YELLOW);
//        }else {
//            holder.closeItemViewTxt.setTextColor(Color.DKGRAY);
//        }
    }

    private void initView(ViewHolder holder, View view){
        holder.dateItemViewTxt = (TextView)view.findViewById(R.id.dateItemViewTxt);
        holder.openItemViewTxt = (TextView)view.findViewById(R.id.openItemViewTxt);
        holder.closeItemViewTxt = (TextView)view.findViewById(R.id.closeItemViewTxt);
        holder.MA20ItemViewTxt = (TextView)view.findViewById(R.id.MA20ItemViewTxt);
        holder.cutlossItemViewTxt = (TextView)view.findViewById(R.id.cutlossItemViewTxt);
        holder.totalWinItemViewTxt = (TextView)view.findViewById(R.id.totalWinItemViewTxt);

    }

    private class ViewHolder{
        TextView dateItemViewTxt;
        TextView openItemViewTxt;
        TextView closeItemViewTxt;
        TextView MA20ItemViewTxt;
        TextView cutlossItemViewTxt;
        TextView totalWinItemViewTxt;
    }
}
