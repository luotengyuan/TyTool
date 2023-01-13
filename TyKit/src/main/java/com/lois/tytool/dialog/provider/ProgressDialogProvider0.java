package com.lois.tytool.dialog.provider;

import android.app.Dialog;
import android.app.ProgressDialog;

import com.lois.tytool.R;
import com.lois.tytool.dialog.builder.ProgressDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class ProgressDialogProvider0 extends DialogProvider<ProgressDialogBuilder> {

    @Override
    public Dialog createInnerDialog(ProgressDialogBuilder dialogBuilder) {
        final ProgressDialog dialog = new ProgressDialog(dialogBuilder.getContext());
        dialog.setMax(100);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if (dialogBuilder.getMessage() != null) {
            dialog.setMessage(dialogBuilder.getMessage());
        } else {
            dialog.setMessage(dialogBuilder.getContext().getString(R.string.loading));
        }
        dialogBuilder.setOnProgressListener(new ProgressDialogBuilder.OnProgressListener() {
            @Override
            public void onProgress(int progress) {
                dialog.setProgress(progress);
            }
        });
        return dialog;
    }


}
