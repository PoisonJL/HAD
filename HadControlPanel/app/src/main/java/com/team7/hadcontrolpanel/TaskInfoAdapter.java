package com.team7.hadcontrolpanel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;

import android.widget.TextView;

import java.util.List;


/**
 *public class extend from array adapter
 */
public class TaskInfoAdapter extends ArrayAdapter<CalEvent> {

    //declear variablbes
    private Activity context;
    private List<CalEvent> eventList;

    /**
     *Constructor, takes list of tasks and context
     */
    public TaskInfoAdapter(Activity context, List<CalEvent> eventList){
        super(context, R.layout.list_view, eventList);
        this.context = context;
        this.eventList = eventList;
    }


    // get view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listView = inflater.inflate(R.layout.list_view ,null,true);

        //get the varaibles y ID
        TextView title = (TextView)listView.findViewById(R.id.txtTitle);
        TextView date = (TextView)listView.findViewById(R.id.txtDate);
        TextView event = (TextView)listView.findViewById(R.id.txtEvent);

        //set variales
        CalEvent events = eventList.get(position);
        title.setText(events.getTitle());
        date.setText(events.getDate());
        event.setText(events.getEvent());

        return listView;
    }
}