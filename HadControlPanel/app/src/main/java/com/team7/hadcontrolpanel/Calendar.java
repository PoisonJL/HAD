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

        // Declare view variables
        Button btnSave = (Button) findViewById(R.id.btnSave);
        txtTitle = (EditText) findViewById(R.id.txtTitle);
        txtDate = (EditText) findViewById(R.id.txtDate);
        txtEvent = (EditText) findViewById(R.id.txtEvent);
        calendarView = (CalendarView) findViewById(R.id.Calenderview);
        myDate = (TextView) findViewById(R.id.myDate);

        //Firebase refenerece to specified field
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");

        /** When calendar icon is clicked,
         * go to DataRetrived class,
         * where it shows all the events*/
        viewCal = (FloatingActionButton) findViewById(R.id.viewCal);
        viewCal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent t = new Intent(Calendar.this, DataRetrived.class);
                startActivity(t);
            }
        });
        // when save button is clicked, call addEvent class
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvent(); // Calling addEvent
            }
        });

        //when the data is selected
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String calDate = String.format("%02d", (i1 + 1)) + "/" + String.format("%02d", i2) + "/" + i;
                myDate.setText(calDate);
                txtDate.setText(calDate);
            }
        });
    }

    /**
     * dialog method
     */
    public void openDialog(String title, String message) {
        //instantiate a new dialog box
        DialogBox dialogBox = new DialogBox(title, message);
        dialogBox.show(getSupportFragmentManager(), "Dialog");
    }

    /**
     * add Event method
     */
    public void addEvent(){
        String title = txtTitle.getText().toString();
        String date = txtDate.getText().toString();
        String event = txtEvent.getText().toString();

        // if title and event is filled, add it to database
        if(!TextUtils.isEmpty(title)&& !TextUtils.isEmpty((event))) {
            // unique timestamp key (firebase feature)
            String id = databaseReference.push().getKey();
            CalEvent events = new CalEvent(id, title, date, event);

            // adding event to database
            databaseReference.child(id).setValue(events);

            // alert user data is saved
            openDialog("Information", "Your Event has been Saved!");
            txtTitle.setText("");
            txtDate.setText("");
            txtEvent.setText("");
        }
        // if one of the title or event is empty, ask user to input again
        else if(TextUtils.isEmpty(title)) {
            openDialog("Alert", "You must enter a Title!");
        }
        else if (TextUtils.isEmpty(event)) {
            openDialog("Alert", "You must enter an Event!");
        }
    }
}