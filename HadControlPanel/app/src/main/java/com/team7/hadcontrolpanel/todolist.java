package com.team7.hadcontrolpanel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *to do list
 */
public class todolist extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //declare variables
    private EditText itemET;
    private Button btn;
    private ListView itemsList;
    //set up firebbase
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private int itemID = 0;
    //list counter
    int listCount = 0;
    String key;

    //clear Arrays
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> itemCounter;

    Map<Integer, Integer> test;


    //when the programs starts it will do this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);
        itemET = findViewById(R.id.item_edit_text);
        // Button find
        btn = findViewById(R.id.add_btn);
        itemsList = findViewById(R.id.items_list);
        items = filehelper.readData(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        itemCounter = filehelper.readData(this);
        itemsList.setAdapter(adapter);
        btn.setOnClickListener(this);
        itemsList.setOnItemClickListener(this);
        // initiate hashmap
        test = new HashMap<Integer, Integer>() {{
        }};
    }

    // when it is clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                String itemEntered = itemET.getText().toString();
                adapter.add(itemEntered);
                itemCounter.add(itemEntered);
                itemET.setText("");

                filehelper.writeData(items, this);
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("ToDo List");
                key = ref.push().getKey();
                ref.child("Item").child(String.valueOf(itemID)).setValue(itemEntered);

                itemID++;
                Toast.makeText(this, "item added", Toast.LENGTH_SHORT).show();
                test.put(listCount, itemID);
                break;
        }
    }


    //when item is clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // index key
        int key = 0;
        String item = items.get(position);
        System.out.println(position);

        //for loop to check for item size
        for (int i = 0; i < itemCounter.size(); i++) {
            if (itemCounter.get(i).contentEquals(item)) {
                key = i;
                System.out.println(key);
            }
        }
        //call remove when the item is clicked
        items.remove(position);
        adapter.notifyDataSetChanged();
        listCount = adapter.getCount();

        //write data
        filehelper.writeData(items, this);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("ToDo List");

        //print the key
        System.out.println(key);
        ref.child("Item").child(String.valueOf(key)).removeValue();

        Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show();
    }
}