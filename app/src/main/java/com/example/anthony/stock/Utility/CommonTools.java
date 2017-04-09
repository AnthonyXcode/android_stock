package com.example.anthony.stock.Utility;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by chanyunyuen on 8/4/2017.
 */

public class CommonTools {
    public static boolean checkServiceRunning(Class target, Context context){
        ActivityManager am = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rs =
                am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo rsi : rs) {
            if (target.getName().equals(rsi.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
