package com.example.anthony.stock.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.anthony.stock.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by chanyunyuen on 2/4/2017.
 */

public class RefreshReceiver extends BroadcastReceiver{

    private static int hour = 0;
    private static int date = 0;
    private NotificationManager manager;
    private String TAG = "RefreshReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Date nowdate = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(nowdate);
        hour = calendar.get(Calendar.HOUR);
        date = calendar.get(Calendar.DATE);

        Toast.makeText(context, "hour: " +hour + "date: " + date,
                Toast.LENGTH_LONG).show();
        Log.i(TAG, "onReceive: " + "hour: " +hour + "date: " + date);

        if (hour != calendar.get(Calendar.HOUR)){

        }

        if (date != calendar.get(Calendar.DATE)){

        }

        if (manager == null){
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Buy / Sell")
                .setContentText("XXX meet target.")
                .setContentInfo("3")
                .setCategory(Notification.CATEGORY_ALARM)
                .setColor(Color.BLACK)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_MAX);
        Notification notification = builder.build();
        manager.notify(1, notification);
    }
}
