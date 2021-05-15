package com.example.dearcalendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class MonthDisplayAdapter extends RecyclerView.Adapter<MonthDisplayHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;
    private Calendar selectedDate;
    private Context context;

    public MonthDisplayAdapter(Context context, ArrayList<String> daysOfMonth, OnItemListener onItemListener, Calendar selectedDate)
    {
        this.context = context;
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.selectedDate = selectedDate;
    }


    @NonNull
    @Override
    public MonthDisplayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.month_calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666);

        return new MonthDisplayHolder(view,onItemListener);
    }

    private boolean equalDates(Calendar date1, Calendar date2)
    {
      if(date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
              && date1.get(Calendar.DATE) == date2.get(Calendar.DATE)
              && date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
      )
          return true;

      return false;
    }

    private String dateFormat(Calendar c)
    {
        String result = c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-"
                + c.get(Calendar.DATE);

        return result;
    }
    private String getColorForDay(String day)
    {
        DatabaseHandler databaseHandler = MainActivity.databaseHandler;
        String sql = "SELECT * FROM events WHERE startDate like '"+ dateFormat(selectedDate) + "';";

        Cursor cursor = databaseHandler.getData(sql);

        HashMap<String,Integer> colorCount = new HashMap<>();
        while(cursor.moveToNext())
        {
            String element = cursor.getString(4);
            if(colorCount.containsKey(element))
                colorCount.put(element, colorCount.get(element)+1);
            else
                colorCount.put(element,1);
        }
        String result = "";
        int max=0;
        for(HashMap.Entry entry : colorCount.entrySet())
        {
            if( (int) entry.getValue() > max)
            {
                result = (String) entry.getKey();
                max = (int) entry.getValue();
            }
        }

        return result;
    }
    @Override
    public void onBindViewHolder(@NonNull MonthDisplayHolder holder, int position)
    {
        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);
        if(!day.equals(""))
            selectedDate.set(Calendar.DATE,Integer.valueOf(day));
        else
            selectedDate.set(Calendar.DATE,1);

        if(!day.equals("") && equalDates(selectedDate,MainActivity.currentDate))
        {
            int color = ContextCompat.getColor(context,R.color.black);
            holder.dayOfMonth.setTextColor(color);
            holder.dayOfMonth.setTypeface(null,Typeface.BOLD);
        }

        if(day.equals(""))
        {
            holder.borders[0].setVisibility(View.INVISIBLE);
            holder.borders[1].setVisibility(View.INVISIBLE);
            holder.borders[2].setVisibility(View.INVISIBLE);
            holder.borders[3].setVisibility(View.INVISIBLE);
        }
        else //if(!MainActivity.eventsLocked)
        {
            String color = getColorForDay(day);
            if(!color.equals(""))
                holder.layout.setBackgroundColor(MainActivity.colors.get(color));
        }
        if(position > 7 && position<42 && !daysOfMonth.get(position-1).equals("")  && daysOfMonth.get(position).equals("") )
        {
            holder.borders[1].setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}
