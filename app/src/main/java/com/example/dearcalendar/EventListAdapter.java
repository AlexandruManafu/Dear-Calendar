package com.example.dearcalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
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


        EventViewHolder holder = new EventViewHolder();
        LayoutInflater inflater = LayoutInflater.from(context);
        int colorId = MainActivity.colors.get(color);
        //This part ensures that scrolling is smooth
        if(convertView==null)
        {
            convertView = inflater.inflate(resource, parent, false);

            holder.title =  convertView.findViewById(R.id.eventTitle);
            holder.element =  convertView.findViewById(R.id.event);
            holder.start =  convertView.findViewById(R.id.eventStart);
            holder.end =  convertView.findViewById(R.id.eventEnd);
            holder.image = convertView.findViewById(R.id.eventImage);

            convertView.setTag(holder);
        }
        else
        {
            holder = (EventViewHolder)  convertView.getTag();
        }


        holder.title.setText(title);
        holder.element.setBackgroundColor(colorId);
        holder.start.setText(start);
        holder.end.setText(end);

        byte[] imageBytes = getItem(position).getImage();
        if(imageBytes!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(context.getResources(),bitmap);
            dr.setCornerRadius(20);
            holder.image.setImageDrawable(dr);
            //imageView.setImageBitmap(bitmap);
        }

        return convertView;
    }




}
