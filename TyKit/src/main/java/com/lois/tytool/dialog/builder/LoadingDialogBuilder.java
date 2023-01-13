package com.lois.tytool.dialog.builder;

import android.content.Context;

import com.lois.tytool.dialog.core.DialogBuilder;
import com.lois.tytool.dialog.core.DialogType;

public class LoadingDialogBuilder extends DialogBuilder<LoadingDialogBuilder> {

    protected LoadingDialogBuilder(Context context, int dialogType) {
        super(context, dialogType);
    }

    public static LoadingDialogBuilder getLoadingDialogBuilder0(Context context) {
        return new LoadingDialogBuilder(context, DialogType.DIALOG_LOADING0);
    }

    public static LoadingDialogBuilder getLoadingDialogBuilder1(Context context) {
        return new LoadingDialogBuilder(context, DialogType.DIALOG_LOADING1);
    }

    public static LoadingDialogBuilder getLoadingDialogBuilder2(Context context) {
        return new LoadingDialogBuilder(context, DialogType.DIALOG_LOADING2);
    }

}
