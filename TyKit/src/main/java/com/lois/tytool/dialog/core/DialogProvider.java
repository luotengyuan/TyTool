package com.lois.tytool.dialog.core;

public abstract class DialogProvider<T extends DialogBuilder<T>> implements IDialogProvider<T> {

    public abstract android.app.Dialog createInnerDialog(T dialogBuilder);

    @Override
    public final IDialog createDialog(T dialogBuilder) {
        android.app.Dialog innerDialog = this.createInnerDialog(dialogBuilder);
        innerDialog.setOnCancelListener(dialogBuilder.getOnCancelListener());
        innerDialog.setOnDismissListener(dialogBuilder.getOnDismissListener());
        innerDialog.setOnShowListener(dialogBuilder.getOnShowListener());
        innerDialog.setCanceledOnTouchOutside(dialogBuilder.isCanceledOnTouchOutside());
        DialogImpl dialog = new DialogImpl(innerDialog);
        dialog.setDialogBuilder(dialogBuilder);
        return dialog;
    }
}
