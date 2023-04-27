package com.ebookfrenzy.lecturenote;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ebookfrenzy.lecturenote.Database.DatabaseHelper;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class BackupActivity extends AppCompatActivity {

    public static final int CREATE_REQUEST_CODE = 40;
    public static final int OPEN_REQUEST_CODE = 41;
    public static final int SAVE_REQUEST_CODE = 42;

    public static final String Interstitial_ad_unit = "ca-app-pub-4208883735301832/7962669741";
    Button btnBackup;
    Button btnRestore;
    DatabaseHelper db;
    LoadingAlert loadingAlert;
    private InterstitialAd intersAd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.backup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnBackup = findViewById(R.id.backupBtn);
        btnRestore = findViewById(R.id.restoreBtn);
        db = new DatabaseHelper(this);
        loadingAlert = new LoadingAlert(BackupActivity.this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {

            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        loadInterestrialAds();

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, "Bookpad_Backup");


                loadingAlert.startAlertDialogBackup();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(intent, CREATE_REQUEST_CODE);
                        loadingAlert.closeAlertDialog();

                    }
                }, 2500);
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                startActivityForResult(intent, OPEN_REQUEST_CODE);


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.backup_alert_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.alert:
                showInfoDialog();
                break;


        }
        return false;
    }

    private void loadInterestrialAds() {
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
                        loadInterestrialAds();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        loadInterestrialAds();
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
    }


    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        final Uri[] currentUri = {null};

        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CREATE_REQUEST_CODE) {
                if (resultData != null) {

                    loadingAlert.startAlertDialogBackupFinalizing();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            currentUri[0] = resultData.getData();
                            writeFileContent(currentUri[0]);
                            loadingAlert.closeAlertDialog();
                        }
                    }, 5000);


                }
            } else if (requestCode == SAVE_REQUEST_CODE) {
                if (resultData != null) {

                }
            } else if (requestCode == OPEN_REQUEST_CODE) {
                if (resultData != null) {
                    currentUri[0] = resultData.getData();
                    try {
                        readFileContent(currentUri[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void writeFileContent(Uri uri) {

        try {
            ParcelFileDescriptor pfd = this.getContentResolver().openFileDescriptor(uri, "w");

            FileWriter fw = new FileWriter(pfd.getFileDescriptor());


            ArrayList<String> idList = new ArrayList<>();
            ArrayList<String> courseList = new ArrayList<>();
            ArrayList<String> topicList = new ArrayList<>();
            ArrayList<String> textList = new ArrayList<>();
            ArrayList<String> monthList = new ArrayList<>();
            ArrayList<String> yearList = new ArrayList<>();
            ArrayList<String> dayList = new ArrayList<>();
            ArrayList<String> timeList = new ArrayList<>();


            Cursor res = db.getdata();
            if (res.getCount() == 0) {

            }
            while (res.moveToNext()) {
                idList.add(res.getString(0).replace("`", "'"));
                courseList.add(res.getString(1).replace("`", "'"));
                topicList.add(res.getString(2).replace("`", "'"));
                textList.add(res.getString(3).replace("`", "'"));
                monthList.add(res.getString(4).replace("`", "'"));
                yearList.add(res.getString(5).replace("`", "'"));
                dayList.add(res.getString(6).replace("`", "'"));
                timeList.add(res.getString(7).replace("`", "'"));
            }

            fw.write("");
            for (int i = 0; i < idList.size(); i++) {
                fw.append("");
                fw.append(courseList.get(i));
                fw.append("`");
                fw.append("");
                fw.append(topicList.get(i));
                fw.append("`");
                fw.append("");
                fw.append(textList.get(i));
                fw.append("`");
                fw.append("");
                fw.append(monthList.get(i));
                fw.append("`");
                fw.append("");
                fw.append(yearList.get(i));
                fw.append("`");
                fw.append("");
                fw.append(dayList.get(i));
                fw.append("`");
                fw.append("");
                fw.append(timeList.get(i));
                fw.append("`");
                fw.append("\n");
            }
            Toast.makeText(BackupActivity.this, "Backup complete", Toast.LENGTH_SHORT).show();
            fw.close();

            if (intersAd != null) {
                intersAd.show(BackupActivity.this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readFileContent(Uri uri) throws IOException {

        try {

            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            Scanner scanner = new Scanner(inputStream);
            String line;
            db.deleteAll();

            scanner.useDelimiter("`");
            while ((scanner.hasNextLine())) {
                String course = scanner.next();
                String topic = scanner.next();
                String text = scanner.next();
                String month = scanner.next();
                String year = scanner.next();
                String day = scanner.next();
                String time = scanner.next();

                db.insertData(course.trim(), topic.trim(), text.trim(), month.trim(), year.trim(), day.trim(), time.trim());
            }

            reader.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(BackupActivity.this, CourseListActivity.class);
        loadingAlert.startAlertDialogRestore();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BackupActivity.this, "Backup restored", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                loadingAlert.closeAlertDialog();
            }
        }, 5000);


    }

    public void showInfoDialog() {

        Dialog dialog = new Dialog(BackupActivity.this);
        dialog.setContentView(R.layout.custom_alert_backup_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.theme_dialog_background));
        dialog.setCancelable(true);
        dialog.show();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.transition_in_left, R.anim.transition_out_right);
    }

}

