package com.team7.hadcontrolpanel;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;
import android.view.View;



public class calender extends AppCompatActivity {
    CalendarView calendarView;
    TextView myDate;
//    private TextInputEditText inputTitle;
//    private TextInputEditText inputDate;
//    private TextInputEditText inputTask ;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

//        inputTitle= findViewById(R.id.title);
//        inputTitle= findViewById(R.id.task);
//        inputTitle= findViewById(R.id.day);
//
//




        calendarView=(CalendarView)findViewById(R.id.Calenderview);
        myDate=(TextView)findViewById(R.id.myDate);


    calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
            String date= (i1+1) +"/" + i2 + "/" +i;
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





}
