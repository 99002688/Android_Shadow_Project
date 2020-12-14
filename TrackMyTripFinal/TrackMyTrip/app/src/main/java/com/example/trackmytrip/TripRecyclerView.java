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
    ArrayList<String> tripname, tripdate;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_recycler_view);
        recyclerView = findViewById(R.id.tripsView);
        Intent intent = getIntent();

        myDB = new MyDataBaseHelper(TripRecyclerView.this);
        tripname = new ArrayList<>();
        tripdate = new ArrayList<>();

        displayData();

        customAdapter = new CustomAdapter(TripRecyclerView.this, tripname, tripdate, new ClickListerner() {
            @Override
            public void onPositionClicked(int position) {
                // callback performed on click
                Intent intent = new Intent(TripRecyclerView.this, MapsActivity.class);
                intent.putExtra("positionOfTrip", position);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(customAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(TripRecyclerView.this));
    }

    void displayData() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Data", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                tripdate.add(cursor.getString(1));
                tripname.add(cursor.getString(0));
            }
        }
    }
}