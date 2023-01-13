package com.lois.tytool.dialog.provider;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lois.tytool.R;
import com.lois.tytool.dialog.builder.ProgressDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;
import com.lois.tytool.dialog.view.ArcProgress;

public class ProgressDialogArcProvider extends DialogProvider<ProgressDialogBuilder> {

    @Override
    public Dialog createInnerDialog(ProgressDialogBuilder dialogBuilder) {
        View contentView = LayoutInflater.from(dialogBuilder.getContext())
                .inflate(R.layout.comm_arc_progress_dialog, null, false);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.progress_message);
        if (dialogBuilder.getMessage() != null) {
            messageTextView.setText(dialogBuilder.getMessage());
        } else {
            messageTextView.setText(R.string.loading);
        }
        final ArcProgress arcProgress =
                (ArcProgress) contentView.findViewById(R.id.arc_progress);
        dialogBuilder.setOnProgressListener(new ProgressDialogBuilder.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                arcProgress.setProgress(progress);
            }
        });
        Dialog dialog = new AlertDialog.Builder(dialogBuilder.getContext()).setView(contentView)
                .create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}
