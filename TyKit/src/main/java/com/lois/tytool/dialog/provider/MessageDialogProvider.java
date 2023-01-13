package com.lois.tytool.dialog.provider;

import android.app.Dialog;

import androidx.appcompat.app.AlertDialog;

import com.lois.tytool.dialog.builder.MessageDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class MessageDialogProvider extends DialogProvider<MessageDialogBuilder> {

    @Override
    public Dialog createInnerDialog(MessageDialogBuilder dialogBuilder) {
        dialogBuilder.defaultButtonText();
        return new AlertDialog.Builder(dialogBuilder.getContext()).setTitle(dialogBuilder.getTitle())
                .setMessage(dialogBuilder.getMessage())
                .setPositiveButton(dialogBuilder.getPositiveButtonText(),
                        dialogBuilder.getOnPositiveButtonClickListener())
                .create();
    }

}
