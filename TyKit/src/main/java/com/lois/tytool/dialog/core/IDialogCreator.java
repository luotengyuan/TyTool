package com.lois.tytool.dialog.core;


import android.os.Bundle;

public interface IDialogCreator {
    IDialog createDialog(int id, Bundle args);
}
