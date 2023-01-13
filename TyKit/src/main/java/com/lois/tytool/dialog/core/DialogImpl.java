package com.lois.tytool.dialog.core;

class DialogImpl implements IDialog {
    private android.app.Dialog dialog;
    private DialogBuilder dialogBuilder;

    DialogImpl(android.app.Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void dismiss() {
        dialog.dismiss();
    }

    @Override
    public IDialog show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        return this;
    }

    @Override
    public android.app.Dialog getInnerDialog() {
        return this.dialog;
    }

    void setDialogBuilder(DialogBuilder dialogBuilder) {
        this.dialogBuilder = dialogBuilder;
    }

    DialogBuilder getDialogBuilder() {
        return dialogBuilder;
    }
}
