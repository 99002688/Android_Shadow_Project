package com.example.trackmytrip;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MyDataBaseHelper extends SQLiteOpenHelper {
    private Context context;

    public static String DATABASE_NAME = "ActualTrips.db";
    public static int DATABASE_VERSION = 1;


    public String TABLE_NAME = "my_table";
    public String TABLE_NAME_2 = "my_table2";
    public String TRIPNAME = "my_name";
    public String DATE = "my_date";
    //public String LOCATION = "my_location";
    public String LATITUDE ="my_latitude";
    public String LONGITUDE ="my_longitude";
    public String VELOCITY ="my_velocity";

    private SQLiteDatabase db;


    public MyDataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String query = "CREATE TABLE " + TABLE_NAME +
//                " (" + DATE + " TEXT, " + LATITUDE +" double, " +  LONGITUDE +" double, " + VELOCITY + " double );";
//        db.execSQL(query);

        String query = "CREATE TABLE "
                + TABLE_NAME + "("
                + TRIPNAME + " TEXT, "
                + DATE + " TEXT, "
                + LATITUDE +" double, "
                + LONGITUDE + " double, "
                + VELOCITY + " double );";

        String query_2 = "CREATE TABLE "
                + TABLE_NAME_2 + "("
                + TRIPNAME + " TEXT, "
                + DATE + " TEXT );";

        db.execSQL(query);
        db.execSQL(query_2);

    }

//    public long insert(ContentValues contentValues){
//
//        long rowID = db.insert(TABLE_NAME, null, contentValues);
//        return rowID;
//    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
    }

    void addLocation(String tripName, double latitude, double longitude, String date, double velocity) {

        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRIPNAME, tripName);

        //cv.put(LOCATION, String.valueOf(location));
        cv.put(LATITUDE, String.valueOf(latitude));
        cv.put(LONGITUDE, String.valueOf(longitude));
        cv.put(DATE, date);
        cv.put(VELOCITY, String.valueOf(velocity));

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }

    }

    void addTrip(String tripName, String date){
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TRIPNAME, tripName);
        cv.put(DATE, date);

        long result = db.insert(TABLE_NAME_2, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    Cursor readAllData() {

        String query = "SELECT * FROM " + TABLE_NAME_2;

        //Cursor c= db.("SELECT * FROM my_table WHERE my_name ='?'" +tripName);
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }

    Cursor readAllLocations(String tripName) {

        String query = "SELECT * FROM  my_table WHERE my_name='"+tripName+"'";

        //Cursor cursor= db.query(TABLE_NAME,"SELECT * FROM my_table WHERE my_name = '+tripName+'");
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
}
