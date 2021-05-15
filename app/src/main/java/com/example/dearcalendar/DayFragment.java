package com.example.dearcalendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
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
        if (!MainActivity.eventsLocked)
        {
            constructList();
            handleButton();
        }
        else
            lockedMessage();


    }

    private void setDayMonth()
    {
        Bundle receive = this.getArguments();
        String received = receive.getString("dayMonth");
        date =  ""+ receive.getInt("year") + "-" +
                receive.getInt("month") + "-" +
                receive.getString("day");

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(received);
    }

    private void lockedMessage()
    {
        ListView listView  = getView().findViewById(R.id.dayList);
        TextView textView = getActivity().findViewById(R.id.dayMessage);
        textView.setText("Events Locked");
        listView.setEmptyView(textView);
    }

    private void decryptEvents(ArrayList<EventHolder> array)
    {
        EncryptionHandler e = new EncryptionHandler();
        for(EventHolder item : array )
        {
            String title= item.getTitle();
            String decrypted = e.decrypt(title,MainActivity.rawPassword);
            item.setTitle(decrypted);
        }
    }

    private void constructList()
    {
        ListView listView  = getView().findViewById(R.id.dayList);
        TextView textView = getActivity().findViewById(R.id.dayMessage);
        listView.setEmptyView(textView);

        DatabaseHandler databaseHandler = MainActivity.databaseHandler;

        ArrayList<EventHolder> list = databaseHandler.getEvents(date);

        databaseHandler.attachImages(list);

        if(!databaseHandler.getSetPassword().equals(""))
            decryptEvents(list);

        EventListAdapter adapter = new EventListAdapter(this.getContext(),R.layout.day_event_layout, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int eventId = list.get(position).getId();
                startCreateEvent(eventId);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                int eventId = list.get(position).getId();
                String sql = "DELETE FROM events WHERE eventId == "+eventId+";";
                databaseHandler.run(sql);

                int recId = list.get(position).getRecId();
                databaseHandler.removeImage(recId);

                list.remove(position);
                constructList();
                return true;
            }
        });
    }

    private void handleButton()
    {
        ImageButton imageButton = getActivity().findViewById(R.id.createEventButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateEvent(-1);
            }
        });
    }

    protected void startFragment(int id, Fragment fragment)
    {
        getFragmentManager().beginTransaction().replace(id,
                fragment).addToBackStack(null).commit(); //display the desired fragment
    }

    //Will edit an existing event when an id is provided
    private void startCreateEvent(int eventId)
    {
        Bundle toSend = this.getArguments();
        CreateEventFragment fragment = new CreateEventFragment();
        if(eventId==-1)
            toSend.putString("task", "Create");
        else
        {
            toSend.putString("task","Edit");
            fragment.setEvent(eventId);
        }

        fragment.setArguments(toSend);
        startFragment(R.id.fragmentContainer,fragment);
    }




}
