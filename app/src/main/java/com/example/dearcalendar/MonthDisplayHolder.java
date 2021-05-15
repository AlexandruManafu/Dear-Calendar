package com.example.dearcalendar;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MonthDisplayHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final TextView dayOfMonth;
    public View[] borders;
    public ConstraintLayout layout;
    private final MonthDisplayAdapter.OnItemListener onItemListener;

    public MonthDisplayHolder(@NonNull View itemView, MonthDisplayAdapter.OnItemListener onItemListener)
    {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.dayNumber);
        layout = itemView.findViewById(R.id.monthCell);
        borders = new View[4];
        borders[0] = itemView.findViewById(R.id.cellBorder1);
        borders[1] = itemView.findViewById(R.id.cellBorder2);
        borders[2] = itemView.findViewById(R.id.cellBorder3);
        borders[3] = itemView.findViewById(R.id.cellBorder4);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }
    @Override
    public void onClick(View view)
    {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}
