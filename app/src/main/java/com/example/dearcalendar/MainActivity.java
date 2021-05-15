package com.example.dearcalendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;
import java.util.HashMap;

//import static android.os.Build.VERSION_CODES.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    public static NavigationView navigationView;
    Toolbar toolbar;
    static HashMap<String,Integer> colors;
    public static Calendar currentDate;
    public static DatabaseHandler databaseHandler;
    public static Boolean eventsLocked;
    public static String rawPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        hideAppName();
        setupNavigation();
        setColors();
        currentDate = Calendar.getInstance();
        databaseHandler = new DatabaseHandler(this);

        // display month format by default
        if(savedInstanceState==null)//show it only on start, will not change if the device is rotated
        {
            startFragment(R.id.fragmentContainer, new MonthFragment());
            navigationView.setCheckedItem(R.id.monthView);
        }

        eventsLocked = true;
        rawPassword = "";
        displayLock();



    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.monthView://if this is clicked
                startFragmentBack(R.id.fragmentContainer, new MonthFragment()); //display the month format
                navigationView.setCheckedItem(R.id.monthView);
                break;
            case R.id.settings:
                startFragmentBack(R.id.fragmentContainer, new SettingsFragment());
                navigationView.setCheckedItem(R.id.options);
                navigationView.setCheckedItem(R.id.settings);
                break;
            case R.id.menuUnlockEvents:
                unlockPopup();
                break;
            case R.id.menuLockEvents:
                lockPopup();
                break;
            case R.id.help:
                startFragmentBack(R.id.fragmentContainer, new HelpFragment());
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragmentBack(int id, Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(id,
                fragment).addToBackStack(null).commit(); //display the desired fragment
    }
    private void startFragment(int id, Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(id,
                fragment).commit(); //display the desired fragment
    }

    private void hideAppName()
    {
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        toolbar.bringToFront();
    }

    private void setupNavigation()
    {
        navigationView.bringToFront(); //fix for fragment selection
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.NavDrawerOpen,  R.string.NavDrawerClose);

        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }



    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public static void displayLock() {
        Menu menu = navigationView.getMenu();

        MenuItem lock = menu.findItem(R.id.menuLockEvents);
        MenuItem unlock = menu.findItem(R.id.menuUnlockEvents);

        if (databaseHandler.getSetPassword().equals(""))
        {
            eventsLocked = false;
            lock.setVisible(true);
            lock.setEnabled(false);
            unlock.setVisible(false);
        } else if (eventsLocked)
        {
            lock.setVisible(false);
            unlock.setVisible(true);
        } else {
            lock.setVisible(true);
            lock.setEnabled(true);
            unlock.setVisible(false);
        }
    }
    private void unlockPopup()
    {
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(this);
        View result = getLayoutInflater().inflate(R.layout.unlock_popup,null);
        alertDialog.setView(result);
        Dialog dialog = alertDialog.create();

        dialog.show();

        EditText password = dialog.findViewById(R.id.unlockPassword);
        TextView passwordHint = dialog.findViewById(R.id.unlockHint);
        TextView error = dialog.findViewById(R.id.unlockError);

        Button confirm = dialog.findViewById(R.id.unlockPasswordConfirm);
        Button cancel = dialog.findViewById(R.id.unlockPasswordCancel);

        String hint = databaseHandler.getPasswordHint();
        passwordHint.setText(hint);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockIfValid(dialog,password.getText().toString());
                error.setText("Incorrect Password");
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }

    private void unlockIfValid(Dialog dialog,String password)
    {
        EncryptionHandler e = new EncryptionHandler();
        String initialPw = databaseHandler.getSetPassword();
        if(e.pwMatch(initialPw,password))
        {
            eventsLocked = false;
            rawPassword = password;
            displayLock();
            startFragment(R.id.fragmentContainer,new MonthFragment());
            dialog.cancel();
        }
    }

    private void lockPopup()
    {
        AlertDialog.Builder alertDialog  = new AlertDialog.Builder(this);
        View result = getLayoutInflater().inflate(R.layout.reset_confirmation,null);
        alertDialog.setView(result);
        Dialog dialog = alertDialog.create();
        dialog.show();

        TextView message = dialog.findViewById(R.id.confirmText);
        String text = "Are you sure you sure you want to lock the events?";
        message.setText(text);

        Button confirm = dialog.findViewById(R.id.resetConfirm);
        Button cancel = dialog.findViewById(R.id.resetCancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventsLocked=true;
                displayLock();
                startFragment(R.id.fragmentContainer,new MonthFragment());
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }


    //Other utilities
    private int getBackgroundColor(int colorId)
    {
        return ContextCompat.getColor(this, colorId);
    }

    private void setColors()
    {
        colors = new HashMap<>();
        colors.put("Select Color",getBackgroundColor(R.color.white));
        colors.put("Green",getBackgroundColor(R.color.caribbean));
        colors.put("Blue",getBackgroundColor(R.color.cornflower));
        colors.put("Yellow",getBackgroundColor(R.color.dandelion));
        colors.put("Red",getBackgroundColor(R.color.brick));
    }
}