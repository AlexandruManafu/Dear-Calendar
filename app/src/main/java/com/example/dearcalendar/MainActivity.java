package com.example.dearcalendar;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

//import static android.os.Build.VERSION_CODES.R;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    protected void startFragment(int id, Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(id,
                fragment).commit(); //display the desired fragment
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //Hide app title from toolbar
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        toolbar.setTitle("");
        toolbar.setSubtitle("");

        //Navigation Menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.NavDrawerOpen,  R.string.NavDrawerClose);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //######################

        //Listen to clicks on nav menu
        navigationView.setNavigationItemSelectedListener(this);


        // display month format by default
        if(savedInstanceState==null)//show it only on start, will not change if the device is rotated
        {
            startFragment(R.id.fragmentContainer, new MonthFragment());
            navigationView.setCheckedItem(R.id.monthView);
        }

    }
    //Ids of buttons are used
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.monthView://if this is clicked
                startFragment(R.id.fragmentContainer, new MonthFragment()); //display the month format

                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))

            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
}