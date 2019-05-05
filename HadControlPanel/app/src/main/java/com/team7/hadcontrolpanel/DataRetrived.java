package com.team7.hadcontrolpanel;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataRetrived extends AppCompatActivity {

    //declare variables
    private ListView listView;
    DatabaseReference databaseReference;
    List<CalEvent> calEventList;

    /**
     * when the programs is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrived);
        listView = findViewById(R.id.list_view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");

        // clear new array
        calEventList = new ArrayList<>();

        // when a list item is long clicked, show dialog
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // get event info on clicked position
                CalEvent calEvent = calEventList.get(position);
                showDialog(calEvent.getEventID(), calEvent.getTitle(), calEvent.getDate(), calEvent.getEvent());
                return false;
            }
        });
    }
    /**
     * when the program is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // clear the list
                calEventList.clear();
                // when data is changed
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    CalEvent calEvent = dataSnap.getValue(CalEvent.class);
                    calEventList.add(calEvent);
                }
                // caling adapter method
                TaskInfoAdapter taskInfoAdapter = new TaskInfoAdapter(DataRetrived.this, calEventList);
                listView.setAdapter(taskInfoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * show the update dialog when it is clicked with passed in value
     */
    private void showDialog(String eventID, String title, String date, String event) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update, null);

        dialogBuilder.setView(dialogView);

        // declare final variables
        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.editTextTitle);
        final EditText editTextDate = (EditText) dialogView.findViewById(R.id.editTextDate);
        final EditText editTextEvent = (EditText) dialogView.findViewById(R.id.editTextEvent);
        final Button btnUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);
        final Button btnDelete = (Button) dialogView.findViewById(R.id.btnDelete);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        editTextTitle.setText(title);
        editTextDate.setText(date);
        editTextEvent.setText(event);

        dialogBuilder.setTitle("Updating Title: " + title);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        // when update button is clicked
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String date = editTextDate.getText().toString();
                String event = editTextEvent.getText().toString();

                // if title or event is filled, update the event
                if(!TextUtils.isEmpty(title)&& !TextUtils.isEmpty((event))) {
                    updateEvent(eventID, title, date, event);
                }
                //else ask user to input again
                else if(TextUtils.isEmpty(title)) {
                    openDialog("Alert", "You must enter a Title!");                }
                else if (TextUtils.isEmpty(event)) {
                    openDialog("Alert", "You must enter an Event!");
                }
                alertDialog.dismiss();
            }
        });

        // when delete button is clicked
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String date = editTextDate.getText().toString();
                String event = editTextEvent.getText().toString();

                // alert user if they are sure to delete or not
                new AlertDialog.Builder(DataRetrived.this).
                        setMessage("Are you sure you want to DELETE this event?").
                        setCancelable(true).
                        setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        deleteEvent(eventID);
                                        dialog.dismiss();
                                    }
                                }
                        ).
                        setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                }
                        ).
                        create().
                        show();
                alertDialog.dismiss();
            }
        });

        //when cancel button is clicked, dismiss popup box
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String date = editTextDate.getText().toString();
                String event = editTextEvent.getText().toString();

                alertDialog.dismiss();
            }
        });
    }

    /**
     * update calender with passed in value
     */
    private boolean updateEvent(String id, String title, String date, String event) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Events").child(id);
        CalEvent calEvents = new CalEvent(id, title, date, event);
        databaseReference.setValue(calEvents);
        Toast.makeText(this, "Event Updated Successfully", Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * update calender with passed in value with passed in unique key
     */
    public void deleteEvent(String EventID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Events").child(EventID);

        DatabaseReference deleteEvent = databaseReference;

        deleteEvent.removeValue();

        Toast.makeText(this, "Event Deleted Successfully", Toast.LENGTH_LONG).show();
    }

    /**
     * dialog method
     */
    public void openDialog(String title, String message) {
        DialogBox dialogBox = new DialogBox(title, message);
        dialogBox.show(getSupportFragmentManager(), "Dialog");
    }
}

