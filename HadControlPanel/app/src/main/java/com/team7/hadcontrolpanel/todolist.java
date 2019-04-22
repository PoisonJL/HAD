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


public class todolist extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText itemET;
    private Button btn;
    private ListView itemsList;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private int itemID = 0;
    int listCount = 0;
    int fbId = 0;

    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    Map <Integer, Integer> test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);
        itemET = findViewById(R.id.item_edit_text);
        btn =findViewById(R.id.add_btn);
        itemsList=findViewById(R.id.items_list);
        items = filehelper.readData( this);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items );
        itemsList.setAdapter(adapter);
        btn.setOnClickListener(this );
        itemsList.setOnItemClickListener(this);
        test = new HashMap<Integer, Integer>() {{}};
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.add_btn:
                String itemEntered = itemET.getText().toString();
                adapter.add(itemEntered);
                itemET.setText("");
                listCount = adapter.getCount();

                filehelper.writeData(items,this);
                db = FirebaseDatabase.getInstance();
                ref = db.getReference("ToDo List");
                ref.child("Item").child(String.valueOf(itemID)).setValue(itemEntered);

                itemID++;
                Toast.makeText(this, "item added", Toast.LENGTH_SHORT).show();
                test.put(listCount, itemID);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fbId++;
        items.remove(position);
        adapter.notifyDataSetChanged();
        listCount = adapter.getCount();

        filehelper.writeData(items,this);
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("ToDo List");
        try {
            ref.child("Item").child(String.valueOf(position)).removeValue();
            if(listCount > 1) {
                reassignKeys(position);
            }
            else {
                test.remove(0);
                ref.child("Item").child(String.valueOf(0)).removeValue();
            }
        } catch(Exception ex) {
            System.out.println("Nothign to delete");
        }
        //Toast.makeText(this, String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    public void reassignKeys(int pos) {
        for (int i = pos; i <= listCount; i++) {
            int tempPos = i+1;
            int tempValue = test.get(tempPos);
            test.remove(i);
            test.put(i, tempValue);
        }
    }
}
