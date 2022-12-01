package com.example.nearblood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class Mapsnavigation extends AppCompatActivity {

    private double receiverLat,receiverLon,currentUserLat,currentUserLon;
    private DatabaseReference myRef;
    private String param2;
    private Uri uri;
    private MyAppPrefsManager myAppPrefsManager;
    Location CurrentLocation,OtherUserLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsnavigation);
        myAppPrefsManager=new MyAppPrefsManager(this);
        myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRef.keepSynced(true);

        uri = getIntent().getData();
        if (uri != null) {

            // if the uri is not null then we are getting
            // the path segments and storing it in list.
            List<String> parameters = uri.getPathSegments();

            // after that we are extracting string
            // from that parameters.
            receiverLat = Double.parseDouble(parameters.get(0));
            receiverLon = Double.parseDouble(parameters.get(1));
            param2 = parameters.get(2);


        }
        Query query = myRef.orderByChild("email").equalTo(param2);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Toast.makeText(Mapsnavigation.this, ""+(snapshot.getChildren(), Toast.LENGTH_SHORT).show();
                if(snapshot.exists()){
                    for(DataSnapshot issue: snapshot.getChildren()){
                        //Toast.makeText(Mapsnavigation.this, ""+issue.getRef(), Toast.LENGTH_SHORT).show();
                        myRef = issue.getRef();
                        Details details = issue.getValue(Details.class);
                        assert details != null;
                        if (!Objects.equals(details.getBloodStatus(), "1")){

                            myRef.child("bloodStatus").setValue("1");
                        }
                        else{
                            Toast.makeText(Mapsnavigation.this, "Donor Already Assigned", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Mapsnavigation.this,HomeActivity.class);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query2  = myRef.orderByChild("email").equalTo(myAppPrefsManager.getUserName());
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for(DataSnapshot issue:snapshot.getChildren()){
                        Details details = issue.getValue(Details.class);
                        assert details != null;

                        currentUserLat =details.getLatitude();
                        currentUserLon =details.getLongitude();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CurrentLocation = new Location("");
        CurrentLocation.setLatitude(currentUserLat);
        CurrentLocation.setLongitude(currentUserLon);
        OtherUserLocation = new Location("");
        OtherUserLocation.setLatitude(receiverLat);
        OtherUserLocation.setLongitude(receiverLon);
        try {
            Uri uri2 = Uri.parse("google.navigation:q="+receiverLat+","+receiverLon);
            Intent i = new Intent(Intent.ACTION_VIEW,uri2);
            i.setPackage("com.google.android.apps.maps");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }catch (ActivityNotFoundException e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        double distance = CurrentLocation.distanceTo(OtherUserLocation);

        if (distance<0.2E7){

            Query query3 = myRef.orderByChild("email").equalTo(param2);
            query3.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){
                        Toast.makeText(Mapsnavigation.this, "exists", Toast.LENGTH_SHORT).show();
                        for(DataSnapshot issue: snapshot.getChildren()){

                            myRef = issue.getRef();
                            Details details = issue.getValue(Details.class);
                            assert details != null;


                            myRef.child("bloodStatus").setValue("0");

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}