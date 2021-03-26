package com.example.dearcalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MonthDisplayAdapter extends RecyclerView.Adapter<MonthDisplayHolder> {

    private final ArrayList<String> daysOfMonth;
    private final OnItemListener onItemListener;

    public MonthDisplayAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener )
    {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
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

    @Override
    public void onBindViewHolder(@NonNull MonthDisplayHolder holder, int position)
    {
        holder.dayOfMonth.setText(daysOfMonth.get(position));
    }

    @Override
    public int getItemCount()
    {
        //System.out.println(daysOfMonth.size());
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}
