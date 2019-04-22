package com.team7.hadcontrolpanel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;


public class calender extends AppCompatActivity {
    CalendarView calendarView;
    TextView myDate;
    //    private TextInputEditText inputTitle;
//    private TextInputEditText inputDate;
//    private TextInputEditText inputTask ;
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Button btnSave = (Button) findViewById(R.id.btnSave);

//        inputTitle= findViewById(R.id.title);
//        inputTitle= findViewById(R.id.task);
//        inputTitle= findViewById(R.id.day);
//
//
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
}
