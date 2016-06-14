package com.weather.app.btweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.weather.app.btweather.model.BtWeatherDB;
import com.weather.app.btweather.model.City;
import com.weather.app.btweather.model.County;
import com.weather.app.btweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riger on 2016/6/14.
 */
public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private BtWeatherDB btWeatherDB;
    private List<String> dataList = new ArrayList<String>();

    //省列表
    private List<Province> provinceList;

    //市列表
    private List<City> cityList;

    //县列表
    private List<County> countyList;

    //选中的省份
    private Province selectedProvince;

    //选中的城市
    private City selectCity;

    //当前选中的级别
    private int currentLevel;


}
