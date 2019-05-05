package com.team7.hadcontrolpanel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

public class TodoItem extends AppCompatActivity {

    //declear variables
    private EditText itemTxt;
    private Button btnAdd;

    private FirebaseDatabase firebase;
    private DatabaseReference dbReference;

    ListView itemListView;
    List<Item> itemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        firebase = FirebaseDatabase.getInstance();
        dbReference = firebase.getReference("ToDo List");

        itemTxt = (EditText) findViewById(R.id.editTextItem);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        itemListView = (ListView) findViewById(R.id.itemListView);
        itemList = new ArrayList<>();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
                itemTxt.setText("");
            }
        });

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = itemList.get(position);
                completedItem(item.getItemID());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear();
                for (DataSnapshot itemSnap : dataSnapshot.getChildren()) {
                    Item item = itemSnap.getValue(Item.class);
                    itemList.add(item);
                }
                TodoList adapter = new TodoList(TodoItem.this, itemList);
                itemListView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addItem() {
        String item = itemTxt.getText().toString();

        if(!TextUtils.isEmpty(item)) {
            firebase = FirebaseDatabase.getInstance();
            dbReference = firebase.getReference("ToDo List");

            String id = dbReference.push().getKey();
            Item items = new Item(id, item);

            dbReference.child(id).setValue(items);
            Toast.makeText(this, "Todo item added", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "You must enter a todo item", Toast.LENGTH_LONG).show();
        }
    }

    private void completedItem(String itemID) {
        firebase = FirebaseDatabase.getInstance();
        dbReference = firebase.getReference("ToDo List").child(itemID);

        DatabaseReference completedItem = dbReference;

        completedItem.removeValue();

        Toast.makeText(this, "Todo Item is Completed", Toast.LENGTH_LONG).show();

    }
}