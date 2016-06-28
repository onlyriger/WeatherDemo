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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.socks.library.KLog;
import com.socks.library.klog.JsonLog;
import com.weather.app.btweather.R;
import com.weather.app.btweather.db.DatabaseHelper;
import com.weather.app.btweather.service.AutoUpdateService;
import com.weather.app.btweather.util.HttpCallbackListener;
import com.weather.app.btweather.util.HttpUtil;
import com.weather.app.btweather.util.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Delayed;

/**
 * Created by riger on 2016/6/16.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {

    private static final String TAG = WeatherActivity.class.getSimpleName();
    private LinearLayout weatherInfoLayout;

    // 用于显示城市名
    private TextView cityNameText;

    // 用于显示发布时间
    private TextView publishText;

    // 用于显示天气描述信息
    private TextView weatherDespText;

    // 用于显示气温1
    private TextView temp1Text;

    // 用于显示气温2
    private TextView temp2Text;

    // 用于显示当期日期
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
     * SQLite数据库
     */
    SQLiteDatabase db = null;

    Boolean flag = true;
    String address_str = null;
    private String cityName = null;
    private String publishTime = null;
    private String nowTime = null;
    private String weatherDesp = null;
    private String temp1 = null;
    private String temp2 = null;
    private View view;
    public static final String DATABASE_FILENAME = "citychina1";
    public static final String PACKAGE_NAME = "com.weather.db1";
    public static final String DATABASE_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() +
            "/" + PACKAGE_NAME;

    /**
     * 枚举各种天气情况
     */
    private enum WeatherKind {
        cloudy, fog, hailstone, light_rain, moderte_rain, overcast, rain_snow, sand_storm, rainstorm,
        shower_rain, snow, sunny, thundershower,allwt;
    }

    private static Map<String,WeatherKind> weather_kind = new HashMap<String,WeatherKind>();
    static {
        weather_kind.put("多云",WeatherKind.cloudy);
        weather_kind.put("雾", WeatherKind.fog);
        weather_kind.put("冰雹", WeatherKind.hailstone);
        weather_kind.put("小雨", WeatherKind.light_rain);
        weather_kind.put("中雨",WeatherKind.moderte_rain);
        weather_kind.put("阴",WeatherKind.overcast);
        weather_kind.put("雨加雪",WeatherKind.rain_snow);
        weather_kind.put("沙尘暴",WeatherKind.sand_storm);
        weather_kind.put("暴雨",WeatherKind.rainstorm);
        weather_kind.put("阵雨",WeatherKind.shower_rain);
        weather_kind.put("小雪",WeatherKind.snow);
        weather_kind.put("晴",WeatherKind.sunny);
        weather_kind.put("雷阵雨",WeatherKind.thundershower);
        weather_kind.put("晴转阴",WeatherKind.allwt);
        weather_kind.put("晴转多云",WeatherKind.allwt);
        weather_kind.put("晴转小雨",WeatherKind.allwt);
        weather_kind.put("晴转中雨",WeatherKind.allwt);
        weather_kind.put("晴转大雨",WeatherKind.allwt);
        weather_kind.put("晴转阵雨",WeatherKind.allwt);
        weather_kind.put("晴转雷阵雨",WeatherKind.allwt);
        weather_kind.put("晴转小雪",WeatherKind.allwt);
        weather_kind.put("晴转中雪",WeatherKind.allwt);
        weather_kind.put("晴转大雪",WeatherKind.allwt);
        weather_kind.put("阴转晴",WeatherKind.allwt);
        weather_kind.put("阴转多云",WeatherKind.allwt);
        weather_kind.put("阴转小雨",WeatherKind.allwt);
        weather_kind.put("阴转中雨",WeatherKind.allwt);
        weather_kind.put("阴转大雨",WeatherKind.allwt);
        weather_kind.put("阴转阵雨",WeatherKind.allwt);
        weather_kind.put("阴转雷阵雨",WeatherKind.allwt);
        weather_kind.put("阴转小雪",WeatherKind.allwt);
        weather_kind.put("阴转中雪",WeatherKind.allwt);
        weather_kind.put("阴转大雪",WeatherKind.allwt);
        weather_kind.put("多云转晴",WeatherKind.allwt);
        weather_kind.put("多云转阴",WeatherKind.allwt);
        weather_kind.put("多云转小雨",WeatherKind.allwt);
        weather_kind.put("多云转中雨",WeatherKind.allwt);
        weather_kind.put("多云转大雨",WeatherKind.allwt);
        weather_kind.put("多云转阵雨",WeatherKind.allwt);
        weather_kind.put("多云转雷阵雨",WeatherKind.allwt);
        weather_kind.put("多云转小雪",WeatherKind.allwt);
        weather_kind.put("多云转中雪",WeatherKind.allwt);
        weather_kind.put("多云转大雪",WeatherKind.allwt);
        weather_kind.put("小雨转晴",WeatherKind.allwt);
        weather_kind.put("小雨转阴",WeatherKind.allwt);
        weather_kind.put("小雨转多云",WeatherKind.allwt);
        weather_kind.put("小雨转中雨",WeatherKind.allwt);
        weather_kind.put("小雨转大雨",WeatherKind.allwt);
        weather_kind.put("中雨转小雨",WeatherKind.allwt);
        weather_kind.put("中雨转大雨",WeatherKind.allwt);
        weather_kind.put("大雨转中雨",WeatherKind.allwt);
        weather_kind.put("大雨转小雨",WeatherKind.allwt);
        weather_kind.put("阵雨转小雨",WeatherKind.allwt);
        weather_kind.put("阵雨转中雨",WeatherKind.allwt);
        weather_kind.put("阵雨转多云",WeatherKind.allwt);
        weather_kind.put("阵雨转晴",WeatherKind.allwt);
        weather_kind.put("阵雨转阴",WeatherKind.allwt);
        weather_kind.put("中雪转小雪",WeatherKind.allwt);
        weather_kind.put("中雪转大雪",WeatherKind.allwt);
        weather_kind.put("小雪转大雪",WeatherKind.allwt);
        weather_kind.put("小雪转中雪",WeatherKind.allwt);
        weather_kind.put("小雪转晴",WeatherKind.allwt);
        weather_kind.put("小雪转阴",WeatherKind.allwt);
        weather_kind.put("小雪转多云",WeatherKind.allwt);
        weather_kind.put("大雪转小雪",WeatherKind.allwt);
        weather_kind.put("大雪转中雪",WeatherKind.allwt);
        weather_kind.put("雾转小雨",WeatherKind.allwt);
        weather_kind.put("雾转中雨",WeatherKind.allwt);
        weather_kind.put("雾转大雨",WeatherKind.allwt);
    }

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
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            address_str = initWeatherData(countyCode);
            queryWeatherChangeInfo(address_str);
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
        queryFromServer(address, "countyCode");
    }

    /**
     * 从数据库读取县对应的天气代号
     */
    private String initWeatherData(final String wt_id) {
        String weatherdata = null;
        String result_str = null;
        String selection = "CITY_ID=?" ;
        String[] selectionArgs = new  String[]{ wt_id };
        // 导入外部数据库复制到手机内存
        copyDataBase();

        Cursor cursor = db.query("city_table",new String[]{"WEATHER_ID"},selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()){
            weatherdata = cursor.getString(0);
            if (wt_id == weatherdata){
                break;
            }
        }
        result_str = weatherdata;
        return result_str;
    }

    /**
     * 复制工程raw目录下数据库文件到手机内存里
     */
    private void copyDataBase() {
        try {
            String weatherfileName = getCacheDir() + "/" + DATABASE_FILENAME;
            File dir = new File(DATABASE_PATH);
            if (!dir.exists()) {
                dir.mkdir();
            }
            try {
                InputStream is = this.getResources().openRawResource(R.raw.citychina);
                FileOutputStream fos = new FileOutputStream(weatherfileName);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 根据数据库文件路径打开数据库
            db = SQLiteDatabase.openOrCreateDatabase(
                    weatherfileName, null);
            if (db != null) {
                KLog.v(TAG,"db build success!");
            } else {
                KLog.v(TAG,"db build failed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询天气代号对应的天气
     */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/adat/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /**
     * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    // 从服务器返回的数据中解析出天气代号
                    String[] array = response.split("\\|");
                    if (array != null && array.length == 2) {
                        String weatherCode = array[1];
                        queryWeatherInfo(weatherCode);
                    }
                    else if ("weatherCode".equals(type)) {
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

    private void queryWeatherChangeInfo(String str) {
        String address = "http://www.weather.com.cn/adat/cityinfo/" + str + ".html";
        queryWeather(address);
    }

    /**
     *  根据传入的地址请求网络并解析返回的json数据
     */
    private void queryWeather(final String address) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject weatherinfo = jsonObject.getJSONObject("weatherinfo");
                    cityName = weatherinfo.getString("city");
                    publishTime = weatherinfo.getString("ptime");
                    weatherDesp = weatherinfo.getString("weather");
                    temp1 = weatherinfo.getString("temp1");
                    temp2 = weatherinfo.getString("temp2");
                    SharedPreferences prefs = getSharedPreferences("weathter_data", WeatherActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("city", cityName);
                    editor.putString("ptime", publishTime);
                    editor.putString("weather", weatherDesp);
                    editor.putString("temp1", temp1);
                    editor.putString("temp2", temp2);
                    editor.commit();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showChangeWt();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Exception e) {
                Toast.makeText(WeatherActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 读取天气信息显示到界面上
     */
    private void showChangeWt() {
        SharedPreferences prefs = getSharedPreferences("weathter_data", WeatherActivity.MODE_PRIVATE);
        cityNameText.setText(prefs.getString("city", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather", ""));
        publishText.setText("今天" + prefs.getString("ptime", "") + "发布");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String date = sdf.format(new java.util.Date());
        nowTime = date;
        currentDateText.setText(nowTime);
        WeatherKind myWeather = weather_kind.get(weatherDesp);
        if (myWeather != null) {
            changeBackground(myWeather);
        } else {
            changeBackground(WeatherKind.allwt);
        }
        currentDateText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String date = sdf.format(new java.util.Date());
        nowTime = date;
        currentDateText.setText(nowTime);
        WeatherKind myWeather = weather_kind.get(weatherDesp);
        if (myWeather != null) {
            changeBackground(myWeather);
        } else {
            changeBackground(WeatherKind.allwt);
        }
        currentDateText.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 设置对应天气背景图
     */
    private void changeBackground(WeatherKind weather){
        view = findViewById(R.id.weather_background);
        switch (weather){
            case cloudy:
                view.setBackgroundResource(R.drawable.cloudy);
                break;
            case fog:
                view.setBackgroundResource(R.drawable.fog);
                break;
            case hailstone:
                view.setBackgroundResource(R.drawable.hailstone);
                break;
            case light_rain:
                view.setBackgroundResource(R.drawable.light_rain);
                break;
            case moderte_rain:
                view.setBackgroundResource(R.drawable.moderte_rain);
                break;
            case overcast:
                view.setBackgroundResource(R.drawable.overcast);
                break;
            case rain_snow:
                view.setBackgroundResource(R.drawable.rain_snow);
                break;
            case sand_storm:
                view.setBackgroundResource(R.drawable.sand_storm);
                break;
            case rainstorm:
                view.setBackgroundResource(R.drawable.rainstorm);
                break;
            case shower_rain:
                view.setBackgroundResource(R.drawable.shower_rain);
                break;
            case snow:
                view.setBackgroundResource(R.drawable.snow);
                break;
            case sunny:
                view.setBackgroundResource(R.drawable.sunny);
                break;
            case thundershower:
                view.setBackgroundResource(R.drawable.thundershower);
                break;
            case allwt:
                view.setBackgroundResource(R.drawable.allwt);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(ChooseAreaActivity.RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String weatherCode = prefs.getString("weather_code", "");
                        if (!TextUtils.isEmpty(weatherCode)) {
                            queryWeatherChangeInfo(weatherCode);
                        }
                        else {
                            publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
                        }
                    }
                },3000);
                break;
            default:
                break;
        }
    }
}
