package com.lois.tytool.dialog.builder;

import android.content.Context;

import com.lois.tytool.dialog.core.DialogBuilder;
import com.lois.tytool.dialog.core.DialogType;

public class AlertDialogBuilder extends DialogBuilder<AlertDialogBuilder> {

    public AlertDialogBuilder(Context context) {
        super(context, DialogType.DIALOG_ALERT);
    }

}
