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

//public class DataRetrived
public class DataRetrived extends AppCompatActivity {

    //declare variables
    private ListView listView;
    DatabaseReference databaseReference;
    List<CalEvent> calEventList;

    /**
     *when the programs is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrived);
        listView = findViewById(R.id.list_view);
        databaseReference = FirebaseDatabase.getInstance().getReference("Events");

        // clear new array
        calEventList = new ArrayList<>();

        // whenever this list is long clicked.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CalEvent calEvent = calEventList.get(position);
                showUpdateDialog(calEvent.getEventID(), calEvent.getTitle(), calEvent.getDate(), calEvent.getEvent());
                return false;
            }
        });
    }
    /**
     *when the program is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calEventList.clear();
                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    CalEvent calEvent = dataSnap.getValue(CalEvent.class);
                    calEventList.add(calEvent);
                }
                TaskInfoAdapter taskInfoAdapter = new TaskInfoAdapter(DataRetrived.this, calEventList);
                listView.setAdapter(taskInfoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     *show the update dialog when it is clicked
     */
    private void showUpdateDialog(String eventID, String title, String date, String event) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update, null);

        dialogBuilder.setView(dialogView);

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

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String date = editTextDate.getText().toString();
                String event = editTextEvent.getText().toString();

                if(!TextUtils.isEmpty(title)&& !TextUtils.isEmpty((event))) {
                    updateCalendar(eventID, title, date, event);
                }
                else if(TextUtils.isEmpty(title)) {
                    editTextTitle.setError("You must enter a Title!");
                }
                else if (TextUtils.isEmpty(event)) {
                    editTextEvent.setError("You must enter a Title!");
                }
                updateCalendar(eventID, title, date, event);
                alertDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String date = editTextDate.getText().toString();
                String event = editTextEvent.getText().toString();

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
     *update calender
     */
    private boolean updateCalendar(String id, String title, String date, String event) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Events").child(id);
        CalEvent calEvents = new CalEvent(id, title, date, event);
        databaseReference.setValue(calEvents);
        Toast.makeText(this, "Event Updated Successfully", Toast.LENGTH_LONG).show();
        return true;
    }

    //delete task
    public void deleteEvent(String EventID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Events").child(EventID);

        DatabaseReference deleteEvent = databaseReference;

        deleteEvent.removeValue();

        Toast.makeText(this, "Event Deleted Successfully", Toast.LENGTH_LONG).show();
    }
}

