package com.example.anthony.stock.KDJ;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anthony.stock.R;

import java.util.ArrayList;

/**
 * Created by chanyunyuen on 14/4/2017.
 */

public class KDJAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<KDJItem> items;
    Context context;
    private String TAG = KDJAdapter.class.getName();

    public KDJAdapter(ArrayList<KDJItem> items, Context context) {
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
            view = layoutInflater.inflate(R.layout.layout_kdj_item, null);
            holder = new Holder();
            initView(view, holder);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }

        KDJItem item = items.get(i);
        setView(holder, item, view);
        return view;
    }

    private void setView(Holder holder, KDJItem item, View view){
        holder.kdjDateTxt.setText(item.getStrDate().substring(0, 10));
        holder.kdjKTxt.setText(String.valueOf((int)item.getValueK()));
        holder.kdjDTxt.setText(String.valueOf((int)item.getValueD()));
        holder.kdjJTxt.setText(String.valueOf((int)item.getValueJ()));
        holder.kdjBuyPriceTxt.setText(String.valueOf((int)item.getBuyPrice()));
        holder.kdjSellPriceTxt.setText(String.valueOf((int)item.getSellPrice()));
        holder.kdjWinOrLossPriceTxt.setText(String.valueOf(item.getWinOrLossValue()));
        if (item.getValueJ() > 100){
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.readySellBlue));
        }else if (item.getValueJ() < 0){
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.readyBuyRed));
        }else {
            view.setBackgroundColor(Color.BLACK);
        }
    }

    private void initView(View view, Holder holder){
        holder.kdjDateTxt = (TextView) view.findViewById(R.id.kdjDateTxt);
        holder.kdjKTxt = (TextView) view.findViewById(R.id.kdjKTxt);
        holder.kdjDTxt = (TextView) view.findViewById(R.id.kdjDTxt);
        holder.kdjJTxt = (TextView) view.findViewById(R.id.kdjJTxt);
        holder.kdjBuyPriceTxt = (TextView)view.findViewById(R.id.kdjBuyPriceTxt);
        holder.kdjSellPriceTxt = (TextView)view.findViewById(R.id.kdjSellPriceTxt);
        holder.kdjWinOrLossPriceTxt = (TextView)view.findViewById(R.id.kdjWinOrLossPriceTxt);
    }

    private class Holder{
        TextView kdjDateTxt;
        TextView kdjKTxt;
        TextView kdjDTxt;
        TextView kdjJTxt;
        TextView kdjBuyPriceTxt;
        TextView kdjSellPriceTxt;
        TextView kdjWinOrLossPriceTxt;
    }
}
