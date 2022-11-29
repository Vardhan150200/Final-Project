package com.example.nearblood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

//    private static final int REQUEST_CHECK_SETTINGS = 2 ;
    EditText etFullName, etMobile, etEmail, etPassword, etAge;
    String name, email, number, password, bloodgroup, age;
    Button btnSave;
    Spinner spinner;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    Double latitude,longitude;
    ProgressDialog progressDialog;
    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView textView;
    FirebaseAuth firebaseAuth;
    private LocationRequest locationRequest;
    private int i;
    private final int PERMISSION_REQUEST_CODE = 100;
    private final String[] foreground_location_permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION};


    private PermissionManager permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Register");
        progressDialog = new ProgressDialog(this);
        utilites.internetCheck(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        spinner = findViewById(R.id.spinnerBlood);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // get the location permissions for foreground
        permissionManager = PermissionManager.getInstance(this);
        if(!permissionManager.checkPermission(foreground_location_permissions)){
            permissionManager.askPermissions(MainActivity.this, foreground_location_permissions, 100);
        }

        if(permissionManager.checkPermission(foreground_location_permissions)){
            Toast.makeText(this, "entered", Toast.LENGTH_SHORT).show();
            if(isGPSEnabled()){
                //Toast.makeText(this, "entered2", Toast.LENGTH_SHORT).show();
                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                        .requestLocationUpdates(locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                        .removeLocationUpdates(this);

                                if (locationResult != null && locationResult.getLocations().size() >0){
                                    //Toast.makeText(MainActivity.this, "entered3", Toast.LENGTH_SHORT).show();
                                    int index = locationResult.getLocations().size() - 1;
                                     latitude = locationResult.getLocations().get(index).getLatitude();
                                     longitude = locationResult.getLocations().get(index).getLongitude();

                                    //Toast.makeText(MainActivity.this, " "+latitude+" "+longitude, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, Looper.getMainLooper());


            }else{
                enableGps();
            }
//            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                    if(location!=null){
//                        latitude = location.getLatitude();
//                        longitude = location.getLongitude();
//                    }
//                }
//            });

        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        etFullName = (EditText) findViewById(R.id.etFullName);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etAge = (EditText) findViewById(R.id.etAge);
        textView = findViewById(R.id.txtLogin);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent(MainActivity.this, LoginActivity.class);
                register.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(register);
            }
        });
        btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = etFullName.getText().toString().trim();
                email = etEmail.getText().toString().trim();
                number = etMobile.getText().toString().trim();
                age = etAge.getText().toString().trim();
                bloodgroup = spinner.getSelectedItem().toString();
                password = etPassword.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    etFullName.setError("Invalid Name");
                } else if (TextUtils.isEmpty(email)) {
                    if ((TextUtils.isEmpty(password)))
                        Toast.makeText(MainActivity.this, "Please Enter Details", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(MainActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();

                } else if (bloodgroup.equals("Select Your Blood Group")) {
                    Toast.makeText(MainActivity.this, "Select Your Blood Group", Toast.LENGTH_SHORT).show();
                }
                else if(age.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter Age !", Toast.LENGTH_SHORT).show();
                }
                else if (!age.isEmpty() ) {
                    try {
                        i = Integer.parseInt(age);
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if(i<18) {
                        etAge.setError("Age should be more than 18 yrs !");
                    } else
                    {
                        saveDate();
                    }
                }



            }
        });

    }

    private void enableGps() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled(){

        LocationManager locationManager=null;
        boolean isEnabled = false;

        if(locationManager == null){
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private void saveDate() {


        String id = myRef.push().getKey();

        Details details = new Details(id, name, email, number, password, bloodgroup,age, latitude, longitude,"0");
        assert id != null;
        myRef.child(id).setValue(details);

        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Email Already Exists.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//finish();
            onBackPressed();
        }
        return true;
    }
}