package com.ebookfrenzy.lecturenote;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
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

import java.util.ArrayList;

public class UpdateActivity extends AppCompatActivity {
    String nameCourse, nameTopic, nameText, nameMonth, nameYear, nameDay, nameTime;
    EditText Topictext, Text;
    TextView Coursetext, Monthtext, Yeartext, Daytext, Timetext;
    ArrayList<String> topics;
    String tempTopic;
    String tempText;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Note");
        setSupportActionBar(toolbar);


        Coursetext = findViewById(R.id.Updatecourse_name);
        Topictext = findViewById(R.id.Updatetopic_name);
        Text = findViewById(R.id.Updatetext_name);
        db = new DatabaseHelper(this);
        topics = new ArrayList<>();


        Monthtext = findViewById(R.id.Updatemonth);
        Yeartext = findViewById(R.id.Updateyear);
        Daytext = findViewById(R.id.Updateday);
        Timetext = findViewById(R.id.Updatetime);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        nameCourse = extras.getString("COURSE");
        nameTopic = extras.getString("TOPIC");
        nameText = extras.getString("TEXT");
        nameMonth = extras.getString("MONTH");
        nameYear = extras.getString("YEAR");
        nameDay = extras.getString("DAY");
        nameTime = extras.getString("TIME");

        Coursetext.setText(nameCourse);
        Topictext.setText(nameTopic);
        Text.setText(nameText);
        Monthtext.setText(nameMonth);
        Yeartext.setText(nameYear);
        Daytext.setText(nameDay);
        Timetext.setText(nameTime);

        tempTopic = nameTopic;
        tempText = nameText;
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


                    if (!topic.isEmpty()) {
                        Boolean checkinsertdata = db.updatedata(course, tempTopic, topic, tempText, text, month, year, day, time);
                        if (checkinsertdata == true) {
                            Toast.makeText(UpdateActivity.this, R.string.entryUpdated, Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(UpdateActivity.this, DisplayActivity.class);
                            intent.putExtra("COURSE", course);
                            intent.putExtra("TOPIC", topic);
                            startActivity(intent);
                            overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);


                        } else {
                            Toast.makeText(UpdateActivity.this, R.string.entryNotUpdated, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(UpdateActivity.this, R.string.pleaseInsertContent, Toast.LENGTH_SHORT).show();
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


        if (topic.equals(tempTopic) && text.equals(tempText)) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Confirm exit");
            alert.setMessage("You will be leaving without saving your work ?");
            alert.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(UpdateActivity.this, CourseListActivity.class);
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
                a.setTextColor(Color.GRAY);
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