package com.team7.hadcontrolpanel;

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

    // initiate variable

    private ListView listView;
    DatabaseReference databaseReference;
    List<CalTask> calTasksList;

    //on create method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrived);
        listView = findViewById(R.id.list_view);
        calTasksList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks");
        calTasksList = new ArrayList<>();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            //when the a task is long clicked
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                CalTask ct = calTasksList.get(position);
                showUpdateDialog(ct.getTaskID(), ct.gettaskname(), ct.gettitlename(), ct.getdayname());
                return false;
            }
        });
    }
    //on state
    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ts : dataSnapshot.getChildren()) {
                    CalTask ct = ts.getValue(CalTask.class);
                    calTasksList.add(ct);
                }
                TaskInfoAdapter taskInfoAdapter = new TaskInfoAdapter(DataRetrived.this, calTasksList);
                listView.setAdapter(taskInfoAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // show update dialog
    private void showUpdateDialog(String taskID, String task, String title, String date) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update, null);

        dialogBuilder.setView(dialogView);

        final EditText editTextTask = (EditText) dialogView.findViewById(R.id.editTextTask);
        final EditText editTextTitle = (EditText) dialogView.findViewById(R.id.editTextTitle);
        final EditText editTextDate = (EditText) dialogView.findViewById(R.id.editTextDate);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle("Updating Title: " + title);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String task = editTextTask.getText().toString();
                String title = editTextTitle.getText().toString();
                String date = editTextDate.getText().toString();

                if(TextUtils.isEmpty(task)) {
                    editTextTask.setError("Task Required");
                    return;
                }
                updateCalendar(taskID, title, task, date);
                alertDialog.dismiss();
                finish();
                startActivity(getIntent());
            }
        });
    }

        // update method
    private boolean updateCalendar(String id, String task, String title, String date) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Tasks").child(id);
        CalTask ct = new CalTask(id, task, title, date);
        databaseReference.setValue(ct);
        Toast.makeText(this, "Task Updated Successfully", Toast.LENGTH_LONG).show();
        return true;
    }
}

