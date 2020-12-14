package com.example.trackmytrip;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Splash Screen
    private static int SPLASH_TIME_OUT = 2000;


    //Initialize Map Variables
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    Button startButton, stopButton, showTrips;
    Timer t = new Timer();
    Calendar c = Calendar.getInstance();
    GoogleMap mMap;
    LocationBroadcastReceiver locationBroadcastReceiver;

    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    TimerTask tt;
    protected LatLng latLng;
    private double latitude;
    private double longitude;
    private double velocity;
    LocationManager locationManager;
    ImageButton imageButton;
    public int spinnerValue = 1000;
    //pop window
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText tripName;
    private String stripName;
    private String var_tripName;
    private String var_Timer;
    private Button done;
    LatLng latlng;
    public ArrayList<String> locations;
    String[] timer = {"Choose time interval", "10 seconds", "30 seconds", "1 minutes", "5 minutes"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationBroadcastReceiver = new LocationBroadcastReceiver();

        //Assign Map variables
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync((OnMapReadyCallback) this);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            //requestlocation
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                // request permission for location
                // startService();
            }
        } else {
            //start service
            // startService();
        }

        //Initialize fused Location
        client = LocationServices.getFusedLocationProviderClient(this);

        //initialize buttons
        startButton = findViewById(R.id.start);
        stopButton = findViewById(R.id.stop);
        showTrips = findViewById(R.id.tripsDB);
        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();

            }
        });

        showTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TripRecyclerView.class);
                startActivity(intent);

            }
        });

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getCurrentLocation();
                dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                final View tripPopupView = getLayoutInflater().inflate(R.layout.popup, null);
                dialogBuilder.setTitle("Enter Trip Details");
                tripName = (EditText) tripPopupView.findViewById(R.id.TripName);
                stripName = tripName.getText().toString();
                done = (Button) tripPopupView.findViewById(R.id.doneButton);
                //Getting the instance of Spinner and applying OnItemSelectedListener on it
                Spinner spin = (Spinner) tripPopupView.findViewById(R.id.spinner);
                //spin.setOnItemSelectedListener(this);
                //Creating the ArrayAdapter instance having the bank name list
                ArrayAdapter aa = new ArrayAdapter(MainActivity.this, android.R.layout.simple_spinner_item, timer);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopButton.setEnabled(true);
                        startButton.setEnabled(false);
                        startService();

                        //define done button of popup
                        if (!(spin.getSelectedItem().toString().equalsIgnoreCase("Choose time interval") && stripName.matches(""))) {
                            var_tripName = tripName.getText().toString();

                            var_Timer = spin.getSelectedItem().toString();

                            dialog.dismiss();

                            switch (var_Timer) {
                                case "10 seconds":
                                    spinnerValue = 10000;
                                    break;

                                case "30 seconds":
                                    spinnerValue = 30000;
                                    break;

                                case "1 minutes":
                                    spinnerValue = 60000;
                                    break;

                                case "5 minutes":
                                    spinnerValue = 300000;
                                    break;

                                default:
                                    spinnerValue = 10000;
                                    break;
                            }
                            String f1 = df.format(c.getTime());
                            MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(MainActivity.this);
                            myDataBaseHelper.addTrip(var_tripName, f1);
                            locations = new ArrayList<>();

                            //creation of timer for updating every interval

                            tt = new TimerTask() {
                                @Override
                                public void run() {
                                    getCurrentLocation();
                                    String f2 = df.format(c.getTime());

                                    myDataBaseHelper.addLocation(var_tripName, latitude, longitude, f2, velocity);


                                }
                            };

                            t.scheduleAtFixedRate(tt, 0, spinnerValue);

                            Toast.makeText(getApplicationContext(), "Trip has Started!", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Details not entered", Toast.LENGTH_LONG).show();

                        }


                    }

                });

                dialogBuilder.setView(tripPopupView);
                dialog = dialogBuilder.create();
                dialog.show();


            }


        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                unregisterReceiver(locationBroadcastReceiver);
                if (tt == null) {
                    Toast.makeText(getApplicationContext(), "Trip should start to end!!", Toast.LENGTH_SHORT).show();
                } else {

                    //String f = df.format(c.getTime());
                    //MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(MainActivity.this);
                    //myDataBaseHelper.addLocation(f, locations, var_tripName);
                    tt.cancel();
                    Toast.makeText(getApplicationContext(), "Trip Ended!!", Toast.LENGTH_LONG).show();
                    mMap.clear();
                }
            }
        });

    }


    public void startService() {
        //register broadcast receiver
        IntentFilter intentFilter = new IntentFilter("ACT_LOC");
        registerReceiver(locationBroadcastReceiver, intentFilter);
        Intent intent = new Intent(MainActivity.this, LocationService.class);
        startService(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public class LocationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("ACT_LOC")) {
                double lat = intent.getDoubleExtra("latitude", 0f);
                double lon = intent.getDoubleExtra("longitude", 0f);
                if (mMap != null) {
                }
                //Toast.makeText(MainActivity.this,"Latitude is: "+ lat+" Longitude is :"+lon,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //marker design
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getCurrentLocation() {
        //Initialize Task Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {

                            latlng = new LatLng(location.getLatitude(), location.getLongitude());
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            velocity = location.getSpeed();
                            // Creating a marker
                            MarkerOptions options = new MarkerOptions()
                                    .position(latlng)
                                    .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.icon_marker));

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 19));

                            googleMap.addMarker(options);

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

}