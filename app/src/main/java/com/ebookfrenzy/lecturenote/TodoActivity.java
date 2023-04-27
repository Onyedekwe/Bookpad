package com.ebookfrenzy.lecturenote;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.Adapters.Todo_adapter;
import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.ebookfrenzy.lecturenote.Database.UserSettings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

public class TodoActivity extends AppCompatActivity implements Todo_adapter.OnNoteListener {
    AdView adView;
    ExtendedFloatingActionButton fab;
    SearchView searchView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    RecyclerView recyclerView;
    DatabaseHelper db;
    ArrayList<String> task;
    Todo_adapter adapter;

    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinutes;
    Boolean AlarmButtonSet = false;
    int isCheckeds;   //1 = systemdefault 2 = light 3 = dark
    ArrayList<String> taskCheck;
    private UserSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        fab = findViewById(R.id.todo_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialogue();
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.to_do);
        setSupportActionBar(toolbar);

        settings = (UserSettings) getApplication();
        loadSharedPreferences();

        searchView = findViewById(R.id.todo_search_View);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        //   navigationView.getMenu().findItem(R.id.nav_todo).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_lectureNote:
                        Intent homeintent = new Intent(TodoActivity.this, CourseListActivity.class);
                        startActivity(homeintent);
                        break;

                    case R.id.nav_back_up:

                        Intent intent = new Intent(TodoActivity.this, BackupActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_rate_app:
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent1);
                        break;

                    case R.id.nav_todo:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_theme:
                        showThemeDialog();
                        break;

                    case R.id.nav_ads:
                        Toast.makeText(TodoActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_help:
                        Intent helpIntent = new Intent(TodoActivity.this, HelpActivity.class);
                        startActivity(helpIntent);
                        break;

                }

