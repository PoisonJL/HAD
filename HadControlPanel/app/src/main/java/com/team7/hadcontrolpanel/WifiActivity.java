package com.team7.hadcontrolpanel;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiActivity extends AppCompatActivity {
    String wifi, password;

    EditText wifiInput;
    EditText passInput;

    Button btnCancel, btnConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        //setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        wifiInput = (EditText) findViewById(R.id.wifiInput);
        passInput = (EditText) findViewById(R.id.passInput);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnConnect = (Button) findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifi = wifiInput.getText().toString();
                password = passInput.getText().toString();

                showToast(wifi);
                showToast(password);
            }
        });
    }

    private void showToast(String text) {
        Toast.makeText(WifiActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
