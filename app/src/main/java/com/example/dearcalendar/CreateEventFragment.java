package com.example.dearcalendar;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import static java.lang.Integer.parseInt;

public class CreateEventFragment extends Fragment {

    private String color;
    private String details;
    private String title;
    private String recurrence;
    private String hourMinute;
    private String startHour;
    private String endHour;
    private int expire;
    private String[] yearMonthDay;

    public CreateEventFragment() {
        assignDefault();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.create_event, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSelectedDate();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        String title = "New Event for "+ yearMonthDay[2]+ " " + (MonthFragment.monthName(parseInt(yearMonthDay[1])));
        toolbar.setTitle(title);

        setupColorSelection();
        setupRecurrenceSelection();

        timePickerStart();
        timePickerEnd();

        listenConfirm();
    }

    private void assignDefault()
    {
        startHour="";
        startHour="";
        endHour="";
        endHour="";
        yearMonthDay = new String[3];
    }

    private void getSelectedDate()
    {
        Bundle received = getArguments();
        yearMonthDay[0] = String.valueOf(received.getInt("year"));
        yearMonthDay[1] = String.valueOf(received.getInt("month"));
        yearMonthDay[2] = received.getString("day");


    }

    private AppCompatSpinner targetSpinner(int spinnerId, String[] options)
    {
        AppCompatSpinner spin = getActivity().findViewById(spinnerId);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        return spin;
    }

    private void setupColorSelection() {

        String[] colors = {"Select Color","Green","Blue","Yellow"};

        AppCompatSpinner spin = targetSpinner(R.id.createEventColor,colors);

        if (spin == null)
            return;

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = parent.getItemAtPosition(position).toString();
                previewColor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void previewColor()
    {
        ImageView imageView = getActivity().findViewById(R.id.createEventPreview);
        imageView.setBackgroundColor(MainActivity.colors.get(color));
    }

    private void setupRecurrenceSelection()
    {
        String[] options = {"Select Recurrence","None","Weekly","Monthly"};

        AppCompatSpinner spin = targetSpinner(R.id.createEventReccurence,options);

        if (spin == null)
            return;

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            EditText expire = getActivity().findViewById(R.id.createEventExpire);
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recurrence = parent.getItemAtPosition(position).toString();

                if(recurrence.equals("Select Recurrence") || recurrence.equals("None"))
                {
                    expire.setVisibility(View.INVISIBLE);
                }
                else
                    expire.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                expire.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void timePickerStart()
    {
        Button button = getActivity().findViewById(R.id.createEventStart);
        selectHour(button,true);
    }

    private void timePickerEnd()
    {
        Button button = getActivity().findViewById(R.id.createEventEnd);
        selectHour(button,false);
    }

    private void selectHour(Button button, boolean start)
    {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker(button,start);
            }
        });
    }

    private void timePicker(Button button, boolean start)
    {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                 hourMinute = String.format(Locale.getDefault(), "%02d:%02d",selectedHour,selectedMinute);
                 updateVars(start);
                 button.setText(hourMinute);
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),onTimeSetListener,
                0,0,true);
        timePickerDialog.show();
    }

    private void updateVars(boolean start)
    {
        if(start)
        {
            startHour = hourMinute;
        }
        else
        {
            endHour = hourMinute;
        }
    }

    private boolean validateForm()
    {
        EditText title = getActivity().findViewById(R.id.createEventTitle);
        this.title = title.getText().toString();

        if(!this.title.equals("") && isTimeSet() && isColorSet() &&validRecurrence())
            return true;
        return false;
    }

    private boolean isColorSet()
    {
        if(color.equals("Select Color"))
        {
            return false;
        }
        return true;
    }

    private boolean isTimeSet()
    {
        if(startHour.equals("") || endHour.equals(""))
            return false;

        return true;
    }

    private int getRecExpiration()
    {
        EditText recurrenceExpire = getActivity().findViewById(R.id.createEventExpire);
        String expireS = recurrenceExpire.getText().toString();
        int expire = -1;

        if(!expireS.equals(""))
            expire = parseInt(expireS);

        return expire;
    }

    private boolean validRecurrence()
    {
        expire = getRecExpiration();

        if(recurrence.equals("None"))
            return true;
        else if(!recurrence.equals("Select Recurrence") && expire>0 && expire <12 )
            return true;

        return false;
    }

    private void listenConfirm()
    {
        Button confirm = getActivity().findViewById(R.id.createEventConfirm);
        TextView error = getActivity().findViewById(R.id.createEventError);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm())
                {
                    error.setVisibility(View.INVISIBLE);
                    createEvent();
                }
                else if(expire>12)
                {
                    error.setText("A recurrent event can have a maximum duration of 12 months");
                    error.setVisibility(View.VISIBLE);
                }
                else
                {
                    error.setText("Only the details field is optional.");
                    error.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void createEvent()
    {
        EditText detailsField = getActivity().findViewById(R.id.createEventDetails);
        details = detailsField.getText().toString();

        Bundle received = getArguments();

        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        String date = yearMonthDay[0] + "-" + yearMonthDay[1] + "-" + yearMonthDay[2];

        if(recurrence.equals("None"))
        {
            boolean op = databaseHandler.addEntry(0,title,"Waiting",color,startHour,
                    endHour,date,"None",0,details);
            if(!op)
                Toast.makeText(getContext(),"Failed", Toast.LENGTH_LONG);
            else
                getActivity().onBackPressed();


        }

    }
}