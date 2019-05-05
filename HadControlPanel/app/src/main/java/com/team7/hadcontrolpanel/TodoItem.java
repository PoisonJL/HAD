package com.team7.hadcontrolpanel;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

    //declare variables
    private EditText itemTxt;
    private Button btnAdd;

    private FirebaseDatabase firebase;
    private DatabaseReference dbReference;
    ListView itemListView;
    List<Item> itemList;

    /**
     * when the programs is created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        //view and database variables
        firebase = FirebaseDatabase.getInstance();
        dbReference = firebase.getReference("ToDo List");
        itemTxt = (EditText) findViewById(R.id.editTextItem);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        itemListView = (ListView) findViewById(R.id.itemListView);
        itemList = new ArrayList<>();

        // when add button is clicked
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(); //calling addIitem method
                itemTxt.setText("");
            }
        });

        //when an item is long clicked
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get the item from the list
                Item item = itemList.get(position);
                completedItem(item.getItemID()); // calling completedItem method
            }
        });
    }
    /**
     * when the program is started
     */
    @Override
    protected void onStart() {
        super.onStart();
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            // when data is changed
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // clear the list
                for (DataSnapshot itemSnap : dataSnapshot.getChildren()) {
                    //get item, add it to list
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

    /**
     * add items to the database
     */
    private void addItem() {
        String item = itemTxt.getText().toString();
        if(!TextUtils.isEmpty(item)) {
            firebase = FirebaseDatabase.getInstance();
            dbReference = firebase.getReference("ToDo List");
            // get unique timestamp key
            String id = dbReference.push().getKey();
            Item items = new Item(id, item);
            dbReference.child(id).setValue(items);
            Toast.makeText(this, "Todo item added", Toast.LENGTH_LONG).show();
        }
        else {
            // alert user if they are sure to delete or not
            new AlertDialog.Builder(TodoItem.this).
                    setMessage("You must enter an item").
                    setCancelable(true).
                    setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            }
                    ).
                    create().
                    show();
        }
    }

    /**
     * delete todo items from the database
     */
    private void completedItem(String itemID) {
        firebase = FirebaseDatabase.getInstance();
        dbReference = firebase.getReference("ToDo List").child(itemID);
        DatabaseReference completedItem = dbReference;

        //remove item from the database
        completedItem.removeValue();
        Toast.makeText(this, "Todo Item is Completed", Toast.LENGTH_LONG).show();
    }
}