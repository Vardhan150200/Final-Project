package com.example.nearblood;

import static com.example.nearblood.Network.IS_NETWORK_AVAILABLE;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class utilites {

    public static void internetCheck(Activity context){
        IntentFilter intentFilter = new IntentFilter(Network.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "Available" : "Not Available";

                if (networkStatus.equals("Not Available"))
                {
                    Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(context, " Internet Found", Toast.LENGTH_SHORT).show();

                }
            }
        }, intentFilter);
    }
}
