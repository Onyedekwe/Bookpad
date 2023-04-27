package com.ebookfrenzy.lecturenote;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class LoadingAlert {
    private final Activity activity;
    private AlertDialog dialog;

    LoadingAlert(Activity myActivity) {
        activity = myActivity;
    }

    void startAlertDialogRestore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout_loading, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    void startAlertDialogBackup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout_backup, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    void startAlertDialogBackupFinalizing() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_layout_backup_finalizing, null));
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
    }

    void closeAlertDialog() {
        dialog.dismiss();
    }
}
