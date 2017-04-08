package com.example.anthony.stock.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.widget.Toast;

public class RefreshService extends Service {

    private RefreshReceiver timeTickReceiver;
    public RefreshService() {
    }

    @Override
    public void onCreate() {
        if (timeTickReceiver == null) {
            timeTickReceiver = new RefreshReceiver();
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Destory", Toast.LENGTH_LONG).show();
        unregisterReceiver(timeTickReceiver);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
