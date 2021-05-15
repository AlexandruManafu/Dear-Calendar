package com.example.dearcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;


public class MonthFragment extends Fragment implements MonthDisplayAdapter.OnItemListener{

    private RecyclerView calendarRecyclerView;
    private Calendar selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.month_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //getActivity().setContentView(R.layout.month_view); //Not needed for fragment
        selectedDate = new GregorianCalendar();
        initWidgets();
        setMonthView();
        handleSwipes();

    }

    //By using getActivity() you an call functions from the main activity here
    private void initWidgets()
    {
        try {
            calendarRecyclerView = getActivity().findViewById(R.id.monthRecycler);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String monthName(int month){
        String[] monthNames = {"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    private String getMonthYear(Calendar c)
    {
        String month = monthName(c.get(Calendar.MONTH));
        int year = c.get(Calendar.YEAR);

        return  "" + month + " " + year;
    }

    public void setMonthView()
    {

        String toDisplay = getMonthYear(selectedDate);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(toDisplay);
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        MonthDisplayAdapter monthDisplayAdapter = new MonthDisplayAdapter(getContext(),daysInMonth,
          this,selectedDate);

        try {
            RecyclerView.LayoutManager layoutManager = new
                    GridLayoutManager(getActivity().getApplicationContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        calendarRecyclerView.setAdapter(monthDisplayAdapter);
    }

    //Such that Monday is the first day and Sunday is the last
    private static int dayOfWeek(int day)
    {
        switch(day)
        {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
        }

        return 1;
    }

    private static ArrayList<String> daysInMonthArray(Calendar current)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        int daysInMonth = current.getActualMaximum(current.DAY_OF_MONTH);

        //get the name of the first day of the month
        GregorianCalendar first = (GregorianCalendar) current.clone();
        first.set(Calendar.DAY_OF_MONTH,1);
        int dayOfWeek = dayOfWeek(first.get(Calendar.DAY_OF_WEEK));

        for(int i = 1; i <= 42; i++)
        {
            if(i < dayOfWeek || i > daysInMonth + dayOfWeek - 1)
            {
                daysInMonthArray.add("");
            }
            else
            {
                daysInMonthArray.add(String.valueOf(i - dayOfWeek + 1));
            }
        }
        return  daysInMonthArray;
    }

    protected void startFragment(int id, Fragment fragment)
    {
        getFragmentManager().beginTransaction().replace(id,
                fragment).addToBackStack(null).commit(); //display the desired fragment
    }


    public void onItemClick(int position, String dayText)
    {
        if(!dayText.equals(""))
        {
            /*
            String message = "Selected Date " + dayText + " " + getMonthYear(selectedDate);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
             */
            String data = dayText + " " + getMonthYear(selectedDate);
            Bundle sendData = new Bundle();
            sendData.putString("task","Create");
            sendData.putString("dayMonth",data);
            sendData.putString("day", dayText);
            sendData.putInt("month", selectedDate.get(Calendar.MONTH));
            sendData.putInt("year", selectedDate.get(Calendar.YEAR));

            Fragment fragment = new DayFragment();
            fragment.setArguments(sendData);

            startFragment(R.id.fragmentContainer, fragment);

        }
    }

    private void refreshMonth(int val)
    {
        selectedDate.add(Calendar.MONTH,val);
        setMonthView();
    }

    private void handleSwipes()
    {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT |
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN) {

            //can ignore this method
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.LEFT)
                    refreshMonth(1);
                else if(direction==ItemTouchHelper.RIGHT)
                    refreshMonth(-1);
                else if(direction==ItemTouchHelper.UP)
                    refreshMonth(-12);
                else if(direction==ItemTouchHelper.DOWN)
                    refreshMonth(12);

            }

            //Override the animation
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(calendarRecyclerView);
    }

}
