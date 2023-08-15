package com.ebookfrenzy.lecturenote;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTopicActivity extends AppCompatActivity {

    String nameCourse;
    EditText Topictext, Text;
    TextView Coursetext, Monthtext, Yeartext, Daytext, Timetext;
    ArrayList<String> topics;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_book_toolbar);
        setSupportActionBar(toolbar);


        Coursetext = findViewById(R.id.course_name);
        Topictext = findViewById(R.id.topic_name);
        Text = findViewById(R.id.text_name);
        db = new DatabaseHelper(this);
        topics = new ArrayList<>();


        Monthtext = findViewById(R.id.month);
        Yeartext = findViewById(R.id.year);
        Daytext = findViewById(R.id.day);
        Timetext = findViewById(R.id.time);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        nameCourse = extras.getString("COURSE");
        Coursetext.setText(nameCourse);

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();


        SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM", Locale.getDefault());
        String currentMonth = sdfMonth.format(date);

        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy", Locale.getDefault());
        String currentYear= sdfYear.format(date);

        SimpleDateFormat sdfDay = new SimpleDateFormat("EEEE", Locale.getDefault());
        String currentDay = sdfDay.format(date);

        SimpleDateFormat sdfDayNumber = new SimpleDateFormat("dd", Locale.getDefault());
        String currentDayNumber = sdfDayNumber.format(date);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String time = timeFormat.format(date);

        String month = currentMonth +" "+ currentDayNumber;
        String year = currentYear + ",";

        Monthtext.setText(month);
        Yeartext.setText(year);
        Daytext.setText(currentDay);
        Timetext.setText(time);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add:
                try {
                    String course = Coursetext.getText().toString();
                    String topic = Topictext.getText().toString();
                    String text = Text.getText().toString();
                    String month = Monthtext.getText().toString();
                    String year = Yeartext.getText().toString();
                    String day = Daytext.getText().toString();
                    String time = Timetext.getText().toString();

                    Cursor res = db.getTopics(course);
                    while (res.moveToNext()) {
                        topics.add(res.getString(2).trim());
                    }

                    if (!topic.isEmpty()) {
                        if (topics.contains(topic.trim())) {
                            Toast.makeText(CreateTopicActivity.this, R.string.topicAlreadyExist, Toast.LENGTH_SHORT).show();
                        } else {
                            Boolean checkinsertdata = db.insertData(course, topic, text, month, year, day, time);
                            if (checkinsertdata == true) {
                                Toast.makeText(CreateTopicActivity.this, R.string.newEntryInserted, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateTopicActivity.this, DisplayActivity.class);
                                intent.putExtra("COURSE", course);
                                intent.putExtra("TOPIC", topic);
                                startActivity(intent);

                            } else {
                                Toast.makeText(CreateTopicActivity.this, R.string.newEntryNotInserted, Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else {
                        Toast.makeText(CreateTopicActivity.this, R.string.pleaseInsertContent, Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    Toast toast = Toast.makeText(this, R.string.databaseUnavailable, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {


        String topic = Topictext.getText().toString();
        String text = Text.getText().toString();


        if (topic.isEmpty() && text.isEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirm exit");
            alert.setMessage("You will be leaving without saving your work ?");
            alert.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(CreateTopicActivity.this, CourseListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);

                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();


            Button a = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (a != null) {
                a.setTextColor(Color.BLACK);
            }

            Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (b != null) {
                b.setTextColor(Color.RED);
                b.setPadding(50, 0, 10, 0);
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

}