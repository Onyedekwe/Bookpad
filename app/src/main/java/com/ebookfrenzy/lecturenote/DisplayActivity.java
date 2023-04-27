package com.ebookfrenzy.lecturenote;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.AlarmClock;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Calendar;
import java.util.Locale;

public class DisplayActivity extends AppCompatActivity {

    public static final String Interstitial_ad_unit = "ca-app-pub-4208883735301832/7962669741";
    TextView coursename, topicname, textname, Monthtext, Yeartext, Daytext, Timetext;
    String nameCourse;
    String nameTopic;
    DatabaseHelper db;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinutes;
    Boolean AlarmButtonSet = false;
    LoadingAlert loadingAlert;
    AdView adView;
    private InterstitialAd intersAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        nameCourse = extras.getString("COURSE");
        nameTopic = extras.getString("TOPIC");
        loadingAlert = new LoadingAlert(DisplayActivity.this);


        coursename = findViewById(R.id.Displaycourse_name);
        topicname = findViewById(R.id.Displaytopic_name);
        textname = findViewById(R.id.Displaytext_name);

        Monthtext = findViewById(R.id.month);
        Yeartext = findViewById(R.id.year);
        Daytext = findViewById(R.id.day);
        Timetext = findViewById(R.id.time);

        db = new DatabaseHelper(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(nameCourse);
        setSupportActionBar(toolbar);
        adView = findViewById(R.id.adView);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        loadInterestrialAds();

        Cursor res = db.getAll(nameCourse, nameTopic);

        if (res.getCount() == 0) {
            Toast.makeText(DisplayActivity.this, R.string.entryNotSavedYet, Toast.LENGTH_SHORT).show();
            return;
        }

        while (res.moveToNext()) {
            coursename.setText(res.getString(1));
            topicname.setText(res.getString(2));
            textname.setText(res.getString(3));
            Monthtext.setText(res.getString(4));
            Yeartext.setText(res.getString(5));
            Daytext.setText(res.getString(6));
            Timetext.setText(res.getString(7));

            if (isNetworkAvailable()) {
                AdRequest request = new AdRequest.Builder().build();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (request != null) {
                            adView.setVisibility(View.VISIBLE);
                            adView.loadAd(request);
                        }
                    }
                }, 5000);
            } else {

            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String Course = coursename.getText().toString();
        String Topic = topicname.getText().toString();
        String Text = textname.getText().toString() ;
        String Month = Monthtext.getText().toString();
        String Year = Yeartext.getText().toString();
        String Day = Daytext.getText().toString();
        String Time = Timetext.getText().toString();

        switch (item.getItemId()) {
            case R.id.edit:
                Intent editintent = new Intent(DisplayActivity.this, UpdateActivity.class);
                editintent.putExtra("COURSE", Course);
                editintent.putExtra("TOPIC", Topic);
                editintent.putExtra("TEXT", Text);
                editintent.putExtra("MONTH", Month);
                editintent.putExtra("YEAR", Year);
                editintent.putExtra("DAY", Day);
                editintent.putExtra("TIME", Time);
                startActivity(editintent);

                break;

            case R.id.reminder:
                showReminderDialogue();
                break;


            case R.id.delete:
                if (intersAd != null) {
                    intersAd.show(DisplayActivity.this);
                }
                showAlertDialogue(getCurrentFocus(), topicname.getText().toString());
                break;

            case R.id.share:
                onSendMessage();
                break;
        }

        return true;
    }

    private void loadInterestrialAds() {
        if (isNetworkAvailable()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(this, Interstitial_ad_unit, adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    super.onAdLoaded(interstitialAd);
                    intersAd = interstitialAd;
                    interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            super.onAdClicked();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                        }

                        @Override
                        public void onAdImpression() {
                            super.onAdImpression();
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent();
                        }
                    });
                }
            });
        } else {

        }

    }


    public void showAlertDialogue(View v, String name) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.deleteThisTopic);
        alert.setMessage(getString(R.string.delete_confirmation) + " " + name);
        alert.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.deletetopic(name);
                Toast.makeText(DisplayActivity.this, R.string.deleted, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DisplayActivity.this, CourseListActivity.class);
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

    public void showReminderDialogue() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomreminderlayout);

        EditText descText = dialog.findViewById(R.id.desc_name);
        Button setTimeBtn = dialog.findViewById(R.id.btnSetTime);
        Button setReminderBtn = dialog.findViewById(R.id.btnSetReminder);

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinutes = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(DisplayActivity.this, R.style.TimePickerTheme, new TimePickerDialog.OnTimeSetListener() {

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
                        setTimeBtn.setText("Set time");
                    }
                });

                timePickerDialog.show();
            }
        });

        setReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!descText.getText().toString().isEmpty()) {
                    if (!AlarmButtonSet.equals(false)) {

                        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                        intent.putExtra(AlarmClock.EXTRA_HOUR, currentHour);
                        intent.putExtra(AlarmClock.EXTRA_MINUTES, currentMinutes);
                        intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name) + ": " + descText.getText().toString());
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                            dialog.dismiss();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(DisplayActivity.this, R.string.thereIsNoIntentApp, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(DisplayActivity.this, R.string.pleaseAddTime, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DisplayActivity.this, R.string.pleaseAddDescription, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AlarmButtonSet = false;

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialoAnimations;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

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

    public void onSendMessage() {
        String courseText = coursename.getText().toString();
        String topicText = topicname.getText().toString();
        String textText = textname.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, courseText.toUpperCase() + "\n" + " \n" + topicText + "\n" + " \n" + textText);

        String chooserTitle = getString(R.string.chooser);
        Intent chosenIntent = Intent.createChooser(intent, chooserTitle);
        startActivity(chosenIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(DisplayActivity.this, TopicListActivity.class);
        main.putExtra("COURSE", coursename.getText());
        startActivity(main);
        overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}