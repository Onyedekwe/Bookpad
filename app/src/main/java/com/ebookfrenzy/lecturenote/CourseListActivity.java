package com.ebookfrenzy.lecturenote;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.Adapters.card_course_caption_adapter;
import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.ebookfrenzy.lecturenote.Database.UserSettings;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import hotchemi.android.rate.AppRate;


public class CourseListActivity extends AppCompatActivity implements card_course_caption_adapter.OnNoteListener {
    AdView adView;
    ExtendedFloatingActionButton fab1, fab2;
    FloatingActionButton fab;
    Animation fabOpen, fabClose, rotateForward, rotateBackward;
    boolean isOpen = false;


    RecyclerView recyclerView;
    DatabaseHelper db;
    ArrayList<String> course;
    card_course_caption_adapter adapter;
    SearchView searchView;
    LinearLayout linearLayout;
    RelativeLayout relativeLayout;
    TextView optionstextview;
    ArrayList<String> checkcourses;
    String tempcourse;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    int isCheckeds;   //1 = systemdefault 2 = light 3 = dark
    private UserSettings settings;

    SharedPreferences sharedPreferences;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courselist);

        fab = findViewById(R.id.main_fab);
        fab1 = findViewById(R.id.create_task_fab);
        fab2 = findViewById(R.id.create_course_fab);

        fabOpen = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        fabClose = AnimationUtils.loadAnimation(this, R.anim.to_bottom);
        rotateForward = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateBackward = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = 1;
                Intent intent = new Intent(CourseListActivity.this, TodoActivity.class);
                intent.putExtra("VALUE", value);
                startActivity(intent);
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, CreateCourseActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);


        settings = (UserSettings) getApplication();

        sharedPreferences = getSharedPreferences(UserSettings.PREFERENCES, MODE_PRIVATE);
        loadSharedPreferences();

        searchView = findViewById(R.id.search_View);
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
        // navigationView.getMenu().findItem(R.id.nav_lectureNote).setChecked(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_lectureNote:
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.nav_todo:
                        int value = 0;
                        Intent todoIntent = new Intent(CourseListActivity.this, TodoActivity.class);
                        todoIntent.putExtra("VALUE", value);
                        startActivity(todoIntent);
                        todoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        break;

                    case R.id.nav_theme:
                        showThemeDialog();
                        break;


                    case R.id.nav_back_up:
                        Intent intent = new Intent(CourseListActivity.this, BackupActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.nav_rate_app:
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent1);

                        break;

                    case R.id.nav_ads:
                        Toast.makeText(CourseListActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_help:
                        Intent helpIntent = new Intent(CourseListActivity.this, HelpActivity.class);
                        startActivity(helpIntent);
                        break;
                }

                return false;
            }
        });
        navigationView.setCheckedItem(R.id.nav_lectureNote);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        db = new DatabaseHelper(this);
        course = new ArrayList<>();
        checkcourses = new ArrayList<>();
        recyclerView = findViewById(R.id.course_list);
        adapter = new card_course_caption_adapter(this, course, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        linearLayout = findViewById(R.id.empty_layout);
        relativeLayout = findViewById(R.id.relativeLayout);
        optionstextview = findViewById(R.id.Choosetxt);
        adView = findViewById(R.id.adView);

        displayData();


        if (isNetworkAvailable()) {
            AdRequest request = new AdRequest.Builder().build();
            relativeLayout.setVisibility(View.VISIBLE);
            adView.loadAd(request);

        } else {

        }


    }

    private void animateFab() {
        if (isOpen) {
            fab.startAnimation(rotateBackward);
            fab1.startAnimation(fabClose);
            fab2.startAnimation(fabClose);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isOpen = false;
        } else {
            fab.startAnimation(rotateForward);
            fab1.startAnimation(fabOpen);
            fab2.startAnimation(fabOpen);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isOpen = true;
        }
    }

    private void filterList(String text) {
        ArrayList<String> filterList = new ArrayList<>();
        displayData();
        for (String item : course) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }

        if (filterList.isEmpty()) {

        } else {

            adapter.setFilterList(filterList);
            HashSet<String> demo = new HashSet<>();
            demo.addAll(filterList);
            course.clear();
            course.addAll(filterList);


        }

    }


    public void displayData() {
        Cursor res = db.getdata();
        if (res.getCount() == 0) {
            linearLayout.setVisibility(View.VISIBLE);

        }
        HashSet<String> demo = new HashSet<>();
        while (res.moveToNext()) {
            demo.add(res.getString(1).trim());
        }
        course.clear();
        course.addAll(demo);
        Collections.sort(course);


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

    @Override
    public void onNoteClick(int position) {
        String temp = course.get(position);
        Intent intent = new Intent(CourseListActivity.this, TopicListActivity.class);
        intent.putExtra("COURSE", temp);
        startActivity(intent);


    }

    @Override
    public void onItemLongClick(int position) {

        String temp = course.get(position);
        showOptionsDialog(temp, position);
        tempcourse = temp;

    }


    private void showOptionsDialog(String name, int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
        LinearLayout infoLayout = dialog.findViewById(R.id.layoutInfo);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
                dialog.dismiss();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showAlertDialogue(getCurrentFocus(), name, position);

            }
        });

        infoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showInfoDialog(name);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    public void showAlertDialogue(View v, String name, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.deleteThisNote);
        alert.setMessage(getString(R.string.delete_confirmation) + " " + name);
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                db.deletecourse(name);
                course.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(CourseListActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                if (course.size() == 0) {
                    linearLayout.setVisibility(View.VISIBLE);
                }

            }
        });

        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();


        Button a = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        int buttonColor1 = ContextCompat.getColor(this, R.color.black);
        if (a != null) {
            a.setTextColor(buttonColor1);
        }

        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        int buttonColor2 = ContextCompat.getColor(this, R.color.red_color);
        if (b != null) {
            b.setTextColor(buttonColor2);
            b.setPadding(50, 0, 10, 0);
        }
    }

    private void showRateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rate Bookpad");
        builder.setMessage(R.string.rate_dialog_message);
        builder.setPositiveButton("Rate app", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent1);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button a = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        int buttonColor1 = ContextCompat.getColor(this, R.color.black);
        if (a != null) {
            a.setTextColor(buttonColor1);
        }

        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        int buttonColor2 = ContextCompat.getColor(this, R.color.royal);
        if (b != null) {
            b.setTextColor(buttonColor2);
            b.setPadding(50, 0, 10, 0);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("LaunchTime", System.currentTimeMillis());
        editor.apply();

    }
    private void showEditDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomeditlayout);
        TextView header = dialog.findViewById(R.id.Choosetxt);
        header.setText(R.string.EditCourse);
        EditText courseText = dialog.findViewById(R.id.course_name);
        courseText.setText(tempcourse);
        Button saveButton = dialog.findViewById(R.id.layoutButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                String courseDemo = courseText.getText().toString();
                Cursor res = db.getdata();
                while (res.moveToNext()) {
                    checkcourses.clear();
                    checkcourses.add(res.getString(1).trim());
                }

                if (!courseDemo.isEmpty()) {

                    if (checkcourses.contains(courseDemo.trim())) {
                        Toast.makeText(CourseListActivity.this, R.string.book_already_exist, Toast.LENGTH_SHORT).show();
                    } else {
                        boolean checkeditdata = db.editCourse(courseDemo, tempcourse);
                        if (checkeditdata) {
                            Toast.makeText(CourseListActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();

                            dialog.dismiss();
                            adapter.notifyDataSetChanged();
                            displayData();
                        } else {
                            Toast.makeText(CourseListActivity.this, R.string.unsuccessful, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(CourseListActivity.this, R.string.please_insert_book, Toast.LENGTH_SHORT).show();
                }
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    private void showInfoDialog(String name) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.coursebottominfolayout);
        TextView courseText = dialog.findViewById(R.id.info_course_name);
        TextView topicsNumberText = dialog.findViewById(R.id.info_topic_number);

        courseText.setText(name.trim());


        ArrayList<String> number = new ArrayList<>();
        Cursor res = db.getTopics(name);

        while (res.moveToNext()) {
            number.add(res.getString(2));
        }

        int topic_number = number.size();
        String numbers = Integer.toString(topic_number);

        topicsNumberText.setText(numbers);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    private void loadSharedPreferences() {

        String theme = sharedPreferences.getString(UserSettings.CUSTOM_THEME, UserSettings.DEFAULT_THEME);
        long launchTime = sharedPreferences.getLong("LaunchTime", 0);

        settings.setCustomTheme(theme);
        updateView();

        if(launchTime == 0){
            launchTime = System.currentTimeMillis();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("LaunchTime", launchTime);
            editor.apply();
        }

        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - launchTime;

        long daysDifference = timeDifference / (1000 * 60 * 60 * 24);

        if(daysDifference >= 7){
            showRateDialog();
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

        Dialog dialog = new Dialog(CourseListActivity.this);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}