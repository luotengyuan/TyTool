package com.lois.tytool.dialog.provider;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AlertDialog;

import com.lois.tytool.R;
import com.lois.tytool.dialog.Utils;
import com.lois.tytool.dialog.builder.OtherDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class ContentViewDialogProvider extends DialogProvider<OtherDialogBuilder> {

    @SuppressLint("RestrictedApi")
    @Override
    public Dialog createInnerDialog(OtherDialogBuilder dialogBuilder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(dialogBuilder.getContext())
                .setTitle(dialogBuilder.getTitle()).setMessage(dialogBuilder.getMessage())
                .setNegativeButton(dialogBuilder.getNegativeButtonText(),
                        dialogBuilder.getOnNegativeButtonClickListener())
                .setPositiveButton(dialogBuilder.getPositiveButtonText(),
                        dialogBuilder.getOnPositiveButtonClickListener());

        TypedValue typedValue = new TypedValue();
        dialogBuilder.getContext().getTheme().resolveAttribute(androidx.appcompat.R.attr.dialogPreferredPadding,
                typedValue, true);

        int DIALOG_SPACING_TOP = Utils.dp2px(dialogBuilder.getContext(), 20);
        int DIALOG_SPACING_BOTTOM = Utils.dp2px(dialogBuilder.getContext(), 10);
        int DIALOG_SPACING_LEFT =
                (int) dialogBuilder.getContext().getResources().getDimension(typedValue.resourceId);
        int DIALOG_SPACING_RIGHT = DIALOG_SPACING_LEFT;

        FrameLayout frameLayout = new FrameLayout(dialogBuilder.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        if (dialogBuilder.getContentViewResId() != 0) {
            dialogBuilder.setContentView(
                    View.inflate(dialogBuilder.getContext(), dialogBuilder.getContentViewResId(), null));
        }

        if (dialogBuilder.getContentView() != null) {
            FrameLayout.LayoutParams layoutParams = dialogBuilder.getLayoutParams();

            if (layoutParams == null) {
                layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
            }
            frameLayout.addView(dialogBuilder.getContentView(), layoutParams);
        }

        builder.setView(frameLayout, DIALOG_SPACING_LEFT, DIALOG_SPACING_TOP, DIALOG_SPACING_RIGHT,
                DIALOG_SPACING_BOTTOM).setRecycleOnMeasureEnabled(true);

        return builder.create();
    }
}
