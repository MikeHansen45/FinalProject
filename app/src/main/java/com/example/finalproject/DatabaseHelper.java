package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class DatabaseHelper extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "EventDb";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "EVENTS";
    public final static String COL_ID = "_id";
    public final static String COL_NAME = "name";
    public final static String COL_TYPE = "type";
    public final static String COL_URL = "URL";
    public final static String COL_MAX = "max";
    public final static String COL_MIN = "min";
    public final static String COL_DATE = "dated";



    public DatabaseHelper(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " text,"
                + COL_TYPE  + " text,"
                + COL_URL + " text,"
                + COL_MAX + " int,"
                + COL_DATE + " text,"
                + COL_MIN + " int);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    public boolean addData(Event item){
        SQLiteDatabase db = this.getWritableDatabase();
        return true;
    }
}
