package com.weather.app.btweather.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import com.weather.app.btweather.db.BtWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riger on 2016/6/13.
 */
public class BtWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "Bt_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static BtWeatherDB btWeatherDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private BtWeatherDB(Context context){
        BtWeatherOpenHelper dbHelper = new BtWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取BtWeatherDB的实例
     */
    public synchronized static BtWeatherDB getInstance(Context context){
        if (btWeatherDB == null){
            btWeatherDB = new BtWeatherDB(context);
        }
        return btWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库获取全国所有省份信息
     */
    public List<Province> loadProvince(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while(cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 将数据库读取某省下所有的城市信息
     */
    public List<City> loadCities(int provinceID){
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City",null,"province_id = ?",
                new String[]{String.valueOf(provinceID)},null,null,null);

        if (cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceID);
                list.add(city);
            }while (cursor.moveToNext());
        }
        if (cursor != null){
            cursor.close();
        }
        return list;
    }
}
