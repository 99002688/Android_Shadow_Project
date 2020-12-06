package com.example.trackmytrip;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "Trips.db";
    private static final int DATABASE_VERSION = 1;

    public static String TABLE_NAME ="my_table";
    public static String DATE="my_date";
    public static String LOCATION="my_location";




    public MyDataBaseHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + DATE + " TEXT, " +
                LOCATION + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    void addLocation(String date, LatLng location){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(DATE, date);
        cv.put(LOCATION, String.valueOf(location));
        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }

    }
}
