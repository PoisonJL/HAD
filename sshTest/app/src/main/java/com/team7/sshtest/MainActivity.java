package com.team7.sshtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class MainActivity extends AppCompatActivity {

    // Make the login changes here
    String username = "pi";
    String password = "haddevice";
    String hostname = "192.168.1.152";
    int port = 22;
    //String command = "mkdir /home/" + username + "/sshtest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        new AsyncTask<Integer, Void, Void>() {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Integer... integers) {
                try {
                    // connect to Pi

                    //Toast.makeText(MainActivity.this, "You were able to create a file - nice!", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(MainActivity.this, "Sorry fam, but the file was not successfully created :(", Toast.LENGTH_LONG).show();
                }
                return null;
            }
        }.execute(1);
    }

    public static String executeRemoteCommand(String username, String password, String hostname, int port)
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
        String test = "sudo touch had.conf";
        cs.setCommand(test);
        cs.connect();
        Thread.sleep(1000);
        cs.disconnect();

        return b.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    public void CreateFile(View v) {
//        Button cfBtn = (Button) v;
//        try {
//            System.out.println(executeRemoteCommand(username, password, hostname, port));
//            Toast.makeText(MainActivity.this, "You were able to create a file - nice!", Toast.LENGTH_LONG).show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(MainActivity.this, "Sorry, but the file was not successfully created :(", Toast.LENGTH_LONG).show();
//        }
//
//    }
}
