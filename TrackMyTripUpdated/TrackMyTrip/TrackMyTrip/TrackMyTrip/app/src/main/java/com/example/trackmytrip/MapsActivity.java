package com.example.trackmytrip;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Double.parseDouble;
import static java.util.Collections.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    FusedLocationProviderClient client;
    private Context mContext;
    private EditText searchPlaceEt;
    private Button saveLocationBtn;
    private GoogleMap mMap;
    public String variable_tripname;
    private GoogleApiClient mGoogleApiClient;
    MyDataBaseHelper myDB;
    ArrayList<String> tripname, tripdate;
    ArrayList<Double> triplatitude;
    ArrayList<Double> triplongitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //Initialize fused Location
        client = LocationServices.getFusedLocationProviderClient(this);


        //get data from database


        myDB = new MyDataBaseHelper(MapsActivity.this);
        tripname = new ArrayList<>();
        tripdate = new ArrayList<>();
        triplatitude = new ArrayList<Double>();
        triplongitude = new ArrayList<Double>();

        //sync of the database to the respective array list
        displayData();


        //positionOfTrip = Integer.valueOf(string);
        //checking for trips context


        mContext = MapsActivity.this;

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted


                if (mMap != null)
                    mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                //checkLocationPermission();
            }
        }
    }

    private void displayData() {
        Cursor cursor = myDB.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No Data", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                tripname.add(cursor.getString(0));
                variable_tripname=cursor.getString(0);

                displayLocations(variable_tripname);

                tripdate.add(cursor.getString(1));
                //tripdetails.add(cursor.getString(2));
            }
        }
    }

    private void displayLocations(String variable_tripname) {
        //var_tn=variable_tripname;
        Cursor c = myDB.readAllLocations(variable_tripname);
        if (c.getCount() == 0) {
            Toast.makeText(this, "No Data in array", Toast.LENGTH_LONG).show();
        } else {
            while (c.moveToNext()) {
                //tripname.add(c.getString(0));
                //tripdate.add(c.getString(1));
                triplatitude.add(c.getDouble(2));
                triplongitude.add(c.getDouble(3));
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        //receiving the intent from recyclerview on click of a button
        //ArrayList<Double> latitude = new ArrayList<Double>();
        //ArrayList<Double> longitude = new ArrayList<Double>();
        double getLatitude, getLongitude;
        ArrayList<LatLng> latsAndLong = new ArrayList<>();

        Intent intent = getIntent();
        int string = intent.getIntExtra("positionOfTrip", 0);

        //checking the position received
        //Toast.makeText(getApplicationContext(), "The Received Position is : " + string, Toast.LENGTH_LONG).show();

        //System.out.println(triplatitude.get(string));
        //System.out.println(triplongitude.get(string));
        Log.d("triplatitude",triplatitude.toString());
        //copy(latitude,triplatitude);
        //Log.d("latitude",latitude.toString());
        //copy(longitude,triplongitude);

//        latitude=triplatitude;
//        longitude=triplongitude;

//        String coordinates = tripdetails.get(string);
//        coordinates = coordinates.replaceAll("\\[", "");
//        coordinates = coordinates.replaceAll("]", "");
//        coordinates = coordinates.replaceAll(" ", "");
//
//        String[] arrSplit = coordinates.split(",");
//        for (int i = 0; i < arrSplit.length; i++) {
//            if (i % 2 == 0) {
//
//                latitude.add(parseDouble(arrSplit[i]));
//                System.out.println(arrSplit[i]);
//
//
//            } else {
//                longitude.add(parseDouble(arrSplit[i]));
//                System.out.println(arrSplit[i]);
//            }

//        }

        System.out.println(triplatitude.size());
        System.out.println(triplongitude.size());
//
//
        mMap = googleMap;
//
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
        for (int i = 0; i < triplatitude.size(); i++) {
            getLatitude = triplatitude.get(i);
            getLongitude = triplongitude.get(i);

            LatLng latLng = new LatLng(getLatitude, getLongitude);
            System.out.println(latLng.latitude);
            System.out.println(latLng.longitude);
            latsAndLong.add(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latsAndLong.get(0), 19));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            mMap.setBuildingsEnabled(true);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latsAndLong.get(0), 18));
        MarkerOptions optionsfirst = new MarkerOptions().position(latsAndLong.get(0)).title("This is first location");
        mMap.addMarker(optionsfirst);
        MarkerOptions optionslast = new MarkerOptions().position(latsAndLong.get(latsAndLong.size() - 1)).title("This is last location");
        mMap.addMarker(optionslast);


        for (int j = 0; j < latsAndLong.size() - 1; j++) {
            PolylineOptions line = new PolylineOptions().add((latsAndLong.get(j)), latsAndLong.get(j + 1)).width(10).color(Color.RED);
            mMap.addPolyline(line);
        }


    }

      @Override
      public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

      }
}
