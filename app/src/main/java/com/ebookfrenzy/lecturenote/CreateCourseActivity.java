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

import androidx.annotation.NonNull;
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

public class CreateCourseActivity extends AppCompatActivity {
    EditText courseText, topicText, Text;
    TextView monthText, yearText, dayText, timeText;
    DatabaseHelper db;
    ArrayList<String> courses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.create_book_toolbar);
        setSupportActionBar(toolbar);

        courseText = findViewById(R.id.course_name);
        topicText = findViewById(R.id.topic_name);
        Text = findViewById(R.id.text_name);
        monthText = findViewById(R.id.month);
        yearText = findViewById(R.id.year);
        dayText = findViewById(R.id.day);
        timeText = findViewById(R.id.time);

        db = new DatabaseHelper(this);
        courses = new ArrayList<>();

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

        monthText.setText(month);
        yearText.setText(year);
        dayText.setText(currentDay);
        timeText.setText(time);


    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add) {
            try {
                String course = courseText.getText().toString().trim();
                String topic = topicText.getText().toString().trim();
                String text = Text.getText().toString().trim();
                String month = monthText.getText().toString().trim();
                String year = yearText.getText().toString().trim();
                String day = dayText.getText().toString().trim();
                String time = timeText.getText().toString().trim();

                Cursor res = db.getdata();
                while (res.moveToNext()) {
                    courses.add(res.getString(1).trim());
                }
                if (!course.isEmpty()) {
                    if (!topic.isEmpty()) {
                        if (courses.contains(course.trim())) {
                            Toast.makeText(CreateCourseActivity.this, R.string.book_already_exist, Toast.LENGTH_SHORT).show();
                        } else {
                            boolean checkInsertData = db.insertData(course, topic, text, month, year, day, time);
                            if (checkInsertData) {
                                Toast.makeText(CreateCourseActivity.this, R.string.newEntryInserted, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateCourseActivity.this, DisplayActivity.class);
                                intent.putExtra("COURSE", course);
                                intent.putExtra("TOPIC", topic);
                                startActivity(intent);

                            } else {
                                Toast.makeText(CreateCourseActivity.this, R.string.newEntryNotInserted, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(CreateCourseActivity.this, R.string.pleaseInsertContent, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateCourseActivity.this, R.string.please_insert_book, Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                Toast toast = Toast.makeText(this, R.string.databaseUnavailable, Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {


        String course = courseText.getText().toString();
        String topic = topicText.getText().toString();
        String text = Text.getText().toString();


        if (course.isEmpty() && topic.isEmpty() && text.isEmpty()) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirm exit");
            alert.setMessage("You will be leaving without saving your work ?");
            alert.setPositiveButton("Leave", (dialog, which) -> {

                Intent intent = new Intent(CreateCourseActivity.this, CourseListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);


            });

            alert.setNegativeButton("Cancel", (dialog, which) -> {

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