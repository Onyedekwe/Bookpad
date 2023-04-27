package com.ebookfrenzy.lecturenote;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ebookfrenzy.lecturenote.Adapters.card_topic_caption_adapter;
import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class TopicListActivity extends AppCompatActivity implements card_topic_caption_adapter.OnNoteListener {
    AdView adView;
    RecyclerView recyclerView;
    DatabaseHelper db;
    ArrayList<String> topics;
    card_topic_caption_adapter adapter;
    String nameCourse;
    String nameTopic;
    ExtendedFloatingActionButton fab2;
    RelativeLayout relativeLayout;
    String course;
    String topic;
    String text;
    String month;
    String year;
    String day;
    String time;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(TopicListActivity.this, CourseListActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        nameCourse = extras.getString("COURSE");


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(nameCourse);
        setSupportActionBar(toolbar);

        fab2 = findViewById(R.id.create_topic_fab);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopicListActivity.this, CreateTopicActivity.class);
                intent.putExtra("COURSE", nameCourse);
                startActivity(intent);
            }
        });

        db = new DatabaseHelper(this);
        topics = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view);
        adapter = new card_topic_caption_adapter(this, topics, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        relativeLayout = findViewById(R.id.relativeLayout);
        adView = findViewById(R.id.adView);


        displayData();

        if (isNetworkAvailable()) {
            AdRequest request = new AdRequest.Builder().build();
            relativeLayout.setVisibility(View.VISIBLE);
            adView.loadAd(request);
        } else {

        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //scroll down
                if (dy > 15 && fab2.isExtended()) {
                    fab2.shrink();
                }

                //scroll up
                if (dy < -15 && !fab2.isExtended()) {
                    fab2.extend();
                }

                //At the top
                if (!recyclerView.canScrollVertically(-1)) {
                    fab2.extend();
                }


            }
        });
    }


    public void displayData() {
        Cursor res = db.getTopics(nameCourse);
        if (res.getCount() == 0) {

        }
        topics.clear();
        while (res.moveToNext()) {
            topics.add(res.getString(2));
        }

    }

    @Override
    public void onNoteClick(int position) {
        String temp = topics.get(position);
        Intent intent = new Intent(TopicListActivity.this, DisplayActivity.class);
        intent.putExtra("COURSE", nameCourse);
        intent.putExtra("TOPIC", temp);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int position) {
        String temp = topics.get(position);
        nameTopic = temp;
        showDialog(temp, position);
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


    private void showDialog(String name, int position) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
        LinearLayout infoLayout = dialog.findViewById(R.id.layoutInfo);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Cursor res = db.getAll(nameCourse, nameTopic);

                if (res.getCount() == 0) {
                    Toast.makeText(TopicListActivity.this, R.string.databaseError, Toast.LENGTH_SHORT).show();
                    return;
                }


                while (res.moveToNext()) {
                    course = res.getString(1);
                    topic = res.getString(2);
                    text = res.getString(3);
                    month = res.getString(4);
                    year = res.getString(5);
                    day = res.getString(6);
                    time = res.getString(7);
                }

                Intent editintent = new Intent(TopicListActivity.this, UpdateActivity.class);
                editintent.putExtra("COURSE", course);
                editintent.putExtra("TOPIC", topic);
                editintent.putExtra("TEXT", text);
                editintent.putExtra("MONTH", month);
                editintent.putExtra("YEAR", year);
                editintent.putExtra("DAY", day);
                editintent.putExtra("TIME", time);
                startActivity(editintent);


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
                showInfoDialog(nameCourse, nameTopic);
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
        alert.setTitle(R.string.deleteThisTopic);
        alert.setMessage(getString(R.string.delete_confirmation) + " " + name);
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deletetopic(name);
                adapter.notifyItemRemoved(position);
                topics.remove(position);

                Toast.makeText(TopicListActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
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

    private void showInfoDialog(String name, String topic) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.topicbottominfolayout);
        TextView courseText = dialog.findViewById(R.id.info_course_name);
        TextView topicsName = dialog.findViewById(R.id.info_topic_name);
        TextView topicdate = dialog.findViewById(R.id.info_topic_date);

        courseText.setText(name.trim());
        topicsName.setText(topic);


        ArrayList<String> number = new ArrayList<>();
        Cursor res = db.getDate(name, topic);

        while (res.moveToNext()) {
            month = res.getString(4);
            year = res.getString(5);
            day = res.getString(6);
        }

        topicdate.setText(day + ", " + month + ", " + year.replace(",", ""));


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.topic_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_menu));

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

        return super.onCreateOptionsMenu(menu);
    }


    private void filterList(String text) {
        ArrayList<String> filterList = new ArrayList<>();
        displayData();
        for (String item : topics) {
            if (item.toLowerCase().contains(text.toLowerCase())) {
                filterList.add(item);
            }
        }

        if (filterList.isEmpty()) {

        } else {
            adapter.setFilterList(filterList);
            HashSet<String> demo = new HashSet<>();
            demo.addAll(filterList);
            topics.clear();
            topics.addAll(filterList);
            Collections.sort(topics, String.CASE_INSENSITIVE_ORDER);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}