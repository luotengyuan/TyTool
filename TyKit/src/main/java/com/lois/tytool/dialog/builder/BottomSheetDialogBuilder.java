package com.lois.tytool.dialog.builder;

import android.content.Context;

import com.lois.tytool.dialog.core.DialogBuilder;
import com.lois.tytool.dialog.core.DialogType;

public class BottomSheetDialogBuilder extends DialogBuilder<BottomSheetDialogBuilder> {

    public BottomSheetDialogBuilder(Context context) {
        super(context, DialogType.DIALOG_BOTTOM_SHEET);
    }

}
