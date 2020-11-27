package com.example.finalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class AudioDBOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "AudioDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "SAVED_AUDIO";
    public final static String COL_ID = "_id";
    public final static String COL_ALBUM_ID = "ALBUM_ID";
    public final static String COL_ARTIST_ID = "ARTIST_ID";
    public final static String COL_ALBUM_NAME = "ALBUM_NAME";
    public final static String COL_ARTIST_NAME = "ARTIST_NAME";
    public final static String COL_RELEASE_YEAR = "RELEASE_YEAR";
    public final static String COL_GENRE = "GENRE";
    public final static String COL_ALBUM_ART = "ALBUM_ART";

    public AudioDBOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ALBUM_ID + " text, " + COL_ARTIST_ID + " text, "
                + COL_ALBUM_NAME + " text, "  + COL_ARTIST_NAME + " text, "
                + COL_RELEASE_YEAR + " text, " + COL_GENRE + " text, "
                + COL_ALBUM_ART + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }
}
