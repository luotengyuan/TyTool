package com.lois.tytool.dialog.provider;

import android.app.Dialog;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.lois.tytool.R;
import com.lois.tytool.dialog.builder.LoadingDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class LoadingDialogProvider0 extends DialogProvider<LoadingDialogBuilder> {

    @Override
    public Dialog createInnerDialog(LoadingDialogBuilder dialogBuilder) {
        Dialog dialog =
                new AlertDialog.Builder(dialogBuilder.getContext()).setView(R.layout.comm_dialog_loading)
                        .create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}
