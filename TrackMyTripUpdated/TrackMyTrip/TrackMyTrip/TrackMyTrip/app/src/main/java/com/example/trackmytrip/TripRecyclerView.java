package com.example.trackmytrip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TripRecyclerView extends AppCompatActivity {
    RecyclerView recyclerView;
    MyDataBaseHelper myDB;
    public String variable_tripname;
    ArrayList<String> tripname,tripdate;
    ArrayList<Double> triplatitude, triplongitude;
    public ArrayList<LatLng> actualLocations;

    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_recycler_view);
        recyclerView=findViewById(R.id.tripsView);
        Intent intent=getIntent();

        myDB = new MyDataBaseHelper(TripRecyclerView.this);
        tripname = new ArrayList<>();
        tripdate = new ArrayList<>();
        triplatitude = new ArrayList<>();
        triplongitude = new ArrayList<>();
        //tripdetails = new ArrayList<String>();
        //actualLocations=new ArrayList<>();


        displayData();



        customAdapter = new CustomAdapter(TripRecyclerView.this,tripname,tripdate,new ClickListerner(){
            @Override public void onPositionClicked(int position) {
                // callback performed on click

                Intent intent = new Intent(TripRecyclerView.this,MapsActivity.class);
                intent.putExtra("positionOfTrip", position);
                startActivity(intent);

            }
        });
        recyclerView.setAdapter(customAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(TripRecyclerView.this));
    }
    void displayData(){
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount()==0)
        {
            Toast.makeText(this,"No Data",Toast.LENGTH_LONG).show();
        }
        else
        {
            while (cursor.moveToNext()){
                tripdate.add(cursor.getString(1));
                tripname.add(cursor.getString(0));
                variable_tripname=cursor.getString(0);

                displayLocations(variable_tripname);

                //tripdetails.add(cursor.getString(2));
            }
        }
    }

    void displayLocations(String variable_tripname){
        //var_tn=variable_tripname;

        Cursor cursor = myDB.readAllLocations(variable_tripname);
        if (cursor.getCount()==0)
        {
            Toast.makeText(this,"No Data in Array",Toast.LENGTH_LONG).show();
        }
        else
        {
            while (cursor.moveToNext()){
                //tripdate.add(cursor.getString(0));
                //tripname.add(cursor.getString(1));
                triplatitude.add(cursor.getDouble(2));
                triplongitude.add(cursor.getDouble(3));

                //tripdetails.add(cursor.getString(2));
            }
        }
    }


}