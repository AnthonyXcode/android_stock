package com.example.anthony.stock.RSI;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.anthony.stock.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by chanyunyuen on 4/2/2017.
 */

public class RSIAdapter extends BaseAdapter {
    ArrayList<RSIItem> items;
    LayoutInflater inflater;
    Context context;
    int validRsi = 30;

    public RSIAdapter(Context context) {
        this.items = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        this.context = context;
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
            holder = new Holder();
            view = inflater.inflate(R.layout.layout_rsi, null);
            initLayout(holder, view);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        RSIItem item = items.get(i);
        setupLayout(holder, item);
        return view;
    }

    private void setupLayout(Holder holder, RSIItem item){
        holder.RsiDateTxt.setText(item.getDay().substring(0, 10));
        holder.RsiTxt.setText(String.valueOf((int) item.getRsi()));
        if (item.getRsi() > 100 - validRsi){
            holder.RsiTxt.setBackgroundColor(ContextCompat.getColor(context, R.color.readySellBlue));
        }else if (item.getRsi() < validRsi){
            holder.RsiTxt.setBackgroundColor(ContextCompat.getColor(context, R.color.readyBuyRed));
        }else {
            holder.RsiTxt.setBackgroundColor(Color.LTGRAY);
        }

        holder.RsiBuyTxt.setText(String.valueOf(item.getBuyPrice()));
        holder.RsiSellTxt.setText(String.valueOf(item.getSellPrice()));
    }

    public void addAll(ArrayList<RSIItem> items, int validRsi){
        this.items.clear();
        this.items.addAll(items);
        this.validRsi = validRsi;
        notifyDataSetChanged();
    }

    public void initLayout(Holder holder, View view){
        holder.RsiTxt = (TextView) view.findViewById(R.id.RsiTxt);
        holder.RsiDateTxt = (TextView) view.findViewById(R.id.RsiDateTxt);
        holder.RsiBuyTxt = (TextView)view.findViewById(R.id.RsiBuyTxt);
        holder.RsiSellTxt = (TextView)view.findViewById(R.id.RsiSellTxt);
    }

    private class Holder{
        TextView RsiDateTxt;
        TextView RsiTxt;
        TextView RsiBuyTxt;
        TextView RsiSellTxt;
    }
}
