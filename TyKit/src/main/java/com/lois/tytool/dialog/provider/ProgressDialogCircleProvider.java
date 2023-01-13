package com.lois.tytool.dialog.provider;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lois.tytool.R;
import com.lois.tytool.dialog.builder.ProgressDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;
import com.lois.tytool.dialog.view.CircleProgress;
import com.lois.tytool.dialog.view.DonutProgress;

public class ProgressDialogCircleProvider extends DialogProvider<ProgressDialogBuilder> {

    @Override
    public Dialog createInnerDialog(ProgressDialogBuilder dialogBuilder) {
        View contentView = LayoutInflater.from(dialogBuilder.getContext())
                .inflate(R.layout.comm_circle_progress_dialog, null, false);
        TextView messageTextView = (TextView) contentView.findViewById(R.id.progress_message);
        if (dialogBuilder.getMessage() != null) {
            messageTextView.setText(dialogBuilder.getMessage());
        } else {
            messageTextView.setText(R.string.loading);
        }
        final CircleProgress circleProgress =
                (CircleProgress) contentView.findViewById(R.id.circle_progress);
        dialogBuilder.setOnProgressListener(new ProgressDialogBuilder.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                circleProgress.setProgress(progress);
            }
        });
        Dialog dialog = new AlertDialog.Builder(dialogBuilder.getContext()).setView(contentView)
                .create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}
