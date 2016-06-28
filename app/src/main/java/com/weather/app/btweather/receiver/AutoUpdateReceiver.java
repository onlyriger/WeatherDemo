package com.weather.app.btweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.weather.app.btweather.service.AutoUpdateService;

/**
 * Created by riger on 2016/6/16.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
