package com.team7.hadcontrolpanel;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class TodoList extends ArrayAdapter<Item> {

    private Activity context;
    private List<Item> itemList;

    public TodoList(Activity context, List<Item> itemList) {
        super(context, R.layout.todoitem_view, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listView = inflater.inflate(R.layout.todoitem_view, null, true);

        TextView txtItem = (TextView) listView.findViewById(R.id.txtItem);

        Item item = itemList.get(position);

        txtItem.setText(item.getTodoItem());

        return listView;
    }
}