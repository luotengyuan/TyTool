package com.lois.tytool.dialog.core;

public interface IDialog {
    void dismiss();

    IDialog show();

    android.app.Dialog getInnerDialog();
}
