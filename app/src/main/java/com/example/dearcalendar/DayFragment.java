package com.example.dearcalendar;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class DayFragment extends Fragment {
    private String date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.day_view, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setDayMonth();
        constructList();
        handleButton();


    }

    private void setDayMonth()
    {
        Bundle receive = this.getArguments();
        String received = receive.getString("dayMonth");
        date =  ""+ receive.getInt("year") + "-" +
                receive.getInt("month") + "-" +
                receive.getString("day");

        System.out.println(date);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(received);
    }

    private ArrayList<EventHolder> toArray(Cursor cursor)
    {
        ArrayList<EventHolder> result = new ArrayList<>();
        while(cursor.moveToNext())
        {
            EventHolder element = new EventHolder();
            element.setTitle(cursor.getString(2));
            element.setColor(cursor.getString(4));
            element.setStart(cursor.getString(5));
            element.setEnd(cursor.getString(6));

            result.add(element);
        }

        return result;
    }

    private void constructList()
    {
        ListView listView  = (ListView) getView().findViewById(R.id.dayList);
        TextView textView = getActivity().findViewById(R.id.dayMessage);
        /*
        EventHolder test = new EventHolder("White", "test", "7:00", "8:00");
        ArrayList<EventHolder> list = new ArrayList<EventHolder>();
         */
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        String sql = "SELECT * FROM events WHERE startDate LIKE '" + date +
                      "' ORDER BY startHour;";
        System.out.println(sql);
        Cursor data = databaseHandler.getData(sql);
        ArrayList<EventHolder> list = toArray(data);
        listView.setEmptyView(textView);


        EventListAdapter adapter = new EventListAdapter(this.getContext(),R.layout.day_event_layout, list);
        listView.setAdapter(adapter);
    }

    private void handleButton()
    {
        ImageButton imageButton = getActivity().findViewById(R.id.createEventButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendStartFragment();
            }
        });
    }

    protected void startFragment(int id, Fragment fragment)
    {
        getFragmentManager().beginTransaction().replace(id,
                fragment).addToBackStack(null).commit(); //display the desired fragment
    }

    private void sendStartFragment()
    {
        Bundle toSend = this.getArguments();
        Fragment fragment = new CreateEventFragment();
        fragment.setArguments(toSend);
        startFragment(R.id.fragmentContainer,fragment);
    }



}
