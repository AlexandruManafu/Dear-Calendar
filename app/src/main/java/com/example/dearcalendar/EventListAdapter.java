package com.example.dearcalendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class EventListAdapter extends ArrayAdapter<EventHolder> {

    private Context context;
    private int resource;


    public EventListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<EventHolder> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String title = getItem(position).getTitle();
        String color = getItem(position).getColor();
        String start = getItem(position).getStart();
        String end = getItem(position).getEnd();

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.eventTitle);
        LinearLayout tvElement = (LinearLayout) convertView.findViewById(R.id.event);
        TextView tvStart = (TextView) convertView.findViewById(R.id.eventStart);
        TextView tvEnd = (TextView) convertView.findViewById(R.id.eventEnd);

        int colorId = MainActivity.colors.get(color);

        tvTitle.setText(title);
        tvElement.setBackgroundColor(colorId);
        tvStart.setText(start);
        tvEnd.setText(end);

        return convertView;
    }


}
