package com.example.anthony.stock.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by chanyunyuen on 8/4/2017.
 */

public class BootCompletedReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, BootCompletedService.class);
        context.startService(serviceIntent);
    }
}
