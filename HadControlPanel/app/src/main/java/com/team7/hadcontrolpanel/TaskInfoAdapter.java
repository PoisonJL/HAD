package com.team7.hadcontrolpanel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;

import com.google.android.gms.tasks.Tasks;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TaskInfoAdapter extends ArrayAdapter<CalTask> {




    private Activity context;
    private List<CalTask>tasksList;




    public TaskInfoAdapter(Activity context, List<CalTask>tasksList){
        super(context,R.layout.activity_calender,tasksList);
        this.context =context;
        this.tasksList=tasksList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listView = inflater.inflate(R.layout.activity_calender,null,true);


        TextView dayName=(TextView)listView.findViewById(R.id.day);
        TextView taskName=(TextView)listView.findViewById(R.id.task);
        TextView titlName=(TextView)listView.findViewById(R.id.title);

        CalTask calTask1= tasksList.get(position);
        taskName.setText(calTask1.getdayname());
        dayName.setText(calTask1.getdayname());
        titlName.setText(calTask1.gettitlename());



        return listView;

    }
}




