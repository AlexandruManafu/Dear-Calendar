package com.example.dearcalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

public class CreateEventFragment extends Fragment {

    private Bundle received;
    private String task;
    private String color;
    private String details;
    private String title;
    private String recurrence;
    private String hourMinute;
    private String startHour;
    private String endHour;
    private int expire;
    private int recId;
    private String[] yearMonthDay;
    private String[] recurrenceOptions;
    private String[] colorOptions;

    private int imageStatus = 1;
    private Uri imageUri;
    private int targetEvent;
    private EditText titleField;
    private AppCompatSpinner colorField;
    private ImageView imagePreview;
    private AppCompatSpinner recurrenceField;
    private Button startField;
    private Button endField;
    private EditText recurrenceExpire;
    private EditText detailsField;
    private Button confirm;



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
        received = getArguments();
        task = received.getString("task");

        getSelectedDate();

        setupTitle();

        targetFields();

        setupColorSelection();

        setupImageAttach();

        setupRecurrenceSelection();

        timePickerStart();
        timePickerEnd();

        confirm = getActivity().findViewById(R.id.createEventConfirm);
        if(task.equals("Edit"))
            loadDefaultValues();

        listenConfirmCreate();
    }

    private void setupTitle()
    {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        String title = "New Event";

        if (task.equals("Create"))
            title = "New Event for " + yearMonthDay[2] + " " + (MonthFragment.monthName(parseInt(yearMonthDay[1])));
        else if (task.equals("Edit"))
            title = "Edit Event for " + yearMonthDay[2] + " " + (MonthFragment.monthName(parseInt(yearMonthDay[1])));

        toolbar.setTitle(title);
    }

    private void targetFields()
    {
        titleField = getActivity().findViewById(R.id.createEventTitle);
        detailsField = getActivity().findViewById(R.id.createEventDetails);
        recurrenceExpire = getActivity().findViewById(R.id.createEventExpire);
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

        colorOptions = new String[]{"Select Color", "Green", "Blue", "Yellow", "Red"};

        colorField = targetSpinner(R.id.createEventColor,colorOptions);

        colorField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void setupImageAttach()
    {
        Button button = getActivity().findViewById(R.id.createEventImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickActivity();
            }
        });
    }

    private void imagePickActivity()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,imageStatus);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == imageStatus && resultCode == Activity.RESULT_OK &&
                data!=null && data.getData() != null  ) {
            imageUri = data.getData();
            previewImage();
        }
    }

    private Bitmap getBitmap(Uri imageUri)
    {
        try {
            return MediaStore.Images.Media.
                    getBitmap(getActivity().getContentResolver(),imageUri);
        } catch (Exception e)
        {
        e.printStackTrace();
        }
        return null;
    }

    private void previewImage()
    {
        Bitmap bitmap;
        if(imageUri!=null)
            bitmap = getBitmap(imageUri);
        else
        {
            DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
            bitmap = databaseHandler.getImage(recId);
        }

        imagePreview = getActivity().findViewById(R.id.createImagePreview);

        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
        dr.setCornerRadius(20);
        imagePreview.setImageDrawable(dr);

        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog(bitmap);
            }
        });
    }

    private void imageDialog(Bitmap bitmap)
    {
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(getContext());
        View result = getLayoutInflater().inflate(R.layout.popup_image,null);
        alertDialog.setView(result);
        AlertDialog dialog = alertDialog.create();

        dialog.show();

        ImageView imageView = dialog.findViewById(R.id.popupImageContent);
        imageView.setImageBitmap(bitmap);

        ImageButton back = dialog.findViewById(R.id.popupImageBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void setupRecurrenceSelection()
    {
        recurrenceOptions = new String[]{"Select Recurrence", "None", "Weekly", "Monthly", "Yearly"};

        recurrenceField = targetSpinner(R.id.createEventReccurence,recurrenceOptions);

        recurrenceField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            EditText expire = getActivity().findViewById(R.id.createEventExpire);
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recurrence = parent.getItemAtPosition(position).toString();

                if (recurrence.equals("Select Recurrence") || recurrence.equals("None")) {
                    expire.setClickable(false);
                    expire.setEnabled(false);
                } else
                {
                    expire.setClickable(true);
                    expire.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                expire.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void timePickerStart()
    {
        startField = getActivity().findViewById(R.id.createEventStart);
        selectHour(startField,true);
    }

    private void timePickerEnd()
    {
        endField = getActivity().findViewById(R.id.createEventEnd);
        selectHour(endField,false);
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
        this.title = titleField.getText().toString();

        if(task.equals("Create") && !this.title.equals("") && isTimeSet() && isColorSet() && validRecurrence())
            return true;
        else if(task.equals("Edit") && !this.title.equals("") && isTimeSet() && isColorSet() )
            return true;

        return false;
    }

    private boolean isColorSet()
    {
        return !color.equals("Select Color");
    }

    private boolean isTimeSet()
    {
        return !startHour.equals("") && !endHour.equals("");
    }

    private int getRecExpiration()
    {
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
        else if(!recurrence.equals("Weekly") && expire>0 && expire <=12 )
            return true;
        else if(expire>0 && expire <= 120)
            return true;

        return false;
    }

    private void listenConfirmCreate()
    {
        TextView error = getActivity().findViewById(R.id.createEventError);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm() && task.equals("Create"))
                {
                    error.setVisibility(View.INVISIBLE);
                    createEvent();
                }
                else if(validateForm() && task.equals("Edit"))
                {
                    error.setVisibility(View.INVISIBLE);
                    editEvent();
                }
                else if(!validRecurrence())
                {
                    error.setText(R.string.recurrence_error);
                    error.setVisibility(View.VISIBLE);
                }
                else
                {
                    error.setText(R.string.empty_error);
                    error.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void encryptTitleDetails(DatabaseHandler databaseHandler)
    {
        if(!databaseHandler.getSetPassword().equals(""))
        {
            EncryptionHandler e = new EncryptionHandler();
            title = e.encrypt(title,MainActivity.rawPassword);
            details = e.encrypt(details,MainActivity.rawPassword);
        }
    }

    private void startFragment(int id, Fragment fragment)
    {
        getActivity().getSupportFragmentManager().beginTransaction().replace(id,
                fragment).commit(); //display the desired fragment
    }

    private void addEntry(DatabaseHandler databaseHandler, String date, int recId)
    {
        boolean op = databaseHandler.addEntry(recId,title,"None",color,startHour,
                endHour,date,recurrence,0,details);
        if(!op)
            Toast.makeText(getContext(),"Failed", Toast.LENGTH_LONG).show();
        else
            startFragment(R.id.fragmentContainer, new MonthFragment());
    }

    private void addEntries(DatabaseHandler  databaseHandler, int recId)
    {
        Calendar startDate = new GregorianCalendar();
        startDate.set(valueOf(yearMonthDay[0]), valueOf(yearMonthDay[1]), valueOf(yearMonthDay[2]));

        Calendar endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.MONTH,expire);
        while(!startDate.after(endDate))
        {
            String date = ""+ startDate.get(Calendar.YEAR) + "-" + startDate.get(Calendar.MONTH) + "-" +startDate.get(Calendar.DATE);
            addEntry(databaseHandler,date,recId);

            if(recurrence.equals("Weekly"))
                startDate.add(Calendar.DATE,7);
            else if(recurrence.equals("Monthly"))
                startDate.add(Calendar.MONTH,1);
            else if(recurrence.equals("Yearly"))
                startDate.add(Calendar.YEAR,1);
        }
    }


    private void createEvent()
    {
        details = detailsField.getText().toString();

        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        String date = yearMonthDay[0] + "-" + yearMonthDay[1] + "-" + yearMonthDay[2];

        encryptTitleDetails(databaseHandler);

        int lastIndex = databaseHandler.getLastIndex();
        if(recurrence.equals("None"))
        {
            addEntry(databaseHandler,date,lastIndex+1);
        }
        else
        {
            addEntries(databaseHandler,lastIndex+1);
        }

        if(imageUri!=null)
        {
            byte[] imageBytes = getBytes(Objects.requireNonNull(getBitmap(imageUri)));
            databaseHandler.addImage(lastIndex + 1, imageBytes);
        }


    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    private void editEvent()
    {
        details = detailsField.getText().toString();
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());

        encryptTitleDetails(databaseHandler);

        boolean op = databaseHandler.editEntry(title,"None",color,startHour,
                endHour,details,targetEvent);

        if(imageUri!=null)
        {
            byte[] imageBytes = getBytes(getBitmap(imageUri));
            databaseHandler.editImage(recId, imageBytes);
        }
        if(!op)
            Toast.makeText(getContext(),"Failed", Toast.LENGTH_LONG).show();
        else
            startFragment(R.id.fragmentContainer,new MonthFragment());

    }

    public void setEvent(int eventId)
    {
        targetEvent = eventId;
    }

    private int getIndex(String[] arr, String value)
    {
        for(int i=0;i<arr.length;i++)
        {
            if(arr[i].equals(value))
                return i;
        }
        return 0;
    }

    private void recurrenceDefaultUnclickable()
    {
        recurrenceField.setSelection(getIndex(recurrenceOptions,recurrence));
        recurrenceField.setClickable(false);
        recurrenceField.setEnabled(false);
        recurrenceExpire.setClickable(false);
        recurrenceExpire.setEnabled(false);
        recurrenceExpire.setInputType(EditorInfo.TYPE_NULL);
    }

    private void decryptTitleDetails(DatabaseHandler databaseHandler)
    {
        if(!databaseHandler.getSetPassword().equals(""))
        {
            EncryptionHandler e = new EncryptionHandler();
            title = e.decrypt(title,MainActivity.rawPassword);
            details = e.decrypt(details,MainActivity.rawPassword);
        }
    }


    private void loadDefaultValues()
    {
        String sql = "SELECT * FROM events WHERE eventId like "+targetEvent+";";
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        Cursor cursor = databaseHandler.getData(sql);

        while(cursor.moveToNext())
        {
            recId = cursor.getInt(1);
            title = cursor.getString(2);
            color = cursor.getString(4);
            startHour = cursor.getString(5);
            endHour = cursor.getString(6);
            recurrence = cursor.getString(8);
            expire = cursor.getInt(9);
            details = cursor.getString(10);
        }
        confirm.setText(R.string.Edit_Event);

        decryptTitleDetails(databaseHandler);

        titleField.setText(title);

        colorField.setSelection(getIndex(colorOptions,color));
        previewColor();
        previewImage();

        recurrenceDefaultUnclickable();

        detailsField.setText(details);
        startField.setText(startHour);
        endField.setText(endHour);



    }

}