package cn.yhq.dialog.provider;

import android.app.Dialog;

import androidx.appcompat.app.AlertDialog;

import cn.yhq.dialog.builder.AlertDialogBuilder;
import cn.yhq.dialog.core.DialogProvider;


/**
 * Created by Yanghuiqiang on 2016/10/8.
 */

public class AlertDialogProvider extends DialogProvider<AlertDialogBuilder> {

    @Override
    public Dialog createInnerDialog(AlertDialogBuilder dialogBuilder) {
        dialogBuilder.defaultButtonText();
        return new AlertDialog.Builder(dialogBuilder.getContext()).setTitle(dialogBuilder.getTitle())
                .setMessage(dialogBuilder.getMessage())
                .setNegativeButton(dialogBuilder.getNegativeButtonText(),
                        dialogBuilder.getOnNegativeButtonClickListener())
                .setPositiveButton(dialogBuilder.getPositiveButtonText(),
                        dialogBuilder.getOnPositiveButtonClickListener())
                .create();
    }

}
