package com.lois.tytool.dialog.core;

import android.util.SparseArray;

import com.lois.tytool.dialog.provider.AlertDialogProvider;
import com.lois.tytool.dialog.provider.BottomSheetDialogProvider;
import com.lois.tytool.dialog.provider.ProgressDialogCircleProvider;
import com.lois.tytool.dialog.provider.ContentViewDialogProvider;
import com.lois.tytool.dialog.provider.EditTextDialogProvider;
import com.lois.tytool.dialog.provider.ListDialogProvider;
import com.lois.tytool.dialog.provider.LoadingDialogProvider0;
import com.lois.tytool.dialog.provider.LoadingDialogProvider1;
import com.lois.tytool.dialog.provider.LoadingDialogProvider2;
import com.lois.tytool.dialog.provider.MessageDialogProvider;
import com.lois.tytool.dialog.provider.ProgressDialogDonutProvider;
import com.lois.tytool.dialog.provider.ProgressDialogProvider0;
import com.lois.tytool.dialog.provider.ProgressDialogArcProvider;


/**
 * 对话框生成工厂
 */
public final class DialogFactory {
    private final static SparseArray<IDialogProvider<?>> dialogProviders = new SparseArray<>();

    static {
        register(DialogType.DIALOG_OTHER, new ContentViewDialogProvider());
        register(DialogType.DIALOG_MESSAGE, new MessageDialogProvider());
        register(DialogType.DIALOG_ALERT, new AlertDialogProvider());
        register(DialogType.DIALOG_LOADING0, new LoadingDialogProvider0());
        register(DialogType.DIALOG_LOADING1, new LoadingDialogProvider1());
        register(DialogType.DIALOG_LOADING2, new LoadingDialogProvider2());
        register(DialogType.DIALOG_LIST, new ListDialogProvider());
        register(DialogType.DIALOG_EDIT_TEXT, new EditTextDialogProvider());
        register(DialogType.DIALOG_BOTTOM_SHEET, new BottomSheetDialogProvider());
        register(DialogType.DIALOG_PROGRESS, new ProgressDialogProvider0());
        register(DialogType.DIALOG_DONUT_PROGRESS, new ProgressDialogDonutProvider());
        register(DialogType.DIALOG_CIRCLE_PROGRESS, new ProgressDialogCircleProvider());
        register(DialogType.DIALOG_ARC_PROGRESS, new ProgressDialogArcProvider());
    }

    public static <T extends DialogBuilder<T>> void register(int dialogType, IDialogProvider<T> dialogProvider) {
        dialogProviders.put(dialogType, dialogProvider);
    }

    public static <T extends DialogBuilder<T>> IDialog create(DialogBuilder<T> dialogBuilder) {
        int dialogType = dialogBuilder.getDialogType();
        IDialogProvider dialogProvider = dialogProviders.get(dialogType);
        return dialogProvider.createDialog(dialogBuilder);
    }

}
