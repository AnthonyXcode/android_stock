package com.example.anthony.stock.CheckData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.anthony.stock.R;

import java.util.ArrayList;

/**
 * Created by test on 27/5/2017.
 */

public class CheckAdapte extends BaseAdapter {
    ArrayList<UncheckedItem> items;
    Context context;
    LayoutInflater inflater;

    public CheckAdapte(ArrayList<UncheckedItem> items, Context context) {
        this.items = items;
        this.context = context;
        inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.layout_check_data_item, null);
            initLayout(holder, convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        setupLayout(position, holder);
        return convertView;
    }

    private void setupLayout(final int position, final Holder holder){
        UncheckedItem item = items.get(position);
        holder.dateEditText.setText(String.valueOf(item.getDate()));
        holder.volumeEditText.setText(String.valueOf(item.getVolume()));
        holder.openEditText.setText(String.valueOf(item.getOpen()));
        holder.closeEditText.setText(String.valueOf(item.getClose()));
        holder.highEditText.setText(String.valueOf(item.getHigh()));
        holder.lowEditText.setText(String.valueOf(item.getLow()));

        holder.checkedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = holder.dateEditText.getText().toString();
                String volume = holder.volumeEditText.getText().toString();
                String open = holder.openEditText.getText().toString();
                String close = holder.closeEditText.getText().toString();
                String low = holder.lowEditText.getText().toString();
                String high = holder.highEditText.getText().toString();
                ((CheckDataActivity)context).uploadData(date, volume, open, close, low, high);
                ((CheckDataActivity)context).removeItem(position);
                notifyDataSetChanged();
            }
        });
    }

    private void initLayout(Holder holder, View view){
        holder.checkedImg = (ImageView) view.findViewById(R.id.checkedImg);
        holder.dateEditText = (EditText) view.findViewById(R.id.dateEditText);
        holder.closeEditText = (EditText) view.findViewById(R.id.closeEditText);
        holder.openEditText = (EditText) view.findViewById(R.id.openEditText);
        holder.volumeEditText = (EditText) view.findViewById(R.id.volumeEditText);
        holder.lowEditText = (EditText) view.findViewById(R.id.lowEditText);
        holder.highEditText = (EditText) view.findViewById(R.id.highEditText);
    }

    private class Holder {
        EditText dateEditText;
        EditText volumeEditText;
        EditText highEditText;
        EditText lowEditText;
        EditText closeEditText;
        EditText openEditText;
        ImageView checkedImg;
    }
}
