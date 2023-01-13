package com.lois.tytool.dialog.provider;

import android.app.Dialog;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.lois.tytool.dialog.builder.BottomSheetDialogBuilder;
import com.lois.tytool.dialog.core.DialogProvider;

public class BottomSheetDialogProvider extends DialogProvider<BottomSheetDialogBuilder> {

    @Override
    public Dialog createInnerDialog(BottomSheetDialogBuilder dialogBuilder) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(dialogBuilder.getContext());

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

        bottomSheetDialog.setContentView(frameLayout);

        return bottomSheetDialog;
    }
}
