package com.example.dearcalendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MonthDisplayHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView dayOfMonth;
    private final MonthDisplayAdapter.OnItemListener onItemListener;

    public MonthDisplayHolder(@NonNull View itemView, MonthDisplayAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.dayNumber);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
