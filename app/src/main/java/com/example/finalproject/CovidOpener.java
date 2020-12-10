package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class CovidOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "CovidDB";
    protected final static int VERSION_NUM = 1;
    public static final String TABLE_NAME = "COVID_DATA";
    public static final String COL_COUNTRY = "COUNTRY";
    public static final String COL_COUNTRYCODE = "COUNTRY_CODE";
    public static final String COL_PROVINCE = "PROVINCE";
    public static final String COL_CITY = "CITY";
    public static final String COL_CITYCODE = "CITY_CODE";
    public static final String COL_LAT = "LAT";
    public static final String COL_LON = "LON";
    public static final String COL_CASES = "CASES";
    public static final String COL_STATUS = "STATUS";
    public static final String COL_DATE = "DATE";
    public static final String COL_ID = "_id";

    public CovidOpener(Context ctx) { super(ctx, DATABASE_NAME, null, VERSION_NUM); }

    @Override
    public void onCreate(SQLiteDatabase cdb) {
        cdb.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_COUNTRY + " text,"
                + COL_COUNTRYCODE + " text,"
                + COL_PROVINCE + " text,"
                + COL_CITY + " text,"
                + COL_CASES + " text,"
                + COL_DATE + " text);");
    }


    @Override
    public void onUpgrade(SQLiteDatabase cdb, int oldVersion, int newVersion) {
        cdb.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(cdb);
    }

    @Override
    public void onDowngrade(SQLiteDatabase cdb, int oldVersion, int newVersion) {
        cdb.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(cdb);
    }
}
