package com.lois.tytool.dialog.provider;

import android.app.Dialog;
import android.app.ProgressDialog;

import com.lois.tytool.R;
import com.lois.tytool.dialog.builder.LoadingDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class LoadingDialogProvider1 extends DialogProvider<LoadingDialogBuilder> {

    @Override
    public Dialog createInnerDialog(LoadingDialogBuilder dialogBuilder) {
        final ProgressDialog dialog = new ProgressDialog(dialogBuilder.getContext());
        dialog.setIndeterminate(true);
        dialog.setProgressNumberFormat(null);
        dialog.setProgressPercentFormat(null);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if (dialogBuilder.getMessage() != null) {
            dialog.setMessage(dialogBuilder.getMessage());
        } else {
            dialog.setMessage(dialogBuilder.getContext().getString(R.string.loading));
        }
        return dialog;
    }


}
