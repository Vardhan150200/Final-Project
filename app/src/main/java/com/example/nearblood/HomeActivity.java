package com.example.nearblood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    Button receive;
    MyAppPrefsManager myAppPrefsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        myAppPrefsManager=new MyAppPrefsManager(this);
        String username= myAppPrefsManager.getUserName();
        Toast.makeText(this, ""+username, Toast.LENGTH_SHORT).show();

        receive = findViewById(R.id.receive);
        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this,DonardetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}