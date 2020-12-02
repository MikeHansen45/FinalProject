package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyRecipeOpener extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "RECIPE_FAVS";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_URL = "URL";
    public static final String COL_INGREDIENTS = "INGREDIENTS";
    public static final String COL_THUMBNAIL = "THUMBNAIL";
    public static final String DATABASE_NAME = "RecipeDB";
    public static final int VERSION_NUM = 1;
    public static final String COL_ID = "_id";

    public MyRecipeOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_URL + " text,"
                + COL_INGREDIENTS + " text,"
                + COL_THUMBNAIL + " text);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }
}
