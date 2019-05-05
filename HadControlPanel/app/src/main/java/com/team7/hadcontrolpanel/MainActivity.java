package com.team7.hadcontrolpanel;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    String ssid;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    String mode;
    int counter;
    private final String TAG = "SnapshotDatabase";

    /**
     * When program is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declare view variables
        Button btnCal = findViewById(R.id.Calender);
        Button btnTodo = findViewById(R.id.TodoList);
        FirebaseApp.initializeApp(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fabPrivacy = (FloatingActionButton) findViewById(R.id.fabPrivacy);

        // instantiate database variables
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("ToDo List");

        //go to todoitem page
        btnTodo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TodoItem.class));
            }
        });

        // go to calendar page
        btnCal.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Calendar.class));
            }
        });

        // when privacy fab is clicked
        fabPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //counter for privacy count
                counter++;
                //database variables
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("Privacy Mode");
                if (counter % 2 == 1) {
                    ref.child("Mode").setValue("Privacy On");
                    mode = "Privacy Mode";
                    Snackbar.make(view, "Entering " + mode, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    ref.child("Mode").setValue("Privacy Off");
                    Snackbar.make(view, "Exiting " + mode, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        // Read from the database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = String.valueOf(dataSnapshot.child("Item").getValue());
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    // go to connecting wifi page
    public void onConnectingWiFiClick(View v) {
        this.startActivity(new Intent(getApplicationContext(), WifiActivity.class));
    }

    //get wifi info
    public void onGetWiFiInfoClick(View v) {
        getWifiName(this); //calling getWiFiName
    }

    //get wifi name
    public String getWifiName(Context context) {
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Connected WiFi");
        // instantiate wifi manager
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            //getting wifi info
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    //getting ssid
                    ssid = wifiInfo.getSSID().replace("\"", "");
                    ref.child("SSID").setValue(ssid);
                    Toast.makeText(this, "SSID: " + ssid, Toast.LENGTH_SHORT).show();
                    return wifiInfo.getSSID();
                }
            }
        }

        return null;
    }
}