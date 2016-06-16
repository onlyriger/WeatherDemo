package com.weather.app.btweather.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

/**
 * Created by riger on 2016/6/16.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        Intent i = new Intent(context,AutoUpdateReceiver.class);
        context.startService(i);
    }
}
