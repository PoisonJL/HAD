package com.team7.hadcontrolpanel;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


//calender class
public class Calendar extends AppCompatActivity {
    //declear variales
    CalendarView calendarView;
    TextView myDate;
    DatabaseReference databaseReference;
    private EditText txtTitle;
    private EditText txtDate;
    private EditText txtEvent;
    private FloatingActionButton viewCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Button btnSave = (Button) findViewById(R.id.btnSave);

        databaseReference= FirebaseDatabase.getInstance().getReference("Events");
        viewCal = findViewById(R.id.viewCal);

        viewCal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent t = new Intent(Calendar.this, DataRetrived.class);
                startActivity(t);
            }
        });

        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtDate = (EditText) findViewById(R.id.txtDate);
        txtEvent = (EditText) findViewById(R.id.txtEvent);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent();
            }
        });

        //set calender view by id
        calendarView = (CalendarView) findViewById(R.id.Calenderview);
        myDate = (TextView) findViewById(R.id.myDate);

        //when it is long clicked
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String calDate = String.format("%02d", (i1 + 1)) + "/" + String.format("%02d", i2) + "/" + i;
                myDate.setText(calDate);
                txtDate.setText(calDate);
            }
        });
    }
    //open new dialog
    public void openDialog(String title, String message) {
        DialogBox dialogBox = new DialogBox(title, message);
        dialogBox.show(getSupportFragmentManager(), "Dialog");
    }
    //add task
    public void addEvent(){
        String title = txtTitle.getText().toString();
        String date = txtDate.getText().toString();
        String event = txtEvent.getText().toString();

        if(!TextUtils.isEmpty(title)&& !TextUtils.isEmpty((event))) {

            String id = databaseReference.push().getKey();

            CalEvent events = new CalEvent(id, title, date, event);
            databaseReference.child(id).setValue(events);
            openDialog("Information", "Your Event has been Saved!");
            txtTitle.setText(" ");
            txtDate.setText(" ");
            txtEvent.setText(" ");
        }
        else if(TextUtils.isEmpty(title)) {
            openDialog("Alert", "You must enter a Title!");
        }
        else if (TextUtils.isEmpty(event)) {
            openDialog("Alert", "You must enter an Event!");
        }
    }
}