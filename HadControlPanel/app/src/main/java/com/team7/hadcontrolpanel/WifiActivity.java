package com.team7.hadcontrolpanel;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class WifiActivity extends AppCompatActivity {
    String wifi, password;
    EditText wifiInput;
    EditText passInput;
    Button btnConnect;
    private FirebaseDatabase db;
    private DatabaseReference ref;

    // Make the login changes here
    String username = "pi";
    String sshPassword = "haddevice";
    String hostname = "192.168.1.152";
    int port = 22;
    int wifiID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        wifiInput = findViewById(R.id.wifiInput);
        passInput = findViewById(R.id.passInput);
        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiID++;
                wifi = wifiInput.getText().toString();
                password = passInput.getText().toString();
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("WiFi Credential").child(String.valueOf(wifiID));

                showToast(wifi);
                showToast(password);

                ref.child("SSID").setValue(wifi);
                ref.child("PASSWORD").setValue(password);
                new AsyncTask<Integer, Void, Void>() {
                    @SuppressLint("WrongThread")
                    @Override
                    protected Void doInBackground(Integer... integers) {
                        try {
                            // connect to Pi
                            executeRemoteCommand(username, sshPassword, hostname, port, wifi, password);
                            //Toast.makeText(MainActivity.this, "You were able to create a file - nice!", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            //Toast.makeText(MainActivity.this, "Sorry fam, but the file was not successfully created :(", Toast.LENGTH_LONG).show();
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

    public static String executeRemoteCommand(String username, String password, String hostname, int port, String wifiName, String pwString)
            throws Exception{
        JSch j = new JSch();
        Session s = j.getSession(username, hostname, port);
        s.setPassword(password);

        Properties p = new Properties();
        p.put("StrictHostKeyChecking", "no");
        s.setConfig(p);

        ChannelExec cs = null;
        try {
            cs = (ChannelExec) s.openChannel("exec");
            cs.setCommand("true");
            cs.connect();
            //cs.disconnect();
        } catch (Throwable t) {
            s = j.getSession(username, hostname, port);
            s.setPassword(password);
            s.setConfig(p);
            s.connect();
        }

        cs = (ChannelExec) s.openChannel("exec");

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        cs.setOutputStream(b);
        String ssid = "touch \"ssid: " + wifiName + "\"";
        String pass = "touch \"password: " + pwString + "\"";
        cs.setCommand(ssid + ";" + pass);
        cs.connect();
        Thread.sleep(1000);
        cs.disconnect();

        return b.toString();
    }
}