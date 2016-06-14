package com.weather.app.btweather.util;

/**
 * Created by riger on 2016/6/14.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
