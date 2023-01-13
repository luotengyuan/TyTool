package com.lois.tytool.dialog.provider;

import android.app.Dialog;

import androidx.appcompat.app.AlertDialog;

import com.lois.tytool.dialog.builder.AlertDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class AlertDialogProvider extends DialogProvider<AlertDialogBuilder> {

    @Override
    public Dialog createInnerDialog(AlertDialogBuilder dialogBuilder) {
        dialogBuilder.defaultButtonText();
        return new AlertDialog.Builder(dialogBuilder.getContext()).setTitle(dialogBuilder.getTitle())
                .setMessage(dialogBuilder.getMessage())
                .setNegativeButton(dialogBuilder.getNegativeButtonText(),
                        dialogBuilder.getOnNegativeButtonClickListener())
                .setPositiveButton(dialogBuilder.getPositiveButtonText(),
                        dialogBuilder.getOnPositiveButtonClickListener())
                .create();
    }

}