                return false;
            }
        });
        navigationView.setCheckedItem(R.id.nav_todo);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        db = new DatabaseHelper(this);
        task = new ArrayList<>();
        taskCheck = new ArrayList<>();


        recyclerView = findViewById(R.id.todo_list);
        adapter = new Todo_adapter(this, task, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linearLayout = findViewById(R.id.empty_layout2);
        relativeLayout = findViewById(R.id.relativeLayout);
        adView = findViewById(R.id.adView);
        displayData();

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        int value = extras.getInt("VALUE");
        if (value == 1) {
            showBottomDialogue();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        if (isNetworkAvailable()) {
            AdRequest request = new AdRequest.Builder().build();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    relativeLayout.setVisibility(View.VISIBLE);
                    adView.loadAd(request);
                }
            }, 5000);
        } else {

        }


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //scroll down
                if (dy > 15 && fab.isExtended()) {
                    fab.shrink();
                }

                //scroll up
                if (dy < -15 && !fab.isExtended()) {
                    fab.extend();
                }

                //At the top
                if (!recyclerView.canScrollVertically(-1)) {
                    fab.extend();
                }
            }
        });

    }

    private void filterList(String text) {
        task.clear();
        ArrayList<String> filterList = new ArrayList<>();
        displayData();
        for (String item : task) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);

            }
        }

        if (filterList.isEmpty()) {

        } else {

            adapter.setFilterList(filterList);
            task.clear();
            task.addAll(filterList);


        }

    }

    public void displayData() {
        Cursor res = db.gettask();
        if (res.getCount() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        }

        while (res.moveToNext()) {
            task.add(res.getString(1));
        }

    }

    @Override
    public void onNoteClick(int position) {

    }

    @Override
    public void onItemLongClick(int position) {

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent main = new Intent(TodoActivity.this, CourseListActivity.class);
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main);
            overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);

        }
    }

    public void showBottomDialogue() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.todobottomlayout);

        EditText descText = dialog.findViewById(R.id.desc_name);
        Button setTimeBtn = dialog.findViewById(R.id.btnSetTime);
        Button saveBtn = dialog.findViewById(R.id.btnSetReminder);

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(TodoActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        currentHour = hourOfDay;
                        currentMinutes = minute;
                        setTimeBtn.setText(String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinutes));
                        AlarmButtonSet = true;
                    }
                }, currentHour, currentMinutes, false);
                timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        AlarmButtonSet = false;
                        setTimeBtn.setText("Set alerts");
                    }
                });
                timePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {


                if (!descText.getText().toString().trim().isEmpty()) {
                    if (!AlarmButtonSet.equals(false)) {
                        Cursor res = db.gettask();
                        while (res.moveToNext()) {
                            taskCheck.add(res.getString(1).trim());
                        }
                        if (taskCheck.contains(descText.getText().toString().trim())) {
                            Toast.makeText(TodoActivity.this, "Task already exist", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                            intent.putExtra(AlarmClock.EXTRA_HOUR, currentHour);
                            intent.putExtra(AlarmClock.EXTRA_MINUTES, currentMinutes);
                            intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name) + ": " + descText.getText().toString());
                            if (intent.resolveActivity(getPackageManager()) != null) {

                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                db.insertTask(descText.getText().toString().trim(), 0);
                                task.add(descText.getText().toString().trim());
                                adapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.INVISIBLE);
                                dialog.dismiss();

                                startActivity(intent);
                                Toast.makeText(TodoActivity.this, "Task inserted", Toast.LENGTH_SHORT).show();



                            } else {
                                Toast.makeText(TodoActivity.this, R.string.thereIsNoIntentApp, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else if (taskCheck.contains(descText.getText().toString().trim())) {
                        Toast.makeText(TodoActivity.this, "Task already exist", Toast.LENGTH_SHORT).show();
                    } else {
                        db.insertTask(descText.getText().toString().trim(), 0);
                        task.add(descText.getText().toString().trim());
                        adapter.notifyDataSetChanged();
                        linearLayout.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                        Toast.makeText(TodoActivity.this, "Task inserted", Toast.LENGTH_SHORT).show();


                    }
                } else {
                    Toast.makeText(TodoActivity.this, R.string.pleaseAddDescription, Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AlarmButtonSet = false;
            }
        });

    }

    private void loadSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.DEFAULT_THEME);

        settings.setCustomTheme(theme);
        updateView();
    }


    public boolean isNetworkAvailable() {
        try {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (manager != null) {
                networkInfo = manager.getActiveNetworkInfo();
            }
            return networkInfo != null && networkInfo.isConnected();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void updateView() {
        if (settings.getCustomTheme().equals(UserSettings.DEFAULT_THEME)) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            isCheckeds = 0;
        }

        if (settings.getCustomTheme().equals(UserSettings.LIGHT_THEME)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            isCheckeds = 1;
        }

        if (settings.getCustomTheme().equals(UserSettings.DARK_THEME)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            isCheckeds = 2;
        }
    }


    public void showThemeDialog() {

        Dialog dialog = new Dialog(TodoActivity.this);
        dialog.setContentView(R.layout.custom_theme_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.theme_dialog_background));
        dialog.setCancelable(true);


        RadioButton systemDefault = dialog.findViewById(R.id.systemDefaultCheck);
        RadioButton light = dialog.findViewById(R.id.lightCheck);
        RadioButton dark = dialog.findViewById(R.id.darkCheck);


        if (settings.getCustomTheme().equals(UserSettings.DEFAULT_THEME)) {
            systemDefault.setChecked(true);
        }

        if (settings.getCustomTheme().equals(UserSettings.LIGHT_THEME)) {
            light.setChecked(true);

        }

        if (settings.getCustomTheme().equals(UserSettings.DARK_THEME)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            dark.setChecked(true);
        }


        systemDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                settings.setCustomTheme(UserSettings.DEFAULT_THEME);
                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
                dialog.dismiss();
                updateView();


            }
        });


        light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                settings.setCustomTheme(UserSettings.LIGHT_THEME);
                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
                dialog.dismiss();
                updateView();

            }
        });

        dark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settings.setCustomTheme(UserSettings.DARK_THEME);
                SharedPreferences.Editor editor = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE).edit();
                editor.putString(UserSettings.CUSTOM_THEME, settings.getCustomTheme());
                editor.apply();
                dialog.dismiss();
                updateView();
            }
        });


        dialog.show();

    }


}