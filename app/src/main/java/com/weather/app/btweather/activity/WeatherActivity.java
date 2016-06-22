package com.weather.app.btweather.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socks.library.KLog;
import com.weather.app.btweather.R;
import com.weather.app.btweather.db.DatabaseHelper;
import com.weather.app.btweather.service.AutoUpdateService;
import com.weather.app.btweather.util.HttpCallbackListener;
import com.weather.app.btweather.util.HttpUtil;
import com.weather.app.btweather.util.Utility;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by riger on 2016/6/16.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private static final String TAG = WeatherActivity.class.getSimpleName();
    private LinearLayout weatherInfoLayout;

    //用于显示城市名
    private TextView cityNameText;

    //用于显示发布时间
    private TextView publishText;

    //用于显示天气描述信息
    private TextView weatherDespText;

    //用于显示气温1
    private TextView temp1Text;

    //用于显示气温2
    private TextView temp2Text;

    //用于显示当期日期
    private TextView currentDateText;

    /**
     * 切换城市按钮
     */
    private Button switchCity;

    /**
     * 更新天气按钮
     */
    private Button refreshWeather;

    /**
     * 数据库
     */
    SQLiteDatabase db = null;

    public static final String DATABASE_FILENAME = "citychina1";
    public static final String PACKAGE_NAME = "com.weather.db1";
    public static final String DATABASE_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() +
            "/" + PACKAGE_NAME;


    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        // 初始化控件
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            // 有县级代号时就去查询空气
            KLog.v(TAG, "1111");
            publishText.setText("同步中...");
            KLog.v(TAG, "2222");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            KLog.v(TAG, "33333");
            queryWeatherCode(countyCode);
            KLog.v(TAG, "44444");
        } else {
            // 没有县级代号时就直接显示本地天气
            showWeather();
        }
    }

    /**
     * 查询县级代号所对应的天气代号
     */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        KLog.v(TAG, "ADDRESS = " + address);

        //queryFromServer(address, "countyCode");
        initWeatherData(countyCode);
    }

    /**
     * 从数据库读取县对应的天气代号查询天气信息
     */
    private void initWeatherData(final String wt_id) {
//        String weatherdata = null;
//        DatabaseHelper helper = new DatabaseHelper(WeatherActivity.this, "citychina");
//        SQLiteDatabase sqLiteDatabase = helper.getReadableDatabase();
//
//        Cursor cursor = sqLiteDatabase.query("city_table", new String[]{"CITY_ID"}, wt_id, null, null, null, null);
//        while (cursor.moveToNext()) {
//            weatherdata = cursor.getString(cursor.getColumnIndex("WEATHER_ID"));
//        }
//        KLog.v(TAG,"weatherdata ============ " + weatherdata);
//        queryWeatherInfo(weatherdata);

        String weatherdata = null;
        String selection = "CITY_ID=?" ;
        String[] selectionArgs = new  String[]{ wt_id };
        copyDataBase();

        Cursor cursor = db.query("city_table",new String[]{"WEATHER_ID"},selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()){
            weatherdata = cursor.getString(0);
            KLog.v(TAG,"succeed  = " + weatherdata);
            if (wt_id == weatherdata){
                //weatherdata = cursor.getString(cursor.getColumnIndex("WEATHER_ID"));
                break;
            }
        }
        KLog.v(TAG,"weatherdata ============ " + weatherdata);
        String wtData = (String) weatherdata;
        queryWeatherInfo(wtData);
        KLog.v(TAG,"end ====  ");
    }

    private void copyDataBase(){
        try {
//            String weatherfileName = DATABASE_PATH + "/" + DATABASE_FILENAME;
            String weatherfileName = getCacheDir() + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists()){
                KLog.v(TAG,"dir == " + dir);
                dir.mkdir();
            }
          //  if (!(new File(weatherfileName)).exists()) {
                KLog.v(TAG,"weatherfileName == " + weatherfileName);
                try {
                InputStream is = this.getResources().openRawResource(R.raw.citychina);
                KLog.v(TAG,"XXXXXXX");
                FileOutputStream fos = new FileOutputStream(weatherfileName);
                KLog.v(TAG,"aaaaaa");
                byte[] buffer = new byte[8192];
                KLog.v(TAG,"yyyyyyy");
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    KLog.v(TAG,"MMMMMM");
                    fos.write(buffer,0,count);
                }
                KLog.v(TAG,"bbbbbb");
                fos.close();
                is.close();
                } catch (Exception e ){
                    KLog.v(TAG,"e  == " + e.toString());
                }
          //  }
            KLog.v(TAG,"CCCCCC");
            db = SQLiteDatabase.openOrCreateDatabase(
                    weatherfileName, null);
            KLog.v(TAG,"wtfilename = " + weatherfileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询天气代号对应的天气
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        //String address = "http://www.weather.com.cn/adat/cityinfo/" + weatherCode + ".html";
        KLog.v(TAG,"query address = " + address);
        queryFromServer(address, "weatherCode");
        KLog.v(TAG,"queryFromServer end");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                KLog.v("query from server onFinish 111111111");
                if ("countyCode".equals(type)) {
                    // 从服务器返回的数据中解析出天气代号
                    String[] array = response.split("\\|");
                    if (array != null && array.length == 2) {
                        String weatherCode = array[1];
                        queryWeatherInfo(weatherCode);
                    } else if ("weatherCode".equals(type)) {
                        KLog.v("query from server onFinish 222222");
                        KLog.v("weather start");
                        Utility.handleWeatherResponse(WeatherActivity.this, response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });

            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);

        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                //intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...==");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }
}
