package com.example.trackmytrip;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



public class MainActivity extends AppCompatActivity {
    //Initialize Map Variables
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    Button startButton;
    Timer t = new Timer();
    protected LatLng latLng;
    //pop window
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText tripName;
    private String stripName;
    private String var_tripName;
    private String var_Timer;
    private Button done;
    String[] timer = {"Choose time interval","10 seconds", "30 seconds", "1 minutes", "5 minutes"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign Map variables
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        //Initialize fused Location
        client = LocationServices.getFusedLocationProviderClient(this);

        //initialize start button
        startButton = findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // createNewTripPopup();
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
                ArrayAdapter aa = new ArrayAdapter(MainActivity.this,android.R.layout.simple_spinner_item,timer);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //define done button of popup
                        if(!(spin.getSelectedItem().toString().equalsIgnoreCase("Choose time interval") && stripName.matches(""))){
                            var_tripName=tripName.getText().toString();
                            var_Timer=spin.getSelectedItem().toString();
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Trip has Started!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Details not entered", Toast.LENGTH_LONG).show();
                        }

                    }
                });

                dialogBuilder.setView(tripPopupView);
                dialog=dialogBuilder.create();
                dialog.show();

                MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(MainActivity.this);

                //creation of timer for updating every interval

                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {

                        myDataBaseHelper.addLocation(Calendar.getInstance().getTime().toString(),latLng);

                    }
                };
                t.schedule(tt,60000);
            }
        });

        //Check Permissions
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            //When permission denied , request for it
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    private void getCurrentLocation() {
        //Initialize Task Location

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());

                            MarkerOptions options = new MarkerOptions().position(latlng).title("I am here");

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));

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
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

}