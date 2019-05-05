package com.team7.hadcontrolpanel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class WifiActivity extends AppCompatActivity {
    String wifi, password, secPro;
    Spinner wifiInput;
    EditText passInput;
    Button btnConnect;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    // Pi credentials to connect to the Pi
    String username = "pi";
    String sshPassword = "haddevice";
    String hostname = "192.168.4.1";
    int port = 22;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        // References the Spinner
        wifiInput = findViewById(R.id.wifiInput);

        System.out.println("Now getting Wifi information...");

        // Initializes the class object that allows the app to scan for Wi-Fi networks
        WifiManager w = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Newer versions require this permission, but this permission will only run once
        // If you experience a glitch, close all Apps (including all Overlay Apps [like Messenger])
        // and restart the phone
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 1);

        // Get a list of all the Wi-FI networks and store them in nl
        List<ScanResult> nl = w.getScanResults();
        List<String> ssidList = new ArrayList<>();
        System.out.println("Now organizing it into a list...");

        // Go through the list of Wi-Fi networks and add them to the SSID list if they have not been
        // added already
        if (nl != null) {
            for (ScanResult nw : nl) {
                System.out.println(nw.SSID);
                String ssidEntry = nw.SSID;
                String ssidInfo = nw.capabilities;
                if (ssidInfo.contains("WPA"))
                    ssidEntry = ssidEntry + "//" + "WPA-PSK";
                else if (ssidInfo.contains("WEP"))
                    ssidEntry = ssidEntry + "//" + "WEP-PSK";
                else
                    ssidEntry = ssidEntry + "//" + "NONE";

                if (!ssidList.contains(ssidEntry))
                    ssidList.add(ssidEntry);
            }
        }

        // Create an adapter and set the adapter to be the dropdown menu
        ArrayAdapter<String> wifiAdapt = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, ssidList);

        wifiInput.setAdapter(wifiAdapt);

        passInput = findViewById(R.id.passInput);
        btnConnect = findViewById(R.id.btnConnect);

        // When the user clicks the "Connect" button, attempt to connect to the Pi using SSH
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifi = wifiInput.getSelectedItem().toString().split("//")[0];
                secPro = wifiInput.getSelectedItem().toString().split("//")[1];

                System.out.println(secPro);
                password = passInput.getText().toString();
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("WiFi Credential").push();

                showToast("SSID: " + wifi + "\n" + "Password: " + password);

                ref.child("SSID").setValue(wifi);
                ref.child("PASSWORD").setValue(password);

                wifiInput.setSelection(-1);
                passInput.setText("");

                // Required to connect to Pi via SSH
                new AsyncTask<Integer, Void, Void>() {
                    @SuppressLint("WrongThread")
                    @Override
                    protected Void doInBackground(Integer... integers) {
                        try {
                            // connect to Pi
                            executeRemoteCommand(username, sshPassword, hostname, port, wifi, password, secPro);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute(1);

                new AlertDialog.Builder(WifiActivity.this).
                        setMessage("Connecting to WiFi").
                        setCancelable(true).
                        setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                }
                        ).
                        create().
                        show();
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(WifiActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    // Connect to the Pi via SSH using the JSch library
    public static String executeRemoteCommand(String username, String password, String hostname, int port, String wifiName, String pwString, String security)
            throws Exception{
        JSch j = new JSch();
        Session s = j.getSession(username, hostname, port);
        s.setPassword(password);
        Properties p = new Properties();
        p.put("StrictHostKeyChecking", "no");
        s.setConfig(p);
        s.connect();

        // Once connected, create a "channel" and execute the required commands
        ChannelExec cs = (ChannelExec) s.openChannel("exec");

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        cs.setOutputStream(b);
        String ssid = "touch \"ssid: " + wifiName + "\"";
        String pass = "touch \"password: " + pwString + "\"";
        String sec = "touch \"security: " + security + "\"";
        cs.setCommand(ssid + ";" + pass + ";" + sec);
        cs.connect();
        Thread.sleep(1000);
        cs.disconnect();
        s.disconnect();

        return b.toString();
    }
}