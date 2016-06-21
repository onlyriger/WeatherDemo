package com.weather.app.btweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by riger on 2016/6/21.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "citychina.db";
    private static final int VERSION = 1;
    Context mContext = null;
    DatabaseHelper mDataHelper = null;
    public static DatabaseHelper mInstance = null;

    public static synchronized DatabaseHelper getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DatabaseHelper(context,null);
        }
        return mInstance;
    }
    /**
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数
     * @param context   上下文对象
     * @param name      数据库名称
     * @param factory
     * @param version   当前数据库的版本，值必须是整数并且是递增的状态
     */

    public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
        //必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version){
        this(context,name,null,version);
    }

    public DatabaseHelper(Context context, String name){
        this(context,name,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("create a database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("upgrade a database");
    }
}
