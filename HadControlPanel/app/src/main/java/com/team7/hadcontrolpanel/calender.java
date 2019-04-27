package com.team7.hadcontrolpanel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class calender extends AppCompatActivity {

    private EditText title,day,task;
    CalendarView calendarView;
    TextView myDate;
    DatabaseReference databaseReference;

    Button btnSave;
    private FloatingActionButton floadingactionbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calender);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        databaseReference =FirebaseDatabase.getInstance().getReference("tasks");

        title =(EditText)findViewById(R.id.title);
        task=(EditText)findViewById(R.id.task);
        day=(EditText)findViewById(R.id.day);






        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
//                Toast.makeText(calender.this, "Successfully saved your task, back to main page", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
            }
        });

        calendarView = (CalendarView) findViewById(R.id.Calenderview);
        myDate = (TextView) findViewById(R.id.myDate);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = (i1 + 1) + "/" + i2 + "/" + i;
                myDate.setText(date);


            }
        });
    }
//    public void confirmInput(View view)
//    {
//
//
//
//
//
//    }

    public void openDialog() {
        DialogBox dialogBox = new DialogBox();
        dialogBox.show(getSupportFragmentManager(), "Dialog");
    }

    public void confirmInput(View view) {


    }
}
