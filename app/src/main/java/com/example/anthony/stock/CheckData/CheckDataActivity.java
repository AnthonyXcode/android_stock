package com.example.anthony.stock.CheckData;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.anthony.stock.FirebaseModel.DateFBModel;
import com.example.anthony.stock.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckDataActivity extends AppCompatActivity {

    EditText dateEditText;
    EditText volumeEditText;
    EditText highEditText;
    EditText lowEditText;
    EditText closeEditText;
    EditText openEditText;
    ImageView checkedImg;
    ListView checkDataListView;
    FirebaseDatabase database;
    DatabaseReference dateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_data);
        setupLayout();
        setupClick();
        setupTool();
    }

    private void setupLayout(){
        openEditText = (EditText) findViewById(R.id.openEditText);
        closeEditText = (EditText) findViewById(R.id.closeEditText);
        lowEditText = (EditText) findViewById(R.id.lowEditText);
        highEditText = (EditText) findViewById(R.id.highEditText);
        checkedImg = (ImageView) findViewById(R.id.checkedImg);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        volumeEditText = (EditText) findViewById(R.id.volumeEditText);
        checkDataListView = (ListView) findViewById(R.id.checkDataListView);
    }

    private void setupClick(){
        checkedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = dateEditText.getText().toString();
                String volume = volumeEditText.getText().toString();
                String open = openEditText.getText().toString();
                String close = closeEditText.getText().toString();
                String low = lowEditText.getText().toString();
                String high = highEditText.getText().toString();
                uploadData(date, volume, open, close, low, high);
            }
        });
    }

    private void setupTool(){
        database = FirebaseDatabase.getInstance();
        dateRef = database.getReference("Date Data");
    }

    public void uploadData(String date, String volume, String open, String close, String low, String high){
        String stringDate = String.valueOf(date) + " GMT+08:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd z");
        String strDate = "";
        try {
            Date newDate = sdf.parse(stringDate);
            strDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, strDate, Toast.LENGTH_SHORT).show();
        if (date.isEmpty() || volume.isEmpty() || open.isEmpty() || close.isEmpty() || low.isEmpty() || high.isEmpty()){
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
            return;
        }
        int dateInt = Integer.parseInt(date);
        int volumeInt = Integer.parseInt(volume);
        int opneInt = Integer.parseInt(open);
        int closeItn = Integer.parseInt(close);
        int lowInt = Integer.parseInt(low);
        int highInt = Integer.parseInt(high);
        DateFBModel model = new DateFBModel();
        model.setDate(dateInt);
        model.setVolume(volumeInt);
        model.setOpen(opneInt);
        model.setClose(closeItn);
        model.setLow(lowInt);
        model.setHigh(highInt);
        model.setStrDate(strDate);
        dateRef.child(strDate).setValue(model);
    }
}
