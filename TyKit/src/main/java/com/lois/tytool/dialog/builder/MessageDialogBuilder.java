package com.lois.tytool.dialog.builder;

import android.content.Context;

import com.lois.tytool.dialog.core.DialogBuilder;
import com.lois.tytool.dialog.core.DialogType;

public class MessageDialogBuilder extends DialogBuilder<MessageDialogBuilder> {

    public MessageDialogBuilder(Context context) {
        super(context, DialogType.DIALOG_MESSAGE);
    }

}
