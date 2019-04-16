package com.team7.hadcontrolpanel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;


public class todolist extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private EditText itemET;
    private Button btn;
    private ListView itemsList;


    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)

        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        itemET = findViewById(R.id.item_edit_text);
        btn =findViewById(R.id.add_btn);
        itemsList=findViewById(R.id.items_list);


        items = filehelper  .readData( this);

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items );
            itemsList.setAdapter(adapter);


            btn.setOnClickListener(this );
itemsList.setOnItemClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.add_btn:
                String itemEntered =itemET.getText().toString();
                adapter.add(itemEntered);
                itemET.setText("");

                filehelper.writeData(items,this);


                Toast.makeText(this, "item added", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    items.remove(position);
    adapter.notifyDataSetChanged();
        Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
    }
}
