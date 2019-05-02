package com.team7.hadcontrolpanel;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DataRetrived extends AppCompatActivity {

    private ListView listView;
    DatabaseReference databaseReference;
    List<CalTask> calTasksList;



        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrived);
        listView =findViewById(R.id.list_view);

        databaseReference =FirebaseDatabase.getInstance().getReference("tasks");

        calTasksList =new ArrayList<>();

    }

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


    protected void update()
    {




    }





}
